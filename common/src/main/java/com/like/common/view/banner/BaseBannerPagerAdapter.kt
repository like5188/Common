package com.like.common.view.banner

import android.view.View
import android.view.ViewGroup

abstract class BaseBannerPagerAdapter<T>(list: List<T>) : androidx.viewpager.widget.PagerAdapter() {
    val mList = mutableListOf<T>().apply {
        when (list.size) {
            1 -> {
                addAll(list)
            }
            else -> {// 在数据的前后两端各添加一条数据。前端添加的是最后一条数据，尾端添加的是第一条数据。
                add(list.last())
                addAll(list)
                add(list.first())
            }
        }
    }

    final override fun getCount(): Int = mList.size

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
