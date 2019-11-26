package com.like.common.sample.banner

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.like.common.sample.R
import com.like.common.util.GlideUtils
import com.like.common.view.banner.BaseBannerPagerAdapter

class BannerPagerAdapter(context: Context, list: List<BannerInfo>) : BaseBannerPagerAdapter<BannerInfo>(list) {
    private val layoutInflater = LayoutInflater.from(context)
    private val mGlideUtils = GlideUtils(context)

    override fun getView(position: Int): View {
        val view = layoutInflater.inflate(R.layout.item_banner, null)
        val iv = view.findViewById<ImageView>(R.id.iv)
        val info = mList[position]
        mGlideUtils.display(info.imageUrl, iv)
        iv.setOnClickListener {
            Log.d("BannerPagerAdapter", info.toString())
        }
        return view
    }

}
