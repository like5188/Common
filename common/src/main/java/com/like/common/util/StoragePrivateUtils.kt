package com.like.common.util

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import java.io.File

/**
 * 内部存储、外部存储私有目录操作工具类。
 * 公共目录操作工具类为：[StoragePublicUtils]
 *
 * 一、内部存储：访问是不需要权限的，内部存储属于应用的私有存储区域，其它应用不可访问，卸载应用后，系统会移除这些目录中存储的文件。空间小。
 *
 * 二、外部存储：可以是外置SD卡 ，也可以是内置存储卡 的部分分区。 外部存储是可以全局访问的，分为公共目录和私有目录。
 *
 */
object StoragePrivateUtils {
    /**
     * 获取指定目录空间大小(byte)。
     *
     * @param dir
     * @return
     */
    fun getStorageSize(dir: String?): Long =
            try {
                val fs = StatFs(dir)
                val count = fs.blockCountLong// 文件系统上总共的块
                val size = fs.blockSizeLong// 文件系统 一个块的大小单位byte
                count * size
            } catch (e: Exception) {
                0L
            }

    /**
     * 获取指定目录的剩余空间大小(byte)。包括预留的一般程序无法访问的
     *
     * @param dir
     * @return
     */
    fun getStorageFreeSize(dir: String?): Long =
            try {
                val fs = StatFs(dir)
                val count = fs.freeBlocksLong// 文件系统上剩余的所有块，包括预留的一般程序无法访问的
                val size = fs.blockSizeLong
                count * size
            } catch (e: Exception) {
                0L
            }

    /**
     * 获取指定目录的可用空间大小(byte)
     *
     * @param dir
     * @return
     */
    fun getStorageAvailableSize(dir: String?): Long =
            try {
                val fs = StatFs(dir)
                val count = fs.availableBlocksLong// 文件系统上剩下的可供程序使用的块
                val size = fs.blockSizeLong
                count * size
            } catch (e: Exception) {
                0L
            }

    /**
     * 内部存储工具类
     *
     * /data/data(user/0)/packageName/xxx
     *
     * 设置项的 Clear Data 和 Clear cache 两个选项，这两个都是清空应用的缓存数据，具体区别如下：
     *     1、Clear Data清理的是外部存储中的应用私有目录下的file文件夹
     *     2、Clear Cache清理的是外部存储中的应用私有目录下的cache文件夹
     *
     * 注意:自 API 级别 17 以来，常量 MODE_WORLD_READABLE 和 MODE_WORLD_WRITEABLE 已被弃用。
     * 从 Android N (7.0) 开始，使用这些常量将会导致引发 SecurityException。
     * 这意味着，面向 Android N 和更高版本的应用无法按名称共享私有文件，尝试共享“file://”URI 将会导致引发 FileUriExposedException。
     * 如需允许其他应用访问存储在内部存储空间内此目录中的文件，请使用具有 FLAG_GRANT_READ_URI_PERMISSION 属性的 FileProvider。
     */
    object InternalStorageHelper {
        /**
         * 获取根目录
         *
         * @param context
         * @return /data/data(user/0)/packageName
         */
        private fun getBaseDir(context: Context): String =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    context.applicationContext.dataDir.absolutePath
                } else {
                    context.applicationContext.filesDir.parent ?: ""
                }

        /**
         * 获取完整空间大小。
         *
         * @param context
         * @return
         */
        fun getSize(context: Context): Long =
                getStorageSize(getBaseDir(context.applicationContext))

        /**
         * 获取剩余空间大小。包括预留的一般程序无法访问的
         *
         * @param context
         * @return
         */
        fun getFreeSize(context: Context): Long =
                getStorageFreeSize(getBaseDir(context.applicationContext))

        /**
         * 获取可用空间大小。
         *
         * @param context
         * @return
         */
        fun getAvailableSize(context: Context): Long =
                getStorageAvailableSize(getBaseDir(context.applicationContext))

        /**
         * 获取数据库所在的路径
         *
         * @param context
         * @param name    子路径或者文件名
         * @return /data/data(user/0)/packageName/databases/name
         */
        fun getDatabasePath(context: Context, name: String): File =
                context.applicationContext.getDatabasePath(name)

        /**
         * 获取Files目录。存储应用的持久性文件
         *
         * @param context
         * @return /data/data(user/0)/packageName/files
         */
        fun getFilesDir(context: Context): File =
                context.applicationContext.filesDir

        /**
         * 获取Cache目录。存储缓存文件。注意：当设备的内部存储空间不足时，Android 可能会删除这些缓存文件以回收空间。因此，请在读取前检查缓存文件是否存在。
         *
         * @param context
         * @return /data/data(user/0)/packageName/cache
         */
        fun getCacheDir(context: Context): File =
                context.applicationContext.cacheDir

        /**
         * 创建并返回一个内部存储的目录
         *
         * @param context
         * @param dirName   目录名
         * @param mode      [android.content.Context.MODE_PRIVATE]、[android.content.Context.MODE_APPEND]
         * @return /data/data(user/0)/packageName/dirName
         */
        fun getDir(context: Context, dirName: String, mode: Int = Context.MODE_PRIVATE): File =
                context.applicationContext.getDir(dirName, mode)
    }

    /**
     * 外部存储私有目录工具类
     *
     * /storage/emulated/(0/1/...)/Android/data/packageName/xxx
     *
     * 文件访问方式与之前Android版本一致，可以通过File path获取资源。卸载应用后，系统会移除这些目录中存储的文件。( Android 4.4 以后不需要申请存储权限)
     * 注意：无法保证可以访问这些目录中的文件，例如从设备中取出可移除的 SD 卡后，就无法访问其中的文件。如果应用的功能取决于这些文件，应改为将文件存储在内部存储空间中。
     * Android 10（API 级别 29）及更高版本为目标平台的应用在默认情况下被授予了对外部存储空间的分区访问权限（即分区存储）。启用分区存储后，应用将无法访问属于其他应用的私有目录。
     */
    object ExternalStorageHelper {

        fun isWritable(): Boolean =
                Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

        fun isReadable(): Boolean =
                Environment.getExternalStorageState() in setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)

        /**
         * 获取根目录
         *
         * @return /storage/emulated/(0/1/...)/Android/data/packageName
         */
        private fun getBaseDir(context: Context): String? =
                if (isWritable()) {
                    context.applicationContext.getExternalFilesDir(null)?.parent
                } else null

        /**
         * 获取完整空间大小。
         *
         * @return
         */
        fun getSize(context: Context): Long =
                if (isWritable()) {
                    getStorageSize(getBaseDir(context))
                } else 0

        /**
         * 获取剩余空间大小。包括预留的一般程序无法访问的
         *
         * @return
         */
        fun getFreeSize(context: Context): Long =
                if (isWritable()) {
                    getStorageFreeSize(getBaseDir(context))
                } else 0

        /**
         * 获取可用空间大小。
         *
         * @return
         */
        fun getAvailableSize(context: Context): Long =
                if (isWritable()) {
                    getStorageAvailableSize(getBaseDir(context))
                } else 0

        /**
         * 根据 type 创建并返回一个外部存储在内置存储卡分区上的私有目录下的Files目录
         *
         * @param context
         * @param type The type of files directory to return. May be {@code null}
         *            for the root of the files directory or one of the following
         *            constants for a subdirectory:
         *            {@link android.os.Environment#DIRECTORY_MUSIC},
         *            {@link android.os.Environment#DIRECTORY_PODCASTS},
         *            {@link android.os.Environment#DIRECTORY_RINGTONES},
         *            {@link android.os.Environment#DIRECTORY_ALARMS},
         *            {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
         *            {@link android.os.Environment#DIRECTORY_PICTURES}, or
         *            {@link android.os.Environment#DIRECTORY_MOVIES}.
         * @return /storage/emulated/(0/1/...)/Android/data/packageName/files/(根据 type 确定此级目录，如果 type 为 null，则没有此级目录)
         */
        fun getExternalFilesDir(context: Context, type: String? = null): File? =
                if (isWritable()) {
                    context.applicationContext.getExternalFilesDir(type)
                } else null

        /**
         * 创建并返回一个外部存储在内置存储卡分区上的私有目录下的Cache目录
         *
         * @param context
         * @return /storage/emulated/(0/1/...)/Android/data/packageName/cache
         */
        fun getExternalCacheDir(context: Context): File? =
                if (isWritable()) {
                    context.applicationContext.externalCacheDir
                } else null

        /**
         * 根据 type 创建并返回一个外部存储在内置存储卡分区或者外置SD卡的私有目录
         *
         * 有些设备支持外插SD卡，所以这类设备的外部存储由内置存储卡分区和外置SD卡组成：
         * 在Android4.3及以下的系统，只能通过Context.getExternalFilesDir(type)来获取外部存储在内置存储卡分区上的私有目录地址，而无法获取到外部存储在外置SD卡上的私有目录地址。
         * 从Android4.4开始，你可以通过Context.getExternalFilesDirs(type)获取一个File数组，File数组中就包含了内置存储卡分区和外置SD卡的私有目录地址。
         *
         * @param context
         * @param type The type of files directory to return. May be {@code null}
         *            for the root of the files directory or one of the following
         *            constants for a subdirectory:
         *            {@link android.os.Environment#DIRECTORY_MUSIC},
         *            {@link android.os.Environment#DIRECTORY_PODCASTS},
         *            {@link android.os.Environment#DIRECTORY_RINGTONES},
         *            {@link android.os.Environment#DIRECTORY_ALARMS},
         *            {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
         *            {@link android.os.Environment#DIRECTORY_PICTURES}, or
         *            {@link android.os.Environment#DIRECTORY_MOVIES}.
         * @return /storage/emulated/(0/1/...)/Android/data/packageName/files/(根据 type 确定此级目录，如果 type 为 null，则没有此级目录)
         * 返回数组中的第一个元素被视为主外部存储卷。除非该卷已满或不可用，否则请使用该卷。
         */
        fun getExternalFilesDirs(context: Context, type: String? = null): Array<out File>? =
                if (isWritable()) {
                    context.applicationContext.getExternalFilesDirs(type)
                } else null

    }
}