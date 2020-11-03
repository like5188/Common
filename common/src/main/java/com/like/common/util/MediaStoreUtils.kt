package com.like.common.util

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
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
 * content://media/<volumeName>/<Uri路径>
●Audio
■  Internal: MediaStore.Audio.Media.INTERNAL_CONTENT_URI
content://media/internal/audio/media。
■  External: MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
content://media/external/audio/media。
■  可移动存储: MediaStore.Audio.Media.getContentUri
content://media/<volumeName>/audio/media。

●  Video
■    Internal: MediaStore.Video.Media.INTERNAL_CONTENT_URI
content://media/internal/video/media。
■    External: MediaStore.Video.Media.EXTERNAL_CONTENT_URI
content://media/external/video/media。
■    可移动存储: MediaStore.Video.Media.getContentUri
content://media/<volumeName>/video/media。

●  Image
■    Internal: MediaStore.Images.Media.INTERNAL_CONTENT_URI
content://media/internal/images/media。
■    External: MediaStore.Images.Media.EXTERNAL_CONTENT_URI
content://media/external/images/media。
■    可移动存储: MediaStore.Images.Media.getContentUri
content://media/<volumeName>/images/media。

●  File
■    MediaStore. Files.Media.getContentUri
content://media/<volumeName>/file。

●  Downloads
■    Internal: MediaStore.Downloads.INTERNAL_CONTENT_URI
content://media/internal/downloads。
■    External: MediaStore.Downloads.EXTERNAL_CONTENT_URI
content://media/external/downloads。
■    可移动存储: MediaStore.Downloads.getContentUri
content://media/<volumeName>/downloads。
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
        var uri: Uri? = null

        companion object {
            val projection = arrayOf(
                    BaseColumns._ID
            )
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        open fun fill(cursor: Cursor, uri: Uri) {
            with(cursor) {
                this@BaseEntity.id = getLongOrNull(getColumnIndexOrThrow(projection[0]))
                this@BaseEntity.uri = ContentUris.withAppendedId(uri, id ?: -1L)
            }
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

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun fill(cursor: Cursor, uri: Uri) {
            super.fill(cursor, uri)
            with(cursor) {
                this@MediaEntity.size = getIntOrNull(getColumnIndexOrThrow(projection[0]))
                this@MediaEntity.displayName = getStringOrNull(getColumnIndexOrThrow(projection[1]))
                this@MediaEntity.title = getStringOrNull(getColumnIndexOrThrow(projection[2]))
                this@MediaEntity.mimeType = getStringOrNull(getColumnIndexOrThrow(projection[3]))
                this@MediaEntity.width = getIntOrNull(getColumnIndexOrThrow(projection[4]))
                this@MediaEntity.height = getIntOrNull(getColumnIndexOrThrow(projection[5]))
                this@MediaEntity.duration = getIntOrNull(getColumnIndexOrThrow(projection[6]))
                this@MediaEntity.orientation = getIntOrNull(getColumnIndexOrThrow(projection[7]))
                this@MediaEntity.dateAdded = Date(TimeUnit.SECONDS.toMillis(getLong(getColumnIndexOrThrow(projection[8]))))
            }
        }

    }

    class FileEntity : MediaEntity() {
        var mediaType: Int? = null

        companion object {
            val projection = arrayOf(
                    MediaStore.Files.FileColumns.MEDIA_TYPE
            )
        }

        override fun toString(): String {
            return "FileEntity(id=$id, uri=$uri, size=$size, displayName=$displayName, title=$title, mimeType=$mimeType, width=$width, height=$height, duration=$duration, orientation=$orientation, dateAdded=$dateAdded, mediaType=$mediaType)"
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun fill(cursor: Cursor, uri: Uri) {
            super.fill(cursor, uri)
            with(cursor) {
                this@FileEntity.mediaType = getIntOrNull(getColumnIndex(projection[0]))
            }
        }
    }

    class ImageEntity : MediaEntity() {
        var description: String? = null

        companion object {
            val projection = arrayOf(
                    MediaStore.Images.ImageColumns.DESCRIPTION
            )
        }

        override fun toString(): String {
            return "ImageEntity(id=$id, uri=$uri, size=$size, displayName=$displayName, title=$title, mimeType=$mimeType, width=$width, height=$height, duration=$duration, orientation=$orientation, dateAdded=$dateAdded, description=$description)"
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun fill(cursor: Cursor, uri: Uri) {
            super.fill(cursor, uri)
            with(cursor) {
                this@ImageEntity.description = getStringOrNull(getColumnIndexOrThrow(projection[0]))
            }
        }
    }

    class AudioEntity : MediaEntity() {
        var artist: String? = null
        var album: String? = null

        companion object {
            val projection = arrayOf(
                    MediaStore.Audio.AudioColumns.ARTIST,
                    MediaStore.Audio.AudioColumns.ALBUM
            )
        }

        override fun toString(): String {
            return "AudioEntity(id=$id, uri=$uri, size=$size, displayName=$displayName, title=$title, mimeType=$mimeType, width=$width, height=$height, duration=$duration, orientation=$orientation, dateAdded=$dateAdded, artist=$artist, album=$album)"
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun fill(cursor: Cursor, uri: Uri) {
            super.fill(cursor, uri)
            with(cursor) {
                this@AudioEntity.artist = getStringOrNull(getColumnIndexOrThrow(projection[0]))
                this@AudioEntity.album = getStringOrNull(getColumnIndexOrThrow(projection[1]))
            }
        }
    }

    class VideoEntity : MediaEntity() {
        var artist: String? = null
        var album: String? = null
        var description: String? = null

        companion object {
            val projection = arrayOf(
                    MediaStore.Video.VideoColumns.ARTIST,
                    MediaStore.Video.VideoColumns.ALBUM,
                    MediaStore.Video.VideoColumns.DESCRIPTION
            )
        }

        override fun toString(): String {
            return "VideoEntity(id=$id, uri=$uri, size=$size, displayName=$displayName, title=$title, mimeType=$mimeType, width=$width, height=$height, duration=$duration, orientation=$orientation, dateAdded=$dateAdded, artist=$artist, album=$album,  description=$description)"
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun fill(cursor: Cursor, uri: Uri) {
            super.fill(cursor, uri)
            with(cursor) {
                this@VideoEntity.artist = getStringOrNull(getColumnIndexOrThrow(projection[0]))
                this@VideoEntity.album = getStringOrNull(getColumnIndexOrThrow(projection[1]))
                this@VideoEntity.description = getStringOrNull(getColumnIndexOrThrow(projection[2]))
            }
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
                while (cursor.moveToNext()) {
                    FileEntity().apply {
                        fill(cursor, contentUri)
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
                while (cursor.moveToNext()) {
                    ImageEntity().apply {
                        fill(cursor, contentUri)
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
                while (cursor.moveToNext()) {
                    AudioEntity().apply {
                        fill(cursor, contentUri)
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
                while (cursor.moveToNext()) {
                    VideoEntity().apply {
                        fill(cursor, contentUri)
                        files += this
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