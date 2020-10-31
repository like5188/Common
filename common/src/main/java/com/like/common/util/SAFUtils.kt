package com.like.common.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts


/**
 * Storage Access Framework
 * 访问外部存储的公共目录：
 *    api<29（Android10）：不需要申请存储权限
 *    api>=29：
 *      1、访问自己应用创建的文件：(不需要申请存储权限)
 *      2、访问其他应用创建的非媒体文件(pdf、office、doc、txt等)：(需要申请存储权限)
 */
object SAFUtils {

    /**
     * 选择文件
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

    inline fun createFile(activityResultCaller: ActivityResultCaller, name: String, crossinline callback: (Uri?) -> Unit) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        // 文件类型
        intent.type = "image/*"
        // 文件名称
        intent.putExtra(Intent.EXTRA_TITLE, name)
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

    fun queryImageInfo(context: Context, imageUri: Uri, callback: (String, Long) -> Unit) {
        // 获取图片信息
        val imageProjection = arrayOf(
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media._ID
        )
        context.contentResolver
                .query(imageUri, imageProjection, null, null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val displayName = cursor.getString(cursor.getColumnIndexOrThrow(imageProjection[0]))
                        val size = cursor.getLong(cursor.getColumnIndexOrThrow(imageProjection[1]))
                        callback(displayName, size)
                    }
                }
    }
}