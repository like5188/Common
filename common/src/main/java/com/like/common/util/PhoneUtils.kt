package com.like.common.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import com.like.common.util.activityresultlauncher.requestPermission

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
     * app显示区域的宽度（像素）（不包含状态栏等系统装饰元素）
     */
    fun getDisplayWidth(context: Context): Int =
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
     * app显示区域的高度（像素）（不包含状态栏等系统装饰元素）
     */
    fun getDisplayHeight(context: Context): Int =
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
     * 屏幕宽度（像素）（包含状态栏等系统装饰元素）
     */
    fun getScreenWidth(context: Context): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.windowManager.currentWindowMetrics.bounds.width()
        } else {
            val displayMetrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            displayMetrics.widthPixels
        }

    /**
     * 屏幕高度（像素）（包含状态栏等系统装饰元素）
     */
    fun getScreenHeight(context: Context): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.windowManager.currentWindowMetrics.bounds.height()
        } else {
            val displayMetrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            displayMetrics.heightPixels
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
     * 缩放密度DPI
     */
    fun getScaledDensity(context: Context): Float = context.resources.displayMetrics.scaledDensity

    /**
     * 获取电话号码
     */
    @SuppressLint("MissingPermission")
    suspend fun getPhoneNumber(activity: ComponentActivity): String? {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.requestPermission(android.Manifest.permission.READ_PHONE_NUMBERS)
        } else {
            activity.requestPermission(android.Manifest.permission.READ_PHONE_STATE)
        }
        return if (permission) {
            activity.applicationContext.telephonyManager.line1Number
        } else {
            null
        }
    }

    suspend fun print(activity: ComponentActivity) {
        Logger.d(
            "androidSystemVersion=${getAndroidSystemVersion()} " +
                    "phoneBrand=${getPhoneBrand()} " +
                    "PhoneModel=${getPhoneModel()} " +
                    "SdkVersion=${getSdkVersion()} " +
                    "screenWidth=${getScreenWidth(activity)} " +
                    "screenHeight=${getScreenHeight(activity)} " +
                    "displayScreenWidth=${getDisplayWidth(activity)} " +
                    "displayScreenHeight=${getDisplayHeight(activity)} " +
                    "density=${getDensity(activity)} " +
                    "densityDpi=${getDensityDpi(activity)} " +
                    "scaledDensity=${getScaledDensity(activity)} " +
                    "phoneNumber=${getPhoneNumber(activity)}"
        )
    }

}
