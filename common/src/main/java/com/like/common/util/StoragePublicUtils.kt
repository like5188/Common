package com.like.common.util

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Date
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 外部存储公共目录操作媒体文件（图片、音频、视频）、其它文件（pdf、office、doc、txt、下载的文件等）的工具类。
 *
 * 外部存储公共目录：/storage/emulated/(0/1/...)/xxx
 * 应用卸载后，文件不会删除。其他应用可以访问，但需要 READ_EXTERNAL_STORAGE 权限
 *
 * 1、媒体文件：MediaStore API
 *      api<29（Android10）：通过 Environment.getExternalStorageDirectory() 方式访问自己应用或者其它应用的文件(需要申请存储权限：<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28" />)。
 *      api>=29：
 *      1、访问自己应用新建的文件(MediaStore.Images、MediaStore.Video、MediaStore.Audio、MediaStore.Downloads)。(不需要申请存储权限)
 *          为什么这里是新建的文件？
 *              当以 Android 10 或更高版本为目标平台的应用启用了分区存储时，系统会将每个媒体文件归因于一个应用，这决定了应用在未请求任何存储权限时可以访问的文件。每个文件只能归因于一个应用。因此，如果您的应用创建的媒体文件存储在照片、视频或音频文件媒体集合中，应用便可以访问该文件。
 *              但是，如果用户卸载并重新安装您的应用，您必须请求 READ_EXTERNAL_STORAGE 才能访问应用最初创建的文件。此权限请求是必需的，因为系统认为文件归因于以前安装的应用版本，而不是新安装的版本。
 *      2、访问其他应用创建的文件(MediaStore.Images、MediaStore.Video、MediaStore.Audio需要申请 READ_EXTERNAL_STORAGE 存储权限；MediaStore.Downloads则应使用 SAF)
 * 2、其它文件：Storage Access Framework (不需要申请存储权限)
 *
 * 按照分区存储的规范，将用户数据(例如图片、视频、音频等)保存在公共目录，把应用数据保存在私有目录
 *
 * 如果您需要与其他应用共享单个文件或应用数据，可以使用 Android 提供的以下 API：
 * 如果您需要与其他应用共享特定文件，请使用 FileProvider API。
 * 如果您需要向其他应用提供数据，可以使用内容提供器。借助内容提供器，您可以完全控制向其他应用提供的读取和写入访问权限。尽管您可以将内容提供器与任何存储媒介一起使用，但它们通常与数据库一起使用。
 *
 * 媒体共享：按照内容提供程序创建指南中的建议使用 content:// URI。如需在搭载 Android 10 的设备上访问共享存储空间中的其他文件，建议您在应用的清单文件中将 requestLegacyExternalStorage 设置为 true 以停用分区存储。
 */
object StoragePublicUtils {

    /**
     * MediaStore 是 android 系统提供的一个多媒体数据库，专门用于存放多媒体信息的，通过 ContentResolver.query() 获取 Cursor 即可对数据库进行操作。
     *
     * MediaStore.Files: 共享的文件,包括多媒体和非多媒体信息
     * MediaStore.Image: 存放图片信息
     * MediaStore.Audio: 存放音频信息
     * MediaStore.Video: 存放视频信息
     * 每个内部类中都又包含了 Media、Thumbnails、MediaColumns(ImageColumns、AudioColumns、VideoColumns)，分别提供了媒体信息，缩略信息和 操作字段。
     */
    object MediaStoreHelper {

        /**
         * 如果启用了分区存储，集合只会显示您的应用创建的照片、视频和音频文件。
         * 如果分区存储不可用或未使用，集合将显示所有类型的媒体文件。
         *
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
                context.applicationContext.contentResolver.query(contentUri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
                    while (cursor.moveToNext()) {
                        files += FileEntity().apply { fill(cursor, contentUri) }
                    }
                }
            }
            return files
        }

        /**
         * （包括照片和屏幕截图），存储在 DCIM/ 和 Pictures/ 目录中。系统将这些文件添加到 MediaStore.Images 表格中。
         *
         * 您需要在应用的清单中声明 ACCESS_MEDIA_LOCATION 权限，然后在运行时请求此权限，应用才能从照片中检索未编辑的 Exif 元数据。
         * 一些照片在其 Exif 元数据中包含位置信息，以便用户查看照片的拍摄地点。但是，由于此位置信息属于敏感信息，如果应用使用了分区存储，默认情况下 Android 10 会对应用隐藏此信息。
         *
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
                context.applicationContext.contentResolver.query(contentUri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
                    while (cursor.moveToNext()) {
                        files += ImageEntity().apply { fill(cursor, contentUri) }
                    }
                }
            }
            return files
        }

        /**
         * 存储在 Alarms/、Audiobooks/、Music/、Notifications/、Podcasts/ 和 Ringtones/ 目录中，以及位于 Music/ 或 Movies/ 目录中的音频播放列表中。系统将这些文件添加到 MediaStore.Audio 表格中。
         *
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
                context.applicationContext.contentResolver.query(contentUri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
                    while (cursor.moveToNext()) {
                        files += AudioEntity().apply { fill(cursor, contentUri) }
                    }
                }
            }
            return files
        }

        /**
         * 存储在 DCIM/、Movies/ 和 Pictures/ 目录中。系统将这些文件添加到 MediaStore.Video 表格中。
         *
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
                context.applicationContext.contentResolver.query(contentUri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
                    while (cursor.moveToNext()) {
                        files += VideoEntity().apply { fill(cursor, contentUri) }
                    }
                }
            }
            return files
        }

        /**
         * 存储在 Download/ 目录中。在搭载 Android 10（API 级别 29）及更高版本的设备上，这些文件存储在 MediaStore.Downloads 表格中。此表格在 Android 9（API 级别 28）及更低版本中不可用。
         *
         * @param selection         查询条件
         * @param selectionArgs     查询条件填充值
         * @param sortOrder         排序依据
         */
        suspend fun getDownloads(context: Context,
                                 selection: String? = null,
                                 selectionArgs: Array<String>? = null,
                                 sortOrder: String? = null
        ): List<DownloadEntity> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                return emptyList()
            }
            val files = mutableListOf<DownloadEntity>()
            withContext(Dispatchers.IO) {
                val projection = BaseEntity.projection + MediaEntity.projection + DownloadEntity.projection
                val contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
                context.applicationContext.contentResolver.query(contentUri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
                    while (cursor.moveToNext()) {
                        files += DownloadEntity().apply { fill(cursor, contentUri) }
                    }
                }
            }
            return files
        }

        /**
         * 更新自己创建的文件。也可以通过更改 MediaColumns.RELATIVE_PATH 在磁盘上移动文件。
         */
        suspend fun updateFile(context: Context, uri: Uri?, fileName: String, relativePath: String = "", selection: String? = null, selectionArgs: Array<String>? = null): Boolean {
            uri ?: return false
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                return false
            }
            return withContext(Dispatchers.IO) {
                try {
                    val values = ContentValues().apply {
                        put(MediaStore.Audio.Media.RELATIVE_PATH, relativePath)
                        put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
                    }
                    context.applicationContext.contentResolver.update(uri, values, selection, selectionArgs) > 0
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
        }

        /**
         * 更新其他应用的媒体文件
         *
         * 如果您的应用使用分区存储，它通常无法更新其他应用存放到媒体库中的媒体文件。不过，您仍可通过捕获平台抛出的 RecoverableSecurityException 来征得用户同意修改文件。然后，您可以请求用户授予您的应用对此特定内容的写入权限
         */
        suspend fun updateFile(activity: Activity, uri: Uri?, onWrite: (ParcelFileDescriptor?) -> Unit) {
            uri ?: return
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                return
            }
            withContext(Dispatchers.IO) {
                try {
                    activity.applicationContext.contentResolver.openFileDescriptor(uri, "w")?.use { pfd ->
                        onWrite(pfd)
                    }
                } catch (securityException: SecurityException) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val recoverableSecurityException = securityException as? RecoverableSecurityException
                                ?: throw RuntimeException(securityException.message, securityException)

                        recoverableSecurityException.userAction.actionIntent.intentSender?.let {
                            activity.startIntentSenderForResult(it, 0, null, 0, 0, 0, null)
                        }
                    } else {
                        throw RuntimeException(securityException.message, securityException)
                    }
                }
            }
        }

        /**
         * 创建文件
         *
         * @param uri           content://media/<volumeName>/<Uri路径>
         * 其中 volumeName 可以是：
         * [android.provider.MediaStore.VOLUME_INTERNAL]
         * [android.provider.MediaStore.VOLUME_EXTERNAL]
         * [android.provider.MediaStore.VOLUME_EXTERNAL_PRIMARY]
         * [android.provider.MediaStore.getExternalVolumeNames]
         *
        ●  Audio
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
         * @param relativePath  相对路径。
         * 如果 uri 为 internal 类型，那么会报错：Writing exception to parcel java.lang.UnsupportedOperationException: Writing to internal storage is not supported.
         * 如果 uri 为 External、可移动存储 类型，那么 relativePath 格式：root/xxx。注意：根目录 root 必须是以下这些：
         * Audio：[Alarms, Music, Notifications, Podcasts, Ringtones]
         * Video：[DCIM, Movies]
         * Image：[DCIM, Pictures]
         * File：[Download, Documents]
         * Downloads：[Download]
         */
        suspend fun createFile(context: Context, uri: Uri?, fileName: String, relativePath: String = ""): Uri? {
            uri ?: return null
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                return null
            }
            return withContext(Dispatchers.IO) {
                try {
                    val values = ContentValues().apply {
                        put(MediaStore.Audio.Media.RELATIVE_PATH, relativePath)
                        put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
                    }
                    context.applicationContext.contentResolver.insert(uri, values)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

        /**
         * 创建文件并写入数据
         *
         * 如果您的应用执行可能非常耗时的操作（例如写入媒体文件），那么在处理文件时对其进行独占访问非常有用。在搭载 Android 10 或更高版本的设备上，您的应用可以通过将 IS_PENDING 标记的值设为 1 来获取此独占访问权限。如此一来，只有您的应用可以查看该文件，直到您的应用将 IS_PENDING 的值改回 0。
         */
        suspend fun createFile(context: Context, uri: Uri?, fileName: String, relativePath: String = "", onWrite: (ParcelFileDescriptor?) -> Unit): Uri? {
            uri ?: return null
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                return null
            }
            return withContext(Dispatchers.IO) {
                try {
                    // Add a media item that other apps shouldn't see until the item is
                    // fully written to the media store.
                    val resolver = context.applicationContext.contentResolver

                    val values = ContentValues().apply {
                        put(MediaStore.Audio.Media.RELATIVE_PATH, relativePath)
                        put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
                        put(MediaStore.Audio.Media.IS_PENDING, 1)
                    }

                    resolver.insert(uri, values)?.also {
                        resolver.openFileDescriptor(it, "w", null).use { pfd ->
                            // Write data into the pending audio file.
                            onWrite(pfd)
                        }
                        // Now that we're finished, release the "pending" status, and allow other apps
                        // to play the audio track.
                        values.clear()
                        values.put(MediaStore.Audio.Media.IS_PENDING, 0)
                        resolver.update(it, values, null, null)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

        /**
         * 删除文件
         *
         * 如果启用了分区存储，您就需要为应用要移除的每个文件捕获 RecoverableSecurityException
         */
        suspend fun deleteFile(activity: Activity, uri: Uri?): Boolean {
            uri ?: return false
            return withContext(Dispatchers.IO) {
                try {
                    activity.applicationContext.contentResolver.delete(uri, null, null) > 0
                } catch (securityException: SecurityException) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        (securityException as? RecoverableSecurityException)?.userAction?.actionIntent?.intentSender?.let {
                            activity.startIntentSenderForResult(it, 0, null, 0, 0, 0, null)
                        }
                    }
                    false
                }
            }
        }

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

        class DownloadEntity : MediaEntity() {
            var downloadUri: String? = null

            companion object {
                @RequiresApi(Build.VERSION_CODES.Q)
                val projection = arrayOf(
                        MediaStore.DownloadColumns.DOWNLOAD_URI
                )
            }

            override fun toString(): String {
                return "DownloadEntity(id=$id, uri=$uri, size=$size, displayName=$displayName, title=$title, mimeType=$mimeType, width=$width, height=$height, duration=$duration, orientation=$orientation, dateAdded=$dateAdded, downloadUri=$downloadUri)"
            }

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun fill(cursor: Cursor, uri: Uri) {
                super.fill(cursor, uri)
                with(cursor) {
                    this@DownloadEntity.downloadUri = getStringOrNull(getColumnIndexOrThrow(projection[0]))
                }
            }
        }
    }

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
     */
    object SAFHelper {

        /**
         * 选择单个文件
         *
         * @return  返回的 Uri 为文件的
         */
        suspend fun openDocument(activityResultCaller: ActivityResultCaller, mimeType: MimeType = MimeType._0): Uri? = suspendCoroutine { cont ->
            //通过系统的文件浏览器选择一个文件
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            //筛选，只显示可以“打开”的结果，如文件(而不是联系人或时区列表)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            //过滤只显示指定类型文件
            intent.type = mimeType.value
            activityResultCaller.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                cont.resume(it?.data?.data)
            }.launch(intent)
        }

        /**
         * 选择文件夹
         *
         * 注意：在Android 11上，无法通过SAF选择External Storage根目录、Downloads目录以及App专属目录(Android/data、Android/obb)
         *
         * @return  返回文件夹 DocumentFile
         */
        suspend fun openDocumentTree(activityResultCaller: ActivityResultCaller): DocumentFile? = suspendCoroutine { cont ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                cont.resume(null)
                return@suspendCoroutine
            }
            //通过系统的文件浏览器选择一个文件
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            activityResultCaller.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val treeUri = it?.data?.data
                val documentFile = if (treeUri == null) {
                    null
                } else {
                    DocumentFile.fromTreeUri(activityResultCaller.context, treeUri)
                }
                cont.resume(documentFile)
            }.launch(intent)
        }

        /**
         * 创建文件
         *
         * @return  返回的 Uri 为文件的
         */
        suspend fun createDocument(activityResultCaller: ActivityResultCaller, fileName: String, mimeType: MimeType = MimeType._0): Uri? = suspendCoroutine { cont ->
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            // Filter to only show results that can be "opened", such as a file (as opposed to a list of contacts or timezones).
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = mimeType.value// 文件类型
            intent.putExtra(Intent.EXTRA_TITLE, fileName)// 文件名称
            activityResultCaller.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                cont.resume(it?.data?.data)
            }.launch(intent)
        }

        /**
         * 删除文件
         */
        suspend fun deleteDocument(context: Context, uri: Uri?): Boolean {
            uri ?: return false
            return withContext(Dispatchers.IO) {
                try {
                    DocumentsContract.deleteDocument(context.applicationContext.contentResolver, uri)
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
        }
    }

    enum class MimeType(val value: String) {
        _apk("application/vnd.android.package-archive"),
        _doc("application/msword"),
        _docx("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        _xls("application/vnd.ms-excel"),
        _xlsx("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
        _exe("application/octet-stream"),
        _gtar("application/x-gtar"),
        _gz("application/x-gzip"),
        _bin("application/octet-stream"),
        _class("application/octet-stream"),
        _jar("application/java-archive"),
        _js("application/x-javascript"),
        _mpc("application/vnd.mpohun.certificate"),
        _msg("application/vnd.ms-outlook"),
        _pdf("application/pdf"),
        _pps("application/vnd.ms-powerpoint"),
        _ppt("application/vnd.ms-powerpoint"),
        _pptx("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
        _rtf("application/rtf"),
        _tar("application/x-tar"),
        _tgz("application/x-compressed"),
        _wps("application/vnd.ms-works"),
        _z("application/x-compress"),
        _zip("application/x-zip-compressed"),
        _png("image/png"),
        _jpeg("image/jpeg"),
        _jpg("image/jpeg"),
        _webp("image/webp"),
        _bmp("image/bmp"),
        _gif("image/gif"),
        _m3u("audio/x-mpegurl"),
        _m4a("audio/mp4a-latm"),
        _m4b("audio/mp4a-latm"),
        _m4p("audio/mp4a-latm"),
        _mp2("audio/x-mpeg"),
        _mp3("audio/x-mpeg"),
        _mpga("audio/mpeg"),
        _ogg("audio/ogg"),
        _rmvb("audio/x-pn-realaudio"),
        _wav("audio/x-wav"),
        _wma("audio/x-ms-wma"),
        _wmv("audio/x-ms-wmv"),
        _prop("text/plain"),
        _rc("text/plain"),
        _c("text/plain"),
        _conf("text/plain"),
        _cpp("text/plain"),
        _h("text/plain"),
        _htm("text/html"),
        _html("text/html"),
        _java("text/plain"),
        _log("text/plain"),
        _sh("text/plain"),
        _txt("text/plain"),
        _xml("text/plain"),
        _3gp("video/3gpp"),
        _asf("video/x-ms-asf"),
        _avi("video/x-msvideo"),
        _m4u("video/vnd.mpegurl"),
        _m4v("video/x-m4v"),
        _mov("video/quicktime"),
        _mp4("video/mp4"),
        _mpe("video/mpeg"),
        _mpeg("video/mpeg"),
        _mpg("video/mpeg"),
        _mpg4("video/mp4"),
        _0("*/*");

        companion object {
            fun isApk(mimeType: String?): Boolean = _apk.value == mimeType

            fun isImage(mimeType: String?): Boolean = mimeType?.startsWith("image/") == true

            fun isGif(mimeType: String?): Boolean = _gif.value == mimeType

            fun isAudio(mimeType: String?): Boolean = mimeType?.startsWith("audio/") == true

            fun isVideo(mimeType: String?): Boolean = mimeType?.startsWith("video/") == true

            fun isText(mimeType: String?): Boolean = mimeType?.startsWith("text/") == true
        }
    }
}