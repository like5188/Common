package com.like.common.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes

private val handler = Handler(Looper.getMainLooper())

data class ToastEvent(
    @StringRes val res: Int = -1,
    val text: String? = null,
    val throwable: Throwable? = null
)

fun Context.showToast(toastEvent: ToastEvent) {
    var msg = try {
        this.getString(toastEvent.res)
    } catch (e: Exception) {
        null
    }
    if (msg.isNullOrEmpty()) {
        msg = toastEvent.text
    }
    if (msg.isNullOrEmpty()) {
        msg = toastEvent.throwable.getCustomMessage()
    }
    this.showToast(msg)
}

fun Context.showToast(throwable: Throwable?) {
    showToast(throwable.getCustomMessage())
}

fun Context.showToast(@StringRes res: Int) {
    val text = try {
        this.getString(res)
    } catch (e: Exception) {
        null
    }
    showToast(text)
}

fun Context.showToast(text: String?) {
    if (Looper.getMainLooper() === Looper.myLooper()) {
        Toast.makeText(this, text ?: "", Toast.LENGTH_SHORT).show()
    } else {
        handler.post { Toast.makeText(this, text ?: "", Toast.LENGTH_SHORT).show() }
    }
}

fun Throwable?.getCustomMessage(): String {
    return (this?.message ?: this?.javaClass?.name) ?: "unknown error"
}
