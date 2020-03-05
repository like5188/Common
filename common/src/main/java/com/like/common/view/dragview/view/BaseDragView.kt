package com.like.common.view.dragview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.like.common.util.GlideUtils
import com.like.common.view.dragview.animation.*
import com.like.common.view.dragview.entity.DragInfo

abstract class BaseDragView(context: Context, private val mClickInfo: DragInfo) : RelativeLayout(context) {
    companion object {
        // 辅助判断单击、双击、长按事件
        private const val DOUBLE_CLICK_INTERVAL = 300L
    }

    protected var mDownX = 0f
    protected var mDownY = 0f
    private var firstClickTime = 0L
    private var secondClickTime = 0L
    private var isUp = false
    private var isDoubleClick = false

    private var mWidth = 0f
    private var mHeight = 0f

    /**
     * 允许y方向滑动的最大值，超过就会退出界面
     */
    private var mMaxCanvasTranslationY = 0f
    private var mMinCanvasScale = 0f

    private var mCanvasBackgroundAlpha = 255
    private var mCanvasTranslationX = 0f
    private var mCanvasTranslationY = 0f
    private var mCanvasScale = 1f

    protected val mConfig: AnimationConfig by lazy { AnimationConfig(mClickInfo, this) }

    private val mRestoreAnimationManager: RestoreAnimationManager by lazy { RestoreAnimationManager(mConfig) }
    private val mEnterAnimationManager: EnterAnimationManager by lazy { EnterAnimationManager(mConfig) }
    private val mExitAnimationManager: ExitAnimationManager by lazy { ExitAnimationManager(mConfig) }
    private val mDisappearAnimationManager: DisappearAnimationManager by lazy { DisappearAnimationManager(mConfig) }

    protected val mGlideUtils: GlideUtils by lazy { GlideUtils(context) }

    init {
        setBackgroundColor(Color.BLACK)
    }

    fun setCanvasTranslationX(translationX: Float) {
        mCanvasTranslationX = translationX
    }

    fun getCanvasTranslationX() = mCanvasTranslationX

    fun setCanvasTranslationY(translationY: Float) {
        mCanvasTranslationY = translationY

        val translateYPercent = Math.abs(translationY) / mHeight
        val scale = 1 - translateYPercent
        setCanvasScale(when {
            scale < mMinCanvasScale -> mMinCanvasScale
            scale > 1f -> 1f
            else -> scale
        })

        val alpha = (255 * (1 - translateYPercent)).toInt()
        setCanvasBackgroundAlpha(when {
            alpha > 255 -> 255
            alpha < 0 -> 0
            else -> alpha
        })
    }

    fun getCanvasTranslationY() = mCanvasTranslationY

    fun setCanvasScale(scale: Float) {
        mCanvasScale = scale
    }

    fun getCanvasScale() = mCanvasScale

    fun setCanvasBackgroundAlpha(backgroundAlpha: Int) {
        mCanvasBackgroundAlpha = backgroundAlpha
        invalidate()
    }

    fun getCanvasBackgroundAlpha() = mCanvasBackgroundAlpha

    override fun onDraw(canvas: Canvas?) {
        setBackgroundColor(Color.argb(mCanvasBackgroundAlpha, 0, 0, 0))
        canvas?.translate(mCanvasTranslationX, mCanvasTranslationY)
        canvas?.scale(mCanvasScale, mCanvasScale, mWidth / 2, mHeight / 2)
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mMaxCanvasTranslationY = measuredHeight / 4f
        mMinCanvasScale = mClickInfo.originWidth / measuredWidth
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        mHeight = h.toFloat()
    }

    fun restore() {
        mRestoreAnimationManager.start()
    }

    fun disappear() {
        mDisappearAnimationManager.start()
    }

    fun enter() {
        mEnterAnimationManager.start()
    }

    fun exit() {
        mExitAnimationManager.start()
    }

    /**
     * 控制相关动画，由子类调用
     */
    fun onActionDown(event: MotionEvent) {
        mDownX = event.x
        mDownY = event.y
        isUp = false
        if (firstClickTime == 0L && secondClickTime == 0L) {//第一次点击
            firstClickTime = System.currentTimeMillis()
            postDelayed({
                if (!isUp) {
                    Log.v("BaseDragView", "长按")
                } else if (!isDoubleClick) {
                    Log.v("BaseDragView", "单击")
                    if (mCanvasTranslationX == 0f && mCanvasTranslationY == 0f) {
                        disappear()
                    }
                }
                isDoubleClick = false
                firstClickTime = 0L
                secondClickTime = 0L
            }, DOUBLE_CLICK_INTERVAL)
        } else {
            secondClickTime = System.currentTimeMillis()
            if (secondClickTime - firstClickTime < DOUBLE_CLICK_INTERVAL) {//两次点击小于DOUBLE_CLICK_INTERVAL
                Log.v("BaseDragView", "双击")
                isDoubleClick = true
            }
            firstClickTime = 0L
            secondClickTime = 0L
        }
    }

    /**
     * 控制相关动画，由子类调用
     */
    protected fun onActionUp(event: MotionEvent) {
        // 防止下拉的时候双手缩放
        if (event.pointerCount == 1) {
            if (mCanvasTranslationY > mMaxCanvasTranslationY) {
                onDestroy()
                exit()
            } else {
                restore()
            }
        }
        isUp = true
    }

    fun delay1000Millis(action: () -> Unit) {
        postDelayed(action, 1000)
    }

    fun delay100Millis(action: () -> Unit) {
        postDelayed(action, 100)
    }

    abstract fun onDestroy()
}