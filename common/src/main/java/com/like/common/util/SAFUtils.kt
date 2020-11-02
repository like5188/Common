package com.like.common.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile


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
     * 选择单个文件
     *
     * @param callback  返回的 Uri 为文件的
     */
    inline fun openDocument(activityResultCaller: ActivityResultCaller, mimeType: MimeType = MimeType._0, crossinline callback: (Uri?) -> Unit) {
        //通过系统的文件浏览器选择一个文件
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        //筛选，只显示可以“打开”的结果，如文件(而不是联系人或时区列表)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        //过滤只显示指定类型文件
        intent.type = mimeType.value
        activityResultCaller.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                callback(it?.data?.data)
            }
        }.launch(intent)
    }

    /**
     * 选择文件夹
     *
     * 注意：在Android 11上，无法通过SAF选择External Storage根目录、Downloads目录以及App专属目录(Android/data、Android/obb)
     *
     * @param callback  返回文件夹 DocumentFile
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    inline fun openDocumentTree(activityResultCaller: ActivityResultCaller, crossinline callback: (DocumentFile?) -> Unit) {
        //通过系统的文件浏览器选择一个文件
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        activityResultCaller.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val treeUri = it?.data?.data
                val documentFile = if (treeUri == null) {
                    null
                } else {
                    DocumentFile.fromTreeUri(activityResultCaller.context, treeUri)
                }
                callback(documentFile)
            }
        }.launch(intent)
    }

    /**
     * 创建文件
     *
     * @param callback  返回的 Uri 为文件的
     */
    inline fun createDocument(activityResultCaller: ActivityResultCaller, fileName: String, mimeType: MimeType = MimeType._0, crossinline callback: (Uri?) -> Unit) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        // Filter to only show results that can be "opened", such as a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = mimeType.value// 文件类型
        intent.putExtra(Intent.EXTRA_TITLE, fileName)// 文件名称
        activityResultCaller.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                callback(it?.data?.data)
            }
        }.launch(intent)
    }

    /**
     * 删除文件
     */
    fun deleteDocument(context: Context, uri: Uri?): Boolean {
        uri ?: return false
        return try {
            DocumentsContract.deleteDocument(context.contentResolver, uri)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}