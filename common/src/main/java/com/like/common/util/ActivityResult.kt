package com.like.common.util

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MainThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ActivityResult {
    class RequestPermission(val activity: ComponentActivity) {
        private var continuation: Continuation<Boolean>? = null
        private var callback: ((Boolean) -> Unit)? = null
        private val launcher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            callback?.invoke(it)
            continuation?.resume(it)
        }

        suspend fun requestPermission(permission: String): Boolean = withContext(Dispatchers.Main) {
            suspendCoroutine {
                continuation = it
                launcher.launch(permission)
            }
        }

        @MainThread
        fun requestPermission(permission: String, callback: (Boolean) -> Unit) {
            this.callback = callback
            launcher.launch(permission)
        }
    }

    class RequestPermissions(val activity: ComponentActivity) {
        private var continuation: Continuation<Boolean>? = null
        private var callback: ((Boolean) -> Unit)? = null
        private val launcher = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val result = it.values.all { it }
            callback?.invoke(result)
            continuation?.resume(result)
        }

        suspend fun requestPermissions(vararg permissions: String): Boolean = withContext(Dispatchers.Main) {
            suspendCoroutine {
                continuation = it
                launcher.launch(permissions)
            }
        }

        @MainThread
        fun requestPermissions(vararg permissions: String, callback: (Boolean) -> Unit) {
            this.callback = callback
            launcher.launch(permissions)
        }
    }

    class StartActivityForResult(val activity: ComponentActivity) {
        private var continuation: Continuation<Intent?>? = null
        private var callback: ((Intent?) -> Unit)? = null
        private val launcher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val intent = if (it.resultCode == Activity.RESULT_OK) {
                it.data
            } else {
                null
            }
            callback?.invoke(intent)
            continuation?.resume(intent)
        }

        suspend fun startActivityForResult(intent: Intent): Intent? = withContext(Dispatchers.Main) {
            suspendCoroutine {
                continuation = it
                launcher.launch(intent)
            }
        }

        @MainThread
        fun startActivityForResult(intent: Intent, callback: (Intent?) -> Unit) {
            this.callback = callback
            launcher.launch(intent)
        }
    }

    class StartIntentSenderForResult(val activity: ComponentActivity) {
        private var continuation: Continuation<Boolean>? = null
        private var callback: ((Boolean) -> Unit)? = null
        private val launcher = activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            val result = it.resultCode == Activity.RESULT_OK
            callback?.invoke(result)
            continuation?.resume(result)
        }

        suspend fun startIntentSenderForResult(intentSenderRequest: IntentSenderRequest): Boolean = withContext(Dispatchers.Main) {
            suspendCoroutine {
                continuation = it
                launcher.launch(intentSenderRequest)
            }
        }

        @MainThread
        fun startIntentSenderForResult(intentSenderRequest: IntentSenderRequest, callback: (Boolean) -> Unit) {
            this.callback = callback
            launcher.launch(intentSenderRequest)
        }
    }
}
