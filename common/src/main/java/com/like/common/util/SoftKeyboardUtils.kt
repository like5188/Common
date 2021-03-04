package com.like.common.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity

object SoftKeyboardUtils {

    private fun getInputMethodManager(context: Context): InputMethodManager {
        return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    /**
     * 软键盘显示隐藏监听
     *
     * @param onStatusChangedListener   软键盘显示隐藏回调。true：显示；false：隐藏；
     */
    fun setOnStatusChangedListener(activity: FragmentActivity, onStatusChangedListener: (Boolean) -> Unit) {
        val decorView = activity.window.decorView//activity的根视图
        var decorViewVisibleHeight = 0//纪录根视图的显示高度
        decorView.viewTreeObserver.addOnGlobalLayoutListener {
            //获取当前根视图在屏幕上显示的大小
            val r = Rect()
            decorView.getWindowVisibleDisplayFrame(r)
            val visibleHeight = r.height()

            when {
                decorViewVisibleHeight == 0 -> decorViewVisibleHeight = visibleHeight
                decorViewVisibleHeight - visibleHeight > 200 -> {//根视图显示高度变小超过200，可以看作软键盘显示了
                    onStatusChangedListener(true)
                    decorViewVisibleHeight = visibleHeight
                }
                visibleHeight - decorViewVisibleHeight > 200 -> {//根视图显示高度变大超过200，可以看作软键盘隐藏了
                    onStatusChangedListener(false)
                    decorViewVisibleHeight = visibleHeight
                }
            }
        }
    }

    /**
     * 显示键盘
     *
     * @param view The currently focused view, which would like to receive soft keyboard input.
     */
    fun show(view: View) {
        getInputMethodManager(view.context).showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    /**
     * 隐藏键盘
     */
    fun hide(activity: Activity) {
        getInputMethodManager(activity).hideSoftInputFromWindow(activity.window.decorView.windowToken, InputMethodManager.SHOW_IMPLICIT)
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