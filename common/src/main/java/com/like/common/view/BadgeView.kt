package com.like.common.view

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.like.common.util.dp
import com.like.common.util.sp

/**
 * 角标
 * 注意：
 * 1、如果用代码创建对象，则需要调用[setTargetView]方法。
 * 2、如果在xml中直接使用，则不需要调用[setTargetView]方法。
 */
open class BadgeView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
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
    private var backgroundColor: Int = -1
    private var backgroundBorderColor: Int = -1
    private var backgroundBorderWidth: Int = -1
    private lateinit var textBounds: Rect
    private lateinit var bgRect: RectF
    private var verticalPadding = 0f.dp// 垂直padding
    private var horizontalPadding = 0f.dp// 水平padding
    var text: String = ""
        set(value) {
            if (field == value) {
                return
            }
            val needRequestLayout = value.length != field.length
            field = value
            visibility = if (field.isEmpty()) {
                GONE
            } else {
                textBounds = Rect().apply {
                    textPaint.getTextBounds(field, 0, field.length, this)
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
        backgroundColor = color
    }

    fun setBackgroundBorder(color: Int, width: Int) {
        backgroundBorderColor = color
        backgroundBorderWidth = width
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
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
        bgRect = if (backgroundBorderWidth > 0) {
            RectF(
                0f + backgroundBorderWidth,
                0f + backgroundBorderWidth,
                (width - backgroundBorderWidth).toFloat(),
                (height - backgroundBorderWidth).toFloat()
            )
        } else {
            RectF(0f, 0f, width.toFloat(), height.toFloat())
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (text.isEmpty()) return
        if (backgroundBorderWidth > 0) {
            bgPaint.color = backgroundBorderColor
            bgPaint.strokeWidth = backgroundBorderWidth.toFloat()
            bgPaint.style = Paint.Style.STROKE
            canvas.drawRoundRect(bgRect, height / 2f, height / 2f, bgPaint)
        }
        bgPaint.color = backgroundColor
        bgPaint.style = Paint.Style.FILL
        canvas.drawRoundRect(bgRect, height / 2f, height / 2f, bgPaint)
        canvas.drawText(
            text,
            horizontalPadding,
            -textPaint.fontMetrics.ascent - textPaint.fontMetrics.leading + verticalPadding,
            textPaint
        )
    }

}