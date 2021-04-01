package com.like.common.sample.activitytest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityTest2Binding
import com.like.common.util.ApplicationHolder
import com.like.common.util.createIntent

/**
 * 窗口模式的 Activity
 */
class TestActivity2 : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityTest2Binding>(this, R.layout.activity_test2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    companion object {
        fun start() {
            val intent = ApplicationHolder.application.createIntent<TestActivity2>()
            ApplicationHolder.application.startActivity(intent)
        }
    }
}
