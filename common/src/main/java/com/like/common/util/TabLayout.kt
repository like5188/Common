package com.like.common.util

import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.tabs.TabLayout
import com.like.common.view.BadgeView

/**
 * 便于 [TabLayout.Tab] 设置 [BadgeView] 的工具类。
 */

fun TabLayout.Tab.getTextView(): AppCompatTextView? {
    return view.getChildAt(1) as? AppCompatTextView
}

fun TabLayout.Tab.setBadgeView(): BadgeView? {
    val textView = getTextView() ?: return null
    return BadgeView(textView.context).apply {
        setTargetView(textView)
    }
}

fun TabLayout.Tab.getBadgeView(): BadgeView? {
    return (view.getChildAt(1) as? FrameLayout)?.getChildAt(1) as? BadgeView
}