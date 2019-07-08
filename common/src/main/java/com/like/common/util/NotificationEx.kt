@file:Suppress("NOTHING_TO_INLINE")

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
 * 添加通知分组。
 */
inline fun Context.createNotificationGroup(groupId: String, groupName: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        getNotificationManager()?.createNotificationChannelGroup(NotificationChannelGroup(groupId, groupName))
    }
}

/**
 * 添加通知渠道。一经创建，就不能更改，覆盖安装也不行，除非卸载重装。
 * 后面的createNotificationChannel方法仅能更新其name/description，以及对importance进行降级，其余配置均无法更新。
 *
 * 可以随时调用，每次系统会检测该通知渠道是否已经存在了，因此不会重复创建，也并不会影响任何效率。
 *
 * @param groupId       groupId。
 * @param channelId     渠道id。
 * @param channelName   渠道名字。显示在设置里面
 * @param importance    渠道重要程度。[android.app.NotificationManager]
 */
inline fun Context.createNotificationChannel(channelId: String, channelName: String, importance: Int) {
    createNotificationChannel("", channelId, channelName, importance)
}

/**
 * 添加通知渠道。一经创建，就不能更改，覆盖安装也不行，除非卸载重装。
 * 后面的createNotificationChannel方法仅能更新其name/description，以及对importance进行降级，其余配置均无法更新。
 *
 * 可以随时调用，每次系统会检测该通知渠道是否已经存在了，因此不会重复创建，也并不会影响任何效率。
 *
 * @param groupId       groupId。
 * @param channelId     渠道id。
 * @param channelName   渠道名字。显示在设置里面
 * @param importance    渠道重要程度。[android.app.NotificationManager]
 */
inline fun Context.createNotificationChannel(groupId: String, channelId: String, channelName: String, importance: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, channelName, importance)
        if (groupId.isNotEmpty())
            channel.group = groupId
        getNotificationManager()?.createNotificationChannel(channel)
    }
}

/**
 * 删除指定渠道
 *
 * 但是这个功能非常不建议大家使用。
 * 因为Google为了防止应用程序随意地创建垃圾通知渠道，会在通知设置界面显示所有被删除的通知渠道数量，严重影响美观。
 */
inline fun Context.deleteNotificationChannel(channelId: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        getNotificationManager()?.deleteNotificationChannel(channelId)
    }
}

/**
 * 获取指定渠道
 */
inline fun Context.getNotificationChannel(channelId: String): NotificationChannel? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getNotificationManager()?.getNotificationChannel(channelId)
        } else {
            null
        }

/**
 * 显示通知
 *
 * @param notificationId    通知id。一样id的通知会覆盖。
 * @param notification      通知
 */
inline fun Context.notify(notificationId: Int, notification: Notification) {
    getNotificationManager()?.notify(notificationId, notification)
}

/**
 * 取消通知
 */
inline fun Context.cancelNotification(notificationId: Int) {
    getNotificationManager()?.cancel(notificationId)
}

/**
 * 取消所有通知
 */
inline fun Context.cancelAllNotification() {
    getNotificationManager()?.cancelAll()
}

/**
 * 跳转到指定渠道的设置界面
 */
inline fun Context.gotoNotificationChannelSetting(channelId: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
        startActivity(intent)
    }
}

inline fun Context.getNotificationManager() =
        applicationContext.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager