package com.like.common.sample.flexbox

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import com.like.common.ui.BaseActivity
import com.like.common.databinding.ActivityFlexboxBinding

class FlexBoxActivity : BaseActivity() {

    private val mBinding: ActivityFlexboxBinding by lazy {
        DataBindingUtil.setContentView<ActivityFlexboxBinding>(this, com.like.common.R.layout.activity_flexbox)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (i in (1..200)) {
            // 通过代码向FlexboxLayout添加View
            val textView = TextView(this)
            textView.text = "Test Label $i"
            textView.gravity = Gravity.CENTER
            textView.setPadding(30, 0, 30, 0)
            textView.setTextColor(resources.getColor(com.like.common.R.color.black))
            // 通过FlexboxLayout.LayoutParams 设置子元素支持的属性
            val params = textView.layoutParams
            if (params is FlexboxLayout.LayoutParams) {
                params.flexBasisPercent = 0.5f
            }
            mBinding.flexboxLayout.addView(textView)
        }
    }

}
