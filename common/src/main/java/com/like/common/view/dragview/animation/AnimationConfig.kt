package com.like.common.view.dragview.animation

import android.app.Activity
import com.like.common.view.dragview.entity.DragInfo
import com.like.common.view.dragview.view.BaseDragView

class AnimationConfig(curClickInfo: DragInfo, val view: BaseDragView) {
    var originScaleX = curClickInfo.originWidth / view.width.toFloat()
    var originScaleY = curClickInfo.originHeight / view.height.toFloat()
    var originTranslationX = curClickInfo.originCenterX - view.width / 2
    var originTranslationY = curClickInfo.originCenterY - view.height / 2

    fun setData(info: DragInfo) {
        originScaleX = info.originWidth / view.width.toFloat()
        originScaleY = info.originHeight / view.height.toFloat()
        originTranslationX = info.originCenterX - view.width / 2
        originTranslationY = info.originCenterY - view.height / 2
    }

    fun finishActivity() {
        val activity = view.context
        if (activity is Activity) {
            activity.finish()
            // 去掉默认的切换效果
            activity.overridePendingTransition(0, 0)
        }
    }

}
