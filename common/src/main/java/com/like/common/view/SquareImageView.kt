package com.like.common.view

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet

/**
 * 宽、高都为宽的正方形ImageView
 */
class SquareImageView : AppCompatImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // We want a square view.
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}