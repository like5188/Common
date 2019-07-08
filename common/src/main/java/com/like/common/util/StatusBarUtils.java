package com.like.common.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * 沉浸工具类
 *
 * @author like
 * @version 1.0
 *          created on 2017/5/9 17:30
 */
public class StatusBarUtils {

    /**
     * Activity沉浸时使用
     *
     * @param rootView 根布局
     */
    public static void setStatusBarColorForActivity(ViewGroup rootView, @ColorInt int color) {
        if (rootView == null) {
            return;
        }
        // 判断当前sdk_int大于4.4(kitkat),则通过代码的形式设置status bar为透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = ((Activity) rootView.getContext()).getWindow();
            // 设置状态栏透明
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 底部虚拟导航栏（例如华为手机）
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 设置根布局的参数
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            // 生成一个状态栏大小的矩形
            View statusView = createStatusView(rootView.getContext(), color);
            // 添加 statusView 到根布局中
            decorView.addView(statusView);
            // 设置布局调整时是否考虑系统窗口
            // 让view可以根据系统窗口(如status bar)来调整自己的布局。
            // 如果值为true,就会调整view的paingding属性来给system windows留出空间
            // 当status bar为透明或半透明时(4.4以上),系统会设置view的paddingTop值为一个适合的值(status bar的高度)让view的内容不被上拉到状态栏，
            // 当在不占据status bar的情况下(4.4以下),会设置paddingTop值为0(因为没有占据status bar所以不用留出空间)。
            rootView.setFitsSystemWindows(true);
            // 定义ViewGroup是否将剪切它的绘制界面并排除padding区域。默认是true
            rootView.setClipToPadding(true);
        }
    }

    /**
     * Fragment沉浸时使用，使Fragment所属的Activity状态栏透明。然后在使用setStatusBarColorForFragment()方法设置沉浸
     *
     * @param activity 需要设置的activity
     */
    public static void setStatusBarTranslucent(Activity activity) {
        if (activity == null) {
            return;
        }
        // 判断当前sdk_int大于4.4(kitkat),则通过代码的形式设置status bar为透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 底部虚拟导航栏（例如华为手机）
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 设置根布局的参数
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            // 设置布局调整时是否考虑系统窗口
            // 让view可以根据系统窗口(如status bar)来调整自己的布局。
            // 如果值为true,就会调整view的paingding属性来给system windows留出空间
            // 当status bar为透明或半透明时(4.4以上),系统会设置view的paddingTop值为一个适合的值(status bar的高度)让view的内容不被上拉到状态栏，
            // 当在不占据status bar的情况下(4.4以下),会设置paddingTop值为0(因为没有占据status bar所以不用留出空间)。
            decorView.setFitsSystemWindows(true);
            // 定义ViewGroup是否将剪切它的绘制界面并排除padding区域。默认是true
            decorView.setClipToPadding(true);
        }
    }

    /**
     * Fragment沉浸时使用，设置状态栏颜色，并沉浸。需要配合setStatusBarTranslucent()方法使用
     * <p>
     * 先设置状态栏透明属性；
     * 给根布局加上一个和状态栏一样大小的矩形View（色块），添加到顶上；
     * 然后设置根布局的 FitsSystemWindows 属性为 true,此时根布局会延伸到状态栏，处在状态栏位置的就是之前添加的色块，这样就给状态栏设置上颜色了。
     *
     * @param rootView 根布局
     * @param color    状态栏颜色值
     */
    public static void setStatusBarColorForFragment(ViewGroup rootView, @ColorInt int color) {
        if (rootView == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 生成一个状态栏大小的矩形
            View statusView = createStatusView(rootView.getContext(), color);
            // 添加 statusView 到根布局中
            rootView.addView(statusView, 0);
        }
    }

    /**
     * 生成一个和状态栏大小相同的矩形条
     *
     * @param context
     * @param color   状态栏颜色值
     * @return 状态栏矩形条
     */
    private static View createStatusView(Context context, @ColorInt int color) {
        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(context));
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);
        return statusView;
    }

    /**
     * 获得状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
}
