package com.like.common.util

import android.content.res.ColorStateList
import android.graphics.drawable.StateListDrawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

fun ImageView.setTint() {
    val colors = intArrayOf(ContextCompat.getColor(context, com.like.common.R.color.common_text_red_0), ContextCompat.getColor(context, com.like.common.R.color.bar_grey))
    val states = arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf())
    setTint(ContextCompat.getColor(context, com.like.common.R.color.bar_grey), ContextCompat.getColor(context, com.like.common.R.color.common_text_red_0))
}

/**
 * 为 [ImageView] 设置 tint 属性，来替代 selector 改变指定状态下的图片颜色。
 *
 * @param normalColor   正常状态下的颜色
 * @param pressColor    按下状态下的颜色
 */
fun ImageView.setTint(normalColor: Int, pressColor: Int) {
    val colors = intArrayOf(pressColor, normalColor)
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
    // wrap() 通过这个方法获取的Drawable对象，在使用DrawableCompat类中的染色一类的方法，可以在不同的API级别上应用着色。因此想要着色就先把原先的Drawable对象wrap一下后回去到新的Drawable对象。
    // mutate() 可以让一个应用中使用同样的片的地方不受影响，因为默认情况下，相同的图片资源是被共享同一种状态的。如果修改了一个实例的状态，那么其他使用这个实例的地方都会被修改。因此这个方法的作用就是让其他地方不受影响。
    val wrapDrawable = DrawableCompat.wrap(state?.newDrawable() ?: stateListDrawable).mutate()
    DrawableCompat.setTintList(wrapDrawable, colorList)
    setImageDrawable(wrapDrawable)
}
