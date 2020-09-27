package com.like.common.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings

fun Context.getConnectivityManager(): ConnectivityManager? = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

/**
 * 网络是否可用
 */
fun Context.isInternetAvailable(): Boolean {
    val cm = getConnectivityManager() ?: return false
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        cm.getNetworkCapabilities(cm.activeNetwork)?.run {
            when {
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true// 移动网络
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true// 以太网
                else -> false
            }
        } ?: false
    } else {
        @Suppress("DEPRECATION")
        cm.activeNetworkInfo?.type == ConnectivityManager.TYPE_WIFI || cm.activeNetworkInfo?.type == ConnectivityManager.TYPE_MOBILE
    }
}

/**
 * wifi 是否连接
 */
fun Context.isWiFiConnected(): Boolean {
    val cm = getConnectivityManager() ?: return false
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        cm.getNetworkCapabilities(cm.activeNetwork)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
    } else {
        @Suppress("DEPRECATION")
        cm.activeNetworkInfo?.type == ConnectivityManager.TYPE_WIFI
    }
}

/**
 * 打开无线网络设置界面
 */
fun Context.openWirelessSettings() {
    startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
}

/**
 * 打开wifi设置界面
 */
fun Context.openWifiSettings() {
    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
}

/**
 * 打开移动数据设置界面
 */
fun Context.openDataRoamingSettings() {
    startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
}