@file:Suppress("NOTHING_TO_INLINE")

package com.like.common.util

import android.graphics.Paint
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

inline fun TextView.setDelLine() {
    this.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
}

inline fun TextView.setBold() {
    this.paint.flags = Paint.FAKE_BOLD_TEXT_FLAG
}

inline fun TextView.setUnderLine() {
    this.paint.flags = Paint.UNDERLINE_TEXT_FLAG
}

inline fun TextView.setSpan(content: String, startEndPairs: List<Pair<Int, Int>>, @ColorRes colors: List<Int>) {
    if (startEndPairs.size != colors.size) {
        this.text = content
        return
    }
    val string = SpannableString(content)
    val context = this.context
    startEndPairs.forEachIndexed { index, pair ->
        val span = ForegroundColorSpan(ContextCompat.getColor(context, colors[index]))
        string.setSpan(span, pair.first, pair.second, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    this.text = string
}

inline fun TextView.setSpan(content: String, start: Int, end: Int, @ColorRes color: Int) {
    val string = SpannableString(content)
    string.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    this.text = string
}

inline fun TextView.setSpan(content: String, positions: IntArray, @ColorRes color: Int) {
    val string = SpannableString(content)
    positions.forEach {
        string.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, color)), it, it + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    this.text = string
}

/**
 * @param drawableOrientation   在[TextView]上调用 drawableStart、drawableTop、drawableEnd、drawableBottom 的标记。
 * 分别对应：1、2、3、4
 * @param width                 图片宽度，dp
 * @param height                图片高度，dp
 */
fun TextView.setDrawable(drawableOrientation: Int, @DrawableRes resId: Int, width: Float, height: Float) {
    val drawable = this.resources.getDrawable(resId)
    drawable.setBounds(0, 0,
            DimensionUtils.dp2px(this.context, width),
            DimensionUtils.dp2px(this.context, height)
    )// 这里使用了setCompoundDrawables()方法，必须设置图片大小
    when (drawableOrientation) {
        1 -> {
            this.setCompoundDrawables(drawable, null, null, null)
        }
        2 -> {
            this.setCompoundDrawables(null, drawable, null, null)
        }
        3 -> {
            this.setCompoundDrawables(null, null, drawable, null)
        }
        4 -> {
            this.setCompoundDrawables(null, null, null, drawable)
        }
    }
}