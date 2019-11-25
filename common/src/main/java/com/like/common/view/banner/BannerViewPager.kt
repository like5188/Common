package com.like.common.view.banner

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import android.widget.Scroller

/**
 * 可以控制是否左右滑动的ViewPager[.setScrollable]，默认不能滑动
 */
class BannerViewPager(context: Context, attrs: AttributeSet?) : androidx.viewpager.widget.ViewPager(context, attrs) {
    private var isScrollable = false

    init {
        // 设置缓存，这样左右拖动即可看见后面的Fragment。
        this.offscreenPageLimit = 3
    }

    fun setScrollable(isScrollable: Boolean) {
        this.isScrollable = isScrollable
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return isScrollable && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return isScrollable && super.onInterceptTouchEvent(ev)
    }

    /**
     * 设置ViewPager切换速度
     *
     * @param duration 默认300毫秒
     */
    fun setScrollSpeed(duration: Int = 300) {
        try {
            val field = androidx.viewpager.widget.ViewPager::class.java.getDeclaredField("mScroller")
            field.isAccessible = true
            field.set(this, FixedSpeedScroller(context, AccelerateInterpolator(), duration))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 加速滚动的Scroller
     */
    class FixedSpeedScroller(context: Context, interpolator: Interpolator, val mDuration: Int)
        : Scroller(context.applicationContext, interpolator) {

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
            super.startScroll(startX, startY, dx, dy, mDuration)
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, mDuration)
        }
    }
}
