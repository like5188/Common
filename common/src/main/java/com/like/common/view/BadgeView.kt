package com.like.common.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import com.like.common.util.dp

/**
 * 数字角标
 */
open class BadgeView(context: Context) : AppCompatTextView(context) {
    var count = 0
        set(value) {
            field = value
            text = transformCountToText(value)
        }

    init {
        if (layoutParams !is FrameLayout.LayoutParams) {
            this.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.END or Gravity.TOP
            )
        }

        this.textSize = 12f
        this.setTextColor(Color.WHITE)
        this.setBackgroundColor(Color.RED)
        count = 0
        gravity = Gravity.CENTER
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

    override fun setBackgroundColor(color: Int) {
        val radius = Float.MAX_VALUE
        val roundRect = RoundRectShape(
            floatArrayOf(
                radius,
                radius,
                radius,
                radius,
                radius,
                radius,
                radius,
                radius
            ), null, null
        )
        val bgDrawable = ShapeDrawable(roundRect)
        bgDrawable.paint.color = color
        background = bgDrawable
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (hide()) return
        val w = measuredWidth
        val h = measuredHeight
        if (text.length < 2) { // 只有1个字符，就画圆
            val padding = (h - w) / 2
            super.setMeasuredDimension(h, h)
            setPadding(padding, 0, padding, 0)
        } else {
            val padding = 6.dp
            super.setMeasuredDimension(w + padding * 2, h)
            setPadding(padding * 2, 0, 0, 0)
        }
    }

    override fun setText(text: CharSequence?, type: BufferType) {
        visibility = if (hide()) {
            GONE
        } else {
            VISIBLE
        }
        super.setText(text, type)
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