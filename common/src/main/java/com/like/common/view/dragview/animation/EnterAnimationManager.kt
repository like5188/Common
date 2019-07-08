package com.like.common.view.dragview.animation

import android.animation.AnimatorSet
import android.animation.ValueAnimator

/**
 * 进入Activity的动画
 */
class EnterAnimationManager(config: AnimationConfig) : BaseAnimationManager(config) {

    override fun fillAnimatorSet(animatorSet: AnimatorSet) {
        animatorSet.play(ValueAnimator.ofFloat(config.originScaleX, 1f).apply {
            addUpdateListener {
                config.curCanvasScale = it.animatedValue as Float
            }
        })
                .with(ValueAnimator.ofFloat(config.originTranslationX, 0f).apply {
                    addUpdateListener {
                        config.curCanvasTranslationX = it.animatedValue as Float
                    }
                })
                .with(ValueAnimator.ofFloat(config.originTranslationY, 0f).apply {
                    addUpdateListener {
                        config.curCanvasTranslationY = it.animatedValue as Float
                    }
                })
                .with(ValueAnimator.ofInt(0, 255).apply {
                    addUpdateListener {
                        config.curCanvasBgAlpha = it.animatedValue as Int
                        config.view.invalidate()
                    }
                })
    }

}