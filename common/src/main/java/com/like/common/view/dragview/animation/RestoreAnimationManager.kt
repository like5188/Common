package com.like.common.view.dragview.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator

/**
 * 在Activity中，view从缩放状态还原的动画管理
 */
class RestoreAnimationManager(config: AnimationConfig) : BaseAnimationManager(config) {

    override
    fun fillAnimatorSet(animatorSet: AnimatorSet) {
        animatorSet.play(ObjectAnimator.ofFloat(config.view, "canvasTranslationX", config.view.getCanvasTranslationX(), 0f))
                .with(ObjectAnimator.ofFloat(config.view, "canvasTranslationY", config.view.getCanvasTranslationY(), 0f))
                .with(ObjectAnimator.ofFloat(config.view, "canvasScale", config.view.getCanvasScale(), 1f))
                .with(ObjectAnimator.ofInt(config.view, "canvasBackgroundAlpha", config.view.getCanvasBackgroundAlpha(), 255))
    }

}