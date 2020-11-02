package com.like.common.util

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Date
import java.util.concurrent.TimeUnit

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
    open class BaseEntity {
        var id: Long? = null

        companion object {
            val projection = arrayOf(
                    BaseColumns._ID
            )
        }
    }

    open class MediaEntity : BaseEntity() {
        var size: Int? = null
        var displayName: String? = null
        var title: String? = null
        var mimeType: String? = null
        var width: Int? = null
        var height: Int? = null
        var duration: Int? = null
        var orientation: Int? = null
        var dateAdded: Date? = null

        companion object {
            @RequiresApi(Build.VERSION_CODES.Q)
            val projection = arrayOf(
                    MediaStore.MediaColumns.SIZE,
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    MediaStore.MediaColumns.TITLE,
                    MediaStore.MediaColumns.MIME_TYPE,
                    MediaStore.MediaColumns.WIDTH,
                    MediaStore.MediaColumns.HEIGHT,
                    MediaStore.MediaColumns.DURATION,
                    MediaStore.MediaColumns.ORIENTATION,
                    MediaStore.MediaColumns.DATE_ADDED
            )
        }
    }

    class FileEntity : MediaEntity() {
        var mediaType: Int? = null
        var uri: Uri? = null

        companion object {
            val projection = arrayOf(
                    MediaStore.Files.FileColumns.MEDIA_TYPE
            )
        }
    }

    class ImageEntity : MediaEntity() {
        var description: String? = null
        var uri: Uri? = null

        companion object {
            val projection = arrayOf(
                    MediaStore.Images.ImageColumns.DESCRIPTION
            )
        }
    }

    class AudioEntity : MediaEntity() {
        var artist: String? = null
        var album: String? = null
        var uri: Uri? = null

        companion object {
            val projection = arrayOf(
                    MediaStore.Audio.AudioColumns.ARTIST,
                    MediaStore.Audio.AudioColumns.ALBUM
            )
        }
    }

    class VideoEntity : MediaEntity() {
        var artist: String? = null
        var album: String? = null
        var description: String? = null
        var uri: Uri? = null

        companion object {
            val projection = arrayOf(
                    MediaStore.Video.VideoColumns.ARTIST,
                    MediaStore.Video.VideoColumns.ALBUM,
                    MediaStore.Video.VideoColumns.DESCRIPTION
            )
        }
    }

    /**
     * @param selection         查询条件
     * @param selectionArgs     查询条件填充值
     * @param sortOrder         排序依据
     */
    suspend fun getFiles(context: Context,
                         selection: String? = null,
                         selectionArgs: Array<String>? = null,
                         sortOrder: String? = null
    ): List<FileEntity> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return emptyList()
        }
        val files = mutableListOf<FileEntity>()
        withContext(Dispatchers.IO) {
            val projection = BaseEntity.projection + MediaEntity.projection + FileEntity.projection
            val contentUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
            context.contentResolver.query(contentUri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(BaseColumns._ID)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)
                val orientationColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.ORIENTATION)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)
                val mediaTypeColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)
                while (cursor.moveToNext()) {
                    FileEntity().apply {
                        id = cursor.getLongOrNull(idColumn)
                        size = cursor.getIntOrNull(sizeColumn)
                        displayName = cursor.getStringOrNull(displayNameColumn)
                        title = cursor.getStringOrNull(titleColumn)
                        mimeType = cursor.getStringOrNull(mimeTypeColumn)
                        width = cursor.getIntOrNull(widthColumn)
                        height = cursor.getIntOrNull(heightColumn)
                        duration = cursor.getIntOrNull(durationColumn)
                        orientation = cursor.getIntOrNull(orientationColumn)
                        dateAdded = Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateAddedColumn)))
                        mediaType = cursor.getIntOrNull(mediaTypeColumn)
                        uri = ContentUris.withAppendedId(contentUri, id ?: -1L)
                        files += this
                    }
                }
            }
        }
        return files
    }

    /**
     * @param selection         查询条件
     * @param selectionArgs     查询条件填充值
     * @param sortOrder         排序依据
     */
    suspend fun getImages(context: Context,
                          selection: String? = null,
                          selectionArgs: Array<String>? = null,
                          sortOrder: String? = null
    ): List<ImageEntity> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return emptyList()
        }
        val files = mutableListOf<ImageEntity>()
        withContext(Dispatchers.IO) {
            val projection = BaseEntity.projection + MediaEntity.projection + ImageEntity.projection
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            context.contentResolver.query(contentUri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(BaseColumns._ID)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)
                val orientationColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.ORIENTATION)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)
                val descriptionColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DESCRIPTION)
                while (cursor.moveToNext()) {
                    ImageEntity().apply {
                        id = cursor.getLongOrNull(idColumn)
                        size = cursor.getIntOrNull(sizeColumn)
                        displayName = cursor.getStringOrNull(displayNameColumn)
                        title = cursor.getStringOrNull(titleColumn)
                        mimeType = cursor.getStringOrNull(mimeTypeColumn)
                        width = cursor.getIntOrNull(widthColumn)
                        height = cursor.getIntOrNull(heightColumn)
                        duration = cursor.getIntOrNull(durationColumn)
                        orientation = cursor.getIntOrNull(orientationColumn)
                        dateAdded = Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateAddedColumn)))
                        description = cursor.getStringOrNull(descriptionColumn)
                        uri = ContentUris.withAppendedId(contentUri, id ?: -1L)
                        files += this
                    }
                }
            }
        }
        return files
    }

    /**
     * @param selection         查询条件
     * @param selectionArgs     查询条件填充值
     * @param sortOrder         排序依据
     */
    suspend fun getAudios(context: Context,
                          selection: String? = null,
                          selectionArgs: Array<String>? = null,
                          sortOrder: String? = null
    ): List<AudioEntity> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return emptyList()
        }
        val files = mutableListOf<AudioEntity>()
        withContext(Dispatchers.IO) {
            val projection = BaseEntity.projection + MediaEntity.projection + AudioEntity.projection
            val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            context.contentResolver.query(contentUri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(BaseColumns._ID)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)
                val orientationColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.ORIENTATION)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
                while (cursor.moveToNext()) {
                    AudioEntity().apply {
                        id = cursor.getLongOrNull(idColumn)
                        size = cursor.getIntOrNull(sizeColumn)
                        displayName = cursor.getStringOrNull(displayNameColumn)
                        title = cursor.getStringOrNull(titleColumn)
                        mimeType = cursor.getStringOrNull(mimeTypeColumn)
                        width = cursor.getIntOrNull(widthColumn)
                        height = cursor.getIntOrNull(heightColumn)
                        duration = cursor.getIntOrNull(durationColumn)
                        orientation = cursor.getIntOrNull(orientationColumn)
                        dateAdded = Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateAddedColumn)))
                        artist = cursor.getStringOrNull(artistColumn)
                        album = cursor.getStringOrNull(albumColumn)
                        uri = ContentUris.withAppendedId(contentUri, id ?: -1L)
                        files.add(this)
                    }
                }
            }
        }
        return files
    }

    /**
     * @param selection         查询条件
     * @param selectionArgs     查询条件填充值
     * @param sortOrder         排序依据
     */
    suspend fun getVideos(context: Context,
                          selection: String? = null,
                          selectionArgs: Array<String>? = null,
                          sortOrder: String? = null
    ): List<VideoEntity> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return emptyList()
        }
        val files = mutableListOf<VideoEntity>()
        withContext(Dispatchers.IO) {
            val projection = BaseEntity.projection + MediaEntity.projection + VideoEntity.projection
            val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            context.contentResolver.query(contentUri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(BaseColumns._ID)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)
                val orientationColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.ORIENTATION)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.ALBUM)
                val descriptionColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DESCRIPTION)
                while (cursor.moveToNext()) {
                    VideoEntity().apply {
                        id = cursor.getLongOrNull(idColumn)
                        size = cursor.getIntOrNull(sizeColumn)
                        displayName = cursor.getStringOrNull(displayNameColumn)
                        title = cursor.getStringOrNull(titleColumn)
                        mimeType = cursor.getStringOrNull(mimeTypeColumn)
                        width = cursor.getIntOrNull(widthColumn)
                        height = cursor.getIntOrNull(heightColumn)
                        duration = cursor.getIntOrNull(durationColumn)
                        orientation = cursor.getIntOrNull(orientationColumn)
                        dateAdded = Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateAddedColumn)))
                        artist = cursor.getStringOrNull(artistColumn)
                        album = cursor.getStringOrNull(albumColumn)
                        description = cursor.getStringOrNull(descriptionColumn)
                        uri = ContentUris.withAppendedId(contentUri, id ?: -1L)
                        files.add(this)
                    }
                }
            }
        }
        return files
    }

    /**
     * 创建文件
     */
    suspend fun createFile(context: Context, uri: Uri?, values: ContentValues): Uri? {
        uri ?: return null
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.insert(uri, values)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * 删除文件
     */
    suspend fun deleteFile(context: Context, uri: Uri?): Boolean {
        uri ?: return false
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.delete(uri, null, null) > 0
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}