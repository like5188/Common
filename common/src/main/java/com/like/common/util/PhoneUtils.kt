package com.like.common.util

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics

object PhoneUtils {

    /**
     * android系统版本
     */
    fun getReleaseVersion(): String = Build.VERSION.RELEASE

    /**
     * 手机品牌
     */
    fun getBrand(): String = Build.BRAND

    /**
     * 手机型号
     */
    fun getModel(): String = Build.MODEL

    /**
     * SDK版本号
     */
    fun getSdkVersion() = Build.VERSION.SDK_INT

    /**
     * 屏幕宽度（像素）
     */
    fun getScreenWidth(context: Context): Int {
        val metric = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(metric)
        return metric.widthPixels
    }

    /**
     * 屏幕高度（像素）
     */
    fun getScreenHeight(context: Context): Int {
        val metric = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(metric)
        return metric.heightPixels
    }

    /**
     * 屏幕密度：1.0     1.5      2         3        3.5
     * dpi：     160     240     320       480       560
     * 分辨率：320x533 480x800 720x1280 1080x1920 1440x2560
     * Res：     mdpi    hdpi    xhdpi   xxhdpi    xxxhdpi
     */
    fun getDensity(context: Context): Float {
        val metric = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(metric)
        return metric.density
    }

    /**
     * 屏幕密度DPI
     */
    fun getDensityDpi(context: Context): Int {
        val metric = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(metric)
        return metric.densityDpi
    }

}