package com.like.common.util

import android.content.Context
import android.content.Intent
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * apk 相关工具类
 */
object ApkUtils {

    // 1、需要权限：<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    // 2、android8.0 需要安装未知来源应用的权限：<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />，
    // 这样会在App调用安装界面的同时，系统会自动询问用户完成授权。注意：此权限用下面的方法无法检查。
    suspend fun install(context: Context, apkFile: File) {
        val fileName = apkFile.name
        val suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase()
        if ("apk" != suffix) return

        // 通过device file explorer来查看这个下载下来的文件时，发现这个文件本身的权限是600，即文件拥有者只有读写权限，而没有运行权限。所以无法进行安装
        withContext(Dispatchers.IO) {
            val process = Runtime.getRuntime().exec("chmod 755 $apkFile")// 将apk权限改为755就可以了。注意，必须调用waitFor。
            process.waitFor()
        }
        try {
            val installIntent = Intent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // android7.0 需要通过FileProvider来获取文件uri。并开始强制启用StrictMode“严苛模式”，这个策略禁止在app外暴露 “file://“URI。
                // 为了与其他应用共享文件，你应该发送"content://"URI ，并授予临时访问权限。授予这个临时访问权限的最签单方法就是使用FileProvider类。
                installIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION // 授予目录临时共享权限
            }
            installIntent.action = Intent.ACTION_VIEW
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            installIntent.setDataAndType(apkFile.getUri(context), "application/vnd.android.package-archive")
            context.startActivity(installIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}