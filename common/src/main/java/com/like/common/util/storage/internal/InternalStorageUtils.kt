package com.like.common.util.storage.internal

import android.content.Context
import android.os.Build
import com.like.common.util.storage.util.StorageSizeUtils
import java.io.File

/**
 * 内部存储目录操作应用专属文件的工具类。
 * 权限：不需要申请存储权限。
 * 内部存储：属于应用的私有存储区域，空间小，其它应用不可访问，卸载应用后，系统会移除这些目录中存储的文件。
 * system/、vendor/、/data/data(user/0)/packageName/xxx 等等
 *
 * 注意:自 API 级别 17 以来，常量 MODE_WORLD_READABLE 和 MODE_WORLD_WRITEABLE 已被弃用。
 * 从 Android N (7.0) 开始，使用这些常量将会导致引发 SecurityException。
 * 这意味着，面向 Android N 和更高版本的应用无法按名称共享私有文件，尝试共享“file://”URI 将会导致引发 FileUriExposedException。
 * 如需允许其他应用访问存储在内部存储空间内此目录中的文件，请使用具有 FLAG_GRANT_READ_URI_PERMISSION 属性的 FileProvider。
 */
object InternalStorageUtils {
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
        StorageSizeUtils.getStorageSize(getBaseDir(context))

    /**
     * 获取剩余空间大小。包括预留的一般程序无法访问的
     *
     * @param context
     * @return
     */
    fun getFreeSize(context: Context): Long =
        StorageSizeUtils.getStorageFreeSize(getBaseDir(context))

    /**
     * 获取可用空间大小。
     *
     * @param context
     * @return
     */
    fun getAvailableSize(context: Context): Long =
        StorageSizeUtils.getStorageAvailableSize(getBaseDir(context))

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
     * 获取Cache目录。存储缓存文件。注意：如果系统的存储空间不足，则可能会在不发出警告的情况下删除您的缓存文件。因此，请在读取前检查缓存文件是否存在。
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