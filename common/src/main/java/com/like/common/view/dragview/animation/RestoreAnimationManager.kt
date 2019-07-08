package com.like.common.view.dragview.animation

import android.animation.AnimatorSet
import android.animation.ValueAnimator

/**
 * 在Activity中，view从缩放状态还原的动画管理
 */
class RestoreAnimationManager(config: AnimationConfig) : BaseAnimationManager(config) {

    override
    fun fillAnimatorSet(animatorSet: AnimatorSet) {
        animatorSet.play(ValueAnimator.ofFloat(config.curCanvasScale, 1f).apply {
            addUpdateListener {
                config.curCanvasScale = it.animatedValue as Float
            }
        })
                .with(ValueAnimator.ofFloat(config.curCanvasTranslationX, 0f).apply {
                    addUpdateListener {
                        config.curCanvasTranslationX = it.animatedValue as Float
                    }
                })
                .with(ValueAnimator.ofFloat(config.curCanvasTranslationY, 0f).apply {
                    addUpdateListener {
                        config.curCanvasTranslationY = it.animatedValue as Float
                    }
                })
                .with(ValueAnimator.ofInt(config.curCanvasBgAlpha, 255).apply {
                    addUpdateListener {
                        config.curCanvasBgAlpha = it.animatedValue as Int
                        config.view.invalidate()
                    }
                })
    }

}