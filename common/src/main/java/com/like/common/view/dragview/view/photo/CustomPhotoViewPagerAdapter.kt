package com.like.common.view.dragview.view.photo

import android.view.View
import android.view.ViewGroup
import com.like.common.view.dragview.view.photo.CustomPhotoView

class CustomPhotoViewPagerAdapter(private val mViews: List<CustomPhotoView>) : androidx.viewpager.widget.PagerAdapter() {
    override fun isViewFromObject(p0: View, p1: Any): Boolean = p0 == p1

    override fun getCount(): Int = mViews.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(mViews[position])
        return mViews[position]
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(mViews[position])
    }
}