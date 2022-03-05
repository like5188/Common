package com.like.common.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/*
 * 注意：创建 Wrapper 时不能用 by lazy{}，只能直接 new，否则会报错。比如：
 * private val requestPermissionWrapper = RequestPermissionWrapper(this)
 * requestPermissionWrapper.requestPermission(android.Manifest.permission.CAMERA) {
            //注意：从 Android 30 开始，没有不再提示选择，系统会在拒绝两次后直接不再提示。
            //如果返回true表示用户点了禁止获取权限，但没有勾选不再提示。
            //返回false表示用户点了禁止获取权限，并勾选不再提示。
            //我们可以通过该方法判断是否要继续申请权限
            if (!it && !ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
                // 用户选择 "不再询问" 后的提示方案
                AlertDialog.Builder(this)
                    .setTitle("授权失败")
                    .setMessage("您需要授权此权限才能使用此功能")
                    .setPositiveButton("去授权") { dialog, which -> // 跳转到设置界面
                        val intent = Intent()
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                        intent.data = Uri.fromParts("package", packageName, null)
                        startActivity(intent)
                    }
                    .setNegativeButton("取消") { dialog, which -> }
                    .create().show()
            }
        }
 */

val ActivityResultCaller.activity: Activity
    get() {
        return when (this) {
            is ComponentActivity -> this
            is Fragment -> activity ?: throw IllegalStateException("Fragment $this not attached to Activity")
            else -> throw IllegalStateException("$this must be androidx.activity.ComponentActivity or androidx.fragment.app.Fragment")
        }
    }

inline fun <reified T : Activity> Context.startActivity(vararg params: Pair<String, Any?>) {
    startActivity(createIntent<T>(*params))
}

class StartActivityForResultWrapper(caller: ActivityResultCaller) {
    val activity = caller.activity
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
            startActivityForResult(activity.createIntent<T>(*params))

    suspend fun startActivityForResult(intent: Intent): Intent? = withContext(Dispatchers.Main) {
        suspendCoroutine {
            continuation = it
            launcher.launch(intent)
        }
    }

    @MainThread
    inline fun <reified T : Activity> startActivityForResult(vararg params: Pair<String, Any?>, noinline callback: (Intent?) -> Unit) {
        startActivityForResult(activity.createIntent<T>(*params), callback)
    }

    @MainThread
    fun startActivityForResult(intent: Intent, callback: (Intent?) -> Unit) {
        this.callback = callback
        launcher.launch(intent)
    }

}

class RequestPermissionWrapper(caller: ActivityResultCaller) {
    val activity = caller.activity
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

class RequestMultiplePermissionsWrapper(caller: ActivityResultCaller) {
    val activity = caller.activity
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

class StartIntentSenderForResultWrapper(caller: ActivityResultCaller) {
    val activity = caller.activity
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
