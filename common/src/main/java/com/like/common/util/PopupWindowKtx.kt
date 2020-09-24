package com.like.common.util

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.PopupWindow

/**
 * 以 View 为内容视图来创建一个 PopupWindow。
 */
fun View.createPopupWindow(): PopupWindow {
    this.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    // 这里必须设置指定的宽高，否则showAsDropDown默认只会向下弹出显示，这种情况有个最明显的缺点就是：弹窗口可能被屏幕截断，显示不全。
    return PopupWindow(this, this.measuredWidth, this.measuredHeight)
            .apply {
                // 必须同时设置下面三个属性，才能在点击外部区域或者返回键时，关闭PopupWindow
                setBackgroundDrawable(ColorDrawable(0x00000000))
                isOutsideTouchable = true
                isFocusable = true
            }
}