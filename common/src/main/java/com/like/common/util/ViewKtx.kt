package com.like.common.util

import android.view.View
import android.view.ViewTreeObserver

inline fun <T : View> T.onGlobalLayoutListener(crossinline block: (T) -> Unit): T {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            block(this@onGlobalLayoutListener)
        }
    })
    return this
}

inline fun <T : View> T.onPreDrawListener(crossinline block: (T) -> Unit): T {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            viewTreeObserver.removeOnPreDrawListener(this)
            block(this@onPreDrawListener)
            return true
        }
    })
    return this
}