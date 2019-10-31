package com.like.common.sample.activitytest

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityTest3Binding

/**
 * 透明的 Activity
 */
class TestActivity3 : AppCompatActivity() {
    private val mBinding: ActivityTest3Binding by lazy {
        DataBindingUtil.setContentView<ActivityTest3Binding>(this, R.layout.activity_test3)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

}
