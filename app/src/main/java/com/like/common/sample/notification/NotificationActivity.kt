package com.like.common.sample.notification

import android.app.PendingIntent
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.RemoteViews
import com.like.common.sample.MainActivity
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityNotificationBinding
import com.like.common.util.createNotificationChannel
import com.like.common.util.gotoNotificationChannelSetting
import com.like.common.util.notify

class NotificationActivity : AppCompatActivity() {
    private val mBinding: ActivityNotificationBinding by lazy {
        DataBindingUtil.setContentView<ActivityNotificationBinding>(this, R.layout.activity_notification)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    fun createNotificationChannel(view: View) {
        createNotificationChannel("d", "通知测试", NotificationManagerCompat.IMPORTANCE_LOW)
    }

    fun notify(view: View) {
        val contentView = RemoteViews(packageName, R.layout.view_download_progress_for_notification)
        contentView.setImageViewResource(R.id.iv_small_icon, R.mipmap.ic_launcher)
        contentView.setImageViewResource(R.id.iv_large_icon, R.mipmap.ic_launcher)
        contentView.setImageViewResource(R.id.iv_controller, R.drawable.ic_placeholder)

        val contentIntent = PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                0
        )

        notify(
                2,
                NotificationCompat.Builder(this, "d")
                        .setContentTitle("收到一条渠道d的消息")
                        .setContentText("2d")
                        .setSmallIcon(R.drawable.icon_0)
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.image_0))
                        .setAutoCancel(true)
                        .setNumber(999)
                        .setCustomContentView(contentView)
                        .setContentIntent(contentIntent)
                        .build()
        )
    }

    fun gotoNotificationChannelSetting(view: View) {
        gotoNotificationChannelSetting("d")
    }

}
