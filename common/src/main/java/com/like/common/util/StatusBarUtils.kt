package com.like.common.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.annotation.ColorInt


/**
 * 沉浸工具类
 *
 * @author like
 * @version 1.0
 * created on 2017/5/9 17:30
 */
object StatusBarUtils {
    /**
     * Activity沉浸时使用
     *
     * @param rootView 根布局
     */
    fun setStatusBarColorForActivity(rootView: ViewGroup?, @ColorInt color: Int) {
        if (rootView == null) {
            return
        }
        // 判断当前sdk_int大于4.4(kitkat),则通过代码的形式设置status bar为透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val window = (rootView.context as Activity).window
            // 设置状态栏透明
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // 底部虚拟导航栏（例如华为手机）
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 设置根布局的参数
            val decorView = window.decorView as ViewGroup
            // 生成一个状态栏大小的矩形
            val statusView = createStatusView(rootView.context, color)
            // 添加 statusView 到根布局中
            decorView.addView(statusView)
            // 设置布局调整时是否考虑系统窗口
            // 让view可以根据系统窗口(如status bar)来调整自己的布局。
            // 如果值为true,就会调整view的paingding属性来给system windows留出空间
            // 当status bar为透明或半透明时(4.4以上),系统会设置view的paddingTop值为一个适合的值(status bar的高度)让view的内容不被上拉到状态栏，
            // 当在不占据status bar的情况下(4.4以下),会设置paddingTop值为0(因为没有占据status bar所以不用留出空间)。
            rootView.fitsSystemWindows = true
            // 定义ViewGroup是否将剪切它的绘制界面并排除padding区域。默认是true
            rootView.clipToPadding = true
        }
    }

    /**
     * Fragment沉浸时使用，使Fragment所属的Activity状态栏透明。然后在使用setStatusBarColorForFragment()方法设置沉浸
     *
     * @param activity 需要设置的activity
     */
    fun setStatusBarTranslucent(activity: Activity?) {
        if (activity == null) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            activity.window.apply {
                // 两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                // SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 设置状态栏字体颜色为暗色
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = Color.TRANSPARENT
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 判断当前sdk_int大于4.4(kitkat),则通过代码的形式设置status bar为透明
            // 设置状态栏透明
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // 底部虚拟导航栏（例如华为手机）
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 设置根布局的参数
            val decorView = activity.window.decorView as ViewGroup
            // 设置布局调整时是否考虑系统窗口
            // 让view可以根据系统窗口(如status bar)来调整自己的布局。
            // 如果值为true,就会调整view的paingding属性来给system windows留出空间
            // 当status bar为透明或半透明时(4.4以上),系统会设置view的paddingTop值为一个适合的值(status bar的高度)让view的内容不被上拉到状态栏，
            // 当在不占据status bar的情况下(4.4以下),会设置paddingTop值为0(因为没有占据status bar所以不用留出空间)。
            decorView.fitsSystemWindows = true
            // 定义ViewGroup是否将剪切它的绘制界面并排除padding区域。默认是true
            decorView.clipToPadding = true
        }
    }

    /**
     * Fragment沉浸时使用，设置状态栏颜色，并沉浸。需要配合setStatusBarTranslucent()方法使用
     *
     *
     * 先设置状态栏透明属性；
     * 给根布局加上一个和状态栏一样大小的矩形View（色块），添加到顶上；
     * 然后设置根布局的 FitsSystemWindows 属性为 true,此时根布局会延伸到状态栏，处在状态栏位置的就是之前添加的色块，这样就给状态栏设置上颜色了。
     *
     * @param rootView 根布局
     * @param color    状态栏颜色值
     */
    fun setStatusBarColorForFragment(rootView: ViewGroup?, @ColorInt color: Int) {
        if (rootView == null) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 生成一个状态栏大小的矩形
            val statusView = createStatusView(rootView.context, color)
            // 添加 statusView 到根布局中
            rootView.addView(statusView, 0)
        }
    }

    /**
     * 生成一个和状态栏大小相同的矩形条
     *
     * @param context
     * @param color   状态栏颜色值
     * @return 状态栏矩形条
     */
    private fun createStatusView(context: Context, @ColorInt color: Int): View =
        View(context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(context))
            setBackgroundColor(color)
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