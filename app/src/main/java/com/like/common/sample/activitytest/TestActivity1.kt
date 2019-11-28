package com.like.common.sample.activitytest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityTest1Binding

/**
 * 正常的 Activity
 */
class TestActivity1 : AppCompatActivity() {
    private val mBinding: ActivityTest1Binding by lazy {
        DataBindingUtil.setContentView<ActivityTest1Binding>(this, R.layout.activity_test1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    fun click0(view: View) {
        setResult(Activity.RESULT_OK, Intent().apply { putExtra("name", "like") })
        finish()
    }
}
