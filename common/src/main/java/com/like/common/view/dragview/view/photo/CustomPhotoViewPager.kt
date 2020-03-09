package com.like.common.view.dragview.view.photo

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * 对多点触控场景时, {@link ViewPager#onInterceptTouchEvent(MotionEvent)}中
 * pointerIndex = -1. 发生IllegalArgumentException: pointerIndex out of range 处理
 */
class CustomPhotoViewPager : ViewPager {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.e("tag", "ViewPager dispatchTouchEvent action=${ev.action}")
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        Log.e("tag", "ViewPager onTouchEvent")
        return super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.e("tag", "ViewPager onInterceptTouchEvent")
        try {
            return super.onInterceptTouchEvent(ev)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
        return false
    }
}