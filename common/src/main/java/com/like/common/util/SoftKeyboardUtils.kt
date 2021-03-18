package com.like.common.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.util.concurrent.atomic.AtomicBoolean

object SoftKeyboardUtils {

    private fun getInputMethodManager(context: Context): InputMethodManager {
        return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    /**
     * 软键盘显示隐藏监听
     *
     * @param onStatusChangedListener   软键盘显示隐藏回调。true：显示；false：隐藏；
     */
    fun setOnStatusChangedListener(activity: Activity, onStatusChangedListener: (Boolean) -> Unit) {
        val isShown = AtomicBoolean(false)//纪录根视图的显示高度
        activity.window.decorView.viewTreeObserver.addOnGlobalLayoutListener {
            val isShowing = isShowing(activity)
            if (isShown.compareAndSet(!isShowing, isShowing)) {
                onStatusChangedListener(isShowing)
            }
        }
    }

    /**
     * 显示键盘
     */
    fun show(editText: EditText) {
        editText.requestFocus()
        getInputMethodManager(editText.context).showSoftInput(
                editText,
                InputMethodManager.SHOW_IMPLICIT
        )
    }

    /**
     * 隐藏键盘
     */
    fun hide(view: View) {
        getInputMethodManager(view.context).hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * 隐藏键盘
     */
    fun hide(activity: Activity) {
        activity.currentFocus?.let {
            hide(it)
        }
    }

    /**
     * 判断软键盘是否弹出
     */
    fun isShowing(activity: Activity): Boolean {
        return getSoftKeyboardHeight(activity) > 0
    }

    /**
     * 获取软键盘高度。
     * 注意：需要在软键盘弹出后获取
     */
    fun getSoftKeyboardHeight(activity: Activity): Int {
        val decorView = activity.window.decorView
        val screenHeight = decorView.height
        val statusBarHeight = getStatusBarHeight(activity)
        val navigationBarHeight = getNavigationBarHeight(activity)
        val r = Rect()
        decorView.getWindowVisibleDisplayFrame(r)//可见的内容区域
        val displayHeight: Int = r.bottom - r.top
        return screenHeight - statusBarHeight - navigationBarHeight - displayHeight
    }

    /**
     * 获得状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        val resources = context.applicationContext.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 获取底部导航栏高度
     */
    fun getNavigationBarHeight(context: Context): Int {
        val resources = context.applicationContext.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }
}