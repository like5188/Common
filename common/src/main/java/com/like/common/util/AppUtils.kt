package com.like.common.util

import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import androidx.core.content.FileProvider
import android.util.Log
import android.view.View
import android.view.Window
import com.like.common.view.callback.RxCallback
import java.io.File
import kotlin.jvm.functions.FunctionN

/**
 * app相关工具类
 *
 * Manifest.permission.WRITE_SETTINGS
 * android8.0 安装未知来源应用需要添加权限Manifest.permission.REQUEST_INSTALL_PACKAGES
 */
class AppUtils private constructor(private val mContext: Context) {
    companion object : SingletonHolder<AppUtils>(object : FunctionN<AppUtils> {
        override val arity: Int = 1 // number of arguments that must be passed to constructor

        override fun invoke(vararg args: Any?): AppUtils {
            return AppUtils(args[0] as Context)
        }
    })

    private val mAppStatus: AppStatus

    init {
        mAppStatus = AppStatus()
        mContext.packageManager?.also { pm ->
            try {
                val pi = pm.getPackageInfo(mContext.packageName, 0)
                mAppStatus.packageName = pi.packageName
                mAppStatus.versionName = pi.versionName
                mAppStatus.versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pi.longVersionCode else pi.versionCode.toLong()
            } catch (e: Exception) {
                Log.e("AppUtils", "获得应用packageName、versionCode、versionName失败 " + e.message)
            }

            try {
                mAppStatus.sign = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    pm.getPackageInfo(mContext.packageName, PackageManager.GET_SIGNING_CERTIFICATES).signingInfo.apkContentsSigners[0].toCharsString()
                } else {
                    pm.getPackageInfo(mContext.packageName, PackageManager.GET_SIGNATURES).signatures[0].toCharsString()
                }
            } catch (e: Exception) {
                Log.e("AppUtils", "获得应用sign失败 " + e.message)
            }
        }
        try {
            mAppStatus.downSource = mContext.packageManager.getApplicationInfo(mContext.packageName, PackageManager.GET_META_DATA)?.metaData?.getString("UMENG_CHANNEL")
        } catch (e: Exception) {
            Log.e("AppUtils", "获得应用downSource信息失败 " + e.message)
        }

        Log.i("AppUtils", mAppStatus.toString())
    }

    /**
     * app相关的状态信息
     */
    class AppStatus {
        /**
         * 版本号码
         */
        var versionCode: Long = 0
        /**
         * 版本名称
         */
        var versionName: String? = null
        /**
         * 渠道号码
         */
        var downSource: String? = null
        /**
         * 包名
         */
        var packageName: String? = null
        /**
         * 签名信息
         */
        var sign: String? = null

        override fun toString(): String {
            return "AppStatus{" +
                    "versionCode=" + versionCode +
                    ", versionName='" + versionName + '\''.toString() +
                    ", downSource='" + downSource + '\''.toString() +
                    ", packageName='" + packageName + '\''.toString() +
                    ", sign='" + sign + '\''.toString() +
                    '}'.toString()
        }
    }

    /**
     * 检测应用是否在前台运行
     *
     * @param packageName 包名
     * @param context     上下文
     * @return 是否存在
     */
    fun isRunForeground(context: Context?, packageName: String = context?.packageName
            ?: ""): Boolean = getActivityManager(context)?.runningAppProcesses?.any {
        it.processName == packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    } ?: false

    /**
     * 检测应用是否在后台运行
     *
     * @param packageName 包名
     * @param context     上下文
     * @return 是否存在
     */
    fun isRunBackground(context: Context?, packageName: String = context?.packageName
            ?: ""): Boolean =
            getActivityManager(context)?.runningAppProcesses?.any {
                it.processName == packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND
            } ?: false

    fun getBaseActivityName(context: Context): String {
        val am = getActivityManager(context) ?: return ""
        val list = am.getRunningTasks(100)
        for (info in list) {
            if (info.baseActivity.packageName == mAppStatus.packageName) {
                return info.baseActivity.className
            }
        }
        return ""
    }

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
     * 判断某个activity是否处于前台
     * <uses-permission android:name = "android.permission.GET_TASKS"></uses-permission>
     *
     * @param context
     * @param cls     需要判断的activity的全类名
     * @return
     */
    fun isTopActivity(context: Context, cls: String): Boolean {
        val applicationContext = context.applicationContext
        val am = getActivityManager(context) ?: return false
        val cn = am.getRunningTasks(1)[0].topActivity
        return !(applicationContext.packageName != cn.packageName || cls != cn.className)
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
        localIntent.data = Uri.fromParts("package", mAppStatus.packageName, null)
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
        val infos = manager.runningAppProcesses
        if (infos != null) {
            for (processInfo in infos) {
                if (processInfo.pid == pid) {
                    return processInfo.processName
                }
            }
        }
        return ""
    }

    private fun getActivityManager(context: Context?) = context?.applicationContext?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager

    fun install(apkFile: File?) {
        apkFile ?: return
        if (!apkFile.exists()) return
        val fileName = apkFile.name
        val suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase()
        if ("apk" != suffix) return
        val applicationContext = mContext.applicationContext
        try {
            val installIntent = Intent()
            //判断是否是AndroidN以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                installIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            } else {
                installIntent.action = Intent.ACTION_VIEW
                installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // android7.0需要通过FileProvider来获取文件uri。
                FileProvider.getUriForFile(applicationContext, applicationContext.packageName + ".fileprovider", apkFile)
            } else {
                Uri.fromFile(apkFile)
            }
            // android8.0 安装未知来源应用需要添加权限Manifest.permission.REQUEST_INSTALL_PACKAGES
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mContext is androidx.fragment.app.FragmentActivity) {
                if (!mContext.packageManager.canRequestPackageInstalls()) {
                    AlertDialog.Builder(mContext)
                            .setTitle("权限申请")
                            .setMessage("您没有允许安装未知来源应用的权限")
                            .setPositiveButton("去申请") { dialog, which ->
                                dialog.cancel()
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${mContext.getPackageName()}"))
                                    RxCallback.getInstance()
                                            .init(mContext)
                                            .startActivityForResult(intent)
                                            .subscribe {
                                                install(installIntent, uri)
                                            }
                                }
                            }.setNegativeButton("放弃安装", null).show()
                } else {
                    install(installIntent, uri)
                }
            } else {
                install(installIntent, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun install(installIntent: Intent, uri: Uri) {
        installIntent.setDataAndType(uri, "application/vnd.android.package-archive")
        mContext.applicationContext.startActivity(installIntent)
    }

    fun exitApp() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mContext.startActivity(startMain)
        System.exit(0)
    }
}