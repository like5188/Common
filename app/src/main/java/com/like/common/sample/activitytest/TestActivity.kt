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
        // 1、设置了启动模式
        // 2、实例已经存在，并且此时的启动模式为 SingleTask 和 SingleInstance，再次启动该 Activity 会触发。
        // 3、实例已经存在，且这个实例位于栈顶，且启动模式为 SingleTop，再次启动该 Activity 会触发。
        // 重新设置 intent
        setIntent(intent)
        // 重新初始化数据
        initView()
        Log.v(TAG, "onNewIntent")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 当设置android:configChanges="orientation"时，横竖屏切换就会触发，并且不会触发其它生命周期方法。
        Log.v(TAG, "onConfigurationChanged")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 当不设置android:configChanges="orientation"时，横竖屏切换就会触发，并且会触发其它生命周期方法。
        Log.v(TAG, "onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        // 当不设置android:configChanges="orientation"时，横竖屏切换就会触发，并且会触发其它生命周期方法。
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

}
