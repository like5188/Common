package com.like.common.sample.timertextview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityTimerTextViewBinding
import com.like.common.util.validator.ValidatorFactory

class TimerTextViewActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityTimerTextViewBinding>(this, R.layout.activity_timer_text_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.etPhone.doAfterTextChanged {
            mBinding.ttv.updateEnable()
        }
        with(mBinding.ttv) {
            val phoneValidator = ValidatorFactory.createPhoneValidator()
            canEnable = {
                phoneValidator.validate(mBinding.etPhone.text.toString().trim()) &&
                        (mBinding.ttv.text == resources.getString(R.string.ttv_onStart) ||
                                mBinding.ttv.text == resources.getString(R.string.ttv_onEnd))
            }
            onStart = {
                mBinding.ttv.text = resources.getString(R.string.ttv_onStart)
            }
            onTick = {
                mBinding.ttv.text = resources.getString(
                    R.string.ttv_onTick,
                    (it / 1000).toString()
                )
            }
            onEnd = {
                mBinding.ttv.text = resources.getString(R.string.ttv_onEnd)
            }
            setOnClickListener {
                mBinding.ttv.start(10000L)
            }
        }
    }

}