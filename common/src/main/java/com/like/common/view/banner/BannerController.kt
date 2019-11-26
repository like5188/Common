package com.like.common.view.banner

import android.os.Handler
import androidx.viewpager.widget.ViewPager
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 控制 [BannerViewPager] 进行自动无限循环
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
    private var mCurPosition = Int.MAX_VALUE / 2
    /**
     * 是否正在自动循环播放
     */
    private var mIsAutoPlaying = AtomicBoolean(false)

    private val mCycleHandler: Handler by lazy {
        Handler {
            if (mIsAutoPlaying.get()) {
                mCurPosition++
                mViewPager.setCurrentItem(mCurPosition, true)
                mCycleHandler.sendEmptyMessageDelayed(0, mCycleInterval)
            }
            true
        }
    }

    init {
        val viewPagerAdapter = mViewPager.adapter ?: throw IllegalArgumentException("adapter of mViewPager can not be null")
        require(viewPagerAdapter is BaseBannerPagerAdapter<*>) { "adapter of mViewPager must be com.like.common.view.banner.BaseBannerPagerAdapter" }

        val realCount = viewPagerAdapter.mRealCount

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            // position当前选择的是哪个页面
            override fun onPageSelected(position: Int) {
                mCurPosition = position
                val realPosition = position % realCount
                // 设置指示器
                mIndicator?.onPageSelected(realPosition)
            }

            // position表示目标位置，positionOffset表示偏移的百分比，positionOffsetPixels表示偏移的像素
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                val realPosition = position % realCount
                mIndicator?.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
            }

            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager.SCROLL_STATE_IDLE -> {// 页面停止在了某页，有可能是手指滑动一页结束，有可能是自动滑动一页结束，开始自动播放。
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

        mCurPosition -= mCurPosition % realCount// 取余处理，避免默认值不能被 realCount 整除
        mViewPager.currentItem = mCurPosition// 这个方法不能触发 onPageScrollStateChanged，所以要单独启动自动播放

        when (realCount) {
            1 -> { // 如果只有一个页面，就限制 ViewPager 不能手动滑动
                mViewPager.setScrollable(false)
            }
            else -> {
                mViewPager.setScrollable(true)
            }
        }
    }

    fun play() {
        if (mCycleInterval <= 0) return
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
