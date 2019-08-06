package com.like.common.view.dragview.view

import android.content.Context
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * 对多点触控场景时, {@link ViewPager#onInterceptTouchEvent(MotionEvent)}中
 * pointerIndex = -1. 发生IllegalArgumentException: pointerIndex out of range 处理
 */
class DragViewPager : androidx.viewpager.widget.ViewPager {
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