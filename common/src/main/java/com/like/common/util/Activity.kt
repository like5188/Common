package com.like.common.util

import android.app.Activity
import android.app.Application
import android.content.Context

/**
 * 通过[Context]启动[Activity]
 */
inline fun <reified T : Activity> Context.startActivity(vararg params: Pair<String, Any?>) {
    val intent = createIntent<T>(*params)
    if (this !is Activity) {
        intent.newTask()
    }
    startActivity(intent)
}