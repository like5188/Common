package com.like.common.util

import android.content.Context
import android.os.Build
import android.provider.MediaStore
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
object MediaStoreUtils {

    open class MediaEntity {
        var size: Int? = null
        var displayName: String? = null
        var title: String? = null
        var mimeType: String? = null
        var width: Int? = null
        var height: Int? = null
        var duration: Int? = null
        var orientation: Int? = null
    }

    class FileEntity : MediaEntity() {
        var mediaType: Int? = null
    }

    class ImageEntity : MediaEntity() {
        var description: String? = null
    }

    class AudioEntity : MediaEntity() {
        var artist: String? = null
        var album: String? = null
    }

    class VideoEntity : MediaEntity() {
        var artist: String? = null
        var album: String? = null
        var description: String? = null
    }

    /**
     * @param selection         查询条件
     * @param selectionArgs     查询条件填充值
     * @param sortOrder         排序依据
     */
    fun getFiles(context: Context,
                 selection: String? = null,
                 selectionArgs: Array<String>? = null,
                 sortOrder: String? = null
    ): List<FileEntity> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return emptyList()
        }
        val projection = arrayOf(
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.TITLE,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
                MediaStore.MediaColumns.DURATION,
                MediaStore.MediaColumns.ORIENTATION,
                MediaStore.Files.FileColumns.MEDIA_TYPE
        )
        val files = mutableListOf<FileEntity>()
        context.contentResolver.query(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), projection, selection, selectionArgs, sortOrder)?.use { cursor ->
            while (cursor.moveToFirst()) {
                FileEntity().apply {
                    // MediaStore.MediaColumns 中的公共字段
                    size = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE))
                    displayName = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                    title = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE))
                    mimeType = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
                    width = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH))
                    height = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT))
                    duration = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.DURATION))
                    orientation = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.ORIENTATION))
                    mediaType = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE))
                    files.add(this)
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
    fun getImages(context: Context,
                  selection: String? = null,
                  selectionArgs: Array<String>? = null,
                  sortOrder: String? = null
    ): List<ImageEntity> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return emptyList()
        }
        val projection = arrayOf(
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.TITLE,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
                MediaStore.MediaColumns.DURATION,
                MediaStore.MediaColumns.ORIENTATION,
                MediaStore.Images.ImageColumns.DESCRIPTION
        )
        val files = mutableListOf<ImageEntity>()
        context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
            while (cursor.moveToFirst()) {
                ImageEntity().apply {
                    // MediaStore.MediaColumns 中的公共字段
                    size = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE))
                    displayName = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                    title = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE))
                    mimeType = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
                    width = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH))
                    height = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT))
                    duration = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.DURATION))
                    orientation = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.ORIENTATION))
                    description = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DESCRIPTION))
                    files.add(this)
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
    fun getAudios(context: Context,
                  selection: String? = null,
                  selectionArgs: Array<String>? = null,
                  sortOrder: String? = null
    ): List<AudioEntity> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return emptyList()
        }
        val projection = arrayOf(
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.TITLE,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
                MediaStore.MediaColumns.DURATION,
                MediaStore.MediaColumns.ORIENTATION,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM
        )
        val files = mutableListOf<AudioEntity>()
        context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
            while (cursor.moveToFirst()) {
                AudioEntity().apply {
                    // MediaStore.MediaColumns 中的公共字段
                    size = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE))
                    displayName = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                    title = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE))
                    mimeType = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
                    width = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH))
                    height = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT))
                    duration = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.DURATION))
                    orientation = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.ORIENTATION))
                    artist = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST))
                    album = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM))
                    files.add(this)
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
    fun getVideos(context: Context,
                  selection: String? = null,
                  selectionArgs: Array<String>? = null,
                  sortOrder: String? = null
    ): List<VideoEntity> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return emptyList()
        }
        val projection = arrayOf(
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.TITLE,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
                MediaStore.MediaColumns.DURATION,
                MediaStore.MediaColumns.ORIENTATION,
                MediaStore.Video.VideoColumns.ARTIST,
                MediaStore.Video.VideoColumns.ALBUM,
                MediaStore.Video.VideoColumns.DESCRIPTION
        )
        val files = mutableListOf<VideoEntity>()
        context.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
            while (cursor.moveToFirst()) {
                VideoEntity().apply {
                    // MediaStore.MediaColumns 中的公共字段
                    size = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE))
                    displayName = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                    title = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE))
                    mimeType = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
                    width = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH))
                    height = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT))
                    duration = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.DURATION))
                    orientation = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.ORIENTATION))
                    artist = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Video.VideoColumns.ARTIST))
                    album = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Video.VideoColumns.ALBUM))
                    description = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DESCRIPTION))
                    files.add(this)
                }
            }
        }
        return files
    }
}