package com.like.common.view.dragview.view.util

import android.view.View

fun View.postDelayed(interval: Long, action: () -> Unit) {
    postDelayed(action, interval)
}