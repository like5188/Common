@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.like.common.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

object ToastHelper {
    private val handler = Handler(Looper.getMainLooper())
    private var toast: Toast? = null// 只保留一个Toast实例，是为了让新的Toast把旧的Toast顶出去

    /**
     * 显示自定义时长、位置的提示
     *
     * @param resId      toast显示的内容的字符串资源id
     * @param duration   toast显示的时长。[Toast.LENGTH_LONG]、[Toast.LENGTH_SHORT]
     * @param gravity    toast的位置。[android.view.Gravity]
     */
    fun show(context: Context, @StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT, gravity: Int = Gravity.BOTTOM) {
        show(context, context.resources.getText(resId), duration, gravity)
    }

    /**
     * 显示自定义时长、位置的提示
     *
     * @param text       toast显示的内容
     * @param duration   toast显示的时长。[Toast.LENGTH_LONG]、[Toast.LENGTH_SHORT]
     * @param gravity    toast的位置。[android.view.Gravity]
     */
    @SuppressLint("ShowToast")
    fun show(context: Context, text: CharSequence?, duration: Int = Toast.LENGTH_SHORT, gravity: Int = Gravity.BOTTOM) {
        context.runOnUiThread {
            if (toast == null) {
                toast = Toast.makeText(context.applicationContext, text?.toString()
                        ?: "null", duration)
            } else {
                // 替换Toast的mNextView，避免gravity无效
                toast?.view = Toast.makeText(context.applicationContext, text?.toString()
                        ?: "null", Toast.LENGTH_SHORT).view
            }
            toast?.setGravity(gravity, 0, 0)
            toast?.show()
        }
    }

    /**
     * 显示自定义时长、位置、自定义视图的提示
     *
     * @param view       自定义的视图
     * @param duration   toast显示的时长。[Toast.LENGTH_LONG]、[Toast.LENGTH_SHORT]
     * @param gravity    toast的位置。[android.view.Gravity]
     */
    fun show(context: Context, view: View, duration: Int = Toast.LENGTH_SHORT, gravity: Int = Gravity.BOTTOM) {
        context.runOnUiThread {
            if (toast == null) {
                toast = Toast(context.applicationContext)
            }
            toast?.view = view
            toast?.duration = duration
            toast?.setGravity(gravity, 0, 0)
            toast?.show()
        }
    }

    /**
     * Execute [f] on the application UI thread.
     */
    private fun Context.runOnUiThread(f: Context.() -> Unit) {
        if (Looper.getMainLooper() === Looper.myLooper()) f() else handler.post { f() }
    }
}

/**
 * @param resId      toast显示的文本资源id
 * @param duration   toast显示的时长。[Toast.LENGTH_LONG]、[Toast.LENGTH_SHORT]
 * @param gravity    toast的位置。[android.view.Gravity]
 */
inline fun Context.toast(@StringRes resId: Int, duration: Int, gravity: Int) = ToastHelper.show(this, resId, duration, gravity)

/**
 * @param message    toast显示的文本
 * @param duration   toast显示的时长。[Toast.LENGTH_LONG]、[Toast.LENGTH_SHORT]
 * @param gravity    toast的位置。[android.view.Gravity]
 */
inline fun Context.toast(message: CharSequence?, duration: Int, gravity: Int) = ToastHelper.show(this, message, duration, gravity)

/**
 * @param view       自定义的View
 * @param duration   toast显示的时长。[Toast.LENGTH_LONG]、[Toast.LENGTH_SHORT]
 * @param gravity    toast的位置。[android.view.Gravity]
 */
inline fun Context.toast(view: View, duration: Int, gravity: Int) = ToastHelper.show(this, view, duration, gravity)

inline fun Context.shortToastBottom(@StringRes resId: Int) = this.toast(resId, Toast.LENGTH_SHORT, Gravity.BOTTOM)
inline fun Context.shortToastBottom(message: CharSequence?) = this.toast(message, Toast.LENGTH_SHORT, Gravity.BOTTOM)
inline fun Context.shortToastBottom(view: View) = this.toast(view, Toast.LENGTH_SHORT, Gravity.BOTTOM)

inline fun Context.shortToastCenter(@StringRes resId: Int) = this.toast(resId, Toast.LENGTH_SHORT, Gravity.CENTER)
inline fun Context.shortToastCenter(message: CharSequence?) = this.toast(message, Toast.LENGTH_SHORT, Gravity.CENTER)
inline fun Context.shortToastCenter(view: View) = this.toast(view, Toast.LENGTH_SHORT, Gravity.CENTER)

inline fun Context.longToastBottom(@StringRes resId: Int) = this.toast(resId, Toast.LENGTH_LONG, Gravity.BOTTOM)
inline fun Context.longToastBottom(message: CharSequence?) = this.toast(message, Toast.LENGTH_LONG, Gravity.BOTTOM)
inline fun Context.longToastBottom(view: View) = this.toast(view, Toast.LENGTH_LONG, Gravity.BOTTOM)

inline fun Context.longToastCenter(@StringRes resId: Int) = this.toast(resId, Toast.LENGTH_LONG, Gravity.CENTER)
inline fun Context.longToastCenter(message: CharSequence?) = this.toast(message, Toast.LENGTH_LONG, Gravity.CENTER)
inline fun Context.longToastCenter(view: View) = this.toast(view, Toast.LENGTH_LONG, Gravity.CENTER)


inline fun Fragment.toast(@StringRes resId: Int, duration: Int, gravity: Int) = activity?.toast(resId, duration, gravity)
inline fun Fragment.toast(message: CharSequence?, duration: Int, gravity: Int) = activity?.toast(message, duration, gravity)
inline fun Fragment.toast(view: View, duration: Int, gravity: Int) = activity?.toast(view, duration, gravity)

inline fun Fragment.shortToastBottom(@StringRes resId: Int) = this.toast(resId, Toast.LENGTH_SHORT, Gravity.BOTTOM)
inline fun Fragment.shortToastBottom(message: CharSequence?) = this.toast(message, Toast.LENGTH_SHORT, Gravity.BOTTOM)
inline fun Fragment.shortToastBottom(view: View) = this.toast(view, Toast.LENGTH_SHORT, Gravity.BOTTOM)

inline fun Fragment.shortToastCenter(@StringRes resId: Int) = this.toast(resId, Toast.LENGTH_SHORT, Gravity.CENTER)
inline fun Fragment.shortToastCenter(message: CharSequence?) = this.toast(message, Toast.LENGTH_SHORT, Gravity.CENTER)
inline fun Fragment.shortToastCenter(view: View) = this.toast(view, Toast.LENGTH_SHORT, Gravity.CENTER)

inline fun Fragment.longToastBottom(@StringRes resId: Int) = this.toast(resId, Toast.LENGTH_LONG, Gravity.BOTTOM)
inline fun Fragment.longToastBottom(message: CharSequence?) = this.toast(message, Toast.LENGTH_LONG, Gravity.BOTTOM)
inline fun Fragment.longToastBottom(view: View) = this.toast(view, Toast.LENGTH_LONG, Gravity.BOTTOM)

inline fun Fragment.longToastCenter(@StringRes resId: Int) = this.toast(resId, Toast.LENGTH_LONG, Gravity.CENTER)
inline fun Fragment.longToastCenter(message: CharSequence?) = this.toast(message, Toast.LENGTH_LONG, Gravity.CENTER)
inline fun Fragment.longToastCenter(view: View) = this.toast(view, Toast.LENGTH_LONG, Gravity.CENTER)