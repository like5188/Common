package com.like.common.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.WindowInsets

object PhoneUtils {

    /**
     * android系统版本
     */
    fun getAndroidSystemVersion(): String = Build.VERSION.RELEASE

    /**
     * 手机品牌
     */
    fun getPhoneBrand(): String = Build.BRAND

    /**
     * 手机型号
     */
    fun getPhoneModel(): String = Build.MODEL

    /**
     * SDK版本号
     */
    fun getSdkVersion() = Build.VERSION.SDK_INT

    /**
     * app可用的屏幕宽度（像素）
     */
    fun getDisplayScreenWidth(context: Context): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = context.windowManager.currentWindowMetrics
            val insets = metrics.windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout()
            )
            metrics.bounds.width() - (insets.right + insets.left)
        } else {
            context.resources.displayMetrics.widthPixels
        }

    /**
     * app可用的屏幕高度（像素）
     */
    fun getDisplayScreenHeight(context: Context): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = context.windowManager.currentWindowMetrics
            val insets = metrics.windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout()
            )
            metrics.bounds.height() - (insets.top + insets.bottom)
        } else {
            context.resources.displayMetrics.heightPixels
        }

    /**
     * 屏幕密度： 1.0     1.5      2         3        3.5
     * dpi：     160     240     320       480       560
     * 分辨率：320x533 480x800 720x1280 1080x1920 1440x2560
     * Res：    mdpi    hdpi    xhdpi   xxhdpi    xxxhdpi
     */
    fun getDensity(context: Context): Float = context.resources.displayMetrics.density

    /**
     * 屏幕密度DPI
     */
    fun getDensityDpi(context: Context): Int = context.resources.displayMetrics.densityDpi

    /**
     * 获取电话号码
     */
    @SuppressLint("MissingPermission")
    suspend fun getPhoneNumber(requestPermissionWrapper: RequestPermissionWrapper): String? {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestPermissionWrapper.requestPermission(android.Manifest.permission.READ_PHONE_NUMBERS)
        } else {
            requestPermissionWrapper.requestPermission(android.Manifest.permission.READ_PHONE_STATE)
        }
        return if (permission) {
            requestPermissionWrapper.activity.applicationContext.telephonyManager.line1Number
        } else {
            null
        }
    }

}
