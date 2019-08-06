package com.like.common.view

import android.animation.Animator
import android.animation.ObjectAnimator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import android.content.Context
import android.os.Handler
import androidx.annotation.IdRes
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
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
    private var mCycleInterval = 0L
    /**
     * 动画时间
     */
    private var mAnimationDuration = 0L
    private var mCurPosition = 0
    private var mHeight = 0f
    private var mOutAnimator: ObjectAnimator? = null
    private var mInAnimator: ObjectAnimator? = null

    private val mCycleHandler = Handler(Handler.Callback {
        mOutAnimator!!.start()
        true
    })

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
     * * 初始化参数，并且开始轮播
     *
     * @param list
     * @param itemLayoutId          VerticalMarqueeView布局里面的需要滚动的布局id
     * @param onDataChanged         滚动导致数据改变的监听，用于渲染数据
     * @param cycleInterval         信息停留的时间，毫秒，默认2000毫秒
     * @param animationDuration     动画时间，毫秒，默认200毫秒
     */
    fun <T> initParamsAndPlay(
            list: List<T>,
            @IdRes itemLayoutId: Int,
            onDataChanged: (T) -> Unit,
            cycleInterval: Long = 2000,
            animationDuration: Long = 200
    ) {
        if (list.isEmpty()) {
            throw IllegalArgumentException("list无效")
        }

        if (cycleInterval <= 0) {
            throw IllegalArgumentException("cycleInterval无效")
        }

        val scrollView: View = findViewById(itemLayoutId)
                ?: throw IllegalArgumentException("itemLayoutId有误")

        mCycleInterval = cycleInterval
        mAnimationDuration = animationDuration

        scrollView.onPreDrawListener {
            mHeight = it.measuredHeight.toFloat()
            mCycleHandler.removeCallbacksAndMessages(null)
            if (mInAnimator == null)
                initInAnimator(it)
            if (mOutAnimator == null)
                initOutAnimator(it, list, onDataChanged)
            mOutAnimator!!.startDelay = mCycleInterval
            onDataChanged.invoke(list[0])
            mOutAnimator!!.start()
        }
    }

    fun getCurPosition(): Int {
        return mCurPosition
    }

    /**
     * 初始化从屏幕下面向上进入的动画效果
     */
    private fun initInAnimator(scrollView: View) {
        mInAnimator = ObjectAnimator.ofFloat(scrollView, "translationY", mHeight, 0f)
        mInAnimator!!.duration = mAnimationDuration
        mInAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mCycleHandler.sendEmptyMessageDelayed(0, mCycleInterval)
            }
        })
    }

    /**
     * 初始化向上出去的动画效果
     */
    private fun <T> initOutAnimator(scrollView: View, list: List<T>, onDataChanged: (T) -> Unit) {
        mOutAnimator = ObjectAnimator.ofFloat(scrollView, "translationY", 0f, -mHeight)
        mOutAnimator!!.duration = mAnimationDuration
        mOutAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                addPosition(list.size)
                onDataChanged.invoke(list[mCurPosition])
                mInAnimator!!.start()
            }
        })
    }

    private fun addPosition(itemCount: Int) {
        mCurPosition++
        if (mCurPosition >= itemCount) {
            mCurPosition = 0
        }
    }

    fun destroy() {
        mOutAnimator!!.cancel()
        mInAnimator!!.cancel()
        mCycleHandler.removeCallbacksAndMessages(null)
    }

    open class AnimatorListenerAdapter : Animator.AnimatorListener {

        override fun onAnimationStart(animation: Animator) {}

        override fun onAnimationEnd(animation: Animator) {}

        override fun onAnimationCancel(animation: Animator) {}

        override fun onAnimationRepeat(animation: Animator) {}

    }
}