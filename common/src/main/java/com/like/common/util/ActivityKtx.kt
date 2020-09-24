package com.like.common.util

import android.app.Activity
import android.content.Context
import com.like.common.base.BaseApplication

/**
 * 通过[BaseApplication]启动[Activity]
 */
inline fun <reified T : Activity> startActivityByApplication(vararg params: Pair<String, Any?>) {
    BaseApplication.sInstance.apply {
        val intent = createIntent<T>(*params).newTask()
        startActivity(intent)
    }
}

/**
 * 通过[Context]启动[Activity]
 */
inline fun <reified T : Activity> Context.startActivity(vararg params: Pair<String, Any?>) {
    val intent = createIntent<T>(*params)
    startActivity(intent)
}