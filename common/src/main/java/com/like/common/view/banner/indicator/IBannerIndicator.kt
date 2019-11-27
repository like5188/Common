package com.like.common.view.banner.indicator

import androidx.viewpager.widget.ViewPager
import com.like.common.view.banner.BannerPagerAdapter
import com.like.common.view.banner.BannerViewPager

/**
 * Banner 的指示器基类。
 * 使用方式：调用 [setViewPager] 方法，和 [com.like.common.view.banner.BannerController] 设置同一个 [com.like.common.view.banner.BannerViewPager] 即可。
 */
interface IBannerIndicator {

    /**
     * @param viewPager [BannerViewPager] 类型，它必须已经设置了 [BannerPagerAdapter]。
     */
    fun setViewPager(viewPager: BannerViewPager) {
        val adapter = viewPager.adapter ?: throw IllegalArgumentException("ViewPager does not have adapter instance.")
        require(adapter is BannerPagerAdapter) { "adapter of viewPager must be com.like.common.view.banner.BannerPagerAdapter" }
        val adapterCount = adapter.count
        if (adapterCount > 0) {
            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                    this@IBannerIndicator.onPageScrollStateChanged(state)
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    if (adapterCount == 1) {
                        // 如果只有一个页面，那么setCurrentItem(0)无法触发onPageSelected方法，因为页面没有变化，只能触发onPageScrolled()方法。
                        // 所以要单独处理来触发指示器的显示效果，因为一般指示器的操作都是在onPageSelected方法中的。
                        this@IBannerIndicator.onPageSelected(getRealPosition(adapterCount, position))
                    } else {
                        this@IBannerIndicator.onPageScrolled(getRealPosition(adapterCount, position), positionOffset, positionOffsetPixels)
                    }
                }

                override fun onPageSelected(position: Int) {
                    this@IBannerIndicator.onPageSelected(getRealPosition(adapterCount, position))
                }
            })
        }
    }

    fun onPageScrollStateChanged(state: Int) {}

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    fun onPageSelected(position: Int) {}

    private fun getRealPosition(adapterCount: Int, position: Int): Int = when {
        adapterCount == 1 -> 0
        position == 0 -> adapterCount - 2 - 1
        else -> (position - 1) % (adapterCount - 2)
    }

}