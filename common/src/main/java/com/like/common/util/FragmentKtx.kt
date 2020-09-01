package com.like.common.util

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

/*
 * startActivity
 * startActivityForResult
 * 权限请求
 */

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

inline fun <reified T : Activity> Fragment.startActivityForResultOk(vararg params: Pair<String, Any?>, crossinline callback: (Intent?) -> Unit) {
    val act = activity ?: return
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            callback(it.data)
        }
    }.launch(act.createIntent<T>(*params))
}

inline fun Fragment.requestPermission(permission: String, crossinline callback: (Boolean) -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        callback(it)
    }.launch(permission)
}

inline fun Fragment.requestPermissionTrue(permission: String, crossinline callback: () -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            callback()
        }
    }.launch(permission)
}

inline fun Fragment.requestPermissions(vararg permissions: String, crossinline callback: (Map<String, Boolean>) -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        callback(it)
    }.launch(permissions)
}

inline fun Fragment.requestPermissionsTrue(vararg permissions: String, crossinline callback: () -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it.values.all { it }) {
            callback()
        }
    }.launch(permissions)
}