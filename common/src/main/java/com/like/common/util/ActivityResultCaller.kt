package com.like.common.util

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

val ActivityResultCaller.context: Activity
    get() {
        return when (this) {
            is ComponentActivity -> this
            is Fragment -> activity ?: throw IllegalStateException("Fragment $this not attached to Activity")
            else -> throw IllegalStateException("$this must be androidx.activity.ComponentActivity or androidx.fragment.app.Fragment")
        }
    }

val ActivityResultCaller.lifecycleOwner: LifecycleOwner
    get() {
        return when (this) {
            is ComponentActivity -> this
            is Fragment -> this
            else -> throw IllegalStateException("$this must be androidx.activity.ComponentActivity or androidx.fragment.app.Fragment")
        }
    }

suspend inline fun <reified T : Activity> ActivityResultCaller.startActivityForResult(vararg params: Pair<String, Any?>): Intent? {
    val intent = context.createIntent<T>(*params)
    return startActivityForResult(intent)
}

@MainThread
inline fun <reified T : Activity> ActivityResultCaller.startActivityForResult(vararg params: Pair<String, Any?>, crossinline callback: (Intent?) -> Unit) {
    val intent = context.createIntent<T>(*params)
    startActivityForResult(intent, callback)
}

suspend fun ActivityResultCaller.startActivityForResult(intent: Intent): Intent? = withContext(Dispatchers.Main) {
    suspendCoroutine { cont ->
        startActivityForResult(intent) {
            cont.resume(it)
        }
    }
}

@MainThread
inline fun ActivityResultCaller.startActivityForResult(intent: Intent, crossinline callback: (Intent?) -> Unit) {
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        callback(if (it.resultCode == Activity.RESULT_OK) {
            it.data
        } else {
            null
        })
    }.launch(intent)
}

suspend fun ActivityResultCaller.startIntentSenderForResult(intentSenderRequest: IntentSenderRequest): Boolean = withContext(Dispatchers.Main) {
    suspendCoroutine { cont ->
        startIntentSenderForResult(intentSenderRequest) {
            cont.resume(it)
        }
    }
}

@MainThread
inline fun ActivityResultCaller.startIntentSenderForResult(intentSenderRequest: IntentSenderRequest, crossinline callback: (Boolean) -> Unit) {
    registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
        callback(it.resultCode == Activity.RESULT_OK)
    }.launch(intentSenderRequest)
}

suspend fun ActivityResultCaller.requestPermission(permission: String): Boolean = withContext(Dispatchers.Main) {
    suspendCoroutine { cont ->
        requestPermission(permission) {
            cont.resume(it)
        }
    }
}

@MainThread
inline fun ActivityResultCaller.requestPermission(permission: String, crossinline callback: (Boolean) -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        callback(it)
    }.launch(permission)
}

suspend fun ActivityResultCaller.requestPermissions(vararg permissions: String): Boolean = withContext(Dispatchers.Main) {
    suspendCoroutine { cont ->
        requestPermissions(*permissions) {
            cont.resume(it)
        }
    }
}

@MainThread
inline fun ActivityResultCaller.requestPermissions(vararg permissions: String, crossinline callback: (Boolean) -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        callback(it.values.all { it })
    }.launch(permissions)
}