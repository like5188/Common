package com.like.common.view.dragview.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator

/**
 * 进入Activity的动画
 */
class EnterAnimationManager(config: AnimationConfig) : BaseAnimationManager(config) {

    override fun fillAnimatorSet(animatorSet: AnimatorSet) {
        animatorSet.play(ObjectAnimator.ofFloat(config.view, "canvasTranslationX", config.originTranslationX, 0f))
                .with(ObjectAnimator.ofFloat(config.view, "canvasTranslationY", config.originTranslationY, 0f))
                .with(ObjectAnimator.ofFloat(config.view, "canvasScale", config.originScaleX, 1f))
                .with(ObjectAnimator.ofInt(config.view, "canvasBackgroundAlpha", 0, 255))
    }

}