package com.like.common.view.banner

import android.os.Handler
import androidx.viewpager.widget.ViewPager
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 控制 [BannerViewPager] 进行自动无限循环
 * 原理：在数据的前后两端各添加一条数据。前端添加的是最后一条数据，尾端添加的是第一条数据。
 *
 * @param mIndicator        在 ViewPager 的 OnPageChangeListener 中会回调此属性的相关方法。用于使用者控制指示器
 * @param mCycleInterval    循环的时间间隔，毫秒。如果<=0，表示不循环播放
 */
class BannerController(
        private val mViewPager: BannerViewPager,
        private val mIndicator: ViewPager.OnPageChangeListener? = null,
        private val mCycleInterval: Long = 3000L
) {
    /**
     * ViewPager的当前位置
     */
    private var mCurPosition = 1
    /**
     * 是否正在自动循环播放
     */
    private var mIsAutoPlaying = AtomicBoolean(false)

    private val mCount = mViewPager.adapter?.count ?: 0

    private val mCycleHandler: Handler by lazy {
        Handler {
            if (mIsAutoPlaying.get()) {
                mCurPosition = mCurPosition % (mCount - 1) + 1
                if (mCurPosition == 1) {
                    mViewPager.setCurrentItem(mCurPosition, false)
                    mCycleHandler.sendEmptyMessage(0)
                } else {
                    mViewPager.currentItem = mCurPosition
                    mCycleHandler.sendEmptyMessageDelayed(0, mCycleInterval)
                }
            }
            true
        }
    }

    init {
        val viewPagerAdapter = mViewPager.adapter ?: throw IllegalArgumentException("mViewPager 的 adapter 不能为 null")
        require(viewPagerAdapter is BannerViewPagerAdapter<*>) { "mViewPager 的 adapter 必须是 com.like.common.view.banner.BannerViewPagerAdapter" }
        require(mCount > 0) { "数据不能为空" }

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            // position当前选择的是哪个页面。注意：如果mCount=1，那么默认会显示第0页，此时不会触发此方法，只会触发onPageScrolled方法。
            override fun onPageSelected(position: Int) {
                mCurPosition = position
                mIndicator?.onPageSelected(getRealPosition(position))
            }

            // position表示目标位置，positionOffset表示偏移的百分比，positionOffsetPixels表示偏移的像素
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                mIndicator?.onPageScrolled(getRealPosition(position), positionOffset, positionOffsetPixels)
            }

            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager.SCROLL_STATE_IDLE -> {// 页面停止在了某页，有可能是手指滑动一页结束，有可能是自动滑动一页结束，开始自动播放。
                        if (mCurPosition == 0) {
                            mViewPager.setCurrentItem(mCount - 2, false)
                        } else if (mCurPosition == mCount - 1) {
                            mViewPager.setCurrentItem(1, false)
                        }
                        play()
                    }
                    ViewPager.SCROLL_STATE_DRAGGING -> {// 手指按下开始滑动，停止自动播放。
                        pause()
                    }
                    ViewPager.SCROLL_STATE_SETTLING -> {// 页面开始自动滑动
                    }
                }
                mIndicator?.onPageScrollStateChanged(state)
            }
        })

        when (mCount) {
            1 -> { // 如果只有一个页面，就限制 ViewPager 不能手动滑动
                mViewPager.setScrollable(false)// 如果不设置，那么即使viewpager在只有一个页面时不能滑动，但是还是会触发onPageScrolled、onPageScrollStateChanged方法
//                mViewPager.currentItem = 0// 不用调用，默认会显示第0页
                mIndicator?.onPageSelected(0)// 但是setCurrentItem(0)无法触发onPageSelected方法，因为页面没有变化，所以要单独调用mIndicator?.onPageSelected(0)来触发指示器的显示效果。
            }
            else -> {
                mViewPager.setScrollable(true)
                // 因为页面变化，所以setCurrentItem方法能触发onPageSelected、onPageScrolled方法，
                // 但是不能触发 onPageScrollStateChanged，因为setCurrentItem方法在第一次是没有滚动效果的，所以要单独启动自动播放
                mViewPager.currentItem = 1
            }
        }
    }

    private fun getRealPosition(position: Int): Int = when {
        mCount == 1 -> 0
        position == 0 -> mCount - 2 - 1
        else -> (position - 1) % (mCount - 2)
    }

    fun play() {
        if (mCycleInterval <= 0) return
        if (mCount <= 1) return
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
