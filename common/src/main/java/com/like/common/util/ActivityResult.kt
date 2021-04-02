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
import kotlin.coroutines.Continuation
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

class StartActivityForResultWrapper(private val caller: ActivityResultCaller) {
    val context = caller.context
    val lifecycleOwner = caller.lifecycleOwner
    private var continuation: Continuation<Intent?>? = null
    private var callback: ((Intent?) -> Unit)? = null
    private val launcher = caller.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val intent = if (it.resultCode == Activity.RESULT_OK) {
            it.data
        } else {
            null
        }
        callback?.invoke(intent)
        continuation?.resume(intent)
    }

    suspend inline fun <reified T : Activity> startActivityForResult(vararg params: Pair<String, Any?>): Intent? =
            startActivityForResult(context.createIntent<T>(*params))

    suspend fun startActivityForResult(intent: Intent): Intent? = withContext(Dispatchers.Main) {
        suspendCoroutine {
            continuation = it
            launcher.launch(intent)
        }
    }

    @MainThread
    inline fun <reified T : Activity> startActivityForResult(vararg params: Pair<String, Any?>, noinline callback: (Intent?) -> Unit) {
        startActivityForResult(context.createIntent<T>(*params), callback)
    }

    @MainThread
    fun startActivityForResult(intent: Intent, callback: (Intent?) -> Unit) {
        this.callback = callback
        launcher.launch(intent)
    }

}

class RequestPermissionWrapper(private val caller: ActivityResultCaller) {
    val context = caller.context
    val lifecycleOwner = caller.lifecycleOwner
    private var continuation: Continuation<Boolean>? = null
    private var callback: ((Boolean) -> Unit)? = null
    private val launcher = caller.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
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

class RequestMultiplePermissionsWrapper(private val caller: ActivityResultCaller) {
    val context = caller.context
    val lifecycleOwner = caller.lifecycleOwner
    private var continuation: Continuation<Boolean>? = null
    private var callback: ((Boolean) -> Unit)? = null
    private val launcher = caller.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
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

class StartIntentSenderForResultWrapper(private val caller: ActivityResultCaller) {
    val context = caller.context
    val lifecycleOwner = caller.lifecycleOwner
    private var continuation: Continuation<Boolean>? = null
    private var callback: ((Boolean) -> Unit)? = null
    private val launcher = caller.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
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
