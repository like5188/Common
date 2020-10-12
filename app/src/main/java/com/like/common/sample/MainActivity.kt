package com.like.common.sample

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import com.like.common.sample.activitytest.TestActivity
import com.like.common.sample.autowired.AutoWiredActivity
import com.like.common.sample.checkradio.CheckAndRadioActivity
import com.like.common.sample.coroutines.CoroutinesActivity
import com.like.common.sample.databinding.ActivityMainBinding
import com.like.common.sample.databinding.ViewMarqueeBinding
import com.like.common.sample.databinding.ViewTitlebarButtonBinding
import com.like.common.sample.dialog.DialogActivity
import com.like.common.sample.drag.DragViewTestActivity
import com.like.common.sample.flexbox.FlexBoxActivity
import com.like.common.sample.fragmenttest.FragmentContainer
import com.like.common.sample.image.ImageActivity
import com.like.common.sample.letterlistview.SidebarViewActivity
import com.like.common.sample.notification.NotificationActivity
import com.like.common.sample.pictureselector.PictureSelectorActivity
import com.like.common.sample.serializable.SerializableActivity
import com.like.common.sample.zxing.ZXingActivity
import com.like.common.util.*
import com.like.common.view.TimerTextView
import com.like.common.view.titlebar.CustomViewManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val mBinding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
//        initDefaultTitlebar()
        initCustomTitlebar()
//        initOriginToolBar()
        initCustomToolbar()
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
        mBinding.ivTintTest.setSelectorSrcResource(R.color.common_divider_gray, R.color.common_text_red_0)
        mBinding.ivTintTest.setSelectorBackgroundResource(R.color.common_divider_gray, R.color.common_text_red_0)
        mBinding.rbTintTest.setSelectorBackgroundResource(R.color.common_divider_gray, R.color.common_text_red_0)
        mBinding.cbTintTest.setSelectorBackgroundResource(R.color.common_divider_gray, R.color.common_text_red_0)
        mBinding.btnTintTest.setSelectorBackgroundResource(R.color.common_divider_gray, R.color.common_text_red_0)
    }

    private fun initCustomTitlebar() {
        mBinding.titlebar.Custom().apply {
            val customViewManager = CustomViewManager(this@MainActivity)
            customViewManager.setTitle("哈哈哈哈啊哈")
            setLeftView(customViewManager.getView())

            val centerBinding = setCenterView(R.layout.view_titlebar_button) as ViewTitlebarButtonBinding
            centerBinding.tv1.text = "111111111111111"
            centerBinding.tv2.text = "2"

            val rightBinding = setRightView(R.layout.view_titlebar_button) as ViewTitlebarButtonBinding
            rightBinding.tv1.text = "11111111111111111111"
            rightBinding.tv2.text = "222"
        }
    }

    private fun initDefaultTitlebar() {
        mBinding.titlebar.Default().apply {
            showNavigation(R.drawable.icon_back) {
                shortToastCenter("返回")
            }

            showTitle("哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈")
            setTitleWidth(100f)

            showDivider(1f, Color.GREEN)

            CustomViewManager(this@MainActivity).apply {
                setIcon(R.drawable.icon_back)
                setOnClickListener { shortToastCenter("菜单0") }
                setTitle("菜单0", Color.BLACK, 12f)
                setMessageCount("0", Color.WHITE, 10, Color.RED)
                setMargin(20, 10, 0, 10)
                setContentPadding(30, 0, 30, 0)
                addMenu(getView())
            }
            CustomViewManager(this@MainActivity).apply {
                setIcon(R.drawable.icon_back)
                setOnClickListener { shortToastCenter("菜单1") }
                setTitle("菜单1", Color.BLACK, 12f)
                setMessageCount("1", Color.WHITE, 10, Color.RED)
                setMargin(0, 10, 20, 10)
                setContentPadding(30, 0, 30, 0)
                addMenu(getView())
            }
        }
    }

    private fun initOriginToolBar() {
        mBinding.toolbar.title = "哈哈哈哈哈哈哈哈哈哈的的"
        mBinding.toolbar.setTitleTextColor(Color.WHITE)
        mBinding.toolbar.setNavigationIcon(R.drawable.icon_back)
        mBinding.toolbar.setNavigationOnClickListener {
            shortToastCenter("返回")
        }
        mBinding.toolbar.inflateMenu(R.menu.origin_toolbar_right_menu_main)
        mBinding.toolbar.overflowIcon = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.icon_0))
        mBinding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_0 -> {
                    shortToastCenter("消息")
                }
                R.id.action_1 -> {
                    shortToastCenter("添加")
                }
                R.id.action_2 -> {
                    shortToastCenter("编辑")
                }
                R.id.action_3 -> {
                    shortToastCenter("删除")
                }
                R.id.action_4 -> {
                    shortToastCenter("查看")
                }
            }
            true
        }
    }

    private fun initCustomToolbar() {
        mBinding.toolbar.setBackgroundColor(Color.WHITE)
        mBinding.tvTitle.text = "title1title1title1title1"
        mBinding.tvTitle.textSize = 18f
        mBinding.tvTitle.setTextColor(Color.BLACK)
        (mBinding.tvTitle.layoutParams as Toolbar.LayoutParams).gravity = Gravity.START

        val customNavigationView = CustomViewManager(this)
        mBinding.toolbar.addView(customNavigationView.getView(), 0)
        customNavigationView.setIcon(R.drawable.icon_back)
        customNavigationView.setTitle("返回")
        customNavigationView.setOnClickListener { shortToastCenter("返回") }
        customNavigationView.setMargin(42, 10, 20, 10)
        customNavigationView.setContentPadding(0, 0, 30, 0)
        customNavigationView.setMessageCount("99+", Color.WHITE, 10, Color.RED)

        mBinding.toolbar.inflateMenu(R.menu.custom_toolbar_right_menu_main)
        mBinding.toolbar.overflowIcon = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.icon_0))
        (MenuItemCompat.getActionProvider(mBinding.toolbar.menu.findItem(R.id.action_0)) as? CustomActionProvider)
                ?.getCustomViewManager()
                ?.apply {
                    setIcon(R.drawable.icon_back)
                    setOnClickListener { shortToastCenter("菜单0") }
                    setTitle("菜单0", Color.BLACK, 12f)
                    setMessageCount("0", Color.WHITE, 10, Color.RED)
                    setMargin(0, 10, 0, 10)
                    setContentPadding(30, 0, 30, 0)
                }
        (MenuItemCompat.getActionProvider(mBinding.toolbar.menu.findItem(R.id.action_1)) as? CustomActionProvider)
                ?.getCustomViewManager()
                ?.apply {
                    setIcon(R.drawable.icon_back)
                    setOnClickListener { shortToastCenter("菜单1") }
                    setTitle("菜单1", Color.BLACK, 12f)
                    setMessageCount("1", Color.WHITE, 10, Color.RED)
                    setMargin(0, 10, 0, 10)
                    setContentPadding(30, 0, 30, 0)
                }
    }

    private fun initMarqueeView() {
        val list = listOf(Pair("like1", "like2"), Pair("like3", "like4"))
        list.forEach { data ->
            val viewMarqueeBinding = DataBindingUtil.inflate<ViewMarqueeBinding>(layoutInflater, R.layout.view_marquee, null, false)
            viewMarqueeBinding.tv1.text = data.first
            viewMarqueeBinding.tv2.text = data.second
            viewMarqueeBinding.tv1.setOnClickListener {
                shortToastCenter(data.first)
            }
            viewMarqueeBinding.tv2.setOnClickListener {
                shortToastCenter(data.second)
            }
            view_flipper.addView(viewMarqueeBinding.root)
        }
        view_flipper.flipInterval = 3000
        view_flipper.startFlipping()
    }

    fun gotoFragmentContainer(view: View) {
        startActivity<FragmentContainer>()
    }

    fun gotoAutoWiredActivity(view: View) {
        startActivity<AutoWiredActivity>(
                "param1" to 1,
                "param2" to null,
                "param" to "3",
                "param4" to 4,
                "param5" to listOf(AutoWiredActivity.P("5"), AutoWiredActivity.P("6"))
        )
    }

    fun gotoZXingActivity(view: View) {
        startActivity(Intent(this, ZXingActivity::class.java))
    }

    fun gotoFlexBoxActivity(view: View) {
        startActivity(Intent(this, FlexBoxActivity::class.java))
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
