package com.like.common.sample.timertextview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityTimerTextViewBinding
import com.like.common.view.TimerTextView

class TimerTextViewActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityTimerTextViewBinding>(this, R.layout.activity_timer_text_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.etPhone.doAfterTextChanged {
            mBinding.ttv.updateEnable()
        }
        mBinding.ttv.canEnable = {
            mBinding.etPhone.length() == 11 &&
                    (mBinding.ttv.text == resources.getString(R.string.ttv_onStart) ||
                            mBinding.ttv.text == resources.getString(R.string.ttv_onEnd))
        }
        mBinding.ttv.tickListener = object : TimerTextView.OnTickListener {
            override fun onStart(time: Long) {
            }

            override fun onTick(time: Long) {
                mBinding.ttv.text = resources.getString(
                    R.string.ttv_onTick,
                    (time / 1000).toString()
                )
            }

            override fun onEnd() {
                mBinding.ttv.text = resources.getString(R.string.ttv_onEnd)
            }
        }
        mBinding.ttv.setOnClickListener {
            mBinding.ttv.start(10000L)
        }
    }

}