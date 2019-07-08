package com.like.common.view.dragview.animation

import android.animation.AnimatorSet
import android.animation.ValueAnimator

/**
 * 从缩放状态到退出Activity的动画
 */
class ExitAnimationManager(config: AnimationConfig) : BaseAnimationManager(config) {

    override fun fillAnimatorSet(animatorSet: AnimatorSet) {
        animatorSet.play(ValueAnimator.ofFloat(config.curCanvasScale, config.originScaleX).apply {
            addUpdateListener {
                config.curCanvasScale = it.animatedValue as Float
            }
        })
                .with(ValueAnimator.ofFloat(config.curCanvasTranslationX, config.originTranslationX).apply {
                    addUpdateListener {
                        config.curCanvasTranslationX = it.animatedValue as Float
                    }
                })
                .with(ValueAnimator.ofFloat(config.curCanvasTranslationY, config.originTranslationY).apply {
                    addUpdateListener {
                        config.curCanvasTranslationY = it.animatedValue as Float
                    }
                })
                .with(ValueAnimator.ofInt(config.curCanvasBgAlpha, 0).apply {
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