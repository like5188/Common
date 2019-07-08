package com.like.common.sample.letterlistview

import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.like.common.ui.BaseActivity
import com.like.common.R
import com.like.common.databinding.ActivitySidebarViewBinding

class SidebarViewActivity : BaseActivity() {
    private val mBinding: ActivitySidebarViewBinding by lazy {
        DataBindingUtil.setContentView<ActivitySidebarViewBinding>(this, R.layout.activity_sidebar_view)
    }
    private val mHandler: Handler by lazy {
        Handler {
            mBinding.circleTextView.visibility = View.GONE
            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding

        val keys = mutableListOf<String>()
        keys.add(0, "全部")
        keys.add("A")
        keys.add("C")
        keys.add("D")
        keys.add("E")
        keys.add("G")
        keys.add("H")
        keys.add("X")
        keys.add("Y")
        keys.add("Z")
//        keys.add("A")
//        keys.add("C")
//        keys.add("D")
//        keys.add("E")
//        keys.add("G")
//        keys.add("H")
//        keys.add("X")
//        keys.add("Y")
//        keys.add("Z")
//        keys.add("A")
//        keys.add("C")
//        keys.add("D")
//        keys.add("E")
//        keys.add("G")
//        keys.add("H")
//        keys.add("X")
//        keys.add("Y")
//        keys.add("Z")
//        keys.add("A")
//        keys.add("C")
//        keys.add("D")
//        keys.add("E")
//        keys.add("G")
//        keys.add("H")
//        keys.add("X")
//        keys.add("Y")
//        keys.add("Z")
//        keys.add("1")
//        keys.add("2")
//        keys.add("3")
        mBinding.sideBar.init(
                Color.parseColor("#00a7ff"),
                Color.WHITE,
                Color.parseColor("#00a700"),
                Color.parseColor("#00000000"),
                Color.parseColor("#40000000"),
                0,
                14
        ) {
            mBinding.circleTextView.visibility = View.VISIBLE
            mBinding.circleTextView.text = it
            mHandler.removeCallbacksAndMessages(null)
            mHandler.sendEmptyMessageDelayed(0, 2000)
        }
        mBinding.sideBar.setDataAndShow(keys)
        mBinding.circleTextView.setBackgroundColor(Color.parseColor("#dedede"))
    }

}
