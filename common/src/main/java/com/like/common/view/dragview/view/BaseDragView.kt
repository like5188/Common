package com.like.common.view.dragview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import com.like.common.view.dragview.animation.BaseAnimationManager
import com.like.common.view.dragview.animation.EnterAnimationManager
import com.like.common.view.dragview.animation.ExitAnimationManager
import com.like.common.view.dragview.animation.RestoreAnimationManager
import com.like.common.view.dragview.entity.DragInfo
import com.like.common.view.dragview.view.util.postDelayed
import kotlin.math.abs

abstract class BaseDragView(context: Context, private var mSelectedDragInfo: DragInfo) : FrameLayout(context) {
    // 用于辅助判断单击、双击、长按、拖动事件
    private var mClickCount = 0// 点击次数
    private var mIsLongPress = false// 是否长按
    private var mIsMove = false// 是否移动了

    private var mMaxCanvasTranslationY = 0f// 允许y方向滑动的最大值，超过就会退出界面
    private var mMinCanvasScale = 0f// 允许的最小缩放系数

    private var mCanvasBackgroundAlpha = 255
    private var mCanvasTranslationX = 0f
    private var mCanvasTranslationY = 0f
    private var mCanvasScale = 1f

    private val mEnterAnimationManager: BaseAnimationManager by lazy { EnterAnimationManager(this, mSelectedDragInfo) }
    private val mExitAnimationManager: BaseAnimationManager by lazy { ExitAnimationManager(this, mSelectedDragInfo) }
    private val mRestoreAnimationManager: BaseAnimationManager by lazy { RestoreAnimationManager(this) }

    init {
        setBackgroundColor(Color.BLACK)
        isClickable
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        Log.e("tag", "dispatchTouchEvent action=${event?.action}")
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.e("tag", "ACTION_DOWN")
                mIsMove = false
                if (mClickCount == 0) {// 第一次点击
                    postDelayed(400L) {
                        if (mIsMove) return@postDelayed
                        when (mClickCount) {
                            0 -> {
                                mIsLongPress = true
                                onLongPress()
                            }
                            1 -> {
                                onClick()
                            }
                            2 -> {
                                onDoubleClick()
                            }
                        }
                        mClickCount = 0
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                Log.e("tag", "ACTION_MOVE")
                mIsMove = true
            }
            MotionEvent.ACTION_UP -> {
                Log.e("tag", "ACTION_UP")
                mClickCount++
                if (mIsLongPress) {
                    mClickCount = 0
                    mIsLongPress = false
                } else if (mIsMove) {
                    mClickCount = 0
                    onDrag()
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun onClick() {
        Log.w("tag", "onClick")
        exit()
    }

    private fun onDoubleClick() {
        Log.w("tag", "onDoubleClick")
    }

    private fun onLongPress() {
        Log.w("tag", "onLongPress")
    }

    private fun onDrag() {
        Log.w("tag", "onDrag")
        if (mCanvasTranslationY > mMaxCanvasTranslationY) {
            exit()
        } else {
            restore()
        }
    }

    protected fun setData(dragInfo: DragInfo) {
        mSelectedDragInfo = dragInfo
    }

    fun setCanvasTranslationX(translationX: Float) {
        mCanvasTranslationX = translationX
        invalidate()
    }

    fun getCanvasTranslationX() = mCanvasTranslationX

    fun setCanvasTranslationY(translationY: Float) {
        mCanvasTranslationY = translationY
        invalidate()
    }

    fun getCanvasTranslationY() = mCanvasTranslationY

    fun setCanvasScale(scale: Float) {
        mCanvasScale = scale
        invalidate()
    }

    fun getCanvasScale() = mCanvasScale

    fun setCanvasBackgroundAlpha(backgroundAlpha: Int) {
        mCanvasBackgroundAlpha = backgroundAlpha
        invalidate()
    }

    fun getCanvasBackgroundAlpha() = mCanvasBackgroundAlpha

    /**
     * 当手指拖动时，scale是根据translationY来计算的
     */
    protected fun calcCanvasScaleByCanvasTranslationY(translationY: Float): Float {
        val translateYPercent = abs(translationY) / measuredHeight
        val scale = 1 - translateYPercent
        return when {
            scale < mMinCanvasScale -> mMinCanvasScale
            scale > 1f -> 1f
            else -> scale
        }
    }

    /**
     * 当手指拖动时，backgroundAlpha是根据translationY来计算的
     */
    protected fun calcCanvasBackgroundAlphaByCanvasTranslationY(translationY: Float): Int {
        val translateYPercent = abs(translationY) / measuredHeight
        val alpha = (255 * (1 - translateYPercent)).toInt()
        return when {
            alpha > 255 -> 255
            alpha < 0 -> 0
            else -> alpha
        }
    }

    override fun onDraw(canvas: Canvas?) {
        setBackgroundColor(Color.argb(mCanvasBackgroundAlpha, 0, 0, 0))
        canvas?.translate(mCanvasTranslationX, mCanvasTranslationY)
        canvas?.scale(mCanvasScale, mCanvasScale, measuredWidth / 2f, measuredHeight / 2f)
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mMaxCanvasTranslationY = measuredHeight / 4f
        mMinCanvasScale = mSelectedDragInfo.originWidth / measuredWidth
    }

    protected fun enter() {
        mEnterAnimationManager.start()
    }

    private fun restore() {
        mRestoreAnimationManager.start()
    }

    fun exit() {
        onDestroy()
        mExitAnimationManager.start()
    }

    protected abstract fun onDestroy()
}