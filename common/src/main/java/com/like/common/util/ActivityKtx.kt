package com.like.common.util

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

/*
 * 启动Activity，传递参数的注入等
 */

inline fun <reified T : Activity> Context.startActivity(vararg params: Pair<String, Any?>) {
    startActivity(createIntent<T>(*params))
}

inline fun <reified T : Activity> ComponentActivity.startActivityForResult(vararg params: Pair<String, Any?>, crossinline callback: (ActivityResult) -> Unit) {
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        callback(it)
    }.launch(createIntent<T>(*params))
}

inline fun <reified T : Activity> Fragment.startActivity(vararg params: Pair<String, Any?>) {
    val act = activity ?: return
    startActivity(act.createIntent<T>(*params))
}

inline fun <reified T : Activity> Fragment.startActivityForResult(vararg params: Pair<String, Any?>, crossinline callback: (ActivityResult) -> Unit) {
    val act = activity ?: return
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        callback(it)
    }.launch(act.createIntent<T>(*params))
}

/**
 * 通过反射从[android.content.Intent]中获取参数值，并赋值给被[AutoWired]注解的字段。
 *
 * 注意：字段名必须与传递参数的key一致
 */
fun Activity.injectForIntentExtras() {
    val extras = intent.extras
    if (extras == null || extras.isEmpty) {
        return
    }
    try {
        javaClass.declaredFields.forEach { field ->
            if (field.isAnnotationPresent(AutoWired::class.java)) {
                val key = field.name
                if (!extras.containsKey(key)) {
                    Logger.w("Field $key Annotated by @AutoWired not found in ${javaClass.simpleName}")
                    return
                }
                field.isAccessible = true
                field.set(this, extras[key])
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}