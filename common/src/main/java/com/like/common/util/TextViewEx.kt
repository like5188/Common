@file:Suppress("NOTHING_TO_INLINE")

package com.like.common.util

import android.graphics.Paint
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView

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
