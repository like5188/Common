package com.like.common.util

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.database.getFloatOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull

/**
 * MediaStore 是 android 系统提供的一个多媒体数据库，专门用于存放多媒体信息的，通过 ContentResolver.query() 获取 Cursor 即可对数据库进行操作。
 *
 * MediaStore.Files: 共享的文件,包括多媒体和非多媒体信息
 * MediaStore.Image: 存放图片信息
 * MediaStore.Audio: 存放音频信息
 * MediaStore.Video: 存放视频信息
 * 每个内部类中都又包含了 Media、Thumbnails、MediaColumns(ImageColumns、AudioColumns、VideoColumns)，分别提供了媒体信息，缩略信息和 操作字段。
 */
@RequiresApi(Build.VERSION_CODES.Q)
object MediaStoreUtils {

    class FileEntity {
        var size: Int? = null
        var displayName: String? = null
        var title: String? = null
        var mimeType: String? = null
        var width: Int? = null
        var height: Int? = null
        var duration: Int? = null
        var orientation: Int? = null

        /**
         * The media type (audio, video, image or playlist)
         * of the file, or 0 for not a media file
         */
        var mediaType: Int? = null
        var artist: String? = null
        var album: String? = null
        var description: String? = null
        var latitude: Float? = null
        var longitude: Float? = null
    }

    /**
     * @param uri
     * 比如：MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)、
     * MediaStore.Images.Media.EXTERNAL_CONTENT_URI、
     * MediaStore.Audio.Media.EXTERNAL_CONTENT_URI、
     * MediaStore.Video.Media.EXTERNAL_CONTENT_URI
     * @param selection         查询条件
     * @param selectionArgs     查询条件填充值
     * @param sortOrder         排序依据
     */
    private fun getFiles(context: Context,
                         uri: Uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
                         selection: String? = null,
                         selectionArgs: Array<String>? = null,
                         sortOrder: String? = null
    ): List<FileEntity> {
        val projection = arrayOf(
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.TITLE,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
                MediaStore.MediaColumns.DURATION,
                MediaStore.MediaColumns.ORIENTATION,
                "media_type", "artist", "album", "description", "latitude", "longitude"
        )
        val files = mutableListOf<FileEntity>()
        context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
            while (cursor.moveToFirst()) {
                FileEntity().apply {
                    size = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE))
                    displayName = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                    title = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE))
                    mimeType = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
                    width = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH))
                    height = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT))
                    duration = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.DURATION))
                    orientation = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.ORIENTATION))
                    mediaType = cursor.getIntOrNull(cursor.getColumnIndex("media_type"))
                    artist = cursor.getStringOrNull(cursor.getColumnIndex("artist"))
                    album = cursor.getStringOrNull(cursor.getColumnIndex("album"))
                    description = cursor.getStringOrNull(cursor.getColumnIndex("description"))
                    latitude = cursor.getFloatOrNull(cursor.getColumnIndex("latitude"))
                    longitude = cursor.getFloatOrNull(cursor.getColumnIndex("longitude"))
                    files.add(this)
                }
            }
        }
        return files
    }
}