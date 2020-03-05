package com.like.common.view.dragview.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator

/**
 * Activity消失的动画
 */
class DisappearAnimationManager(config: AnimationConfig) : BaseAnimationManager(config) {

    override fun fillAnimatorSet(animatorSet: AnimatorSet) {
        animatorSet.play(ObjectAnimator.ofFloat(config.view, "canvasTranslationX", 0f, config.originTranslationX))
                .with(ObjectAnimator.ofFloat(config.view, "canvasTranslationY", 0f, config.originTranslationY))
                .with(ObjectAnimator.ofFloat(config.view, "canvasScale", 1f, config.originScaleX))
                .with(ObjectAnimator.ofInt(config.view, "canvasBackgroundAlpha", 255, 0))
    }

    override fun onEnd() {
        config.finishActivity()
    }

}