package com.like.common.util

import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Vibrator
import android.telephony.TelephonyManager
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager

val Context.windowManager: WindowManager get() = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
val Context.telephonyManager: TelephonyManager get() = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
val Context.activityManager: ActivityManager get() = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
val Context.connectivityManager: ConnectivityManager get() = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
val Context.notificationManager: NotificationManager get() = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
val Context.inputMethodManager: InputMethodManager get() = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
val Context.vibrator: Vibrator get() = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
