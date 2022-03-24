package com.like.common.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager


/**
 * 沉浸工具类
 *
 * @author like
 * @version 1.0
 * created on 2017/5/9 17:30
 */
object StatusBarUtils {

    /**
     * 设置状态栏透明
     */
    fun setStatusBarTranslucent(activity: Activity?) {
        activity ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            activity.window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                // 两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                // SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 设置状态栏字体颜色为暗色
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                statusBarColor = Color.TRANSPARENT
            }
        } else {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    fun fitStatusBarHeight(view: View) {
        val layoutParams = view.layoutParams
        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT ||
            layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT
        ) {// 如果不是固定高度的控件，那么直接增加一个状态栏高度的paddingTop
            view.setPadding(
                view.paddingLeft,
                view.paddingTop + getStatusBarHeight(view.context),
                view.paddingRight,
                view.paddingBottom
            )
        } else {// 如果是写死高度的控件，则需要同时给高度增加一个状态栏高度。
            layoutParams.height += getStatusBarHeight(view.context)
            view.setPadding(
                view.paddingLeft,
                view.paddingTop + getStatusBarHeight(view.context),
                view.paddingRight,
                view.paddingBottom
            )
        }
    }

    fun setStatusBarLightMode(activity: Activity, isLightMode: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var option: Int = activity.window.decorView.systemUiVisibility
            option = if (isLightMode) {
                option or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                option and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            activity.window.decorView.systemUiVisibility = option
        }
    }

    /**
     * 获得状态栏高度
     *
     * @param context
     * @return
     */
    fun getStatusBarHeight(context: Context): Int =
        context.resources.getDimensionPixelSize(
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        )

}