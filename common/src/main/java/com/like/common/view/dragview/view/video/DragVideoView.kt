package com.like.common.view.dragview.view.video

import android.content.Context
import android.view.MotionEvent
import com.like.common.util.onGlobalLayoutListener
import com.like.common.view.dragview.entity.DragInfo
import com.like.common.view.dragview.view.BaseDragView

class DragVideoView(context: Context, info: DragInfo) : BaseDragView(context, info) {
    private var mDownX = 0f
    private var mDownY = 0f
    private val mCustomVideoView: CustomVideoView by lazy { CustomVideoView(context) }

    init {
        addView(mCustomVideoView)
        onGlobalLayoutListener {
            enter()
            mCustomVideoView.play(info.videoUrl, info.thumbImageUrl)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        // 当scale == 1时才能drag
        if (scaleX == 1f && scaleY == 1f) {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    mDownX = event.x
                    mDownY = event.y
                    super.dispatchTouchEvent(event)
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    // 单手指按下，并在Y方向上拖动了一段距离
                    if (event.pointerCount == 1) {
                        setCanvasTranslationX(event.x - mDownX)
                        val transitionY = event.y - mDownY
                        setCanvasTranslationY(transitionY)
                        setCanvasScale(calcCanvasScaleByCanvasTranslationY(transitionY))
                        setCanvasBackgroundAlpha(calcCanvasBackgroundAlphaByCanvasTranslationY(transitionY))
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onDestroy() {
        mCustomVideoView.stop()
    }

}