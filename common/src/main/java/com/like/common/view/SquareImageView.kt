package com.like.common.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

/**
 * 宽、高都为宽的正方形ImageView
 *
 * 注意：如果这里用AppCompatImageView，那么在Android 4.4手机中，布局文件中使用android:onClick="onClick"会在点击的时候报错：
 * java.lang.IllegalStateException: Could not find a method onClick(View) in the activity class androidx.appcompat.widget.TintContextWrapper for onClick handler on view class com.like.common.view.SquareImageView with id 'iv_1'
 */
class SquareImageView : ImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // We want a square view.
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}