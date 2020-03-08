package com.like.common.view.dragview.view.util

import android.view.MotionEvent
import android.view.View

/**
 * [MotionEvent]处理，用于判断单击、双击、长按、拖动事件
 */
class EventHandler(private val mView: View) {
    private var mClickCount = 0// 点击次数
    private var mIsLongPress = false// 是否长按
    private var mIsMove = false// 是否移动了

    var mOnClick: (() -> Unit)? = null
    var mOnDoubleClick: (() -> Unit)? = null
    var mOnLongPress: (() -> Unit)? = null
    var mOnDrag: (() -> Unit)? = null

    fun handle(event: MotionEvent?) {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mIsMove = false
                if (mClickCount == 0) {// 第一次点击
                    mView.postDelayed(400L) {
                        if (mIsMove) return@postDelayed
                        when (mClickCount) {
                            0 -> {
                                mIsLongPress = true
                                mOnLongPress?.invoke()
                            }
                            1 -> {
                                mOnClick?.invoke()
                            }
                            2 -> {
                                mOnDoubleClick?.invoke()
                            }
                        }
                        mClickCount = 0
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                mIsMove = true
            }
            MotionEvent.ACTION_UP -> {
                mClickCount++
                if (mIsLongPress) {
                    mClickCount = 0
                    mIsLongPress = false
                } else if (mIsMove) {
                    mClickCount = 0
                    mOnDrag?.invoke()
                }
            }
        }
    }

}