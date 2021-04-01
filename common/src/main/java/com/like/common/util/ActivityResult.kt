package com.like.common.util

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ActivityResult {
    class RequestPermission(val activity: ComponentActivity) {
        private var continuation: Continuation<Boolean>? = null
        private val launcher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            continuation?.resume(it)
        }

        suspend fun requestPermission(permission: String): Boolean = suspendCoroutine {
            continuation = it
            launcher.launch(permission)
        }
    }

    class RequestPermissions(val activity: ComponentActivity) {
        private var continuation: Continuation<Boolean>? = null
        private val launcher = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            continuation?.resume(it.values.all { it })
        }

        suspend fun requestPermissions(vararg permissions: String): Boolean = suspendCoroutine {
            continuation = it
            launcher.launch(permissions)
        }
    }

    class StartActivityForResult(val activity: ComponentActivity) {
        private var continuation: Continuation<Intent?>? = null
        private val launcher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            continuation?.resume(if (it.resultCode == Activity.RESULT_OK) {
                it.data
            } else {
                null
            })
        }

        suspend fun startActivityForResult(intent: Intent): Intent? = suspendCoroutine {
            continuation = it
            launcher.launch(intent)
        }
    }

    class StartIntentSenderForResult(val activity: ComponentActivity) {
        private var continuation: Continuation<Boolean>? = null
        private val launcher = activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            continuation?.resume(it.resultCode == Activity.RESULT_OK)
        }

        suspend fun startIntentSenderForResult(intentSenderRequest: IntentSenderRequest): Boolean = suspendCoroutine {
            continuation = it
            launcher.launch(intentSenderRequest)
        }
    }
}
