package com.like.common.view.dragview.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import com.like.common.view.dragview.entity.DragInfo
import com.like.common.view.dragview.view.BaseDragView
import com.like.common.view.dragview.activity.BaseDragViewActivity

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
        animatorSet.play(ObjectAnimator.ofFloat(mDragView, "childrenTranslationX", mDragView.childrenTranslationX, mDragInfo.getInitTranslationX(mDragView)))
                .with(ObjectAnimator.ofFloat(mDragView, "childrenTranslationY", mDragView.childrenTranslationY, mDragInfo.getInitTranslationY(mDragView)))
                .with(ObjectAnimator.ofFloat(mDragView, "childrenScale", mDragView.childrenScale, mDragInfo.getInitScaleX(mDragView)))
                .with(ObjectAnimator.ofInt(mDragView, "backgroundAlpha", mDragView.backgroundAlpha, 0))
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