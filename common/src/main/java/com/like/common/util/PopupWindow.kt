package com.like.common.util

import android.view.View
import android.widget.PopupWindow

/**
 * 以 View 为内容视图来创建一个 PopupWindow。
 *
 * @param isFocusable   是否响应手机返回键或者点击外部区域消失
 */
fun View.toPopupWindow(width: Int = 0, height: Int = 0, isFocusable: Boolean = true): PopupWindow {
    var realWidth = width
    var realHeight = height
    if (realWidth == 0 || realHeight == 0) {
        this.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        realWidth = this.measuredWidth
        realHeight = this.measuredHeight
    }
    // 这里必须设置指定的宽高，否则showAsDropDown默认只会向下弹出显示，这种情况有个最明显的缺点就是：弹窗口可能被屏幕截断，显示不全。
    return PopupWindow(this, realWidth, realHeight).apply { this.isFocusable = isFocusable }
}