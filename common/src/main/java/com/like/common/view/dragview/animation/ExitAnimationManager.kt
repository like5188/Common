package com.like.common.view.dragview.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator

/**
 * 从缩放状态到退出Activity的动画
 */
class ExitAnimationManager(config: AnimationConfig) : BaseAnimationManager(config) {

    override fun fillAnimatorSet(animatorSet: AnimatorSet) {
        animatorSet.play(ObjectAnimator.ofFloat(config.view, "canvasTranslationX", config.view.getCanvasTranslationX(), config.originTranslationX))
                .with(ObjectAnimator.ofFloat(config.view, "canvasTranslationY", config.view.getCanvasTranslationY(), config.originTranslationY))
                .with(ObjectAnimator.ofFloat(config.view, "canvasScale", config.view.getCanvasScale(), config.originScaleX))
                .with(ObjectAnimator.ofInt(config.view, "canvasBackgroundAlpha", config.view.getCanvasBackgroundAlpha(), 0))
    }

    override fun onEnd() {
        config.finishActivity()
    }

}