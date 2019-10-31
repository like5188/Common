package com.like.common.sample.activitytest

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityTestBinding

/**
 * Activity 相关的测试
 */
class TestActivity : AppCompatActivity() {
    companion object {
        private val TAG = TestActivity::class.java.simpleName
    }

    private val mBinding: ActivityTestBinding by lazy {
        DataBindingUtil.setContentView<ActivityTestBinding>(this, R.layout.activity_test)
    }

    private fun initView() {
        intent?.let {
            val name = it.getStringExtra("name")
            mBinding.tv.text = name
        }
    }

    /***************************生命周期方法***************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        initView()
        Log.v(TAG, "onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
    }

    /***************************非生命周期方法，需要一定的条件来触发***************************/
    override fun onRestart() {
        super.onRestart()
        // 有 onStop() 状态恢复的时候触发
        Log.v(TAG, "onRestart")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // 触发条件：首先是设置了启动模式，并且实例已经存在
        // 1、如果启动模式为 SingleTask 和 SingleInstance，再次启动该 Activity。
        // 2、这个实例位于栈顶，且启动模式为 SingleTop，再次启动该 Activity。

        // 重新设置 intent
        setIntent(intent)
        // 重新初始化数据
        initView()
        Log.v(TAG, "onNewIntent")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 触发条件：
        // 1、当设置android:configChanges="orientation"时，横竖屏切换。注意：不会触发其它生命周期方法。
        Log.v(TAG, "onConfigurationChanged")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 触发条件：
        // 1、当不设置android:configChanges="orientation"时，横竖屏切换。
        // 2、按 home、menu、锁屏 键
        // 3、启动其它 Activity
        // 但是当用户主动去销毁一个 Activity 时，例如在应用中按返回键，onSaveInstanceState() 就不会被调用，所以用做数据的持久化保存，更应该在 onPause() 方法中进行。
        Log.v(TAG, "onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        // 触发条件：
        // 1、当不设置android:configChanges="orientation"时，横竖屏切换
        Log.v(TAG, "onRestoreInstanceState")
    }

    fun click0(view: View) {
        // 用来测试 singleTop 启动模式生命周期：onPause() -> onNewIntent() -> onResume()
        // 此时需要在 onNewIntent() 方法中重新设置 setIntent(intent)，然后重新初始化数据，否则得不到新数据。
        val intent = Intent(this, TestActivity::class.java)
        intent.putExtra("name", "自己启动自己")
        startActivity(intent)
    }

    fun click1(view: View) {
        startActivity(Intent(this, TestActivity1::class.java))
    }

    fun click2(view: View) {
        startActivity(Intent(this, TestActivity2::class.java))
    }

}
