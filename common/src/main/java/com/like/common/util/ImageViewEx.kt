package com.like.common.util

import android.content.res.ColorStateList
import android.graphics.drawable.StateListDrawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

fun ImageView.setTint() {
    val colors = intArrayOf(ContextCompat.getColor(context, com.like.common.R.color.common_text_red_0), ContextCompat.getColor(context, com.like.common.R.color.bar_grey))
    val states = arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf())
    setTint(colors, states)
}

/**
 * 为 [ImageView] 设置 tint 属性，来替代 selector 改变指定状态下的图片颜色。
 *
 * @param colors    不同状态下显示的颜色
 * @param states    状态数组。例如：[android.R.attr.state_pressed]，正常状态时传递空数组即可
 * 例子：
 * val colors = intArrayOf(ContextCompat.getColor(context, R.color.normalColor), ContextCompat.getColor(context, R.color.pressColor))
 * val states = arrayOf(intArrayOf(), intArrayOf(android.R.attr.state_pressed))
 */
fun ImageView.setTint(colors: IntArray, states: Array<IntArray>) {
    val colorList = ColorStateList(states, colors)
    val stateListDrawable = StateListDrawable()
    stateListDrawable.addState(states[0], drawable)//注意顺序
    stateListDrawable.addState(states[1], drawable)
    val state = stateListDrawable.constantState
    val wrapDrawable = DrawableCompat.wrap(state?.newDrawable() ?: drawable).mutate()
    DrawableCompat.setTintList(wrapDrawable, colorList)
    setImageDrawable(wrapDrawable)
}