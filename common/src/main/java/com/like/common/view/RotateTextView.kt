package com.like.common.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import com.like.common.R

/**
 * 能旋转角度的TextView
 */
class RotateTextView(context: Context, attrs: AttributeSet) : android.support.v7.widget.AppCompatTextView(context, attrs, android.R.attr.textViewStyle) {
    private val mDegrees: Float

    init {
        this.gravity = Gravity.CENTER
        val a = context.obtainStyledAttributes(attrs, R.styleable.RotateTextView)
        mDegrees = a.getFloat(R.styleable.RotateTextView_degree, 0f)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredWidth)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.translate(compoundPaddingLeft.toFloat(), extendedPaddingTop.toFloat())
        canvas.rotate(mDegrees, this.width / 2f, this.height / 2f)
        super.onDraw(canvas)
        canvas.restore()
    }

}