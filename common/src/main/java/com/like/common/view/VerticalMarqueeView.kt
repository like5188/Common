package com.like.common.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.IdRes
import com.like.common.util.onPreDrawListener

/**
 * 垂直向上跑马灯效果
 *
 * 在xml布局文件中直接使用。然后调用initParamsAndPlay()启动
 */
class VerticalMarqueeView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    /**
     * 信息停留的时间
     */
    private var mCycleInterval = 2000L
    private var mCurPosition = 0
    private var mHeight = 0f
    private val mAnimatorSet: AnimatorSet by lazy {
        AnimatorSet().apply {
            duration = 2000L
        }
    }
    private var mOnPageChangeListener: ((Int) -> Unit)? = null
    private val mCycleHandler = Handler {
        mAnimatorSet.start()
        true
    }

    /**
     * 初始化参数
     *
     * @param dataCount     数据条数
     * @param itemLayoutId  VerticalMarqueeView布局里面的需要滚动的布局id
     */
    fun init(dataCount: Int, @IdRes itemLayoutId: Int) {
        val scrollView: View = findViewById(itemLayoutId) ?: throw IllegalArgumentException("itemLayoutId有误")
        if (dataCount <= 1) {
            throw IllegalArgumentException("mDataCount 必须大于1才能滚动")
        }
        scrollView.onPreDrawListener {
            mHeight = it.measuredHeight.toFloat()
            mOnPageChangeListener?.invoke(mCurPosition)
            mAnimatorSet.addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator) {
                    mCurPosition++
                    if (mCurPosition >= dataCount) {
                        mCurPosition = 0
                    }
                    mOnPageChangeListener?.invoke(mCurPosition)
                    play()
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
            val inAnimator = ObjectAnimator.ofFloat(this, "translationY", mHeight, 0f)
            val outAnimator = ObjectAnimator.ofFloat(this, "translationY", 0f, -mHeight)
            mAnimatorSet.playSequentially(outAnimator, inAnimator)
            play()
        }
    }

    fun play() {
        mCycleHandler.sendEmptyMessageDelayed(0, mCycleInterval)
    }

    fun pause() {
        mCycleHandler.removeCallbacksAndMessages(null)
        mAnimatorSet.pause()
    }

    /**
     * 滚动导致数据改变的监听，用于渲染数据
     */
    fun setOnPageChangeListener(listener: (Int) -> Unit) {
        mOnPageChangeListener = listener
    }

    /**
     * 设置动画时间，毫秒，默认200毫秒
     */
    fun setAnimationDuration(animationDuration: Long) {
        mAnimatorSet.duration = animationDuration
    }

    /**
     * 设置信息停留的时间，毫秒，默认2000毫秒
     */
    fun setCycleInterval(interval: Long) {
        if (interval <= 0) {
            throw IllegalArgumentException("interval 必须大于0")
        }
        mCycleInterval = interval
    }

    fun getPosition(): Int = mCurPosition

}