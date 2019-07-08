package com.like.common.view.banner

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

abstract class BaseBannerPagerAdapter<T>(val context: Context, val list: List<T>) : PagerAdapter() {
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
