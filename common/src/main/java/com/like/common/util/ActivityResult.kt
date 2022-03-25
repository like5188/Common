package com.like.common.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

/*
 * Activity 返回结果相关的跳转工具类
 * 注意：
 * 1、注册必须在Activity#onStart()方法之前，所以创建 Wrapper 时不能用 by lazy{}，只能直接 new，否则会报错。
 * 2、startActivityForResult 启动的界面时，会忽略目标界面的 launchMode 设置。
 * 3、意外情况：Activity_A启动另一个activity_B，然后A意外被kill掉了，这时候从B返回了，A重新创建了，原先注册的地方不会执行回调。
 *
 * 使用例子：
 * private val requestPermissionLauncher = RequestPermissionLauncher(this)
 * requestPermissionLauncher.launch(android.Manifest.permission.CAMERA) {
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

/**
 * @param I             输入
 * @param O             Android 的 ActivityResult 框架返回值
 * @param RealResult    使用者需要的返回值
 */
open class BaseActivityResultLauncher<I, O, RealResult>(caller: ActivityResultCaller, contract: ActivityResultContract<I, O>) {
    val activity = caller.activity
    private var continuation: CancellableContinuation<RealResult>? = null
    private var callback: ActivityResultCallback<RealResult>? = null
    private val launcher = caller.registerForActivityResult(contract) {
        val realResult = transformResult(it)
        callback?.onActivityResult(realResult)
        callback = null
        continuation?.resume(realResult)
        continuation?.cancel()
        continuation = null
    }

    /**
     * 结果转换
     */
    open fun transformResult(result: O): RealResult {
        @Suppress("UNCHECKED_CAST")
        return result as RealResult
    }

    suspend fun launch(input: I): RealResult = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine {
            continuation = it
            launcher.launch(input)
        }
    }

    @MainThread
    fun launch(input: I, callback: ActivityResultCallback<RealResult>) {
        this.callback = callback
        launcher.launch(input)
    }
}

class RequestPermissionLauncher(caller: ActivityResultCaller) :
    BaseActivityResultLauncher<String, Boolean, Boolean>(
        caller, ActivityResultContracts.RequestPermission()
    )

class RequestMultiplePermissionsLauncher(caller: ActivityResultCaller) :
    BaseActivityResultLauncher<Array<out String>, Map<String, Boolean>, Boolean>(
        caller, ActivityResultContracts.RequestMultiplePermissions()
    ) {

    override fun transformResult(result: Map<String, Boolean>): Boolean {
        return result.values.all { it }
    }

}

class StartActivityForResultLauncher(caller: ActivityResultCaller) :
    BaseActivityResultLauncher<Intent, ActivityResult, ActivityResult>(
        caller, ActivityResultContracts.StartActivityForResult()
    ) {

    suspend inline fun <reified T : Activity> launch(vararg params: Pair<String, Any?>): ActivityResult =
        launch(activity.createIntent<T>(*params))

    @MainThread
    inline fun <reified T : Activity> launch(
        vararg params: Pair<String, Any?>,
        callback: ActivityResultCallback<ActivityResult>
    ) {
        launch(activity.createIntent<T>(*params), callback)
    }

}

class StartIntentSenderForResultLauncher(caller: ActivityResultCaller) :
    BaseActivityResultLauncher<IntentSenderRequest, ActivityResult, ActivityResult>(
        caller, ActivityResultContracts.StartIntentSenderForResult()
    )
