package com.like.common.view.dragview.animation

import android.animation.AnimatorSet

/**
 * Animator: 提供创建属性动画的基类，基本不会直接使用这个类。
 * ValueAnimator:属性动画用到的主要的时间引擎，负责计算各个帧的属性值，基本上其他属性动画都会直接或间接继承它；
 * ObjectAnimator： ValueAnimator 的子类，对指定对象的属性执行动画。
 * AnimatorSet：Animator 的子类，用于组合多个 Animator。
 */
abstract class BaseAnimationManager {
    private val mAnimatorSet: AnimatorSet = AnimatorSet()

    @Synchronized
    fun start() {
        if (mAnimatorSet.isStarted) {
            return
        }
        mAnimatorSet.removeAllListeners()
        fillAnimatorSet(mAnimatorSet)
        mAnimatorSet.start()
    }

    abstract fun fillAnimatorSet(animatorSet: AnimatorSet)

}