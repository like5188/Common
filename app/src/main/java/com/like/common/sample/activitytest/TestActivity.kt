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

    override fun onRestart() {
        super.onRestart()
        Log.v(TAG, "onRestart")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // 重新设置 intent
        setIntent(intent)
        // 重新初始化数据
        initView()
        Log.v(TAG, "onNewIntent")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.v(TAG, "onConfigurationChanged")
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
