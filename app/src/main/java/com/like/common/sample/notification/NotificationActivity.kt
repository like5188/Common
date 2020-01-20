package com.like.common.sample.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.like.common.sample.MainActivity
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityNotificationBinding
import com.like.common.util.createNotificationChannel
import com.like.common.util.createNotificationChannelGroups
import com.like.common.util.gotoChannelNotificationSetting
import com.like.common.util.notifyNotification

class NotificationActivity : AppCompatActivity() {
    private val mBinding: ActivityNotificationBinding by lazy {
        DataBindingUtil.setContentView<ActivityNotificationBinding>(this, R.layout.activity_notification)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    @SuppressLint("NewApi")
    fun createNotificationChannel(view: View) {
        val group = NotificationChannelGroup("group1", "分组1")
        createNotificationChannelGroups(listOf(group))

        val channel1 = NotificationChannel("channel1", "渠道1", NotificationManager.IMPORTANCE_LOW)
        channel1.group = "group1"
        // 开启指示灯，如果设备有的话
        channel1.enableLights(true)
        // 设置指示灯颜色
        channel1.lightColor = ContextCompat.getColor(this, R.color.colorPrimary)
        // 是否在久按桌面图标时显示此渠道的通知
        channel1.setShowBadge(true)
        // 设置是否应在锁定屏幕上显示此频道的通知
        channel1.lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
        // 设置绕过免打扰模式
        channel1.setBypassDnd(true)
        createNotificationChannel(channel1)

        val channel2 = NotificationChannel("channel2", "渠道2", NotificationManager.IMPORTANCE_LOW)
        channel2.group = "group1"
        createNotificationChannel(channel2)
    }

    fun notifyNotification(view: View) {
        val contentView = RemoteViews(packageName, R.layout.view_download_progress_for_notification)
        contentView.setImageViewResource(R.id.iv_small_icon, R.mipmap.ic_launcher)
        contentView.setImageViewResource(R.id.iv_large_icon, R.mipmap.ic_launcher)
        contentView.setImageViewResource(R.id.iv_controller, R.mipmap.ic_launcher)

        val contentIntent = PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                0
        )

        notifyNotification(
                1,
                NotificationCompat.Builder(this, "channel1")
                        .setContentTitle("标题1")
                        .setContentText("内容1")
                        .setSmallIcon(R.drawable.icon_0)
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.image_0))
                        .setAutoCancel(true)
                        .setNumber(11)
                        .setCustomContentView(contentView)
                        .setContentIntent(contentIntent)
                        .build()
        )
        notifyNotification(
                2,
                NotificationCompat.Builder(this, "channel2")
                        .setContentTitle("标题2")
                        .setContentText("内容2")
                        .setSmallIcon(R.drawable.icon_0)
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.image_2))
                        .setAutoCancel(true)
                        .setNumber(22)
                        .setCustomContentView(contentView)
                        .setContentIntent(contentIntent)
                        .build()
        )
    }

    fun gotoChannelNotificationSetting(view: View) {
        gotoChannelNotificationSetting("channel1")
    }

}
