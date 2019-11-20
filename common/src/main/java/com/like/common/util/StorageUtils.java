package com.like.common.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * 存储空间工具类
 */
public class StorageUtils {

    /**
     * 获取指定目录空间大小(byte)。
     *
     * @param dir
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getStorageSize(String dir) {
        StatFs fs = new StatFs(dir);
        long count = fs.getBlockCountLong();// 文件系统上总共的块
        long size = fs.getBlockSizeLong();// 文件系统 一个块的大小单位byte
        return count * size;
    }

    /**
     * 获取指定目录的剩余空间大小(byte)。包括预留的一般程序无法访问的
     *
     * @param dir
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getStorageFreeSize(String dir) {
        StatFs fs = new StatFs(dir);
        long count = fs.getFreeBlocksLong();// 文件系统上剩余的所有块，包括预留的一般程序无法访问的
        long size = fs.getBlockSizeLong();
        return count * size;
    }

    /**
     * 获取ExternalStorage的可用空间大小(byte)
     *
     * @param dir
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getStorageAvailableSize(String dir) {
        StatFs fs = new StatFs(dir);
        long count = fs.getAvailableBlocksLong();// 文件系统上剩下的可供程序使用的块
        long size = fs.getBlockSizeLong();
        return count * size;
    }

    /**
     * 内部存储工具类，此工具类全部基于InternalStorage
     */
    public static class InternalStorageHelper {
        /**
         * 获取根目录
         *
         * @param context
         * @return /data/data/packagename
         */
        @SuppressLint("SdCardPath")
        public static String getBaseDir(Context context) {
            return "/data/data/" + context.getApplicationContext().getPackageName();
        }

        /**
         * 获取完整空间大小。
         *
         * @param context
         * @return
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public static long getSize(Context context) {
            return getStorageSize(getBaseDir(context.getApplicationContext()));
        }

        /**
         * 获取剩余空间大小。包括预留的一般程序无法访问的
         *
         * @param context
         * @return
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public static long getFreeSize(Context context) {
            return getStorageFreeSize(getBaseDir(context.getApplicationContext()));
        }

        /**
         * 获取可用空间大小。
         *
         * @param context
         * @return
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public static long getAvailableSize(Context context) {
            return getStorageAvailableSize(getBaseDir(context.getApplicationContext()));
        }

        /**
         * 获取数据库默认存储路径
         *
         * @param context
         * @return /data/data/packagename/databases
         */
        @SuppressLint("SdCardPath")
        public static String getDatabaseDir(Context context) {
            return "/data/data/" + context.getApplicationContext().getPackageName() + "/databases";
        }

        /**
         * 获取指定数据库名称的文件
         *
         * @param context
         * @param databaseName
         * @return /data/data/packagename/databases/databaseName
         */
        public static File getDatabaseFile(Context context, String databaseName) {
            return context.getApplicationContext().getDatabasePath(databaseName);
        }

        /**
         * 获取Files目录
         *
         * @param context
         * @return /data/data/packagename/files
         */
        public static File getFilesDir(Context context) {
            return context.getApplicationContext().getFilesDir();
        }

        /**
         * 获取Cache目录
         *
         * @param context
         * @return /data/data/packagename/cache
         */
        public static File getCacheDir(Context context) {
            return context.getApplicationContext().getCacheDir();
        }
    }

    /**
     * 外部存储工具类，此工具类全部基于ExternalStorage
     */
    public static class ExternalStorageHelper {
        /**
         * 判断是否被挂载
         *
         * @return
         */
        public static boolean isMounted() {
            return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        }

        /**
         * 获取根目录
         *
         * @return /storage/emulated/(0/1/...)或者/mnt/sdcard
         */
        public static String getBaseDir() {
            if (isMounted()) {
                return Environment.getExternalStorageDirectory().getAbsolutePath();
            }
            return null;
        }

        /**
         * 获取完整空间大小。
         *
         * @return
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public static long getSize() {
            if (isMounted()) {
                return getStorageSize(getBaseDir());
            }
            return 0;
        }

        /**
         * 获取剩余空间大小。包括预留的一般程序无法访问的
         *
         * @return
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public static long getFreeSize() {
            if (isMounted()) {
                return getStorageFreeSize(getBaseDir());
            }
            return 0;
        }

        /**
         * 获取可用空间大小。
         *
         * @return
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public static long getAvailableSize() {
            if (isMounted()) {
                return getStorageAvailableSize(getBaseDir());
            }
            return 0;
        }

        /**
         * 获取公共目录下的指定类型的文件目录
         *
         * @param type Environment.DIRECTORY_DCIM、Environment.DIRECTORY_DOWNLOADS、Environment.DIRECTORY_PICTURES 等等
         * @return /storage/emulated/(0/1/...)/(DCIM/Download/Pictures/...)
         */
        public static File getPublicDir(String type) {
            if (isMounted()) {
                return Environment.getExternalStoragePublicDirectory(type);
            }
            return null;
        }

        /**
         * 获取私有目录下的Files目录
         *
         * @param context
         * @param type    Environment.DIRECTORY_DCIM、Environment.DIRECTORY_DOWNLOADS、Environment.DIRECTORY_PICTURES 等等
         * @return /storage/emulated/(0/1/...)/Android/data/packagename/files/(
         * DCIM/Download/Pictures/...)
         */
        public static File getPrivateFilesDir(Context context, String type) {
            if (isMounted()) {
                return context.getApplicationContext().getExternalFilesDir(type);
            }
            return null;
        }

        /**
         * 获取私有目录下的Cache目录
         *
         * @param context
         * @return /storage/emulated/(0/1/...)/Android/data/packagename/cache
         */
        public static File getPrivateCacheDir(Context context) {
            if (isMounted()) {
                return context.getApplicationContext().getExternalCacheDir();
            }
            return null;
        }

    }

}
