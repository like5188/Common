package com.like.common.sample.activitytest

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityLaunchmode3Binding

class LaunchModeActivity3 : AppCompatActivity() {
    private val mBinding: ActivityLaunchmode3Binding by lazy {
        DataBindingUtil.setContentView<ActivityLaunchmode3Binding>(this, R.layout.activity_launchmode3)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    fun click0(view: View) {
        startActivity(Intent(this, LaunchModeActivity1::class.java))
    }
}
