package com.like.common.sample

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.like.common.sample.activitytest.TestActivity
import com.like.common.sample.banner.BannerActivity
import com.like.common.sample.ble.BleActivity
import com.like.common.sample.checkradio.CheckAndRadioActivity
import com.like.common.sample.coroutines.CoroutinesActivity
import com.like.common.sample.databinding.ActivityMainBinding
import com.like.common.sample.dialog.DialogActivity
import com.like.common.sample.drag.DragViewTestActivity
import com.like.common.sample.flexbox.FlexBoxActivity
import com.like.common.sample.image.ImageActivity
import com.like.common.sample.letterlistview.SidebarViewActivity
import com.like.common.sample.notification.NotificationActivity
import com.like.common.sample.pictureselector.PictureSelectorActivity
import com.like.common.sample.serializable.SerializableActivity
import com.like.common.sample.update.UpdateActivity
import com.like.common.util.SPUtils
import com.like.common.util.shortToastCenter
import com.like.common.view.TimerTextView
import com.like.common.view.toolbar.ToolbarUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val mToolbarUtils: ToolbarUtils by lazy {
        ToolbarUtils(this, fl_toolbarContainer)
                .showTitle("sample", R.color.common_text_white_0)
                .showNavigationButton(R.drawable.icon_back, View.OnClickListener { shortToastCenter("返回") })
                .showCustomNavigationView(R.drawable.icon_back, "拍照", View.OnClickListener { shortToastCenter("拍照啦！") })
                .setCustomNavigationViewTextColor(ActivityCompat.getColor(this, R.color.common_text_white_0))
                .setCustomNavigationViewMessageCount("99+")
                .setCustomNavigationViewMessageBackgroundColor(ContextCompat.getColor(this, R.color.common_text_red_0))
                .setCustomNavigationViewMessageTextSize(10)
                .setDividerHeight(30)
                .setRightMenu(R.menu.toolbar_right_menu_main)
                .setRightMenuMargin(R.id.action_0, 10, 0)
                .replaceMenuWithCustomView(R.id.action_0, R.drawable.icon_0, "消息1", listener = View.OnClickListener { shortToastCenter("消息") })
                .setRightMenuTextColor(R.id.action_0, ActivityCompat.getColor(this, R.color.common_text_white_0))
                .setRightMenuMessageCount(R.id.action_0, "1").setDividerColor(Color.RED)
                .replaceMenuWithCustomView(R.id.action_1, R.drawable.icon_0, "添加1", listener = View.OnClickListener { shortToastCenter("添加") })
                .setRightMenuMessageCount(R.id.action_1, "2").setDividerColor(Color.RED)
    }
    private val mBinding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        mToolbarUtils
        SPUtils.getInstance().init(this)
        initMarqueeView()
        mBinding.timerTextView.setOnTickListener(object : TimerTextView.OnTickListener {
            override fun onStart(time: Long) {
                mBinding.timerTextView.text = "开始倒计时 ${time / 1000} 秒"
            }

            override fun onTick(time: Long) {
                Log.w("TimerTextView", "time=$time time/1000=${time / 1000}")
                mBinding.timerTextView.text = "剩余 ${time / 1000} 秒"
            }

            override fun onEnd() {
                mBinding.timerTextView.text = "重新获取验证码"
            }
        })
        mBinding.timerTextView.setOnClickListener {
            mBinding.timerTextView.start(15000)
        }
    }

    private fun initMarqueeView() {
        val list = listOf(Pair("like1", "like2"), Pair("like3", "like4"))
        mBinding.verticalMarqueeView.initParamsAndPlay(list, R.id.ll, {
            mBinding.tv1.text = it.first
            mBinding.tv2.text = it.second
        })
        mBinding.tv1.setOnClickListener {
            shortToastCenter(list[mBinding.verticalMarqueeView.getCurPosition()].first)
        }
        mBinding.tv2.setOnClickListener {
            shortToastCenter(list[mBinding.verticalMarqueeView.getCurPosition()].second)
        }
    }

    fun gotoFlexBoxActivity(view: View) {
        startActivity(Intent(this, FlexBoxActivity::class.java))
    }

    fun gotoBleActivity(view: View) {
        startActivity(Intent(this, BleActivity::class.java))
    }

    fun gotoCoroutinesActivity(view: View) {
        startActivity(Intent(this, CoroutinesActivity::class.java))
    }

    fun gotoImageActivity(view: View) {
        startActivity(Intent(this, ImageActivity::class.java))
    }

    fun gotoPictureSelectorActivity(view: View) {
        startActivity(Intent(this, PictureSelectorActivity::class.java))
    }

    fun gotoDragViewTestActivity(view: View) {
        startActivity(Intent(this, DragViewTestActivity::class.java))
    }

    fun gotoCheckAndRadioActivity(view: View) {
        startActivity(Intent(this, CheckAndRadioActivity::class.java))
    }

    fun gotoSidebarViewActivity(view: View) {
        startActivity(Intent(this, SidebarViewActivity::class.java))
    }

    fun gotoBannerActivity(view: View) {
        startActivity(Intent(this, BannerActivity::class.java))
    }

    fun gotoUpdateActivity(view: View) {
        startActivity(Intent(this, UpdateActivity::class.java))
    }

    fun gotoNotificationActivity(view: View) {
        startActivity(Intent(this, NotificationActivity::class.java))
    }

    fun gotoSerializableActivity(view: View) {
        startActivity(Intent(this, SerializableActivity::class.java))
    }

    fun gotoDialogActivity(view: View) {
        startActivity(Intent(this, DialogActivity::class.java))
    }

    fun gotoTestActivity(view: View) {
        val intent = Intent(this, TestActivity::class.java)
        intent.putExtra("name", "从 MainActivity 跳转过来")
        startActivity(intent)
    }

}
