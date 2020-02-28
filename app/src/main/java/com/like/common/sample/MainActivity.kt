package com.like.common.sample

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.like.common.sample.activitytest.TestActivity
import com.like.common.sample.checkradio.CheckAndRadioActivity
import com.like.common.sample.coroutines.CoroutinesActivity
import com.like.common.sample.databinding.ActivityMainBinding
import com.like.common.sample.databinding.ViewMarqueeBinding
import com.like.common.sample.dialog.DialogActivity
import com.like.common.sample.drag.DragViewTestActivity
import com.like.common.sample.flexbox.FlexBoxActivity
import com.like.common.sample.image.ImageActivity
import com.like.common.sample.letterlistview.SidebarViewActivity
import com.like.common.sample.notification.NotificationActivity
import com.like.common.sample.pictureselector.PictureSelectorActivity
import com.like.common.sample.serializable.SerializableActivity
import com.like.common.sample.zxing.ZXingActivity
import com.like.common.util.SPUtils
import com.like.common.util.setSelectorBackgroundResource
import com.like.common.util.setSelectorSrcResource
import com.like.common.util.shortToastCenter
import com.like.common.view.TimerTextView
import com.like.common.view.toolbar.ToolbarUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val mOriginToolbarUtils: ToolbarUtils by lazy {
        // 使用原生Toolbar显示标题栏
        ToolbarUtils(this, fl_toolbarContainer).apply {
            showTitle("0123456789", Color.WHITE)
            getNavigationManager().apply {
                showView(R.drawable.icon_back, View.OnClickListener { shortToastCenter("返回") })
            }
            getMenuManager().apply {
                showMenu(R.menu.toolbar_right_menu_main, R.drawable.icon_0, Toolbar.OnMenuItemClickListener {
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
                })
            }
            showDivider()
        }
    }
    private val mCustomToolbarUtils: ToolbarUtils by lazy {
        // 使用自定义的Toolbar显示标题栏
        ToolbarUtils(this, fl_toolbarContainer).apply {
            setBackgroundColor(Color.WHITE)
            showCustomTitle("0123456789aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", Color.BLACK, 18f)
            showDivider(1f, Color.LTGRAY)
            getNavigationManager().apply {
                showCustomView(R.drawable.icon_back, "返回", Color.BLACK, 12f,
                        View.OnClickListener { shortToastCenter("返回") }
                )
                setCustomViewMargin(42, 10, 20, 10)
                setCustomViewContentPadding(0, 0, 30, 0)
                showCustomViewMessageCount("99+", Color.WHITE, 10, Color.RED)
            }
            getMenuManager().apply {
                showMenu(R.menu.toolbar_right_menu_main, R.drawable.icon_0, Toolbar.OnMenuItemClickListener {
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
                })

                replaceMenuWithCustomView(R.id.action_0, View.OnClickListener { shortToastCenter("消息1") })
                setCustomViewMenuIcon(R.id.action_0, R.drawable.icon_0)
                setCustomViewMenuTitle(R.id.action_0, "消息1", Color.BLACK, 12f)
                setCustomViewMenuMessageCount(R.id.action_0, "0", Color.WHITE, 10, Color.RED)
                setCustomViewMenuMargin(R.id.action_0, 10, 10)
                setCustomViewMenuContentPadding(R.id.action_0, 30, 0, 30, 0)

                replaceMenuWithCustomView(R.id.action_1, View.OnClickListener { shortToastCenter("添加1") })
                setCustomViewMenuIcon(R.id.action_1, R.drawable.icon_0)
                setCustomViewMenuTitle(R.id.action_1, "添加1", Color.BLACK, 12f)
                setCustomViewMenuMessageCount(R.id.action_1, "1", Color.WHITE, 10, Color.RED)
                setCustomViewMenuMargin(R.id.action_1, 10, 10)
                setCustomViewMenuContentPadding(R.id.action_1, 0, 0, 30, 0)

                replaceMenuWithCustomView(R.id.action_2, View.OnClickListener { shortToastCenter("编辑1") })
                setCustomViewMenuIcon(R.id.action_2, R.drawable.icon_0)
                setCustomViewMenuTitle(R.id.action_2, "编辑1", Color.BLACK, 12f)
                setCustomViewMenuMessageCount(R.id.action_2, "2", Color.WHITE, 10, Color.RED)
                setCustomViewMenuMargin(R.id.action_2, 10, 10)
                setCustomViewMenuContentPadding(R.id.action_2, 0, 0, 30, 0)

                replaceMenuWithCustomView(R.id.action_3, View.OnClickListener { shortToastCenter("删除1") })
                setCustomViewMenuIcon(R.id.action_3, R.drawable.icon_0)
                setCustomViewMenuTitle(R.id.action_3, "删除1", Color.BLACK, 12f)
                setCustomViewMenuMessageCount(R.id.action_3, "3", Color.WHITE, 10, Color.RED)
                setCustomViewMenuMargin(R.id.action_3, 10, 10)
                setCustomViewMenuContentPadding(R.id.action_3, 0, 0, 30, 0)

                replaceMenuWithCustomView(R.id.action_4, View.OnClickListener { shortToastCenter("查看1") })
                setCustomViewMenuIcon(R.id.action_4, R.drawable.icon_0)
                setCustomViewMenuTitle(R.id.action_4, "查看1", Color.BLACK, 12f)
                setCustomViewMenuMessageCount(R.id.action_4, "4", Color.WHITE, 10, Color.RED)
                setCustomViewMenuMargin(R.id.action_4, 10, 10)
                setCustomViewMenuContentPadding(R.id.action_4, 0, 0, 30, 0)
            }
        }
    }

    private val mBinding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        mOriginToolbarUtils
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

    fun gotoZXingActivity(view: View) {
        startActivity(Intent(this, ZXingActivity::class.java))
    }

    fun gotoFlexBoxActivity(view: View) {
        startActivity(Intent(this, FlexBoxActivity::class.java))

//        val p = PermissionUtils(this)
//        p.checkStoragePermissionGroup {
//            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "1.txt")
//            if (file.exists()) {
//                Log.v("tag", "读取文件:$file ${file.readText()}")
//                file.delete()
//            } else {
//                Log.i("tag", "创建文件:$file")
//                file.createNewFile()
//                file.writeText("123")
//            }
//        }
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
