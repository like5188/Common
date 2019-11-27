package com.like.common.sample.banner

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.like.common.sample.R
import com.like.common.util.GlideUtils
import com.like.common.view.banner.BannerPagerAdapter

class MyBannerPagerAdapter(context: Context, private val list: List<BannerInfo>) : BannerPagerAdapter(list.size) {
    private val mLayoutInflater by lazy { LayoutInflater.from(context) }
    private val mGlideUtils by lazy { GlideUtils(context) }

    override fun onInstantiateItem(position: Int): View {
        return mLayoutInflater.inflate(R.layout.item_banner, null).apply {
            val iv = findViewById<ImageView>(R.id.iv)
            val info = list[position]
            mGlideUtils.display(info.imageUrl, iv)
            iv.setOnClickListener {
                Log.d("BannerPagerAdapter", info.toString())
            }
        }
    }

}