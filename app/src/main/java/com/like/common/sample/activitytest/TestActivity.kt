package com.like.common.sample.activitytest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityTestBinding

/**
 * 对话框测试
 */
class TestActivity : AppCompatActivity() {
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
        Log.d("tag", "onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d("tag", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("tag", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("tag", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("tag", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("tag", "onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("tag", "onRestart")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // 重新设置 intent
        setIntent(intent)
        // 重新初始化数据
        initView()
        Log.d("tag", "onNewIntent")
    }

    fun click0(view: View) {
        // 用来测试 singleTop 启动模式生命周期：onPause() -> onNewIntent() -> onResume()
        // 此时需要在 onNewIntent() 方法中重新设置 setIntent(intent)，然后重新初始化数据，否则得不到新数据。
        val intent = Intent(this, TestActivity::class.java)
        intent.putExtra("name", "自己启动自己")
        startActivity(intent)
    }

}
