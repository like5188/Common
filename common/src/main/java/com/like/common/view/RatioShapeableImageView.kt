package com.like.common.view

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.imageview.ShapeableImageView
import com.like.common.R

/**
 * 1、可以设置宽高比。
 * 2、可以设置形状。
 */
/*
<com.like.common.view.RatioShapeableImageView
    android:layout_width="match_parent"
    android:layout_height="0dp"
    app:shapeAppearanceOverlay="@style/CircleRatioShapeableImageView"
    app:height_ratio="3.5"
    app:width_ratio="2.5"/>
 */
class RatioShapeableImageView(context: Context, attrs: AttributeSet?) : ShapeableImageView(context, attrs) {
    var widthRatio = 0f
    var heightRatio = 0f

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView)
        widthRatio = a.getFloat(R.styleable.AspectRatioImageView_width_ratio, 0f)
        heightRatio = a.getFloat(R.styleable.AspectRatioImageView_height_ratio, 0f)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val newHeightMeasureSpec = if (widthRatio >= 0f && heightRatio >= 0f) {
            val height = MeasureSpec.getSize(widthMeasureSpec) * heightRatio / widthRatio
            MeasureSpec.makeMeasureSpec(height.toInt(), MeasureSpec.EXACTLY)
        } else {
            heightMeasureSpec
        }
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
    }

}
