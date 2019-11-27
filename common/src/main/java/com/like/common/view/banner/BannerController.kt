package com.like.common.view.banner

import android.os.Handler
import androidx.viewpager.widget.ViewPager
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 通过 [BannerViewPager]、[BannerPagerAdapter] 控制 Banner 进行无限轮播
 * 原理：当数据量大于1时，在数据的前后两端各添加一条数据。前端添加的是最后一条数据，尾端添加的是第一条数据。
 */
class BannerController {
    /**
     * 是否正在自动循环播放
     */
    private val mIsAutoPlaying = AtomicBoolean(false)
    /**
     * ViewPager的当前位置
     */
    private var mCurPosition = 1
    /**
     * Adapter 中的实际数据数量
     */
    private var mAdapterCount = 0
    private var mViewPager: BannerViewPager? = null
    /**
     * 循环的时间间隔，毫秒。如果<=0，表示不循环播放。默认3000L
     */
    private var mCycleInterval: Long = 3000L

    private val mCycleHandler: Handler by lazy {
        Handler {
            if (mIsAutoPlaying.get() && mCycleInterval > 0 && mAdapterCount > 1) {
                mViewPager?.let {
                    mCurPosition = mCurPosition % (mAdapterCount - 1) + 1
                    if (mCurPosition == 1) {
                        it.setCurrentItem(mCurPosition, false)
                        mCycleHandler.sendEmptyMessage(0)
                    } else {
                        it.currentItem = mCurPosition
                        mCycleHandler.sendEmptyMessageDelayed(0, mCycleInterval)
                    }
                }
            }
            true
        }
    }

    private val mOnPageChangeListener = object : ViewPager.OnPageChangeListener {
        // position当前选择的是哪个页面。注意：如果mCount=1，那么默认会显示第0页，此时不会触发此方法，只会触发onPageScrolled方法。
        override fun onPageSelected(position: Int) {
            mCurPosition = position
        }

        // position表示目标位置，positionOffset表示偏移的百分比，positionOffsetPixels表示偏移的像素
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageScrollStateChanged(state: Int) {
            when (state) {
                ViewPager.SCROLL_STATE_IDLE -> {// 页面停止在了某页，有可能是手指滑动一页结束，有可能是自动滑动一页结束，开始自动播放。
                    mViewPager?.let {
                        if (mCurPosition == 0) {
                            it.setCurrentItem(mAdapterCount - 2, false)
                        } else if (mCurPosition == mAdapterCount - 1) {
                            it.setCurrentItem(1, false)
                        }
                        play()
                    }
                }
                ViewPager.SCROLL_STATE_DRAGGING -> {// 手指按下开始滑动，停止自动播放。
                    pause()
                }
                ViewPager.SCROLL_STATE_SETTLING -> {// 页面开始自动滑动
                }
            }
        }
    }

    fun setCycleInterval(interval: Long): BannerController {
        mCycleInterval = interval
        return this
    }

    /**
     * @param viewPager [BannerViewPager] 类型，它必须已经设置了 [BannerPagerAdapter]。
     */
    fun setViewPager(viewPager: BannerViewPager): BannerController {
        val adapter = viewPager.adapter ?: throw IllegalArgumentException("viewPager 没有设置 adapter")
        require(adapter is BannerPagerAdapter) { "viewPager 的 adapter 必须继承 com.like.common.view.banner.BannerPagerAdapter" }
        mViewPager = viewPager
        mAdapterCount = adapter.count
        if (mAdapterCount > 0) {
            viewPager.addOnPageChangeListener(mOnPageChangeListener)

            when (mAdapterCount) {
                1 -> { // 如果只有一个页面，就限制 ViewPager 不能手动滑动
                    viewPager.setScrollable(false)// 如果不设置，那么即使viewpager在只有一个页面时不能滑动，但是还是会触发onPageScrolled、onPageScrollStateChanged方法
//                mViewPager.currentItem = 0// 不用调用，默认会显示第0页
                }
                else -> {
                    viewPager.setScrollable(true)
                    // 因为页面变化，所以setCurrentItem方法能触发onPageSelected、onPageScrolled方法，
                    // 但是不能触发 onPageScrollStateChanged，因为setCurrentItem方法在第一次是没有滚动效果的，所以要单独启动自动播放
                    viewPager.currentItem = 1
                }
            }
        }
        return this
    }

    fun play() {
        if (mCycleInterval <= 0) return
        if (mAdapterCount <= 1) return
        if (mIsAutoPlaying.compareAndSet(false, true)) {
            mCycleHandler.removeCallbacksAndMessages(null)
            mCycleHandler.sendEmptyMessageDelayed(0, mCycleInterval)
        }
    }

    fun pause() {
        if (mIsAutoPlaying.compareAndSet(true, false)) {
            mCycleHandler.removeCallbacksAndMessages(null)
        }
    }

}
