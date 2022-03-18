package com.like.common.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.util.Log
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
        val w = measuredWidth
        val h = measuredHeight
        if (hide()) return
        val padding: Int
        if (text.length < 2) { // 宽小于高，并且只有1个字符，就画圆
            padding = abs(h - w) / 2
            super.setMeasuredDimension(h, h)
        } else {
            padding = 4.dp
            super.setMeasuredDimension(w + padding * 2, h)
        }
        setPadding(padding, 0, padding, 0)
    }

    private fun hide(): Boolean = text.isNullOrEmpty() || text.toString() == "0"

    override fun setText(text: CharSequence?, type: BufferType) {
        visibility = if (hide()) {
            GONE
        } else {
            VISIBLE
        }
        super.setText(text, type)
    }

    open fun transformCountToText(count: Int): String? {
        return count.toString()
    }

//    /*
//     * Attach the BadgeView to the TabWidget
//     *
//     * @param target the TabWidget to attach the BadgeView
//     *
//     * @param tabIndex index of the tab
//     */
//    fun setTargetView(target: TabWidget, tabIndex: Int) {
//        val tabView = target.getChildTabViewAt(tabIndex)
//        setTargetView(tabView)
//    }

    /*
     * Attach the BadgeView to the target dragPhotoView
     *
     * @param target the dragPhotoView to attach the BadgeView
     */
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
                // use a new Framelayout container for adding badge
                val parentContainer = target.parent as ViewGroup
                val groupIndex = parentContainer.indexOfChild(target)
                parentContainer.removeView(target)
                val badgeContainer = FrameLayout(context)
                val parentLayoutParams = target.layoutParams
                badgeContainer.layoutParams = parentLayoutParams
                target.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
                parentContainer.addView(badgeContainer, groupIndex, parentLayoutParams)
                badgeContainer.addView(target)
                badgeContainer.addView(this)
            }
            null -> {
                Log.e(javaClass.simpleName, "ParentView is needed")
            }
        }
    }

}