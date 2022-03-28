package com.like.common.util

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings

/**
 * 判断 gps 是否打开
 */
fun Context.isOpenGps(): Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

/**
 * 打开 gps 设置页面的 Intent
 */
fun getLocationSettingsIntent() = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
