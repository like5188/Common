package com.like.common.view.dragview.view.photo

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * 对多点触控场景时, {@link ViewPager#onInterceptTouchEvent(MotionEvent)}中
 * pointerIndex = -1. 发生IllegalArgumentException: pointerIndex out of range 处理
 */
class CustomPhotoViewPager : ViewPager {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        try {
            return super.onInterceptTouchEvent(ev)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
        return false
    }
}