package com.like.common.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.like.common.view.callback.RxCallback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

/**
 * apk 相关工具类
 */
class ApkUtils {
    private var mContext: Context? = null
    private var mPermissionUtils: PermissionUtils? = null
    private var mRxCallback: RxCallback? = null

    constructor(fragmentActivity: FragmentActivity) {
        mContext = fragmentActivity
        mPermissionUtils = PermissionUtils(fragmentActivity)
        mRxCallback = RxCallback(fragmentActivity)
    }

    constructor(fragment: Fragment) {
        mContext = fragment.context
        mPermissionUtils = PermissionUtils(fragment)
        mRxCallback = RxCallback(fragment)
    }

    @SuppressLint("DefaultLocale")
    fun install(apkFile: File) {
        if (!apkFile.exists()) return
        val fileName = apkFile.name
        val suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase()
        if ("apk" != suffix) return
        val permissionUtils = mPermissionUtils ?: return
        val rxCallback = mRxCallback ?: return
        val context = mContext ?: return

        // android6.0以上需要申请危险权限：<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionUtils.checkStoragePermissions({
                if (!it) return@checkStoragePermissions
                // android8.0 安装未知来源应用需要添加权限Manifest.permission.REQUEST_INSTALL_PACKAGES
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!context.packageManager.canRequestPackageInstalls()) {
                        AlertDialog.Builder(context)
                                .setTitle("权限申请")
                                .setMessage("您没有允许安装未知来源应用的权限")
                                .setPositiveButton("去申请") { dialog, _ ->
                                    dialog.cancel()
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}"))
                                        rxCallback.startActivityForResult(intent).subscribe {
                                            install(context, apkFile)
                                        }
                                    }
                                }
                                .setNegativeButton("放弃安装", null)
                                .show()
                    } else {
                        install(context, apkFile)
                    }
                } else {
                    install(context, apkFile)
                }
            })
        } else {
            GlobalScope.launch {
                // 通过device file explorer来查看这个下载下来的cache中的文件时，发现这个文件本身的权限是600，即使是文件拥有者只有读写权限，而没有运行权限。所以无法进行安装
                val job = launch {
                    val process = Runtime.getRuntime().exec("chmod 755 $apkFile")// 将apk权限改为755就可以了。注意，必须调用waitFor。
                    process.waitFor()
                }
                job.join()
                install(context, apkFile)
            }
        }
    }

    private fun install(context: Context, apkFile: File) {
        try {
            val installIntent = Intent()
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // android7.0 需要通过FileProvider来获取文件uri。并开始强制启用StrictMode“严苛模式”，这个策略禁止在app外暴露 “file://“URI。
                // 为了与其他应用共享文件，你应该发送"content://"URI ，并授予临时访问权限。授予这个临时访问权限的最签单方法就是使用FileProvider类。
                installIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION // 授予目录临时共享权限
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", apkFile) // 格式：content://xxx
            } else {// android7.0 之前
                Uri.fromFile(apkFile)// 格式：file://xxx
            }
            installIntent.action = Intent.ACTION_VIEW
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            installIntent.setDataAndType(uri, "application/vnd.android.package-archive")
            context.startActivity(installIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}