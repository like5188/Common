package com.like.common.view.dragview.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet

abstract class BaseAnimationManager(val config: AnimationConfig) {
    private var isStart: Boolean = false
    private val animatorSet: AnimatorSet = AnimatorSet().apply {
        duration = AnimationConfig.DURATION
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                isStart = true
                onStart()
                super.onAnimationStart(animation)
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
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