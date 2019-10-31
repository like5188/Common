package com.like.common.sample.activitytest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityTest2Binding

/**
 * 非窗口模式的 Activity
 */
class TestActivity2 : AppCompatActivity() {
    private val mBinding: ActivityTest2Binding by lazy {
        DataBindingUtil.setContentView<ActivityTest2Binding>(this, R.layout.activity_test2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

}
