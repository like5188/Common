package com.like.common.view.dragview.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import com.like.common.view.dragview.entity.DragInfo
import com.like.common.view.dragview.view.BaseDragView

/**
 * 进入Activity的动画
 */
class EnterAnimationManager(
        private val mDragView: BaseDragView,
        private val mDragInfo: DragInfo,
        private val mDuration: Long = 300L
) : BaseAnimationManager() {

    override fun fillAnimatorSet(animatorSet: AnimatorSet) {
        animatorSet.duration = mDuration
        animatorSet.play(ObjectAnimator.ofFloat(mDragView, "childrenTranslationX", mDragInfo.getInitTranslationX(mDragView), 0f))
                .with(ObjectAnimator.ofFloat(mDragView, "childrenTranslationY", mDragInfo.getInitTranslationY(mDragView), 0f))
                .with(ObjectAnimator.ofFloat(mDragView, "childrenScale", mDragInfo.getInitScaleX(mDragView), 1f))
                .with(ObjectAnimator.ofInt(mDragView, "backgroundAlpha", 0, 255))
    }

}