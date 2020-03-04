package com.like.common.view.dragview.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet

/**
 * Animator: 提供创建属性动画的基类，基本不会直接使用这个类。
 * ValueAnimator:属性动画用到的主要的时间引擎，负责计算各个帧的属性值，基本上其他属性动画都会直接或间接继承它；
 * ObjectAnimator： ValueAnimator 的子类，对指定对象的属性执行动画。
 * AnimatorSet：Animator 的子类，用于组合多个 Animator。
 */
abstract class BaseAnimationManager(val config: AnimationConfig) {
    private var isStart: Boolean = false
    private val animatorSet: AnimatorSet = AnimatorSet().apply {
        duration = AnimationConfig.DURATION
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                isStart = true
                onStart()
            }

            override fun onAnimationEnd(animation: Animator?) {
                animation?.removeAllListeners()
                isStart = false
                onEnd()
            }
        })
    }

    @Synchronized
    fun start() {
        if (isStart) {
            return
        }
        fillAnimatorSet(animatorSet)
        animatorSet.start()
    }

    fun cancel() {
        animatorSet.cancel()
    }

    abstract fun fillAnimatorSet(animatorSet: AnimatorSet)
    open fun onStart() {}
    open fun onEnd() {}

}