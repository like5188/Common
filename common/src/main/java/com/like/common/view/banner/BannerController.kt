package com.like.common.view.banner

import android.os.Handler
import android.util.Log
import androidx.viewpager.widget.ViewPager

/**
 * 控制 [BannerViewPager] 进行自动无限循环
 *
 * @param mIndicatorController  指示器控制器
 * @param mCycleInterval        循环的时间间隔，毫秒。如果<=0，表示不循环播放
 */
class BannerController(
        private val mViewPager: BannerViewPager,
        private val mIndicatorController: IIndicatorController? = null,
        private val mCycleInterval: Long = 3000L
) {
    /**
     * ViewPager的当前位置
     */
    private var mCurPosition = Int.MAX_VALUE / 2
    /**
     * 是否正在自动循环播放
     */
    private var mIsAutoPlaying: Boolean = false

    private val mCycleHandler: Handler by lazy {
        Handler {
            if (mIsAutoPlaying) {
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
        mCurPosition -= mCurPosition % realCount// 取余处理，避免默认值不能被 realCount 整除
        mViewPager.currentItem = mCurPosition

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            // position当前选择的是哪个页面
            override fun onPageSelected(position: Int) {
                mCurPosition = position
                val realPosition = mCurPosition % realCount
                // 设置指示器
                mIndicatorController?.select(realPosition)
                Log.i("BannerController", "onPageSelected position=$position")
            }

            // position表示目标位置，positionOffset表示偏移的百分比，positionOffsetPixels表示偏移的像素
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                Log.v("BannerController", "onPageScrolled position=$position positionOffset=$positionOffset positionOffsetPixels=$positionOffsetPixels")
            }

            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    0 -> {// 手指停止在某页
                        if (!mIsAutoPlaying)
                            play()
                    }
                    1 -> {// 手指开始滑动
                        if (mIsAutoPlaying)
                            pause()
                    }
                    2 -> {// 手指松开了页面自动滑动
                    }
                }
                Log.d("BannerController", "onPageScrollStateChanged state=$state")
            }
        })

        when (realCount) {
            1 -> {
                mViewPager.setScrollable(false)
            }
            else -> {
                mViewPager.setScrollable(true)
                if (mCycleInterval > 0) {
                    play()
                }
            }
        }
    }

    fun play() {
        if (mIsAutoPlaying) {
            return
        }
        mIsAutoPlaying = true
        mCycleHandler.removeCallbacksAndMessages(null)
        mCycleHandler.sendEmptyMessageDelayed(0, mCycleInterval)
    }

    fun pause() {
        if (!mIsAutoPlaying) {
            return
        }
        mIsAutoPlaying = false
        mCycleHandler.removeCallbacksAndMessages(null)
    }

}
