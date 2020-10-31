package com.like.common.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts


/**
 * Storage Access Framework
 *
 * 1、在Android 4.4 之前，如果想从另外一个App中选择一个文件（比如从图库中选择一张图片文件）必须触发一个ACTION为ACTION_PICK或者ACTION_GET_CONTENT的Intent，再在候选的App中选择一个App，从中获得你想要的文件，最关键的是被选择的App中要具有能为你提供文件的功能，但如果一个不负责任的第三方开发者注册了一个恰恰符合你需求的Intent，但是没有实现返回文件的功能，那么就会出现意想不到的错误。
 * 2、Android 4.4中引入了Storage Access Framework存储访问框架（SAF）。SAF为用户浏览手机中存储的内容（不仅包括文档、图片，视频、音频、下载、GoogleDrive等，还包括所有继承自DocumentsProvider的特定云存储、本地存储提供的内容）提供了统一的管理和展现形式
 * 无论内容来自于哪里，是哪个应用调用浏览系统文件内容的命令，SAF都会用一个统一的界面（DocumentsUI App）让你去使用，通过发送Intent.ACTION_OPEN_DOCUMENT的 Intent来弹出一个很漂亮的界面
 *
 * 主要角色成员包括：
 * 1、Document Provider 文件存储服务提供者。
 * Document Provider让一个存储服务（比如Google Drive）可以对外以统一的形式展示自己所管理的文件，一个Document Provider代码上就是实现了DocumentsProvider.java的子类
 * 2、DocumentsUI 文件存储选择器App
 *
 * 访问外部存储的公共目录：
 *    api<29（Android10）：不需要申请存储权限
 *    api>=29：
 *      1、访问自己应用创建的文件：(不需要申请存储权限)
 *      2、访问其他应用创建的非媒体文件(pdf、office、doc、txt等)：(需要申请存储权限)
 */
object SAFUtils {

    /**
     * 选择图片文件
     */
    inline fun openDocument(activityResultCaller: ActivityResultCaller, crossinline callback: (Uri?) -> Unit) {
        //通过系统的文件浏览器选择一个文件
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        //筛选，只显示可以“打开”的结果，如文件(而不是联系人或时区列表)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        //过滤只显示图像类型文件
        intent.type = "image/*"
        activityResultCaller.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                callback(it?.data?.data)
            }
        }.launch(intent)
    }

    inline fun createFile(activityResultCaller: ActivityResultCaller, fileName: String, mimeType: String, crossinline callback: (Uri?) -> Unit) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        // Filter to only show results that can be "opened", such as a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = mimeType// 文件类型
        intent.putExtra(Intent.EXTRA_TITLE, fileName)// 文件名称
        activityResultCaller.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                callback(it?.data?.data)
            }
        }.launch(intent)
    }

    fun getBitmapFromUri(context: Context, uri: Uri?): Bitmap? {
        uri ?: return null
        return try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use {
                val fileDescriptor = it.fileDescriptor
                BitmapFactory.decodeFileDescriptor(fileDescriptor)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun readTextFromUri(context: Context, uri: Uri?): String? {
        uri ?: return null
        return try {
            context.contentResolver.openInputStream(uri)?.bufferedReader()?.readText()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun writeTextToUri(context: Context, uri: Uri?, text: String) {
        uri ?: return
        try {
            context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.write(text)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteFile(context: Context, uri: Uri?): Boolean {
        uri ?: return false
        return try {
            DocumentsContract.deleteDocument(context.contentResolver, uri)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun dumpImageMetaData(context: Context, imageUri: Uri, callback: (String, String) -> Unit) {
        // 获取图片信息
        context.contentResolver
                .query(imageUri, null, null, null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        val size = cursor.getString(cursor.getColumnIndex(OpenableColumns.SIZE))
                        callback(displayName, size)
                    }
                }
    }
}