package com.like.common.view.banner

import android.view.View
import android.view.ViewGroup

abstract class BannerPagerAdapter(dataCount: Int) : androidx.viewpager.widget.PagerAdapter() {
    /**
     * Adapter 中的实际数据数量
     * 当数据量大于1时，在数据的前后两端各添加一条数据。前端添加的是最后一条数据，尾端添加的是第一条数据。
     */
    private val mAdapterCount = if (dataCount > 1) {
        dataCount + 2
    } else {
        dataCount
    }

    final override fun getCount(): Int = mAdapterCount

    final override fun isViewFromObject(p0: View, p1: Any): Boolean = p0 == p1

    final override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val realPosition = getRealPosition(mAdapterCount, position)// 在原始数据列表中的位置
        val view = onInstantiateItem(realPosition)
        container.addView(view)
        return view
    }

    final override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    private fun getRealPosition(adapterCount: Int, position: Int): Int = when {
        adapterCount == 1 -> 0
        position == 0 -> adapterCount - 2 - 1
        else -> (position - 1) % (adapterCount - 2)
    }

    abstract fun onInstantiateItem(position: Int): View

}