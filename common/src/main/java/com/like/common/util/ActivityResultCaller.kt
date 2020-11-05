package com.like.common.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

val ActivityResultCaller.context: Context
    get() {
        return when (this) {
            is ComponentActivity -> this
            is Fragment -> activity ?: throw IllegalStateException("Fragment $this not attached to Activity")
            else -> throw IllegalStateException("$this must be androidx.activity.ComponentActivity or androidx.fragment.app.Fragment")
        }
    }

suspend inline fun <reified T : Activity> ActivityResultCaller.startActivityForResult(vararg params: Pair<String, Any?>): Intent? = suspendCoroutine { cont ->
    val intent = context.createIntent<T>(*params)
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        cont.resume(
                if (it.resultCode == Activity.RESULT_OK) {
                    it.data
                } else {
                    null
                }
        )
    }.launch(intent)
}

suspend fun ActivityResultCaller.startActivityForResult(intent: Intent): Intent? = suspendCoroutine { cont ->
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        cont.resume(
                if (it.resultCode == Activity.RESULT_OK) {
                    it.data
                } else {
                    null
                }
        )
    }.launch(intent)
}

suspend fun ActivityResultCaller.startIntentSenderForResult(intentSenderRequest: IntentSenderRequest): Boolean = suspendCoroutine { cont ->
    registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
        cont.resume(it.resultCode == Activity.RESULT_OK)
    }.launch(intentSenderRequest)
}

suspend fun ActivityResultCaller.requestPermission(permission: String): Boolean = suspendCoroutine { cont ->
    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        cont.resume(it)
    }.launch(permission)
}

suspend fun ActivityResultCaller.requestPermissions(vararg permissions: String): Boolean = suspendCoroutine { cont ->
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        cont.resume(it.values.all { it })
    }.launch(permissions)
}