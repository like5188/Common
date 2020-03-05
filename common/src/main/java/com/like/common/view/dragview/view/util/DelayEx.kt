package com.like.common.view.dragview.view.util

import android.view.View

fun View.delay1000Millis(action: () -> Unit) {
    postDelayed(action, 1000)
}

fun View.delay100Millis(action: () -> Unit) {
    postDelayed(action, 100)
}