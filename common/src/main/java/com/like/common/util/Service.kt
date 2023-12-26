package com.like.common.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat

/**
 * 把服务变成前台服务
 * @param contentTitle  通知标题
 * @param contentText   通知文本
 * @param smallIcon     通知小图标
 * @param largeIcon     通知大图标
 * @param id            The identifier for this notification as per NotificationManager.notify(int, Notification); must not be 0.
 * @param channelName   通知渠道名称
 * @param channelId     通知渠道id
 */
fun Service.startForeground(
    contentTitle: String,
    contentText: String,
    @DrawableRes smallIcon: Int,
    @DrawableRes largeIcon: Int,
    id: Int = this.hashCode(),
    channelName: String = this::class.java.simpleName,
    channelId: String = "${channelName}_id",
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(channelId) == null) {
        notificationManager.createNotificationChannel(
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        )
    }
    startForeground(
        id,
        NotificationCompat.Builder(this, channelId).setSmallIcon(smallIcon).setLargeIcon(BitmapFactory.decodeResource(resources, largeIcon))
            .setContentTitle(contentTitle).setContentText(contentText).build()
    )
}

/**
 * 把前台服务变成后台服务，并移除通知
 */
fun Service.stopForeground() {
    stopForeground(true)
}
