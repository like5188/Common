package com.like.common.util

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import java.io.File

/**
 * 存储空间工具类
 *
 * Android系统使用的是虚拟文件系统（VFS），VFS提供了供存储设备挂载的节点。同一个存储设备经过分区后，不同的分区可以挂载到不同的节点上，如手机的内置存储卡。
 *
 * 使用内部存储是不需要权限的，内部存储属于应用的私有存储区域，其它应用不可访问，当应用被卸载时，内部存储中的文件也会被删除。
 *
 * 外部存储可以是外置SD卡 ，也可以是内置存储卡 的部分分区。 外部存储分为公共目录和私有目录.
 * 外部存储是可以全局访问的，但需要申请权限：
 *      1 Android 4.4 以前访问私有目录和公共目录都需要申请 WRITE_EXTERNAL_STORAGE 权限
 *      2 Android 4.4 以后访问私有目录不需要申请权限，但公共目录是需要申请 WRITE_EXTERNAL_STORAGE 权限
 * 自 API 级别 17 以来，常量 MODE_WORLD_READABLE 和 MODE_WORLD_WRITEABLE 已被弃用。从 Android N 开始，使用这些常量将会导致引发 SecurityException。
 *
 * 如果缓存的数据量比较大，请不要保存到内部存储中，如果想保存可共享给其它应用的数据，请保存到外部存储的公共目录中。
 *
 * 设置项的 Clear Data 和 Clear cache 两个选项，这两个都是清空应用的缓存数据，具体区别如下：
 *      1 Clear Data清理的是外部存储中的应用私有目录下的file文件夹
 *      2 Clear Cache清理的是外部存储中的应用私有目录下的cache文件夹
 */
object StorageUtils {
    /**
     * 获取指定目录空间大小(byte)。
     *
     * @param dir
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
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
     * 内部存储工具类，此工具类全部基于InternalStorage
     */
    object InternalStorageHelper {
        /**
         * 获取根目录
         *
         * @param context
         * @return /data/data(user/0)/packagename
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
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        fun getSize(context: Context): Long =
                getStorageSize(getBaseDir(context.applicationContext))

        /**
         * 获取剩余空间大小。包括预留的一般程序无法访问的
         *
         * @param context
         * @return
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        fun getFreeSize(context: Context): Long =
                getStorageFreeSize(getBaseDir(context.applicationContext))

        /**
         * 获取可用空间大小。
         *
         * @param context
         * @return
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        fun getAvailableSize(context: Context): Long =
                getStorageAvailableSize(getBaseDir(context.applicationContext))

        /**
         * 获取数据库所在的路径
         *
         * @param context
         * @param name    子路径或者文件名
         * @return /data/data(user/0)/packagename/databases/name
         */
        fun getDatabasePath(context: Context, name: String): File =
                context.applicationContext.getDatabasePath(name)

        /**
         * 获取Files目录
         *
         * @param context
         * @return /data/data(user/0)/packagename/files
         */
        fun getFilesDir(context: Context): File =
                context.applicationContext.filesDir

        /**
         * 获取Cache目录
         *
         * @param context
         * @return /data/data(user/0)/packagename/cache
         */
        fun getCacheDir(context: Context): File =
                context.applicationContext.cacheDir

        /**
         * 创建并返回一个内部存储的文件
         *
         * @param context
         * @param name      文件名
         * @param mode      [android.content.Context.MODE_PRIVATE]、[android.content.Context.MODE_APPEND]
         * 注意:自 API 级别 17 以来，常量 MODE_WORLD_READABLE 和 MODE_WORLD_WRITEABLE 已被弃用。从 Android N 开始，
         * 使用这些常量将会导致引发 SecurityException。这意味着，面向 Android N 和更高版本的应用无法按名称共享私有文件，
         * 尝试共享“file://”URI 将会导致引发FileUriExposedException。 如果您的应用需要与其他应用共享私有文件，则可以将 FileProvider 与 FLAG_GRANT_READ_URI_PERMISSION 配合使用。
         *
         * @return /data/data(user/0)/packagename/name
         */
        fun getDir(context: Context, name: String, mode: Int = Context.MODE_PRIVATE): File =
                context.applicationContext.getDir(name, mode)
    }

    /**
     * 外部存储工具类，此工具类全部基于ExternalStorage
     */
    object ExternalStorageHelper {
        /**
         * 判断是否被挂载
         *
         * @return
         */
        fun isMounted(): Boolean =
                Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

        /**
         * 获取根目录
         *
         * @return /storage/emulated/(0/1/...)或者/mnt/sdcard
         */
        private fun getBaseDir(): String? =
                if (isMounted()) {
                    Environment.getExternalStorageDirectory().absolutePath
                } else null

        /**
         * 获取完整空间大小。
         *
         * @return
         */
        fun getSize(): Long =
                if (isMounted()) {
                    getStorageSize(getBaseDir())
                } else 0

        /**
         * 获取剩余空间大小。包括预留的一般程序无法访问的
         *
         * @return
         */
        fun getFreeSize(): Long =
                if (isMounted()) {
                    getStorageFreeSize(getBaseDir())
                } else 0

        /**
         * 获取可用空间大小。
         *
         * @return
         */
        fun getAvailableSize(): Long =
                if (isMounted()) {
                    getStorageAvailableSize(getBaseDir())
                } else 0

        /**
         * 获取私有目录下的Files目录
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
         * @return /storage/emulated/(0/1/...)/Android/data/packagename/files/(DCIM/Download/Pictures/...)
         */
        fun getExternalFilesDir(context: Context, type: String): File? =
                if (isMounted()) {
                    context.applicationContext.getExternalFilesDir(type)
                } else null

        /**
         * 获取私有目录下的Cache目录
         *
         * @param context
         * @return /storage/emulated/(0/1/...)/Android/data/packagename/cache
         */
        fun getExternalCacheDir(context: Context): File? =
                if (isMounted()) {
                    context.applicationContext.externalCacheDir
                } else null

    }
}