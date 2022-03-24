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
     * 设置状态栏为透明
     */
    fun setStatusBarTranslucent(activity: Activity?) {
        activity ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            activity.window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                // 两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                statusBarColor = Color.TRANSPARENT
            }
        } else {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    /**
     * 对[view]增加状态栏高度的[paddingTop]
     * 注意：如果[view]是[RelativeLayout]，那么增加[paddingTop]会影响到使用了[layout_centerVertical]、[layout_centerInParent]这两个在垂直方向上居中的属性的子view。
     * 如果它的子view使用了这两个属性，那么需要自行处理，最好再包裹一层。
     */
    fun fitStatusBarHeight(view: View) {
        val layoutParams = view.layoutParams
        if (layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT &&
            layoutParams.height != ViewGroup.LayoutParams.MATCH_PARENT
        ) {// 如果是固定高度的控件，则需要改变它的高度，为它增加一个状态栏高度。
            layoutParams.height += getStatusBarHeight(view.context)
        }
        view.setPadding(
            view.paddingLeft,
            view.paddingTop + getStatusBarHeight(view.context),
            view.paddingRight,
            view.paddingBottom
        )
    }

    /**
     * 设置状态栏中的文字、图标颜色为暗色或者亮色。
     *
     * @param lightMode true：设置状态栏字体颜色为暗色；false：设置状态栏字体颜色为亮色
     */
    fun setStatusBarLightMode(activity: Activity, lightMode: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var option: Int = activity.window.decorView.systemUiVisibility
            option = if (lightMode) {
                // SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 设置状态栏字体颜色为暗色
                option or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                option and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            activity.window.decorView.systemUiVisibility = option
        }
    }

    /**
     * 获得状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int =
        context.resources.getDimensionPixelSize(
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        )

}