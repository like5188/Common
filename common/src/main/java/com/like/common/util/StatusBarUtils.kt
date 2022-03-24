package com.like.common.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

/*
 * 状态栏工具类
 *
 * @author like
 * created on 2022/03/23
 */

/**
 * 设置透明的状态栏
 * @param dark  是否设置状态栏中的文字、图标颜色为暗色，只在 api>=23 时有效。
 */
fun Activity.setTransparentStatusBar(dark: Boolean = false) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        // 5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            // 两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            var option = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            if (dark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                option = option or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            decorView.systemUiVisibility = option
            statusBarColor = Color.TRANSPARENT
        }
    } else {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }
}

/**
 * 对[view]增加状态栏高度的[paddingTop]
 * 注意：如果[view]是[RelativeLayout]，那么增加[paddingTop]会影响到使用了[layout_centerVertical]、[layout_centerInParent]这两个在垂直方向上居中的属性的子view。
 * 如果它的子view使用了这两个属性，那么需要自行处理，最好再包裹一层。
 */
fun View.fitStatusBar() {
    if (layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT &&
        layoutParams.height != ViewGroup.LayoutParams.MATCH_PARENT
    ) {// 如果是固定高度的控件，则需要改变它的高度，为它增加一个状态栏高度。
        layoutParams.height += context.statusBarHeight
    }
    setPadding(
        paddingLeft,
        paddingTop + context.statusBarHeight,
        paddingRight,
        paddingBottom
    )
}

/**
 * 状态栏高度
 */
val Context.statusBarHeight: Int
    get() {
        return resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"))
    }

