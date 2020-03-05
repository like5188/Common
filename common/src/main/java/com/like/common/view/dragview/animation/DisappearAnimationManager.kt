package com.like.common.view.dragview.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import com.like.common.view.dragview.entity.DragInfo
import com.like.common.view.dragview.view.BaseDragView

/**
 * Activity消失的动画
 */
class DisappearAnimationManager(private val mDragView: BaseDragView, private val mDragInfo: DragInfo) : BaseAnimationManager() {

    override fun fillAnimatorSet(animatorSet: AnimatorSet) {
        animatorSet.play(ObjectAnimator.ofFloat(mDragView, "canvasTranslationX", 0f, mDragInfo.getInitTranslationX(mDragView)))
                .with(ObjectAnimator.ofFloat(mDragView, "canvasTranslationY", 0f, mDragInfo.getInitTranslationY(mDragView)))
                .with(ObjectAnimator.ofFloat(mDragView, "canvasScale", 1f, mDragInfo.getInitScaleX(mDragView)))
                .with(ObjectAnimator.ofInt(mDragView, "canvasBackgroundAlpha", 255, 0))
    }

    override fun onEnd() {
        mDragView.finishActivity()
    }

}