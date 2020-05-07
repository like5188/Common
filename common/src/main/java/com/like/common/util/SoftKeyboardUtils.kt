package com.like.common.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager

object SoftKeyboardUtils {

    private fun getInputMethodManager(context: Context): InputMethodManager {
        return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    /**
     * 显示键盘
     *
     * @param view The currently focused view, which would like to receive soft keyboard input.
     */
    fun show(view: View) {
        getInputMethodManager(view.context)
                .showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    /**
     * 隐藏键盘
     */
    fun hide(activity: Activity) {
        getInputMethodManager(activity)
                .hideSoftInputFromWindow(activity.window.decorView.windowToken, InputMethodManager.SHOW_IMPLICIT)
    }

    /**
     * 键盘是否已经打开
     */
    fun isShowing(activity: Activity): Boolean {
        // 获取当前屏幕内容的高度
        val screenHeight = activity.window.decorView.height
        // 获取View可见区域的bottom
        val rect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(rect)
        return screenHeight - rect.bottom - getSoftButtonsBarHeight(activity) != 0
    }

    /**
     * 底部虚拟按键栏的高度
     */
    private fun getSoftButtonsBarHeight(activity: Activity): Int {
        val metrics = DisplayMetrics()
        // 这个方法获取可能不是真实屏幕的高度
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val usableHeight = metrics.heightPixels
        // 获取当前屏幕的真实高度
        activity.windowManager.defaultDisplay.getRealMetrics(metrics)
        val realHeight = metrics.heightPixels
        return if (realHeight > usableHeight) {
            realHeight - usableHeight
        } else {
            0
        }
    }
}