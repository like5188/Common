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
import kotlin.math.abs

abstract class BaseDragView(context: Context, private var mSelectedDragInfo: DragInfo) : FrameLayout(context) {
    companion object {
        // 辅助判断单击、双击、长按事件
        private const val DOUBLE_CLICK_INTERVAL = 300L
    }

    private var mFirstDownTime = 0L
    private var isUp = false
    private var isDoubleClick = false

    /**
     * 允许y方向滑动的最大值，超过就会退出界面
     */
    private var mMaxCanvasTranslationY = 0f
    private var mMinCanvasScale = 0f

    private var mCanvasBackgroundAlpha = 255
    private var mCanvasTranslationX = 0f
    private var mCanvasTranslationY = 0f
    private var mCanvasScale = 1f

    private val mEnterAnimationManager: BaseAnimationManager by lazy { EnterAnimationManager(this, mSelectedDragInfo) }
    private val mExitAnimationManager: BaseAnimationManager by lazy { ExitAnimationManager(this, mSelectedDragInfo) }
    private val mRestoreAnimationManager: BaseAnimationManager by lazy { RestoreAnimationManager(this) }

    init {
        setBackgroundColor(Color.BLACK)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                isUp = false
                if (mFirstDownTime == 0L) {//第一次点击
                    mFirstDownTime = System.currentTimeMillis()
                    postDelayed({
                        if (!isUp) {
                            onLongPress()
                        } else if (!isDoubleClick) {
                            onClick()
                        }
                        isDoubleClick = false
                        mFirstDownTime = 0L
                    }, DOUBLE_CLICK_INTERVAL)
                } else {
                    if (System.currentTimeMillis() - mFirstDownTime < DOUBLE_CLICK_INTERVAL) {//两次点击小于DOUBLE_CLICK_INTERVAL
                        onDoubleClick()
                        isDoubleClick = true
                    }
                    mFirstDownTime = 0L
                }
            }
            MotionEvent.ACTION_UP -> {
                // 防止下拉的时候双手缩放
                if (event.pointerCount == 1) {
                    onDrag()
                }
                isUp = true
            }
        }
        return super.dispatchTouchEvent(event)
    }

    fun onClick() {
        if (mCanvasTranslationX == 0f && mCanvasTranslationY == 0f) {
            onDestroy()
            exit()
        }
    }

    private fun onDoubleClick() {

    }

    private fun onLongPress() {

    }

    private fun onDrag() {
        if (mCanvasTranslationY > mMaxCanvasTranslationY) {
            onDestroy()
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

    fun enter() {
        mEnterAnimationManager.start()
    }

    private fun restore() {
        mRestoreAnimationManager.start()
    }

    private fun exit() {
        mExitAnimationManager.start()
    }

    abstract fun onDestroy()
}