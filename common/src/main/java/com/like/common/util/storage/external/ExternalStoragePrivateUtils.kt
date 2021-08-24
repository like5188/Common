package com.like.common.util.storage.external

import android.content.Context
import android.os.Environment
import com.like.common.util.storage.util.StorageSizeUtils
import java.io.File

// 分区存储改变了应用在设备的外部存储设备中存储和访问文件的方式。
/**
 * 外部存储私有目录操作应用专属文件的工具类。
 * 权限：Android 4.4 以后不需要申请存储权限。
 * 公共目录操作工具类为：[ExternalStoragePublicUtils]
 * /storage/emulated/(0/1/...)/Android/data/packageName/xxx
 *
 * 外部存储私有目录：卸载应用后，系统会移除这些目录中存储的文件。
 *  注意：外部存储可以是外置SD卡 ，也可以是内置存储卡 的部分分区。 外部存储是可以全局访问的，分为公共目录和私有目录。
 *
 * 文件访问方式与之前Android版本一致，可以通过File path获取资源。
 * 注意：无法保证可以访问这些目录中的文件，例如从设备中取出可移除的 SD 卡后，就无法访问其中的文件。如果应用的功能取决于这些文件，应改为将文件存储在内部存储空间中。
 * Android 10（API 级别 29）及更高版本为目标平台的应用在默认情况下被授予了对外部存储空间的分区访问权限（即分区存储）。启用分区存储后，应用将无法访问属于其他应用的私有目录。
 *
 * 设置项的 Clear Data 和 Clear cache 两个选项，这两个都是清空应用的缓存数据，具体区别如下：
 *     1、Clear Data清理的是外部存储中的应用私有目录下的file文件夹
 *     2、Clear Cache清理的是外部存储中的应用私有目录下的cache文件夹
 */
object ExternalStoragePrivateUtils {

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
            StorageSizeUtils.getStorageSize(getBaseDir(context))
        } else 0

    /**
     * 获取剩余空间大小。包括预留的一般程序无法访问的
     *
     * @return
     */
    fun getFreeSize(context: Context): Long =
        if (isWritable()) {
            StorageSizeUtils.getStorageFreeSize(getBaseDir(context))
        } else 0

    /**
     * 获取可用空间大小。
     *
     * @return
     */
    fun getAvailableSize(context: Context): Long =
        if (isWritable()) {
            StorageSizeUtils.getStorageAvailableSize(getBaseDir(context))
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