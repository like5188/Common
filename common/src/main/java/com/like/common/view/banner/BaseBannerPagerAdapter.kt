package com.like.common.view.banner

import android.view.View
import android.view.ViewGroup

/**
 * 持有[mRealCount]，并且[getCount]方法返回[Int.MAX_VALUE]
 */
abstract class BaseBannerPagerAdapter<T>(val list: List<T>) : androidx.viewpager.widget.PagerAdapter() {
    val mRealCount = list.size

    final override fun getCount(): Int = Int.MAX_VALUE

    final override fun isViewFromObject(p0: View, p1: Any): Boolean = p0 == p1

    final override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = getView(position)
        container.addView(view)
        return view
    }

    final override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    abstract fun getView(position: Int): View
}
