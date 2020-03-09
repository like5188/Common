package com.like.common.view.dragview.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import com.like.common.view.dragview.entity.DragInfo
import com.like.common.view.dragview.view.BaseDragView
import com.like.common.view.dragview.view.BaseDragViewActivity

/**
 * 退出Activity的动画
 */
class ExitAnimationManager(
        private val mDragView: BaseDragView,
        private val mDragInfo: DragInfo,
        private val mDuration: Long = 300L
) : BaseAnimationManager() {

    override fun fillAnimatorSet(animatorSet: AnimatorSet) {
        animatorSet.duration = mDuration
        animatorSet.play(ObjectAnimator.ofFloat(mDragView, "canvasTranslationX", mDragView.getCanvasTranslationX(), mDragInfo.getInitTranslationX(mDragView)))
                .with(ObjectAnimator.ofFloat(mDragView, "canvasTranslationY", mDragView.getCanvasTranslationY(), mDragInfo.getInitTranslationY(mDragView)))
                .with(ObjectAnimator.ofFloat(mDragView, "canvasScale", mDragView.getCanvasScale(), mDragInfo.getInitScaleX(mDragView)))
                .with(ObjectAnimator.ofInt(mDragView, "canvasBackgroundAlpha", mDragView.getCanvasBackgroundAlpha(), 0))
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                val activity = mDragView.context
                if (activity is BaseDragViewActivity) {
                    activity.finish()
                }
            }
        })
    }

}