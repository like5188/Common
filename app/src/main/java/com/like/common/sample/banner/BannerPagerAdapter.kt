package com.like.common.sample.banner

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.like.common.util.GlideUtils
import com.like.common.view.banner.BaseBannerPagerAdapter
import com.like.common.sample.R

class BannerPagerAdapter(context: Context, list: List<BannerInfo>) : BaseBannerPagerAdapter<BannerInfo>(context, list) {
    private val layoutInflater = LayoutInflater.from(context)
    private val mGlideUtils = GlideUtils(context)

    override fun getView(container: ViewGroup?, position: Int): View {
        val view = layoutInflater.inflate(R.layout.item_banner, null)
        val iv = view.findViewById<ImageView>(R.id.iv)
        val dataPosition = position % list.size
        val info = list[dataPosition]
        mGlideUtils.display(info.imageUrl, iv)
        iv.setOnClickListener {
            Log.d("BannerPagerAdapter", info.toString())
        }
        return view
    }

}
