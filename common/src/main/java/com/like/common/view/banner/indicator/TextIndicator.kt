package com.like.common.view.banner.indicator

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.like.common.view.CircleTextView

/**
 * 文本指示器
 * 一个圆形的 TextView，显示内容为 1/3
 *
 * @param mContext
 * @param mDataCount    指示器的数量
 * @param mContainer    指示器的容器
 */
class TextIndicator(
        private val mContext: Context,
        private val mDataCount: Int,
        private val mContainer: ViewGroup
) : IBannerIndicator {
    private val mCircleTextView = CircleTextView(mContext)

    init {
        if (mDataCount > 0) {
            mContainer.removeAllViews()
            mContainer.addView(mCircleTextView)
        }
    }

    fun setTextColor(@ColorInt color: Int) {
        mCircleTextView.setTextColor(color)
    }

    fun setBackgroundColor(@ColorInt color: Int) {
        mCircleTextView.setBackgroundColor(color)
    }

    fun setTextSize(textSize: Float) {
        mCircleTextView.textSize = textSize
    }

    fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        mCircleTextView.setPadding(left, top, right, bottom)
    }

    override fun onPageSelected(position: Int) {
        if (mDataCount <= 0) return
        mCircleTextView.text = "${position + 1}/$mDataCount"
    }

}