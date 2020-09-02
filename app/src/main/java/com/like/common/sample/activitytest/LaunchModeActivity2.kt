package com.like.common.sample.activitytest

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityLaunchmode2Binding

class LaunchModeActivity2 : AppCompatActivity() {
    private val mBinding: ActivityLaunchmode2Binding by lazy {
        DataBindingUtil.setContentView<ActivityLaunchmode2Binding>(this, R.layout.activity_launchmode2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    fun click0(view: View) {
        startActivity(Intent(this, LaunchModeActivity3::class.java))
    }
}
