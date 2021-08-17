package com.like.common.sample.anim

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityAnimBinding
import com.like.common.util.dp

/**
 * Activity 相关的测试
 */
class AnimActivity : AppCompatActivity() {
    companion object {
        private val TAG = AnimActivity::class.java.simpleName
    }

    private val mBinding: ActivityAnimBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_anim)
    }
    private var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    fun add(view: View) {
        mBinding.llContainer.addView(TextView(this).apply {
            text = "${i++}"
            setBackgroundColor(Color.GRAY)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = 10.dp
            }
            setPadding(10.dp, 10.dp, 10.dp, 10.dp)
            setOnClickListener {
                mBinding.llContainer.removeView(this)
            }
        })
    }

}
