package com.like.common.util

import android.app.Activity
import android.content.Context
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
     * 判断软键盘是否弹出
     */
    fun isShowing(view: View): Boolean {
        val imm: InputMethodManager = getInputMethodManager(view.context)
        return if (imm.hideSoftInputFromWindow(view.windowToken, 0)) {
            imm.showSoftInput(view, 0)
            true
        } else {
            false
        }
    }

}