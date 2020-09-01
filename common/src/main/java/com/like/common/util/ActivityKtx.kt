package com.like.common.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts

/*
 * startActivity
 * startActivityForResult
 * 权限请求
 * Intent传递的参数的注入等
 */

inline fun <reified T : Activity> Context.startActivity(vararg params: Pair<String, Any?>) {
    startActivity(createIntent<T>(*params))
}

inline fun <reified T : Activity> ComponentActivity.startActivityForResult(vararg params: Pair<String, Any?>, crossinline callback: (ActivityResult) -> Unit) {
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        callback(it)
    }.launch(createIntent<T>(*params))
}

inline fun <reified T : Activity> ComponentActivity.startActivityForResultOk(vararg params: Pair<String, Any?>, crossinline callback: (Intent?) -> Unit) {
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            callback(it.data)
        }
    }.launch(createIntent<T>(*params))
}

inline fun ComponentActivity.requestPermission(permission: String, crossinline callback: (Boolean) -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        callback(it)
    }.launch(permission)
}

inline fun ComponentActivity.requestPermissionTrue(permission: String, crossinline callback: () -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            callback()
        }
    }.launch(permission)
}

inline fun ComponentActivity.requestPermissions(vararg permissions: String, crossinline callback: (Map<String, Boolean>) -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        callback(it)
    }.launch(permissions)
}

inline fun ComponentActivity.requestPermissionsTrue(vararg permissions: String, crossinline callback: () -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it.values.all { it }) {
            callback()
        }
    }.launch(permissions)
}

/**
 * 通过反射从[android.content.Intent]中获取参数值，并赋值给被[AutoWired]注解的字段。
 * 例子：
 * @AutoWired
 * private var param4: Int? = null
 * @AutoWired
 * private var param5: List<P>? = null
 *
 * 注意：
 * 1、[android.content.Intent]中传递的参数的key必须和字段名一致。
 * 如果不一致，则会警告：@AutoWired field com.like.common.sample.autowired.AutoWiredActivity.param3 not found
 * 2、数据类型必须一致。
 * 如果不一致，则会抛异常：[java.lang.IllegalArgumentException]
 */
@Throws(java.lang.IllegalArgumentException::class)
fun Activity.injectForIntentExtras() {
    val declaredFields = try {
        javaClass.declaredFields
    } catch (e: Exception) {
        null
    }
    if (declaredFields.isNullOrEmpty()) {
        return
    }

    val extras = intent.extras
    for (field in declaredFields) {
        if (field.isAnnotationPresent(AutoWired::class.java)) {
            val key = field.name
            if (extras?.containsKey(key) == true) {
                field.isAccessible = true
                field.set(this, extras[key])
                continue
            }
            Logger.w("@AutoWired field ${javaClass.name}.$key not found")
        }
    }
}