package com.like.common.view.dragview.animation

import android.animation.AnimatorSet
import com.like.common.view.dragview.entity.DragInfo
import com.like.common.view.dragview.view.BaseDragView

/**
 * 动画管理
 */
abstract class BaseAnimationManager {
    private val mAnimatorSet: AnimatorSet = AnimatorSet()

    @Synchronized
    fun start() {
        if (mAnimatorSet.isStarted) {
            return
        }
        mAnimatorSet.removeAllListeners()
        fillAnimatorSet(mAnimatorSet)
        mAnimatorSet.start()
    }

    protected fun getInitScaleX(dragView: BaseDragView, dragInfo: DragInfo) =
        dragInfo.originRect.width() / dragView.width.toFloat()

    protected fun getInitScaleY(dragView: BaseDragView, dragInfo: DragInfo) =
        dragInfo.originRect.height() / dragView.height.toFloat()

    protected fun getInitTranslationX(dragView: BaseDragView, dragInfo: DragInfo): Float {
        val originCenterX: Float = dragInfo.originRect.left + dragInfo.originRect.width() / 2f
        return originCenterX - dragView.width / 2
    }

    protected fun getInitTranslationY(dragView: BaseDragView, dragInfo: DragInfo): Float {
        val originCenterY: Float = dragInfo.originRect.top + dragInfo.originRect.height() / 2f
        return originCenterY - dragView.height / 2
    }

    protected abstract fun fillAnimatorSet(animatorSet: AnimatorSet)

}