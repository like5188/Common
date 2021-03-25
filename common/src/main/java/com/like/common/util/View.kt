package com.like.common.util

import android.view.View
import android.view.ViewTreeObserver
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible

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

fun View.visible() {
    if (!this.isVisible) {
        this.isVisible = true
    }
}

fun View.gone() {
    if (!this.isGone) {
        this.isGone = true
    }
}

fun View.invisible() {
    if (!this.isInvisible) {
        this.isInvisible = true
    }
}