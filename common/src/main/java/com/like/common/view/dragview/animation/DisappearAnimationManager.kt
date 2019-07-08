package com.like.common.view.dragview.animation

import android.animation.AnimatorSet
import android.animation.ValueAnimator

/**
 * Activity消失的动画
 */
class DisappearAnimationManager(config: AnimationConfig) : BaseAnimationManager(config) {

    override fun fillAnimatorSet(animatorSet: AnimatorSet) {
        animatorSet.play(ValueAnimator.ofFloat(1f, config.originScaleX).apply {
            addUpdateListener {
                config.curCanvasScale = it.animatedValue as Float
            }
        })
                .with(ValueAnimator.ofFloat(0f, config.originTranslationX).apply {
                    addUpdateListener {
                        config.curCanvasTranslationX = it.animatedValue as Float
                    }
                })
                .with(ValueAnimator.ofFloat(0f, config.originTranslationY).apply {
                    addUpdateListener {
                        config.curCanvasTranslationY = it.animatedValue as Float
                    }
                })
                .with(ValueAnimator.ofInt(255, 0).apply {
                    addUpdateListener {
                        config.curCanvasBgAlpha = it.animatedValue as Int
                        config.view.invalidate()
                    }
                })
    }

    override fun onEnd() {
        config.finishActivity()
    }

}