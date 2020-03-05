package com.like.common.view.dragview.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import com.like.common.view.dragview.view.BaseDragView

/**
 * 在Activity中，view从缩放状态还原的动画管理
 */
class RestoreAnimationManager(private val mDragView: BaseDragView) : BaseAnimationManager() {

    override
    fun fillAnimatorSet(animatorSet: AnimatorSet) {
        animatorSet.play(ObjectAnimator.ofFloat(mDragView, "canvasTranslationX", mDragView.getCanvasTranslationX(), 0f))
                .with(ObjectAnimator.ofFloat(mDragView, "canvasTranslationY", mDragView.getCanvasTranslationY(), 0f))
                .with(ObjectAnimator.ofFloat(mDragView, "canvasScale", mDragView.getCanvasScale(), 1f))
                .with(ObjectAnimator.ofInt(mDragView, "canvasBackgroundAlpha", mDragView.getCanvasBackgroundAlpha(), 255))
    }

}