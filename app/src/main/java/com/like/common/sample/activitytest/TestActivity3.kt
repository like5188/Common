package com.like.common.sample.activitytest

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.activityresultlauncher.util.startActivity
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityTest3Binding
import com.like.common.util.ApplicationHolder

/**
 * 透明的 Activity
 */
class TestActivity3 : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityTest3Binding>(this, R.layout.activity_test3)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    companion object {
        fun start(context: Context? = null) {
            val ctx = context ?: ApplicationHolder.application
            ctx.startActivity<TestActivity3>()
        }
    }
}
