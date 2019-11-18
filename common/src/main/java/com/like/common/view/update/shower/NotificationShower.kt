package com.like.common.view.update.shower

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.like.common.R
import com.like.common.util.cancelNotification
import com.like.common.util.createNotificationChannel
import com.like.common.util.notify
import com.like.common.util.toDataStorageUnit
import com.like.common.view.update.TAG_PAUSE_OR_CONTINUE
import com.like.livedatabus.LiveDataBus
import com.like.retrofit.utils.getCustomNetworkMessage

/**
 * 普通更新使用通知栏显示进度条
 */
class NotificationShower(
        private val context: Context,
        @DrawableRes smallIcon: Int,
        clickPendingIntent: PendingIntent,
        channelId: String,
        channelName: String
) : IShower {
    companion object {
        private const val NOTIFICATION_ID = 1111
    }

    private val remoteViews by lazy {
        val controlIntent = PendingIntent.getBroadcast(
                context,
                1,
                Intent("action").apply {
                    putExtra("type", TAG_PAUSE_OR_CONTINUE)
                    setPackage(context.packageName)// 8.0以上必须添加包名才能接收到静态广播
                },
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        RemoteViews(context.packageName, R.layout.view_download_progress_for_notification)
                .apply { setOnClickPendingIntent(R.id.iv_controller, controlIntent) }
    }
    private val notification by lazy {
        val builder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && channelId.isNotEmpty() && channelName.isNotEmpty()) {
                    context.createNotificationChannel(channelId, channelName, NotificationManagerCompat.IMPORTANCE_LOW)
                    NotificationCompat.Builder(context, channelId)
                } else {
                    NotificationCompat.Builder(context).setCustomBigContentView(remoteViews)// 避免显示不完全。
                }
        builder.setCustomContentView(remoteViews)
//                .setOngoing(true)// 将Ongoing设为true 那么notification将不能滑动删除
                .setSmallIcon(smallIcon)
                .setContentIntent(clickPendingIntent)
                .build()
    }

    override fun onDownloadPending() {
        updateNotification("正在连接服务器...")
    }

    @Synchronized
    override fun onDownloadRunning(currentSize: Long, totalSize: Long) {
        updateNotification("下载中，请稍后...", currentSize, totalSize)
    }

    override fun onDownloadPaused(currentSize: Long, totalSize: Long) {
        updateNotification("已经暂停下载", currentSize, totalSize, true)
    }

    override fun onDownloadSuccessful(totalSize: Long) {
        context.cancelNotification(NOTIFICATION_ID)
    }

    override fun onDownloadFailed(throwable: Throwable?) {
        updateNotification(throwable.getCustomNetworkMessage())
    }

    private fun updateNotification(status: String, currentSize: Long = -1, totalSize: Long = -1, pause: Boolean = false) {
        remoteViews.setTextViewText(R.id.tv_status, status)
        remoteViews.setImageViewResource(R.id.iv_controller, if (pause) R.drawable.download_start else R.drawable.download_pause)
        if (currentSize > 0 && totalSize > 0) {
            val progress = Math.round(currentSize.toFloat() / totalSize.toFloat() * 100)
            remoteViews.setTextViewText(R.id.tv_percent, "$progress%")
            remoteViews.setTextViewText(R.id.tv_size, "${currentSize.toDataStorageUnit()}/${totalSize.toDataStorageUnit()}")
            remoteViews.setProgressBar(R.id.pb_progress, 100, progress, false)
        }
        context.notify(NOTIFICATION_ID, notification)
    }

}

class NotificationControllerBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            "action" -> {
                when (intent.getStringExtra("type")) {
                    TAG_PAUSE_OR_CONTINUE -> LiveDataBus.post(TAG_PAUSE_OR_CONTINUE)
                }
            }
        }
    }
}
