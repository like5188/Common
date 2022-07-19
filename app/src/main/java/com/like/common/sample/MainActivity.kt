package com.like.common.sample

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.hjq.toast.ToastUtils
import com.like.common.sample.activitytest.TestActivity
import com.like.common.sample.anim.AnimActivity
import com.like.common.sample.autowired.AutoWiredActivity
import com.like.common.sample.checkradio.CheckAndRadioActivity
import com.like.common.sample.coroutines.CoroutinesActivity
import com.like.common.sample.databinding.ActivityMainBinding
import com.like.common.sample.databinding.ViewMarqueeBinding
import com.like.common.sample.dialog.DialogActivity
import com.like.common.sample.drag.DragViewTestActivity
import com.like.common.sample.flexbox.FlexBoxActivity
import com.like.common.sample.fragmenttest.FragmentContainer
import com.like.common.sample.image.ImageActivity
import com.like.common.sample.letterlistview.SidebarViewActivity
import com.like.common.sample.notification.NotificationActivity
import com.like.common.sample.serializable.SerializableActivity
import com.like.common.sample.storage.StorageActivity
import com.like.common.sample.timertextview.TimerTextViewActivity
import com.like.common.sample.uistatuscontroller.UiStatusControllerActivity
import com.like.common.sample.zxing.ZXingActivity
import com.like.common.util.*
import com.like.common.view.toolbar.CustomToolbarMenu
import com.like.common.view.toolbar.CustomToolbarMenuActionProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        initOriginToolBar()
        initCustomMenuToolbar()
        SPUtils.getInstance().init(this)
        initMarqueeView()
        mBinding.ivTintTest.setSelectorSrcResource(R.color.common_divider_gray, R.color.common_text_red_0)
        mBinding.ivTintTest.setSelectorBackgroundResource(R.color.common_divider_gray, R.color.common_text_red_0)
        mBinding.rbTintTest.setSelectorBackgroundResource(R.color.common_divider_gray, R.color.common_text_red_0)
        mBinding.cbTintTest.setSelectorBackgroundResource(R.color.common_divider_gray, R.color.common_text_red_0)
        mBinding.btnTintTest.setSelectorBackgroundResource(R.color.common_divider_gray, R.color.common_text_red_0)
        lifecycleScope.launch {
            mBinding.etSearch.search()
                .filter {
                    !it.isNullOrEmpty() && it.length > 3
                }
                .map {
                    delay(1000)
                    "search $it"
                }
                .collect {
                    Logger.w("搜索成功：$it")
                }
        }
    }

    private fun initOriginToolBar() {
        mBinding.toolbar1.title = "Origin ToolBar"
        mBinding.toolbar1.setTitleTextColor(Color.WHITE)
        mBinding.toolbar1.setNavigationIcon(R.drawable.icon_back)
        mBinding.toolbar1.setNavigationOnClickListener {
            ToastUtils.show("返回")
        }
        mBinding.toolbar1.inflateMenu(R.menu.origin_toolbar_right_menu_main)
        mBinding.toolbar1.overflowIcon = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.icon_0))
        mBinding.toolbar1.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_0 -> {
                    ToastUtils.show("消息")
                }
                R.id.action_1 -> {
                    ToastUtils.show("添加")
                }
                R.id.action_2 -> {
                    ToastUtils.show("编辑")
                }
                R.id.action_3 -> {
                    ToastUtils.show("删除")
                }
                R.id.action_4 -> {
                    ToastUtils.show("查看")
                }
            }
            true
        }
    }

    private fun initCustomMenuToolbar() {
        mBinding.toolbar2.setBackgroundColor(Color.WHITE)
        with(mBinding.tvTitle) {
            text = "Custom Toolbar"
            textSize = 18f
            setTextColor(Color.BLACK)
            (layoutParams as Toolbar.LayoutParams).gravity = Gravity.CENTER
        }

        with(CustomToolbarMenu(this)) {
            mBinding.toolbar2.addView(getView(), 0)
            setIcon(R.drawable.icon_back)
            setText("返回")
            setOnClickListener { ToastUtils.show("返回") }
            setMargin(42, 10, 20, 10)
            setContentPadding(0, 0, 30, 0)
            getBadgeView().apply {
                count = 100
                setBackgroundColor(Color.GRAY)
                setBackgroundBorder(Color.RED, 1.dp)
                setTextColor(Color.WHITE)
            }
        }

        mBinding.toolbar2.inflateMenu(R.menu.custom_toolbar_right_menu_main)
        mBinding.toolbar2.overflowIcon = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.icon_0))
        (MenuItemCompat.getActionProvider(mBinding.toolbar2.menu.findItem(R.id.action_0)) as? CustomToolbarMenuActionProvider)?.apply {
            setIcon(R.drawable.icon_back)
            setOnClickListener { ToastUtils.show("菜单0") }
            setText("菜单0", Color.BLACK, 12f)
            setMargin(0, 10, 0, 10)
            setContentPadding(30, 0, 30, 0)
            getBadgeView().apply {
                setBackgroundColor(Color.GREEN)
                setTextColor(Color.BLACK)
                setTextSize(8f.dp)
                count = 2
            }
        }
        (MenuItemCompat.getActionProvider(mBinding.toolbar2.menu.findItem(R.id.action_1)) as? CustomToolbarMenuActionProvider)?.apply {
            setIcon(R.drawable.icon_back)
            setOnClickListener { ToastUtils.show("菜单1") }
            setText("菜单1", Color.BLACK, 12f)
            setMargin(0, 10, 0, 10)
            setContentPadding(30, 0, 30, 0)
            getBadgeView().apply {
                count = 22
                setBackgroundColor(Color.RED)
                setTextColor(Color.WHITE)
                setTextSize(18f.dp)
            }
        }
    }

    private fun initMarqueeView() {
        val list = listOf(Pair("like1", "like2"), Pair("like3", "like4"))
        list.forEach { data ->
            val viewMarqueeBinding = DataBindingUtil.inflate<ViewMarqueeBinding>(layoutInflater, R.layout.view_marquee, null, false)
            viewMarqueeBinding.tv1.text = data.first
            viewMarqueeBinding.tv2.text = data.second
            viewMarqueeBinding.tv1.setOnClickListener {
                ToastUtils.show(data.first)
            }
            viewMarqueeBinding.tv2.setOnClickListener {
                ToastUtils.show(data.second)
            }
            mBinding.viewFlipper.addView(viewMarqueeBinding.root)
        }
        mBinding.viewFlipper.flipInterval = 3000
        mBinding.viewFlipper.startFlipping()
    }

    fun location(view: View) {
        lifecycleScope.launch {
            val l = AMapLocationUtils(this@MainActivity).location()
            Logger.e("${l?.latitude} ${l?.longitude}")
        }
//        NavigationUtils.navigation(this@MainActivity, 29.512043, 106.499777)
    }

    fun showUiStatusController(view: View) {
        startActivity(Intent(this, UiStatusControllerActivity::class.java))
    }

    fun gotoTimerTextViewActivity(view: View) {
        startActivity(Intent(this, TimerTextViewActivity::class.java))
    }

    fun gotoAnimActivity(view: View) {
        startActivity(Intent(this, AnimActivity::class.java))
    }

    fun gotoFragmentContainer(view: View) {
        FragmentContainer.start()
    }

    fun gotoStorageActivity(view: View) {
        startActivity(Intent(this, StorageActivity::class.java))
    }

    fun gotoAutoWiredActivity(view: View) {
        val intent = createIntent<AutoWiredActivity>(
            "param1" to 1,
            "param2" to null,
            "param" to "3",
            "param4" to 4,
            "param5" to listOf(AutoWiredActivity.P("5"), AutoWiredActivity.P("6"))
        )
        startActivity(intent)
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
