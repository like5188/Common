package com.like.common.view

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.imageview.ShapeableImageView
import com.like.common.R

/**
 * 可以设置宽高比、形状的 ImageView。
 */
/*
<com.like.common.view.RatioShapeableImageView
    android:id="@+id/ariv"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    app:shapeAppearanceOverlay="@style/CircleRatioShapeableImageView"
    app:height_ratio="3.5"
    app:width_ratio="2.5"/>
 */
class RatioShapeableImageView(context: Context, attrs: AttributeSet?) : ShapeableImageView(context, attrs) {
    private var widthRatio = 0f
    private var heightRatio = 0f

    init {

        val a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView)
        widthRatio = a.getFloat(R.styleable.AspectRatioImageView_width_ratio, 0f)
        heightRatio = a.getFloat(R.styleable.AspectRatioImageView_height_ratio, 0f)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightSpec = if (widthRatio == 0f || heightRatio == 0f) {
            heightMeasureSpec
        } else {
            val height = MeasureSpec.getSize(widthMeasureSpec) * heightRatio / widthRatio
            MeasureSpec.makeMeasureSpec(height.toInt(), MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightSpec)
    }
}