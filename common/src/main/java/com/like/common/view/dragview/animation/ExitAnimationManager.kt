package com.like.common.view.dragview.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import com.like.common.view.dragview.entity.DragInfo
import com.like.common.view.dragview.view.BaseDragView

/**
 * 从缩放状态到退出Activity的动画
 */
class ExitAnimationManager(private val mDragView: BaseDragView, private val mDragInfo: DragInfo) : BaseAnimationManager() {

    override fun fillAnimatorSet(animatorSet: AnimatorSet) {
        animatorSet.play(ObjectAnimator.ofFloat(mDragView, "canvasTranslationX", mDragView.getCanvasTranslationX(), mDragInfo.getInitTranslationX(mDragView)))
                .with(ObjectAnimator.ofFloat(mDragView, "canvasTranslationY", mDragView.getCanvasTranslationY(), mDragInfo.getInitTranslationY(mDragView)))
                .with(ObjectAnimator.ofFloat(mDragView, "canvasScale", mDragView.getCanvasScale(), mDragInfo.getInitScaleX(mDragView)))
                .with(ObjectAnimator.ofInt(mDragView, "canvasBackgroundAlpha", mDragView.getCanvasBackgroundAlpha(), 0))
    }

    override fun onEnd() {
        mDragView.finishActivity()
    }

}