package com.like.common.sample.banner

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.like.common.util.GlideUtils
import com.like.common.R

class MyViewPagerAdapter(val context: Context, val list: List<BannerInfo>) : PagerAdapter() {
    private val layoutInflater = LayoutInflater.from(context)
    private val mGlideUtils = GlideUtils(context)

    override fun isViewFromObject(p0: View, p1: Any): Boolean = p0 == p1

    override fun getCount(): Int = list.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = layoutInflater.inflate(R.layout.item_banner, null)
        val iv = view.findViewById<ImageView>(R.id.iv)
        val info = list[position]
        mGlideUtils.display(info.imageUrl, iv)
        iv.setOnClickListener {
            Log.d("BannerPagerAdapter", info.toString())
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}