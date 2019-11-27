package com.like.common.view.banner.indicator

import android.content.Context
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.like.common.view.CircleTextView

/**
 * 数字指示器
 *
 * @param mContext
 * @param mDataCount    指示器的数量
 * @param mContainer    指示器的容器
 */
class NumberIndicator(
        private val mContext: Context,
        private val mDataCount: Int,
        private val mContainer: ViewGroup
) : ViewPager.OnPageChangeListener {
    private val mCircleTextView = CircleTextView(mContext)

    init {
        if (mDataCount > 0) {
            mContainer.removeAllViews()
            mContainer.addView(mCircleTextView)
        }
    }

    override fun onPageSelected(position: Int) {
        if (mDataCount <= 0) return
        mCircleTextView.text = "${position + 1}/$mDataCount"
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

}