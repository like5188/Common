package com.like.common.view.dragview.view

import android.content.Context
import android.graphics.Color
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout
import com.like.common.view.dragview.animation.BaseAnimationManager
import com.like.common.view.dragview.animation.EnterAnimationManager
import com.like.common.view.dragview.animation.ExitAnimationManager
import com.like.common.view.dragview.animation.RestoreAnimationManager
import com.like.common.view.dragview.entity.DragInfo
import kotlin.math.abs

/**
 * 自定义的ViewGroup。仿微信朋友圈图片视频预览效果。
 * 支持对它的所有孩子进行拖动、缩放操作；支持它的背景透明度随着拖动变化；支持进入动画、退出动画、拖动后的还原动画。
 */
abstract class BaseDragView(context: Context, private val mSelectedDragInfo: DragInfo) : FrameLayout(context) {
    private val mEnterAnimationManager: BaseAnimationManager by lazy { EnterAnimationManager(this, mSelectedDragInfo) }
    private val mExitAnimationManager: BaseAnimationManager by lazy { ExitAnimationManager(this, mSelectedDragInfo) }
    private val mRestoreAnimationManager: BaseAnimationManager by lazy { RestoreAnimationManager(this) }

    private val mGestureDetector: GestureDetector by lazy {
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                return true// 必须返回true，才能触发其它事件。
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                // 单击
                exitAnimation()
                return super.onSingleTapConfirmed(e)
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                // 双击
                return super.onDoubleTap(e)
            }

            override fun onLongPress(e: MotionEvent) {
                // 长按
            }
        })
    }

    private var mMaxTranslationY = 0f// 允许y方向滑动的最大值，超过就会退出界面
    private var mMinScale = 0f// 允许的最小缩放系数

    private var mBackgroundAlpha = 255// 背景透明度
    private var mChildrenTranslationX = 0f// 所有孩子的 TranslationX
    private var mChildrenTranslationY = 0f// 所有孩子的 TranslationY
    private var mChildrenScale = 1f// 所有孩子的 scale
    private var mDownX = 0f// 按下时的x，用于计算 TranslationX
    private var mDownY = 0f// 按下时的y，用于计算 TranslationY
    private var mLastX = 0f// 上次的x，用于计算 dx
    private var mLastY = 0f// 上次的y，用于计算 dy

    init {
        setBackgroundColor(Color.BLACK)
        isClickable = true
        isLongClickable = true
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
        mGestureDetector.onTouchEvent(event)
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
                mChildrenTranslationX = event.x - mDownX
                mChildrenTranslationY = event.y - mDownY
                mChildrenScale = calcCanvasScaleByCanvasTranslationY(mChildrenTranslationY)
                mBackgroundAlpha = calcCanvasBackgroundAlphaByCanvasTranslationY(mChildrenTranslationY)
                update(mChildrenTranslationX, mChildrenTranslationY, mChildrenScale, mBackgroundAlpha)
            }
            MotionEvent.ACTION_UP -> {
                // 拖动
                if (mChildrenTranslationY > mMaxTranslationY) {
                    exitAnimation()
                } else {
                    restoreAnimation()
                }
            }
        }
        return true
    }

    fun setChildrenTranslationX(translationX: Float) {
        mChildrenTranslationX = translationX
    }

    fun getChildrenTranslationX() = mChildrenTranslationX

    fun setChildrenTranslationY(translationY: Float) {
        mChildrenTranslationY = translationY
    }

    fun getChildrenTranslationY() = mChildrenTranslationY

    fun setChildrenScale(scale: Float) {
        mChildrenScale = scale
    }

    fun getChildrenScale() = mChildrenScale

    fun setBackgroundAlpha(backgroundAlpha: Int) {
        mBackgroundAlpha = backgroundAlpha
        // 因为动画播放是把这个属性放在最后，所以我们也在这里更新界面
        update(mChildrenTranslationX, mChildrenTranslationY, mChildrenScale, mBackgroundAlpha)
    }

    fun getBackgroundAlpha() = mBackgroundAlpha

    /**
     * 当手指拖动时，更新界面
     */
    private fun update(translationX: Float, translationY: Float, scale: Float, alpha: Int) {
        (0 until childCount).forEach {
            val child = getChildAt(it)
            child.translationX = translationX
            child.translationY = translationY
            child.scaleX = scale
            child.scaleY = scale
        }
        setBackgroundColor(Color.argb(alpha, 0, 0, 0))
    }

    /**
     * 当手指拖动时，scale是根据translationY来计算的
     */
    private fun calcCanvasScaleByCanvasTranslationY(translationY: Float): Float {
        val translateYPercent = abs(translationY) / measuredHeight
        val scale = 1 - translateYPercent
        return when {
            scale < mMinScale -> mMinScale
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mMaxTranslationY = measuredHeight / 4f
        mMinScale = mSelectedDragInfo.originWidth / measuredWidth
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