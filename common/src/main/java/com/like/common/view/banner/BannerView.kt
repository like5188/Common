package com.like.common.view.banner

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewpager.widget.ViewPager
import com.like.common.util.onPreDrawListener

/**
 * 在xml布局文件中直接使用。然后调用init()初始化，最后调用setAdapterAndPlay()启动循环
 */
class BannerView(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private var viewPager: BannerViewPager? = null
    private var indicatorContainer: LinearLayout? = null
    /**
     * 循环的时间间隔。如果<=0，表示不循环播放
     */
    private var cycleInterval: Long = 3000L
    /**
     * banner的高宽比
     */
    private var heightWidthRatio: Float = 1f
    /**
     * 正常状态的指示器图片id
     */
    private var normalIndicatorResId: Int = -1
    /**
     * 选中状态的指示器图片id。每个位置可以设置不同的图片。
     */
    private var selectedIndicatorResIds: List<Int>? = null
    /**
     * 指示器之间的间隔，默认10dp
     */
    private var indicatorPadding: Int = 10
    /**
     * 指示器控制器
     */
    private var mIndicatorViewControl: IndicatorViewControl? = null
    /**
     * ViewPager的当前位置
     */
    private var mCurPosition = 100000
    /**
     * 是否正在自动循环播放
     */
    private var isAutoPlaying: Boolean = false

    private val mCycleHandler: Handler by lazy {
        Handler {
            if (isAutoPlaying) {
                // Logger.d("handleMessage mCurPosition=" + mCurPosition);
                mCurPosition++
                viewPager?.setCurrentItem(mCurPosition, true)
                mCycleHandler.sendEmptyMessageDelayed(0, cycleInterval)
            }
            true
        }
    }

    init {
        if (context is LifecycleOwner) {
            (context as LifecycleOwner).lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroy() {
                    destroy()
                }
            })
        }
    }

    /**
     * 初始化参数
     * @param heightWidthRatio          banner的高宽比
     * @param normalIndicatorResId      正常状态的指示器图片id
     * @param selectedIndicatorResIds   选中状态的指示器图片id。每个位置可以设置不同的图片。
     * @param viewPagerId               BannerViewPager的id
     * @param indicatorContainerId      LinearLayout类型的指示器容器的id。默认为-1，即没有小圆点
     * @param indicatorPadding          指示器之间的间隔，单位dp，默认10dp
     * @param cycleInterval             循环时间间隔，毫秒，默认3000毫秒。如果<=0，表示不循环播放
     */
    @JvmOverloads
    fun init(
            heightWidthRatio: Float,
            @DrawableRes normalIndicatorResId: Int,
            selectedIndicatorResIds: List<Int>,
            @IdRes viewPagerId: Int,
            @IdRes indicatorContainerId: Int = -1,
            indicatorPadding: Int = 10,
            cycleInterval: Long = 3000
    ): BannerView {
        this.viewPager = findViewById(viewPagerId) ?: throw IllegalArgumentException("viewPagerId有误")
        this.indicatorContainer = findViewById(indicatorContainerId)
        this.heightWidthRatio = heightWidthRatio
        this.normalIndicatorResId = normalIndicatorResId
        this.selectedIndicatorResIds = selectedIndicatorResIds
        this.indicatorPadding = indicatorPadding
        this.cycleInterval = cycleInterval
        return this
    }

    fun setAdapterAndPlay(adapter: BaseBannerPagerAdapter<*>) {
        if (adapter.mRealCount <= 0) {
            throw IllegalArgumentException("BaseBannerPagerAdapter的数据不能为空")
        }
        viewPager?.onPreDrawListener {
            it.layoutParams.height = (it.width * heightWidthRatio).toInt()
            it.adapter = adapter
            // 取余处理，避免默认值不能被整除
            mCurPosition -= mCurPosition % adapter.mRealCount
            it.currentItem = mCurPosition

            it.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                // position当前选择的是哪个页面
                override fun onPageSelected(position: Int) {
                    mCurPosition = position
                    // 设置指示器
                    mIndicatorViewControl?.select(mCurPosition % adapter.mRealCount)
                }

                // position表示目标位置，positionOffset表示偏移的百分比，positionOffsetPixels表示偏移的像素
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageScrollStateChanged(state: Int) {
                    when (state) {
                        1 -> {// 开始滑动
                            if (isAutoPlaying)
                                pausePlay()
                        }
                        2 -> {// 手指松开了页面自动滑动
                        }
                        0 -> {// 停止在某页
                            if (!isAutoPlaying)
                                continuePlay()
                        }
                    }
                }
            })

            play(adapter)
        }
    }

    private fun play(adapter: BaseBannerPagerAdapter<*>) {
        when (adapter.mRealCount) {
            1 -> {
                viewPager!!.setScrollable(false)
                pausePlay()
            }
            else -> {
                // 初始化指示器视图
                if (indicatorContainer != null) {
                    mIndicatorViewControl = IndicatorViewControl(context, indicatorContainer, adapter.mRealCount, normalIndicatorResId, selectedIndicatorResIds, indicatorPadding)
                }
                viewPager!!.setScrollable(true)
                if (cycleInterval > 0) {
                    // 开始轮播
                    startPlay()
                }
            }
        }
    }

    private fun startPlay() {
        isAutoPlaying = true
        mCycleHandler.removeCallbacksAndMessages(null)
        mCycleHandler.sendEmptyMessageDelayed(0, cycleInterval)
    }

    fun pausePlay() {
        isAutoPlaying = false
        mCycleHandler.removeCallbacksAndMessages(null)
    }

    fun continuePlay() {
        isAutoPlaying = true
        mCycleHandler.removeCallbacksAndMessages(null)
        mCycleHandler.sendEmptyMessageDelayed(0, cycleInterval)
    }

    fun destroy() {
        mCycleHandler.removeCallbacksAndMessages(null)
        mIndicatorViewControl?.destroy()
    }

}
