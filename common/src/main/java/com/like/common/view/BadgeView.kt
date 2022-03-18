package com.like.common.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import com.like.common.util.dp
import com.like.common.util.sp
import kotlin.math.abs

open class BadgeView(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    AppCompatTextView(context, attrs, defStyle) {
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

        this.textSize = 12f.sp
        this.setTextColor(Color.WHITE)
        this.setBackgroundColor(Color.RED)
        gravity = Gravity.CENTER
        count = 0
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
                (target.parent as FrameLayout).addView(this)
            }
            is ViewGroup -> {
                // use a new FrameLayout container for adding badge
                val parentContainer = target.parent as ViewGroup
                val groupIndex = parentContainer.indexOfChild(target)
                parentContainer.removeView(target)

                val oldTargetLayoutParams = target.layoutParams
                target.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
                )

                FrameLayout(context).apply {
                    layoutParams = oldTargetLayoutParams
                    addView(target)
                    addView(this@BadgeView)
                    parentContainer.addView(this, groupIndex, oldTargetLayoutParams)
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
        val padding: Int
        if (text.length < 2) { // 只有1个字符，就画圆
            padding = abs(h - w) / 2
            super.setMeasuredDimension(h, h)
        } else {
            padding = 4.dp
            super.setMeasuredDimension(w + padding * 2, h)
        }
        setPadding(padding, 0, padding, 0)
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
    open fun transformCountToText(count: Int): String? {
        return count.toString()
    }

    /**
     * 在什么时候隐藏 [BadgeView]
     */
    open fun hide(): Boolean = text.isNullOrEmpty() || text.toString() == "0"

}