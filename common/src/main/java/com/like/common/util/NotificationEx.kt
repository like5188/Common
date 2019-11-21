package com.like.common.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.provider.Settings

/**
 * IMPORTANCE_MIN 开启通知，不会弹出，但没有提示音，状态栏中无显示
 * IMPORTANCE_LOW 开启通知，不会弹出，不发出提示音，状态栏中显示
 * IMPORTANCE_DEFAULT 开启通知，不会弹出，发出提示音，状态栏中显示
 * IMPORTANCE_HIGH 开启通知，会弹出，发出提示音，状态栏中显示
 */

/**
 * 添加通知渠道组。
 */
fun Context.createNotificationChannelGroups(groups: List<NotificationChannelGroup>) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        getNotificationManager()?.createNotificationChannelGroups(groups)
    }
}

/**
 * 删除指定通知渠道组及其包含的所有通知渠道
 */
fun Context.deleteNotificationChannelGroup(groupId: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        getNotificationManager()?.deleteNotificationChannelGroup(groupId)
    }
}

fun Context.getNotificationChannelGroup(groupId: String): NotificationChannelGroup? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        getNotificationManager()?.getNotificationChannelGroup(groupId)
    } else {
        null
    }
}

fun Context.getNotificationChannelGroups(): List<NotificationChannelGroup>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        getNotificationManager()?.notificationChannelGroups
    } else {
        null
    }
}

/**
 * 添加通知渠道。
 * 一经创建，就不能更改，覆盖安装也不行，除非卸载重装。
 * 后面的createNotificationChannel方法仅能更新其name/description，以及对importance进行降级，其余配置均无法更新。
 *
 * 可以随时调用，每次系统会检测该通知渠道是否已经存在了，因此不会重复创建，也并不会影响任何效率。
 */
fun Context.createNotificationChannel(channel: NotificationChannel) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        getNotificationManager()?.createNotificationChannel(channel)
    }
}

/**
 * 删除指定通知渠道
 *
 * 但是这个功能非常不建议大家使用。
 * 因为Google为了防止应用程序随意地创建垃圾通知渠道，会在通知设置界面显示所有被删除的通知渠道数量，严重影响美观。
 */
fun Context.deleteNotificationChannel(channelId: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        getNotificationManager()?.deleteNotificationChannel(channelId)
    }
}

/**
 * 获取指定渠道
 */
fun Context.getNotificationChannel(channelId: String): NotificationChannel? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getNotificationManager()?.getNotificationChannel(channelId)
        } else {
            null
        }

fun Context.getNotificationChannels(): List<NotificationChannel>? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getNotificationManager()?.notificationChannels
        } else {
            null
        }

/**
 * 显示通知
 *
 * @param notificationId    通知id。一样id的通知会覆盖。
 * @param notification      通知
 */
fun Context.notifyNotification(notificationId: Int, notification: Notification) {
    getNotificationManager()?.notify(notificationId, notification)
}

/**
 * 取消通知
 */
fun Context.cancelNotification(notificationId: Int) {
    getNotificationManager()?.cancel(notificationId)
}

/**
 * 取消所有通知
 */
fun Context.cancelAllNotifications() {
    getNotificationManager()?.cancelAll()
}

/**
 * 跳转到指定通知渠道的设置界面
 */
fun Context.gotoChannelNotificationSetting(channelId: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
        startActivity(intent)
    }
}

fun Context.getNotificationManager() =
        applicationContext.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager