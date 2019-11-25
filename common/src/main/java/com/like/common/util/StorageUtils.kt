package com.like.common.util

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import java.io.File

/**
 * 存储空间工具类
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
         * @param type    Environment.DIRECTORY_DCIM、Environment.DIRECTORY_DOWNLOADS、Environment.DIRECTORY_PICTURES 等等
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