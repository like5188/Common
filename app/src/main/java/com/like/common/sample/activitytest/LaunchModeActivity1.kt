package com.like.common.sample.activitytest

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityLaunchmode1Binding

/**
 * 查看activity栈信息用命令：adb shell dumpsys activity
 */
class LaunchModeActivity1 : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityLaunchmode1Binding>(this, R.layout.activity_launchmode1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    fun click0(view: View) {
        val intent = Intent(this@LaunchModeActivity1, LaunchModeActivity2::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        BaseApplication.sInstance.startActivity(intent)
        startActivity(intent)
    }
}
