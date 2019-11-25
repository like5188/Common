package com.like.common.view.update.shower

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.like.common.R
import com.like.common.util.cancelNotification
import com.like.common.util.createNotificationChannel
import com.like.common.util.notifyNotification
import com.like.common.util.toDataStorageUnit
import com.like.common.view.update.TAG_PAUSE_OR_CONTINUE
import com.like.livedatabus.LiveDataBus
import com.like.retrofit.utils.getCustomNetworkMessage

/**
 * 普通更新使用通知栏显示进度条
 */
abstract class NotificationShower(private val context: Context) : IShower {
    companion object {
        private const val NOTIFICATION_ID = 1111
    }

    private val remoteViews by lazy {
        val remoteViews = RemoteViews(context.packageName, R.layout.view_download_progress_for_notification)
        val controlIntent = PendingIntent.getBroadcast(
                context,
                1,
                Intent("action").apply {
                    putExtra("type", TAG_PAUSE_OR_CONTINUE)
                    setPackage(context.packageName)// 8.0以上必须添加包名才能接收到静态广播
                },
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        remoteViews.setOnClickPendingIntent(R.id.iv_controller, controlIntent)
        onRemoteViewsCreated(remoteViews)
        remoteViews
    }
    private val notification by lazy {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "update"
            val channelName = "更新"
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            context.createNotificationChannel(channel)
            NotificationCompat.Builder(context, channelId).setCustomContentView(remoteViews)
        } else {
            NotificationCompat.Builder(context).setCustomBigContentView(remoteViews)// 避免显示不完全。
        }
        onBuilderCreated(builder)
        builder.build()
    }

    abstract fun onBuilderCreated(builder: NotificationCompat.Builder)

    abstract fun onRemoteViewsCreated(remoteViews: RemoteViews)

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
        context.notifyNotification(NOTIFICATION_ID, notification)
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
