package com.like.common.sample.activitytest

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityLaunchmode2Binding

class LaunchModeActivity2 : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityLaunchmode2Binding>(this, R.layout.activity_launchmode2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    fun click0(view: View) {
        val intent = Intent(this@LaunchModeActivity2, LaunchModeActivity3::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        BaseApplication.sInstance.startActivity(intent)
        startActivity(intent)
    }
}
