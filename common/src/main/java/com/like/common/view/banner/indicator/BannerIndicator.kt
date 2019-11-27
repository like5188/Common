package com.like.common.view.banner.indicator

import androidx.viewpager.widget.ViewPager
import com.like.common.view.banner.BannerPagerAdapter

open class BannerIndicator {
    private var mAdapterCount = 0
    private val mOnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
            this@BannerIndicator.onPageScrollStateChanged(state)
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            if (mAdapterCount == 1) {
                // 如果只有一个页面，那么setCurrentItem(0)无法触发onPageSelected方法，因为页面没有变化，只能触发onPageScrolled()方法。
                // 所以要单独处理来触发指示器的显示效果。
                this@BannerIndicator.onPageSelected(getRealPosition(position))
            } else {
                this@BannerIndicator.onPageScrolled(getRealPosition(position), positionOffset, positionOffsetPixels)
            }
        }

        override fun onPageSelected(position: Int) {
            this@BannerIndicator.onPageSelected(getRealPosition(position))
        }
    }

    fun setViewPager(viewPager: ViewPager) {
        val adapter = viewPager.adapter ?: throw IllegalArgumentException("viewPager 没有设置 adapter")
        require(adapter is BannerPagerAdapter) { "viewPager 的 adapter 必须继承 com.like.common.view.banner.BannerPagerAdapter" }
        mAdapterCount = adapter.count
        if (mAdapterCount > 0) {
            viewPager.addOnPageChangeListener(mOnPageChangeListener)
        }
    }

    protected open fun onPageScrollStateChanged(state: Int) {}

    protected open fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    protected open fun onPageSelected(position: Int) {}

    private fun getRealPosition(position: Int): Int = when {
        mAdapterCount == 1 -> 0
        position == 0 -> mAdapterCount - 2 - 1
        else -> (position - 1) % (mAdapterCount - 2)
    }

}