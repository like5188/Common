package com.like.common.util

import android.graphics.Rect
import android.graphics.RectF

fun RectF.toRect(): Rect = Rect().also {
    it.set(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
}

fun Rect.toRectF(): RectF = RectF().also {
    it.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
}

/**
 * 是否与指定的 Rect 有交集
 */
fun RectF.hasIntersectionWith(rect: RectF): Boolean =
    contains(rect.left, rect.top) ||
            contains(rect.right, rect.top) ||
            contains(rect.left, rect.bottom) ||
            contains(rect.right, rect.bottom)