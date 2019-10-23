package com.like.common.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.like.common.R

/**
 * 可以设置宽高比的 ImageView
 */
/*
<com.like.common.view.AspectRatioImageView
    android:id="@+id/ariv"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    app:height_ratio="3.5"
    app:width_ratio="2.5"/>
 */
class AspectRatioImageView(context: Context, attrs: AttributeSet?) : ImageView(context, attrs) {
    companion object {
        private const val DEFAULT_WIDTH_RATIO = 2.5f
        private const val DEFAULT_HEIGHT_RATIO = 3.5f
    }

    private var widthRatio = 0f
    private var heightRatio = 0f

    init {

        val a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView)
        try {
            widthRatio = a.getFloat(R.styleable.AspectRatioImageView_width_ratio, 0f)
            heightRatio = a.getFloat(R.styleable.AspectRatioImageView_height_ratio, 0f)

            if (widthRatio == 0f || heightRatio == 0f) {
                widthRatio = DEFAULT_WIDTH_RATIO
                heightRatio = DEFAULT_HEIGHT_RATIO
            }
        } finally {
            a.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = MeasureSpec.getSize(widthMeasureSpec) * heightRatio / widthRatio
        val heightSpec = MeasureSpec.makeMeasureSpec(height.toInt(), MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, heightSpec)
    }
}