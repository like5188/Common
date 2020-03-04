package com.like.common.view.dragview.animation

import android.app.Activity
import com.like.common.view.dragview.entity.DragInfo
import com.like.common.view.dragview.view.BaseDragView

class AnimationConfig(curClickInfo: DragInfo, val view: BaseDragView) {
    companion object {
        const val DURATION = 300L
    }

    /**
     * 允许y方向滑动的最大值，超过就会退出界面
     */
    val mMaxCanvasTranslationY = view.height.toFloat() / 4

    var curCanvasBgAlpha = 255
    var curCanvasTranslationX = 0f
    var curCanvasTranslationY = 0f
    var curCanvasScale = 1f
    var originScaleX = curClickInfo.originWidth / view.width.toFloat()
    var originScaleY = curClickInfo.originHeight / view.height.toFloat()
    var originTranslationX = curClickInfo.originCenterX - view.width / 2
    var originTranslationY = curClickInfo.originCenterY - view.height / 2
    var minCanvasScale = curClickInfo.originWidth / view.width

    fun setData(info: DragInfo) {
        curCanvasBgAlpha = 255
        curCanvasTranslationX = 0f
        curCanvasTranslationY = 0f
        curCanvasScale = 1f
        originScaleX = info.originWidth / view.width.toFloat()
        originScaleY = info.originHeight / view.height.toFloat()
        originTranslationX = info.originCenterX - view.width / 2
        originTranslationY = info.originCenterY - view.height / 2
        minCanvasScale = info.originWidth / view.width
    }

    fun finishActivity() {
        val activity = view.context
        if (activity is Activity) {
            activity.finish()
            // 去掉默认的切换效果
            activity.overridePendingTransition(0, 0)
        }
    }

    fun updateCanvasTranslationX(translationX: Float) {
        curCanvasTranslationX = translationX
    }

    fun updateCanvasTranslationY(translationY: Float) {
        curCanvasTranslationY = translationY
    }

    fun updateCanvasScale() {
        val translateYPercent = Math.abs(curCanvasTranslationY) / view.height
        val scale = 1 - translateYPercent
        curCanvasScale = when {
            scale < minCanvasScale -> minCanvasScale
            scale > 1f -> 1f
            else -> scale
        }
    }

    fun updateCanvasBgAlpha() {
        val translateYPercent = Math.abs(curCanvasTranslationY) / view.height
        val alpha = (255 * (1 - translateYPercent)).toInt()
        curCanvasBgAlpha = when {
            alpha > 255 -> 255
            alpha < 0 -> 0
            else -> alpha
        }
    }
}
