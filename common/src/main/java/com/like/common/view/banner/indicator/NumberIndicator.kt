package com.like.common.view.banner.indicator

import android.content.Context
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.like.common.view.CircleTextView

/**
 * 数字指示器
 *
 * @param mContext
 * @param mCount        指示器的数量
 * @param mContainer    指示器的容器
 */
class NumberIndicator(
        private val mContext: Context,
        private val mCount: Int,
        private val mContainer: ViewGroup
) : ViewPager.OnPageChangeListener {
    private val mCircleTextView = CircleTextView(mContext)

    init {
        require(mCount > 0) { "mCount 必须大于0" }

        mContainer.removeAllViews()
        mContainer.addView(mCircleTextView)
    }

    override fun onPageSelected(position: Int) {
        mCircleTextView.text = "${position + 1}/$mCount"
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

}