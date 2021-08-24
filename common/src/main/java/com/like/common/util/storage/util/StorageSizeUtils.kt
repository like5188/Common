package com.like.common.util.storage.util

import android.os.StatFs

object StorageSizeUtils {
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
}
