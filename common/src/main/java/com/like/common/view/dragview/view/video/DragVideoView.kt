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

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        var intercepted = false
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = event.x
                mDownY = event.y
                intercepted = false
            }
            MotionEvent.ACTION_MOVE -> {
                intercepted = event.pointerCount == 1 && scaleX == 1f && scaleY == 1f
            }
            MotionEvent.ACTION_UP -> {
                intercepted = false
            }
        }
        return intercepted
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_MOVE -> {
                updateProperties(event.x - mDownX, event.y - mDownY)
            }
        }
        return true
    }

    override fun onDestroy() {
        mCustomVideoView.stop()
    }

}