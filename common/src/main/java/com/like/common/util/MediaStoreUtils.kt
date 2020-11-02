package com.like.common.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

/**
 * MediaStore 是 android 系统提供的一个多媒体数据库，专门用于存放多媒体信息的，通过 ContentResolver.query() 获取 Cursor 即可对数据库进行操作。
 *
 * MediaStore.Files: 共享的文件,包括多媒体和非多媒体信息
 * MediaStore.Image: 存放图片信息
 * MediaStore.Audio: 存放音频信息
 * MediaStore.Video: 存放视频信息
 * 每个内部类中都又包含了 Media、Thumbnails、MediaColumns(ImageColumns、AudioColumns、VideoColumns)，分别提供了媒体信息，缩略信息和 操作字段。
 */
object MediaStoreUtils {

    private fun getUriData(context: Context, uri: Uri) {
        val projection = arrayOf("_data", "_display_name", "_size", "mime_type", "title", "duration")
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                val size = cursor.getString(cursor.getColumnIndex(OpenableColumns.SIZE))
            }
        }
    }
}