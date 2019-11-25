package com.like.common.view.banner

import android.view.View
import android.view.ViewGroup

abstract class BaseBannerPagerAdapter<T>(val list: List<T>) : androidx.viewpager.widget.PagerAdapter() {
    val mRealCount = list.size

    override fun getCount(): Int = Int.MAX_VALUE

    override fun isViewFromObject(p0: View, p1: Any): Boolean = p0 == p1

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = getView(container, position)
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    abstract fun getView(container: ViewGroup?, position: Int): View
}
