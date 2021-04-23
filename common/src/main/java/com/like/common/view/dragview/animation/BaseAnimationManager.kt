package com.like.common.view.dragview.animation

import android.animation.AnimatorSet

/**
 * 动画管理
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