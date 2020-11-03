package com.like.common.util

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
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
 * 外部存储公共目录操作工具类。
 *
 * 外部存储公共目录：/storage/emulated/(0/1/...)/xxx
 *      api<29（Android10）：通过 Environment.getExternalStorageDirectory() 方式访问(需要申请存储权限)。通过SAF访问(不需要申请存储权限)
 *      api>=29文件需要通过MediaStore API或者Storage Access Framework方式访问。
 *      1、访问自己应用新建的文件：MediaStore API、SAF。(不需要申请存储权限)
 *      2、访问其他应用创建的文件：(需要申请存储权限)
 *          ①媒体文件(图片、音频、视频)：MediaStore API
 *          ①非媒体文件(pdf、office、doc、txt等)：SAF
 *
 * 按照分区存储的规范，将用户数据(例如图片、视频、音频等)保存在公共目录，把应用数据保存在私有目录
 */
object StoragePublicUtils {

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
    object MediaStoreHelper {

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
                        files += FileEntity().apply { fill(cursor, contentUri) }
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
                        files += ImageEntity().apply { fill(cursor, contentUri) }
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
                        files += AudioEntity().apply { fill(cursor, contentUri) }
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
                        files += VideoEntity().apply { fill(cursor, contentUri) }
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
                    DocumentsContract.deleteDocument(context.contentResolver, uri)
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