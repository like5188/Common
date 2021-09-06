package com.like.common.util.storage.external

import android.Manifest
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import androidx.core.database.getFloatOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.like.common.util.RequestPermissionWrapper
import com.like.common.util.StartActivityForResultWrapper
import com.like.common.util.StartIntentSenderForResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.sql.Date
import java.util.concurrent.TimeUnit

// 分区存储改变了应用在设备的外部存储设备中存储和访问文件的方式。
/**
 * 外部存储公共目录 操作媒体文件（图片、音频、视频）的工具类（只针对使用 MediaStore API 的访问方式）。
 * 外部存储公共目录：应用卸载后，文件不会删除。
 * /storage/emulated/(0/1/...)/(MediaStore.Images/MediaStore.Video/MediaStore.Audio)
 *
 * 权限：
 * 1、Android10以下：如果访问方式为：Environment.getExternalStorageDirectory()，那么需要申请存储权限：<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28" />
 * 2、如果访问方式为：MediaStore API 访问其它应用或者自己的旧版本应用的“媒体文件”时需要申请 READ_EXTERNAL_STORAGE 权限。
 *          当以 Android 10 或更高版本为目标平台的应用启用了分区存储时，系统会将每个媒体文件归因于一个应用，这决定了应用在未请求任何存储权限时可以访问的文件。每个文件只能归因于一个应用。因此，如果您的应用创建的媒体文件存储在照片、视频或音频文件媒体集合中，应用便可以访问该文件。
 *          但是，如果用户卸载并重新安装您的应用，您必须请求 READ_EXTERNAL_STORAGE 才能访问应用最初创建的文件。此权限请求是必需的，因为系统认为文件归因于以前安装的应用版本，而不是新安装的版本。
 *          MediaStore数据库增加owner_package_name字段记录文件属于哪个应用， 应用卸载后owner_package_name字段会置空，也就是说，卸载重装后，之前创建的文件，已不属于应用创建的了，需要相关存储权限才能再次读写
 *      WRITE_EXTERNAL_STORAGE 权限在 android11 里面已被废弃。
 *      所有文件访问权限：像文件管理操作或备份和还原操作等需要访问大量的文件，通过执行以下操作，这些应用可以获得” 所有文件访问权限”：
 *      声明 MANAGE_EXTERNAL_STORAGE 权限
 *      将用户引导至系统设置页面，在该页面上，用户可以对应用启用授予所有文件的管理权限选项
 *
 * 访问方式：MediaStore API（Android10以下也可以使用：Environment.getExternalStorageDirectory()）
 *      Android11：如果应用具有 READ_EXTERNAL_STORAGE 权限，则可以使用文件直接路径去访问媒体，但是应用的性能会略有下降，还是推荐使用 MediaStore API。
 * 注意：如果您不希望媒体扫描程序发现您的文件，请在特定于应用的目录中添加名为 .nomedia 的空文件（请注意文件名中的句点前缀）。这可以防止媒体扫描程序读取您的媒体文件并通过 MediaStore API 将它们提供给其他应用。
 *
 * 按照分区存储的规范，将用户数据(例如图片、视频、音频等)保存在公共目录，把应用数据保存在私有目录
 *
 * 如果您需要与其他应用共享单个文件或应用数据，可以使用 Android 提供的以下 API：
 *      如果您需要与其他应用共享特定文件，请使用 FileProvider API。
 *      如果您需要向其他应用提供数据，可以使用内容提供器。借助内容提供器，您可以完全控制向其他应用提供的读取和写入访问权限。尽管您可以将内容提供器与任何存储媒介一起使用，但它们通常与数据库一起使用。
 *      媒体共享：按照内容提供程序创建指南中的建议使用 content:// URI。如需在搭载 Android 10 的设备上访问共享存储空间中的其他文件，建议您在应用的清单文件中将 requestLegacyExternalStorage 设置为 true 以停用分区存储。
 *
 * Android 存储用例和最佳做法：https://developer.android.google.cn/training/data-storage/use-cases
 * 访问共享存储空间中的媒体文件：https://developer.android.google.cn/training/data-storage/shared/media#toggle-pending-status
 *
 * MediaStore 是 android 系统提供的一个多媒体数据库，专门用于存放多媒体信息的，通过 ContentResolver.query() 获取 Cursor 即可对数据库进行操作。
 *
 * MediaStore.Files: 共享的文件,包括多媒体和非多媒体信息
 * MediaStore.Image: 存放图片信息
 * MediaStore.Audio: 存放音频信息
 * MediaStore.Video: 存放视频信息
 * 每个内部类中都又包含了 Media、Thumbnails、MediaColumns(ImageColumns、AudioColumns、VideoColumns)，分别提供了媒体信息，缩略信息和 操作字段。
 *
 * 执行批量操作需要的权限(在 Android 10 中，应用在对MediaStore的每一个文件请求编辑或删除时都必须一个个地得到用户的确认。而在 Android 11 中，应用可以一次请求修改或者删除多个媒体文件。)
 * createWriteRequest (ContentResolver, Collection)	用户向应用授予对指定媒体文件组的写入访问权限的请求。
 * createFavoriteRequest (ContentResolver, Collection, boolean)	用户将设备上指定的媒体文件标记为 “收藏” 的请求。对该文件具有读取访问权限的任何应用都可以看到用户已将该文件标记为 “收藏”。
 * createTrashRequest (ContentResolver, Collection, boolean)	用户将指定的媒体文件放入设备垃圾箱的请求。垃圾箱中的内容在特定时间段（默认为 7 天）后会永久删除。
 * createDeleteRequest (ContentResolver, Collection)	用户立即永久删除指定的媒体文件（而不是先将其放入垃圾箱）的请求。
 * 系统在调用以上任何一个方法后，会构建一个 PendingIntent 对象。应用调用此 intent 后，用户会看到一个对话框，请求用户同意应用更新或删除指定的媒体文件。
 */
object MediaStoreUtils {

    /**
     * 拍照
     * 照片存储位置：/storage/emulated/0/Pictures
     *
     * @param isThumbnail     表示返回值是否为缩略图
     */
    suspend fun takePhoto(
        requestPermissionWrapper: RequestPermissionWrapper,
        startActivityForResultWrapper: StartActivityForResultWrapper,
        isThumbnail: Boolean = false
    ): Bitmap? {
        return null
//        // 如果你的应用没有配置android.permission.CAMERA权限，则不会出现下面的问题。如果你的应用配置了android.permission.CAMERA权限，那么你的应用必须获得该权限的授权，否则会出错
//        if (!requestPermissionWrapper.requestPermission(Manifest.permission.CAMERA)) {
//            return null
//        }
//
//        val context = requestPermissionWrapper.activity.applicationContext
//        //android 11 无法唤起第三方相机了，只能唤起系统相机.如果要使用特定的第三方相机应用来代表其捕获图片或视频，可以通过为intent设置软件包名称或组件来使这些intent变得明确。
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        return if (isThumbnail) {
//            // 如果[MediaStore.EXTRA_OUTPUT]为 null，那么返回拍照的缩略图，可以通过下面的方法获取。
//            startActivityForResultWrapper.startActivityForResult(intent)?.getParcelableExtra("data")
//        } else {
//            val imageUri = createFile(
//                requestPermissionWrapper,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                System.currentTimeMillis().toString(),
//                Environment.DIRECTORY_PICTURES
//            ) ?: return null
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
//            // 如果[MediaStore.EXTRA_OUTPUT]不为 null，那么返回值不为 null，表示拍照成功返回，其中 imageUri 参数则是照片的 Uri。
//            startActivityForResultWrapper.startActivityForResult(intent)
//            UriUtils.getBitmapFromUriByFileDescriptor(context, imageUri)
//        }
    }

    /**
     * 获取文件：
     * 如果启用了分区存储，只能获取自己应用创建的文件。如果拥有 READ_EXTERNAL_STORAGE 权限，才会返回所有应用的文件。
     * 如果分区存储不可用或未使用，获得所有文件。
     *
     * 如果要显示特定文件夹中的文件，请求 READ_EXTERNAL_STORAGE 权限，根据 MediaColumns.DATA 的值检索媒体文件，该值包含磁盘上的媒体项的绝对文件系统路径。
     */
    suspend fun getFiles(
        context: Context,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = null
    ): List<FileEntity> {
        return getEntities(context, FileEntity.getContentUri(), FileEntity.getProjections(), selection, selectionArgs, sortOrder)
    }

    /**
     * 获取图片文件：
     * 如果启用了分区存储，只能获取自己应用创建的文件。如果拥有 READ_EXTERNAL_STORAGE 权限，才会返回所有应用的文件。
     * 如果分区存储不可用或未使用，获得所有文件。
     * 如果开启了分区存储，要想获取位置信息，请单独使用 [UriUtils.getLatLongFromUri()] 方法。
     *
     * （包括照片和屏幕截图），存储在 DCIM/ 和 Pictures/ 目录中。系统将这些文件添加到 MediaStore.Images 表格中。
     *
     * 您需要在应用的清单中声明 ACCESS_MEDIA_LOCATION 权限，然后在运行时请求此权限，应用才能从照片中检索未编辑的 Exif 元数据。
     * 用户在 Settings UI 里看不到这个权限，但是它属于运行时权限，所以必须要在 Manifest 里声明该权限，并在运行时同时请求该权限和读取外部存储权限
     * 一些照片在其 Exif 元数据中包含位置信息，以便用户查看照片的拍摄地点。但是，由于此位置信息属于敏感信息，如果应用使用了分区存储，默认情况下 Android 10 会对应用隐藏此信息。
     */
    suspend fun getImages(
        context: Context,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = null
    ): List<ImageEntity> {
        return getEntities(context, ImageEntity.getContentUri(), ImageEntity.getProjections(), selection, selectionArgs, sortOrder)
    }

    /**
     * 获取音频文件：
     * 如果启用了分区存储，只能获取自己应用创建的文件。如果拥有 READ_EXTERNAL_STORAGE 权限，才会返回所有应用的文件。
     * 如果分区存储不可用或未使用，获得所有文件。
     *
     * 存储在 Alarms/、Audiobooks/、Music/、Notifications/、Podcasts/ 和 Ringtones/ 目录中，以及位于 Music/ 或 Movies/ 目录中的音频播放列表中。系统将这些文件添加到 MediaStore.Audio 表格中。
     */
    suspend fun getAudios(
        context: Context,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = null
    ): List<AudioEntity> {
        return getEntities(context, AudioEntity.getContentUri(), AudioEntity.getProjections(), selection, selectionArgs, sortOrder)
    }

    /**
     * 获取视频文件：
     * 如果启用了分区存储，只能获取自己应用创建的文件。如果拥有 READ_EXTERNAL_STORAGE 权限，才会返回所有应用的文件。
     * 如果分区存储不可用或未使用，获得所有文件。
     * 如果开启了分区存储，要想获取位置信息，请单独使用 [UriUtils.getLatLongFromUri()] 方法。
     *
     * 存储在 Movies/ 目录中。系统将这些文件添加到 MediaStore.Video 表格中。
     */
    suspend fun getVideos(
        context: Context,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = null
    ): List<VideoEntity> {
        return getEntities(context, VideoEntity.getContentUri(), VideoEntity.getProjections(), selection, selectionArgs, sortOrder)
    }

    /**
     * @param projection        需要返回的列。比如：arrayOf(MediaStore.Video.Media._ID,MediaStore.Video.Media.DISPLAY_NAME)
     * @param selection         查询条件。比如："${MediaStore.Video.Media.DURATION} >= ?"
     * @param selectionArgs     查询条件填充值。比如：arrayOf(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES).toString())
     * @param sortOrder         排序依据。比如："${MediaStore.Video.Media.DISPLAY_NAME} ASC"
     */
    private suspend inline fun <reified T : BaseEntity> getEntities(
        context: Context,
        uri: Uri,
        projection: Array<String>,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): List<T> {
        val files = mutableListOf<T>()
        withContext(Dispatchers.IO) {
            context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
                ?.use { cursor ->
                    while (cursor.moveToNext()) {
                        files += when (T::class.java) {
                            FileEntity::class.java -> FileEntity(cursor)
                            ImageEntity::class.java -> ImageEntity(cursor)
                            AudioEntity::class.java -> AudioEntity(cursor)
                            VideoEntity::class.java -> VideoEntity(cursor)
                            else -> throw RuntimeException("get entities error")
                        } as T
                    }
                }
        }
        return files
    }

    /**
     * 创建媒体文件
     *
     * @param uri   External、可移动存储 类型的 URI
     * 如果为 internal 类型，那么会报错：Writing exception to parcel java.lang.UnsupportedOperationException: Writing to internal storage is not supported.
     *
     * content://media/<volumeName>/<Uri路径> 其中 volumeName 可以是：
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

     * @param displayName   文件名称。
     * 必须输入正确的后缀。
     * 因为如果是 android10 以下或者 Android11，则必须要有后缀。
     * android10 版本，最好不加后缀，因为有些后缀不能被识别，比如".png"，创建后的文件名会自动变为".png.jpg"，当然，如果是".jpg"，那么就不会再自动加后缀了。
     *
     * @param relativePath  相对路径。格式：root/xxx。注意：根目录 root 必须是以下这些：
     * MediaStore.Audio：[Alarms, Audiobooks, Music, Notifications, Podcasts, Ringtones]
     * MediaStore.Video：[DCIM, Movies, Pictures]
     * MediaStore.Images：[DCIM, Pictures]
     *
     * @param onWrite       写入数据的操作
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun createFile(
        requestPermissionWrapper: RequestPermissionWrapper,
        uri: Uri,
        displayName: String,
        relativePath: String,
        onWrite: ((FileOutputStream?) -> Unit)? = null
    ): Uri? {
        if (displayName.isEmpty()) {
            return null
        }
        if (relativePath.isEmpty()) {
            return null
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            !requestPermissionWrapper.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {
            return null
        }

        val resolver = requestPermissionWrapper.activity.applicationContext.contentResolver
        return withContext(Dispatchers.IO) {
            try {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // >= android10，那么此路径不存在也会自动创建
                        put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
                        if (onWrite != null) {
                            // 如果您的应用执行可能非常耗时的操作（例如写入媒体文件），那么在处理文件时对其进行独占访问非常有用。
                            // 在搭载 Android 10 或更高版本的设备上，您的应用可以通过将 IS_PENDING 标记的值设为 1 来获取此独占访问权限。
                            // 如此一来，只有您的应用可以查看该文件，直到您的应用将 IS_PENDING 的值改回 0。
                            put(MediaStore.MediaColumns.IS_PENDING, 1)
                        }
                    } else {
                        val dir = "${Environment.getExternalStorageDirectory().path}/$relativePath"
                        val file = File(dir)
                        if (!file.exists()) {
                            file.mkdirs()
                        }
                        put(MediaStore.MediaColumns.DATA, "$dir/$displayName")
                    }
                }

                resolver.insert(uri, values)?.also {
                    if (onWrite != null) {
                        resolver.openFileDescriptor(it, "w", null).use { pfd ->
                            // Write data into the pending file.
                            FileOutputStream(pfd?.fileDescriptor).use { fos ->
                                onWrite(fos)
                            }
                        }
                        // Now that we're finished, release the "pending" status, and allow other apps
                        // to use.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            values.clear()
                            values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                            resolver.update(it, values, null, null)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * 更新文件。
     *
     * todo android 10 失败 java.lang.IllegalStateException: android.system.ErrnoException: rename failed: ENOENT (No such file or directory)
     * todo android 11 失败 java.nio.file.NoSuchFileException: /storage/emulated/0/Pictures/like1/22.png.jpg
     * @param relativePath  相对路径，用于移动文件。
     */
    suspend fun updateFile(
        requestPermissionWrapper: RequestPermissionWrapper,
        uri: Uri,
        displayName: String,
        relativePath: String = "",
        selection: String? = null,
        selectionArgs: Array<String>? = null
    ): Boolean {
        if (displayName.isEmpty()) {
            return false
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            !requestPermissionWrapper.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {
            return false
        }
        return withContext(Dispatchers.IO) {
            try {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // >= android10，那么此路径不存在也会自动创建
                        put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
                    } else {
                        val dir = "${Environment.getExternalStorageDirectory().path}/$relativePath"
                        val file = File(dir)
                        if (!file.exists()) {
                            file.mkdirs()
                        }
                        put(MediaStore.MediaColumns.DATA, "$dir/$displayName")
                    }
                }
                requestPermissionWrapper.activity.applicationContext.contentResolver.update(uri, values, selection, selectionArgs) > 0
            } catch (securityException: SecurityException) {
                // 如果您的应用使用分区存储，它通常无法更新其他应用存放到媒体库中的媒体文件。
                // 不过，您仍可通过捕获平台抛出的 RecoverableSecurityException 来征得用户同意修改文件。然后，您可以请求用户授予您的应用对此特定内容的写入权限。
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    !Environment.isExternalStorageLegacy()// 开启了分区存储
                ) {
                    (securityException as? RecoverableSecurityException)?.userAction?.actionIntent?.intentSender?.let {
                        requestPermissionWrapper.activity.startIntentSenderForResult(it, 0, null, 0, 0, 0, null)
                    }
                }
                false
            }
        }
    }

    /**
     * 删除文件
     *
     * 如果启用了分区存储，您就需要为应用要移除的每个文件捕获 RecoverableSecurityException
     */
    suspend fun deleteFile(
        requestPermissionWrapper: RequestPermissionWrapper,
        uri: Uri
    ): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            !requestPermissionWrapper.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {
            return false
        }
        return withContext(Dispatchers.IO) {
            try {
                requestPermissionWrapper.activity.applicationContext.contentResolver.delete(uri, null, null) > 0
            } catch (securityException: SecurityException) {
                // 如果您的应用使用分区存储，它通常无法更新其他应用存放到媒体库中的媒体文件。
                // 不过，您仍可通过捕获平台抛出的 RecoverableSecurityException 来征得用户同意修改文件。然后，您可以请求用户授予您的应用对此特定内容的写入权限。
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    !Environment.isExternalStorageLegacy()// 开启了分区存储
                ) {
                    (securityException as? RecoverableSecurityException)?.userAction?.actionIntent?.intentSender?.let {
                        requestPermissionWrapper.activity.startIntentSenderForResult(it, 0, null, 0, 0, 0, null)
                    }
                }
                false
            }
        }
    }

    /**
     * 用户向应用授予对指定媒体文件组的写入访问权限的请求。
     *
     * 系统在调用此方法后，会构建一个 PendingIntent 对象。应用调用此 intent 后，用户会看到一个对话框，请求用户同意应用更新指定的媒体文件。
     */
    @RequiresApi(Build.VERSION_CODES.R)
    suspend fun createWriteRequest(
        startIntentSenderForResultWrapper: StartIntentSenderForResultWrapper,
        uris: List<Uri>
    ): Boolean {
        val pendingIntent =
            MediaStore.createWriteRequest(startIntentSenderForResultWrapper.activity.applicationContext.contentResolver, uris)
        val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent).build()
        // Launch a system prompt requesting user permission for the operation.
        return startIntentSenderForResultWrapper.startIntentSenderForResult(intentSenderRequest)
    }

    /**
     * 用户立即永久删除指定的媒体文件（而不是先将其放入垃圾箱）的请求。
     */
    @RequiresApi(Build.VERSION_CODES.R)
    suspend fun createDeleteRequest(
        startIntentSenderForResultWrapper: StartIntentSenderForResultWrapper,
        uris: List<Uri>
    ): Boolean {
        val pendingIntent =
            MediaStore.createDeleteRequest(startIntentSenderForResultWrapper.activity.applicationContext.contentResolver, uris)
        val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent).build()
        // Launch a system prompt requesting user permission for the operation.
        return startIntentSenderForResultWrapper.startIntentSenderForResult(intentSenderRequest)
    }

    /**
     * 用户将指定的媒体文件放入设备垃圾箱的请求。垃圾箱中的内容会在系统定义的时间段后被永久删除。
     *
     * @param isTrashed     注意：如果您的应用是设备 OEM 的预安装图库应用，您可以将文件放入垃圾箱而不显示对话框。如需执行该操作，请直接将 IS_TRASHED 设置为 1。及把参数设置为 true
     */
    @RequiresApi(Build.VERSION_CODES.R)
    suspend fun createTrashRequest(
        startIntentSenderForResultWrapper: StartIntentSenderForResultWrapper,
        uris: List<Uri>,
        isTrashed: Boolean
    ): Boolean {
        val pendingIntent = MediaStore.createTrashRequest(
            startIntentSenderForResultWrapper.activity.applicationContext.contentResolver,
            uris,
            isTrashed
        )
        val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent).build()
        // Launch a system prompt requesting user permission for the operation.
        return startIntentSenderForResultWrapper.startIntentSenderForResult(intentSenderRequest)
    }

    /**
     * 用户将设备上指定的媒体文件标记为“收藏”的请求。对该文件具有读取访问权限的任何应用都可以看到用户已将该文件标记为“收藏”。
     */
    @RequiresApi(Build.VERSION_CODES.R)
    suspend fun createFavoriteRequest(
        startIntentSenderForResultWrapper: StartIntentSenderForResultWrapper,
        uris: List<Uri>,
        isFavorite: Boolean
    ): Boolean {
        val pendingIntent = MediaStore.createFavoriteRequest(
            startIntentSenderForResultWrapper.activity.applicationContext.contentResolver,
            uris,
            isFavorite
        )
        val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent).build()
        // Launch a system prompt requesting user permission for the operation.
        return startIntentSenderForResultWrapper.startIntentSenderForResult(intentSenderRequest)
    }

    open class BaseEntity(cursor: Cursor) {
        companion object {
            val projection = arrayOf(
                BaseColumns._ID
            )

            fun getProjections(): Array<String> {
                return projection
            }
        }

        var id: Long? = null
        var uri: Uri? = null

        init {
            with(cursor) {
                id = getLongOrNull(getColumnIndexOrThrow(projection[0]))
                uri = ContentUris.withAppendedId(
                    when (this@BaseEntity) {
                        is FileEntity -> FileEntity.getContentUri()
                        is ImageEntity -> ImageEntity.getContentUri()
                        is AudioEntity -> AudioEntity.getContentUri()
                        is VideoEntity -> VideoEntity.getContentUri()
                        else -> throw RuntimeException("get uri error")
                    },
                    id ?: -1L
                )
            }
        }

        override fun toString(): String {
            return "id=$id, uri=$uri"
        }

    }

    open class MediaEntity(cursor: Cursor) : BaseEntity(cursor) {
        companion object {
            val projection = arrayOf(
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.TITLE,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
            )

            @RequiresApi(Build.VERSION_CODES.Q)
            val projectionQ = arrayOf(
                MediaStore.MediaColumns.ORIENTATION,
                MediaStore.MediaColumns.DURATION,
            )

            @RequiresApi(Build.VERSION_CODES.R)
            val projectionR = arrayOf(
                MediaStore.MediaColumns.ARTIST,
                MediaStore.MediaColumns.ALBUM
            )

            fun getProjections(): Array<String> {
                var projections = BaseEntity.getProjections() + projection
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    projections += projectionQ
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    projections += projectionR
                }
                return projections
            }

        }

        var size: Int? = null
        var displayName: String? = null
        var title: String? = null
        var mimeType: String? = null
        var dateAdded: Date? = null
        var width: Int? = null
        var height: Int? = null
        var orientation: Int? = null
        var duration: Int? = null
        var artist: String? = null
        var album: String? = null

        init {
            with(cursor) {
                size = getIntOrNull(getColumnIndexOrThrow(projection[0]))
                displayName = getStringOrNull(getColumnIndexOrThrow(projection[1]))
                title = getStringOrNull(getColumnIndexOrThrow(projection[2]))
                mimeType = getStringOrNull(getColumnIndexOrThrow(projection[3]))
                dateAdded = Date(TimeUnit.SECONDS.toMillis(getLong(getColumnIndexOrThrow(projection[4]))))
                width = getIntOrNull(getColumnIndexOrThrow(projection[5]))
                height = getIntOrNull(getColumnIndexOrThrow(projection[6]))

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    orientation = getIntOrNull(getColumnIndexOrThrow(projectionQ[0]))
                    duration = getIntOrNull(getColumnIndexOrThrow(projectionQ[1]))
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    artist = getStringOrNull(getColumnIndexOrThrow(projectionR[0]))
                    album = getStringOrNull(getColumnIndexOrThrow(projectionR[1]))
                }
            }
        }

        override fun toString(): String {
            return "${super.toString()}, size=$size, displayName=$displayName, title=$title, mimeType=$mimeType, dateAdded=$dateAdded, width=$width, height=$height, orientation=$orientation, duration=$duration, artist=$artist, album=$album"
        }

    }

    class FileEntity(cursor: Cursor) : MediaEntity(cursor) {
        companion object {
            val projection = arrayOf(
                MediaStore.Files.FileColumns.MEDIA_TYPE
            )

            fun getProjections(): Array<String> {
                return MediaEntity.getProjections() + projection
            }

            fun getContentUri(): Uri = MediaStore.Files.getContentUri("external")
        }

        /**
        int MEDIA_TYPE_NONE = 0;
        int MEDIA_TYPE_IMAGE = 1;
        int MEDIA_TYPE_AUDIO = 2;
        int MEDIA_TYPE_VIDEO = 3;
        int MEDIA_TYPE_PLAYLIST = 4;
        int MEDIA_TYPE_SUBTITLE = 5;
        int MEDIA_TYPE_DOCUMENT = 6;
         */
        var mediaType: Int? = null

        init {
            with(cursor) {
                mediaType = getIntOrNull(getColumnIndex(projection[0]))
            }
        }

        private fun getMediaTypeString(): String = when (mediaType) {
            1 -> "image"
            2 -> "audio"
            3 -> "video"
            4 -> "playlist"
            5 -> "subtitle"
            6 -> "document"
            else -> "none"
        }

        override fun toString(): String {
            return "FileEntity(${super.toString()}, mediaType=${getMediaTypeString()})"
        }

    }

    class ImageEntity(cursor: Cursor) : MediaEntity(cursor) {
        companion object {
            val projection = arrayOf(
                MediaStore.Images.ImageColumns.DESCRIPTION,
                MediaStore.Images.ImageColumns.LATITUDE,
                MediaStore.Images.ImageColumns.LONGITUDE,
            )

            fun getProjections(): Array<String> {
                return MediaEntity.getProjections() + projection
            }

            fun getContentUri(): Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        var description: String? = null

        // 如果开启了分区存储，获取位置信息请使用 [UriUtils.getLatLongFromImageUri()] 方法。
        var latitude: Float? = null
        var longitude: Float? = null

        init {
            with(cursor) {
                description = getStringOrNull(getColumnIndexOrThrow(projection[0]))

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                    Environment.isExternalStorageLegacy()// 如果没有开启分区存储
                ) {
                    latitude = getFloatOrNull(getColumnIndexOrThrow(projection[1]))
                    longitude = getFloatOrNull(getColumnIndexOrThrow(projection[2]))
                }
            }
        }

        override fun toString(): String {
            return "ImageEntity(${super.toString()}, description=$description, latitude=$latitude, longitude=$longitude)"
        }

    }

    class AudioEntity(cursor: Cursor) : MediaEntity(cursor) {
        companion object {
            fun getProjections(): Array<String> {
                return MediaEntity.getProjections()
            }

            fun getContentUri(): Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        override fun toString(): String {
            return "AudioEntity(${super.toString()})"
        }
    }

    class VideoEntity(cursor: Cursor) : MediaEntity(cursor) {
        companion object {
            val projection = arrayOf(
                MediaStore.Video.VideoColumns.DESCRIPTION,
                MediaStore.Video.VideoColumns.LATITUDE,
                MediaStore.Video.VideoColumns.LONGITUDE,
            )

            fun getProjections(): Array<String> {
                return MediaEntity.getProjections() + projection
            }

            fun getContentUri(): Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        var description: String? = null

        // 如果开启了分区存储，获取位置信息请使用 [UriUtils.getLatLongFromImageUri()] 方法。
        var latitude: Float? = null
        var longitude: Float? = null

        init {
            with(cursor) {
                description = getStringOrNull(getColumnIndexOrThrow(projection[0]))

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                    Environment.isExternalStorageLegacy()// 如果没有开启分区存储
                ) {
                    latitude = getFloatOrNull(getColumnIndexOrThrow(ImageEntity.projection[1]))
                    longitude = getFloatOrNull(getColumnIndexOrThrow(ImageEntity.projection[2]))
                }
            }
        }

        override fun toString(): String {
            return "VideoEntity(${super.toString()}, description=$description, latitude=$latitude, longitude=$longitude)"
        }

    }

}
