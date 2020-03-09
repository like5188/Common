package com.like.common.view.dragview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.MotionEvent
import android.widget.FrameLayout
import com.like.common.view.dragview.animation.BaseAnimationManager
import com.like.common.view.dragview.animation.EnterAnimationManager
import com.like.common.view.dragview.animation.ExitAnimationManager
import com.like.common.view.dragview.animation.RestoreAnimationManager
import com.like.common.view.dragview.entity.DragInfo
import com.like.common.view.dragview.view.util.EventHandler
import kotlin.math.abs

/**
 * 自定义的支持拖动、动画的View。
 */
abstract class BaseDragView(context: Context, private val mSelectedDragInfo: DragInfo) : FrameLayout(context) {
    private val mEnterAnimationManager: BaseAnimationManager by lazy { EnterAnimationManager(this, mSelectedDragInfo) }
    private val mExitAnimationManager: BaseAnimationManager by lazy { ExitAnimationManager(this, mSelectedDragInfo) }
    private val mRestoreAnimationManager: BaseAnimationManager by lazy { RestoreAnimationManager(this) }

    private val mEventHandler: EventHandler by lazy {
        EventHandler(this).apply {
            mOnClick = {
                exitAnimation()
            }
            mOnDrag = {
                if (mCanvasTranslationY > mMaxCanvasTranslationY) {
                    exitAnimation()
                } else {
                    restoreAnimation()
                }
            }
        }
    }

    private var mMaxCanvasTranslationY = 0f// 允许y方向滑动的最大值，超过就会退出界面
    private var mMinCanvasScale = 0f// 允许的最小缩放系数

    private var mCanvasBackgroundAlpha = 255
    private var mCanvasTranslationX = 0f
    private var mCanvasTranslationY = 0f
    private var mCanvasScale = 1f
    private var mDownX = 0f
    private var mDownY = 0f
    private var mLastX = 0f
    private var mLastY = 0f

    init {
        setBackgroundColor(Color.BLACK)
        isClickable
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = event.x
                mDownY = event.y
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - mLastX
                val dy = event.y - mLastY
                parent.requestDisallowInterceptTouchEvent(handleMoveEvent(event, dx, dy))
            }
        }
        mLastX = event.x
        mLastY = event.y
        mEventHandler.handle(event)
        return super.dispatchTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        var intercepted = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                updateProperties(event.x - mDownX, event.y - mDownY)
            }
        }
        return true
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
     * 当手指拖动时，更新属性
     */
    private fun updateProperties(translationX: Float, translationY: Float) {
        setCanvasTranslationX(translationX)
        setCanvasTranslationY(translationY)
        setCanvasScale(calcCanvasScaleByCanvasTranslationY(translationY))
        setCanvasBackgroundAlpha(calcCanvasBackgroundAlphaByCanvasTranslationY(translationY))
    }

    /**
     * 当手指拖动时，scale是根据translationY来计算的
     */
    private fun calcCanvasScaleByCanvasTranslationY(translationY: Float): Float {
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
    private fun calcCanvasBackgroundAlphaByCanvasTranslationY(translationY: Float): Int {
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

    protected fun enterAnimation() {
        mEnterAnimationManager.start()
    }

    private fun restoreAnimation() {
        mRestoreAnimationManager.start()
    }

    fun exitAnimation() {
        onDestroy()
        mExitAnimationManager.start()
    }

    protected abstract fun handleMoveEvent(event: MotionEvent, dx: Float, dy: Float): Boolean

    protected abstract fun onDestroy()
}