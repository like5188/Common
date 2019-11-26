package com.like.common.view.banner

import android.view.View
import android.view.ViewGroup

/**
 * 无限轮播 ViewPager 的辅助 PagerAdapter
 * 原理：当数据量大于1时，在数据的前后两端各添加一条数据。前端添加的是最后一条数据，尾端添加的是第一条数据。
 */
abstract class BannerViewPagerAdapter(private val mDataCount: Int) : androidx.viewpager.widget.PagerAdapter() {

    final override fun getCount(): Int = if (mDataCount > 1) {
        mDataCount + 2
    } else {
        mDataCount
    }

    final override fun isViewFromObject(p0: View, p1: Any): Boolean = p0 == p1

    final override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = getView(getRealPosition(position))
        container.addView(view)
        return view
    }

    final override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    /**
     * 获取页面视图。
     *
     * @param position  在原始数据集合中的真实位置，不是在处理过的集合中的位置。
     */
    abstract fun getView(position: Int): View

    private fun getRealPosition(position: Int): Int = when {
        count == 1 -> 0
        position == 0 -> count - 2 - 1
        else -> (position - 1) % (count - 2)
    }
}
