package com.like.common.util

import android.Manifest
import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

fun File.getUri(context: Context, authority: String) = UriUtils.getUriByFile(context, authority, this)

fun Uri.getFilePath(context: Context) = UriUtils.getFilePathByUri(context, this)

object UriUtils {

    /**
     * 获取图片或者视频的位置信息
     *
     * 一些照片在其 Exif 元数据中包含位置信息，以便用户查看照片的拍摄地点。
     * 但是，由于此位置信息属于敏感信息，如果应用使用了分区存储，默认情况下 Android 10 会对应用隐藏此信息。
     * 所以需要用此方法获取位置信息。
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    @RequiresPermission(Manifest.permission.ACCESS_MEDIA_LOCATION)
    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun getLatLongFromUri(context: Context, uri: Uri?): FloatArray? {
        uri ?: return null
        return withContext(Dispatchers.IO) {
            try {
                // 更新 Uri
                val photoUri = MediaStore.setRequireOriginal(uri)
                context.contentResolver.openInputStream(photoUri)?.use { stream ->
                    val output = floatArrayOf(0f, 0f)
                    ExifInterface(stream).run {
                        // If lat/long is null, fall back to the coordinates (0, 0).
                        getLatLong(output)
                    }
                    output
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getBitmapFromUriByFileDescriptor(context: Context, uri: Uri?): Bitmap? {
        uri ?: return null
        return withContext(Dispatchers.IO) {
            try {
                // 使用文件描述符打开媒体文件
                context.contentResolver.openFileDescriptor(uri, "r")?.use {
                    val fileDescriptor = it.fileDescriptor
                    BitmapFactory.decodeFileDescriptor(fileDescriptor)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getBitmapFromUriByStream(context: Context, uri: Uri?): Bitmap? {
        uri ?: return null
        return withContext(Dispatchers.IO) {
            try {
                // 使用文件流打开媒体文件
                context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getThumbnailFromUri(context: Context, uri: Uri?, with: Int, height: Int): Bitmap? {
        uri ?: return null
        return try {
            context.contentResolver.loadThumbnail(uri, Size(with, height), null)
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

    fun getUriByFile(context: Context, authority: String, file: File): Uri =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // android7.0 需要通过FileProvider来获取文件uri。并开始强制启用StrictMode“严苛模式”，这个策略禁止在app外暴露 “file://“URI。
            // 为了与其他应用共享文件，你应该发送"content://"URI ，并授予临时访问权限。授予这个临时访问权限的最签单方法就是使用FileProvider类。
            FileProvider.getUriForFile(context.applicationContext, authority, file)
        } else {
            Uri.fromFile(file)
        }

    fun getFilePathByUri(context: Context, uri: Uri): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getFilePathAbove19(context, uri) ?: ""
        } else {
            getFilePathBelow19(context, uri)
        }

    /**
     * API19以下获取文件路径的方法
     */
    private fun getFilePathBelow19(context: Context, uri: Uri): String {
        var path = ""
        // 这里开始的第二部分，获取图片的路径：低版本的是没问题的，但是sdk>19会获取不到
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        // 好像是android多媒体数据库的封装接口，具体的看Android文档
        context.applicationContext.contentResolver.query(uri, proj, null, null, null)?.use { cursor ->
            // 获得用户选择的图片的索引值
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            // 将光标移至开头 ，这个很重要，不小心很容易引起越界
            if (cursor.moveToFirst()) {
                // 最后根据索引值获取图片路径
                // 结果类似：/mnt/sdcard/DCIM/Camera/IMG_20151124_013332.jpg
                path = cursor.getString(columnIndex)
            }
        }
        return path
    }

    /**
     * API19以上获取文件路径的方法
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun getFilePathAbove19(context: Context, uri: Uri): String? {
        val applicationContext = context.applicationContext
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(applicationContext, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri).toLong()
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), id)
                return getDataColumn(applicationContext, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(applicationContext, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {
            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(applicationContext, uri, null, null)
        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)
        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }
}