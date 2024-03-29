package com.like.common.sample.timertextview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityTimerTextViewBinding

class TimerTextViewActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityTimerTextViewBinding>(this, R.layout.activity_timer_text_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.ttv.apply {
            init(tvPhone = mBinding.etPhone, length = 10000L, step = 1000L)
            setOnClickListener {
                start()
            }
        }
    }

}