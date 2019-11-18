package com.like.common.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.Window
import kotlin.system.exitProcess

/**
 * app相关工具类
 */
object AppUtils {
    fun getActivityManager(context: Context?) = context?.applicationContext?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager

    /**
     * 版本名称
     */
    fun getVersionName(context: Context) =
            try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                packageInfo.versionName
            } catch (e: Exception) {
                null
            }

    /**
     * 版本号码
     */
    fun getVersionCode(context: Context) =
            try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode else packageInfo.versionCode.toLong()
            } catch (e: Exception) {
                null
            }

    /**
     * 签名信息
     */
    fun getSign(context: Context) =
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES).signingInfo.apkContentsSigners[0].toCharsString()
                } else {
                    context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES).signatures[0].toCharsString()
                }
            } catch (e: Exception) {
                null
            }

    /**
     * 渠道号码
     */
    fun getDownSource(context: Context) =
            try {
                context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA).metaData?.getString("UMENG_CHANNEL")
            } catch (e: Exception) {
                null
            }

    /**
     * 检测应用是否在前台运行
     *
     * @param packageName 包名
     * @param context     上下文
     * @return 是否存在
     */
    fun isRunForeground(context: Context?, packageName: String = context?.packageName ?: ""): Boolean =
            getActivityManager(context)?.runningAppProcesses?.any {
                it.processName == packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            } ?: false

    /**
     * 检测应用是否在后台运行
     *
     * @param packageName 包名
     * @param context     上下文
     * @return 是否存在
     */
    fun isRunBackground(context: Context?, packageName: String = context?.packageName ?: ""): Boolean =
            getActivityManager(context)?.runningAppProcesses?.any {
                it.processName == packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND
            } ?: false

    /**
     * 判断服务是否启动, 注意只要名称相同, 会检测任何服务.
     *
     * @param context
     * @param serviceClass 需要判断的服务类
     * @return
     */
    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val am = getActivityManager(context) ?: return false
        val runningServices = am.getRunningServices(Integer.MAX_VALUE)// 参数表示需要获取的正在运行的服务数量，这里我们取最大值
        if (runningServices != null && !runningServices.isEmpty()) {
            for (r in runningServices) {
                // 添加Uid验证, 防止服务重名, 当前服务无法启动
                if (getUid(context) == r.uid) {
                    if (serviceClass.name == r.service.className) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * 获取应用的Uid, 用于验证服务是否启动
     *
     * @param context 上下文
     * @return uid
     */
    private fun getUid(context: Context?): Int {
        if (context == null) {
            return -1
        }

        val pid = android.os.Process.myPid()
        val am = getActivityManager(context) ?: return -1
        val runningAppProcesses = am.runningAppProcesses
        if (runningAppProcesses != null && runningAppProcesses.isNotEmpty()) {
            for (processInfo in runningAppProcesses) {
                if (processInfo.pid == pid) {
                    return processInfo.uid
                }
            }
        }
        return -1
    }

    /**
     * 获取通知栏和标题栏的总高度
     *
     * @param activity
     * @return
     */
    fun getTopBarHeight(activity: Activity): Int {
        return activity.window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
    }

    /**
     * 跳转到应用设置页面
     *
     * @param context
     */
    fun gotoAppDetailSettingActivity(context: Context) {
        val localIntent = Intent()
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
        localIntent.data = Uri.fromParts("package", context.packageName, null)
        if (context is Activity) {
            context.finish()
        }
        context.startActivity(localIntent)
    }

    /**
     * 获取进程名称
     *
     * @param context 上下文
     * @return 进程名称
     */
    fun getProcessName(context: Context): String {
        val pid = android.os.Process.myPid()
        val manager = getActivityManager(context) ?: return ""
        manager.runningAppProcesses.apply {
            for (processInfo in this) {
                if (processInfo.pid == pid) {
                    return processInfo.processName
                }
            }
        }
        return ""
    }

    fun exitApp(context: Context?) {
        context?.let {
            val startMain = Intent(Intent.ACTION_MAIN)
            startMain.addCategory(Intent.CATEGORY_HOME)
            startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            it.startActivity(startMain)
        }
        exitProcess(0)
    }
}