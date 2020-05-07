package com.like.common.util

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView

/**
 * 提供了滚动状态监听的HorizontalScrollView
 */
class StateHorizontalScrollView(context: Context, attrs: AttributeSet) : HorizontalScrollView(context, attrs) {
    companion object {
        private const val DELAY_MILLIS = 100L
        /**
         * 空闲
         */
        const val SCROLL_STATE_IDLE = 0
        /**
         * 拖动
         */
        const val SCROLL_STATE_DRAGGING = 1
        /**
         * 惯性滑动
         */
        const val SCROLL_STATE_SETTLING = 2
    }

    /**
     * 上次滑动的时间
     */
    private var lastScrollTime: Long = -1L
    private var isActionUp = true
    private var mScrollListeners = mutableListOf<OnScrollListener>()

    private val scrollerTask: Runnable = object : Runnable {
        override fun run() {
            if (isActionUp && (System.currentTimeMillis() - lastScrollTime) > 100L) {
                lastScrollTime = -1L
                mScrollListeners.forEach {
                    it.onScrollStateChanged(this@StateHorizontalScrollView, SCROLL_STATE_IDLE)
                }
            } else {
                postDelayed(this, DELAY_MILLIS)
            }
        }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (lastScrollTime == -1L) {
            mScrollListeners.forEach {
                it.onScrollStateChanged(this, SCROLL_STATE_DRAGGING)
            }
            postDelayed(scrollerTask, DELAY_MILLIS)
        }
        // 更新ScrollView的滑动时间
        lastScrollTime = System.currentTimeMillis()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                isActionUp = false
            }
            MotionEvent.ACTION_UP -> {
                if (lastScrollTime != -1L) {
                    mScrollListeners.forEach {
                        it.onScrollStateChanged(this, SCROLL_STATE_SETTLING)
                    }
                }
                isActionUp = true
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun addOnScrollListener(listener: OnScrollListener) {
        mScrollListeners.add(listener)
    }

    fun removeOnScrollListener(listener: OnScrollListener) {
        mScrollListeners.remove(listener)
    }

    fun clearOnScrollListeners() {
        mScrollListeners.clear()
    }

    interface OnScrollListener {
        /**
         * Callback method to be invoked when HorizontalScrollViewForExcelPanel's scroll state changes.
         *
         * @param newState     The updated scroll state. One of [.SCROLL_STATE_IDLE], [.SCROLL_STATE_DRAGGING] or [.SCROLL_STATE_SETTLING].
         */
        fun onScrollStateChanged(hsv: StateHorizontalScrollView, newState: Int)
    }
}