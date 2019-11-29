package com.like.common.util

import android.content.res.ColorStateList
import android.graphics.drawable.StateListDrawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

fun ImageView.setTint() {
    var drawable = ContextCompat.getDrawable(context, com.like.common.R.drawable.dialog_close)
    val colors = intArrayOf(ContextCompat.getColor(context, com.like.common.R.color.common_text_red_0), ContextCompat.getColor(context, com.like.common.R.color.bar_grey))
    val states = arrayOfNulls<IntArray>(2)
    states[0] = intArrayOf(android.R.attr.state_pressed)
    states[1] = intArrayOf()
    val colorList = ColorStateList(states, colors)
    val stateListDrawable = StateListDrawable()
    stateListDrawable.addState(states[0], drawable)//注意顺序
    stateListDrawable.addState(states[1], drawable)
    val state = stateListDrawable.constantState
    drawable = DrawableCompat.wrap(state?.newDrawable() ?: stateListDrawable).mutate()
    DrawableCompat.setTintList(drawable, colorList)
    setImageDrawable(drawable)
}