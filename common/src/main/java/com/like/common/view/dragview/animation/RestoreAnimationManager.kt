package com.like.common.view.dragview.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import com.like.common.view.dragview.view.BaseDragView

/**
 * 在Activity中，view从缩放状态还原的动画
 */
class RestoreAnimationManager(
        private val mDragView: BaseDragView,
        private val mDuration: Long = 300L
) : BaseAnimationManager() {

    override fun fillAnimatorSet(animatorSet: AnimatorSet) {
        animatorSet.duration = mDuration
        animatorSet.play(ObjectAnimator.ofFloat(mDragView, "childrenTranslationX", mDragView.getChildrenTranslationX(), 0f))
                .with(ObjectAnimator.ofFloat(mDragView, "childrenTranslationY", mDragView.getChildrenTranslationY(), 0f))
                .with(ObjectAnimator.ofFloat(mDragView, "childrenScale", mDragView.getChildrenScale(), 1f))
                .with(ObjectAnimator.ofInt(mDragView, "backgroundAlpha", mDragView.getBackgroundAlpha(), 255))
    }

}