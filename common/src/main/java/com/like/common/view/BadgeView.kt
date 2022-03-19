package com.like.common.view

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.like.common.util.dp
import com.like.common.util.sp

/**
 * 数字角标
 * 注意：设置[count]需要反正最后，触发绘制。
 */
open class BadgeView(context: Context) : View(context) {
    private val textPaint by lazy {
        TextPaint().apply {
            isAntiAlias = true
            color = Color.WHITE
            textSize = 12f.sp
        }
    }
    private val bgPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.RED
        }
    }
    private lateinit var textBounds: Rect
    private lateinit var bgRect: RectF
    private var verticalPadding = 0f.dp// 垂直padding
    private var horizontalPadding = 0f.dp// 水平padding
    private var text = ""
    var count = 0
        set(value) {
            if (field == value) {
                return
            }
            val needRequestLayout = transformCountToText(value).length != transformCountToText(field).length
            field = value
            visibility = if (hide()) {
                GONE
            } else {
                text = transformCountToText(value)
                textBounds = Rect().apply {
                    textPaint.getTextBounds(text, 0, text.length, this)
                }
                VISIBLE
            }
            if (needRequestLayout) {// 需要重新onMeasure、onDraw
                requestLayout()
            } else {// 需要重新onDraw
                invalidate()
            }
        }

    init {
        this.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.END or Gravity.TOP
        )
    }

    fun setTargetView(target: View?) {
        if (parent != null) {
            (parent as ViewGroup).removeView(this)
        }
        if (target == null) {
            return
        }
        when (target.parent) {
            is FrameLayout -> {
                (target.parent as FrameLayout).addView(this@BadgeView)
            }
            is ViewGroup -> {
                // use a new FrameLayout container for adding badge
                val parentContainer = target.parent as ViewGroup
                val groupIndex = parentContainer.indexOfChild(target)
                parentContainer.removeView(target)

                FrameLayout(context).apply {
                    layoutParams = target.layoutParams

                    target.layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    addView(target)

                    addView(this@BadgeView)
                    parentContainer.addView(this, groupIndex)
                }
            }
            null -> {
                throw IllegalArgumentException("BadgeView setTargetView failure! parent of target is null")
            }
        }
    }

    fun setTextColor(color: Int) {
        textPaint.color = color
    }

    fun setTextSize(size: Float) {
        textPaint.textSize = size
    }

    override fun setBackgroundColor(color: Int) {
        bgPaint.color = color
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (hide()) return
        verticalPadding = textPaint.fontMetrics.descent / 2f
        val w = textPaint.measureText(text).toInt()
        val h =
            (textPaint.fontMetrics.descent - textPaint.fontMetrics.ascent - textPaint.fontMetrics.leading).toInt() + (verticalPadding * 2).toInt()
        if (text.length < 2) { // 只有1个字符，就画圆
            horizontalPadding = (h - w) / 2f
            super.setMeasuredDimension(h, h)
        } else {
            horizontalPadding = textPaint.fontMetrics.descent * 2f
            super.setMeasuredDimension(w + (horizontalPadding * 2f).toInt(), h)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bgRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        if (hide()) return
        canvas.drawRoundRect(bgRect, height / 2f, height / 2f, bgPaint)
        canvas.drawText(
            text,
            horizontalPadding,
            -textPaint.fontMetrics.ascent - textPaint.fontMetrics.leading + verticalPadding,
            textPaint
        )
    }

    /**
     * 把数字转换成字符串供 [BadgeView] 显示
     */
    open fun transformCountToText(count: Int): String {
        return when {
            count <= 0 -> ""
            count < 100 -> count.toString()
            else -> "99+"
        }
    }

    /**
     * 在什么时候隐藏 [BadgeView]
     */
    open fun hide(): Boolean = count <= 0

}