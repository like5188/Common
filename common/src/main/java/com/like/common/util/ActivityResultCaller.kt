package com.like.common.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

val ActivityResultCaller.context: Context
    get() {
        return when (this) {
            is ComponentActivity -> this
            is Fragment -> activity ?: throw IllegalStateException("Fragment $this not attached to Activity")
            else -> throw IllegalStateException("$this must be androidx.activity.ComponentActivity or androidx.fragment.app.Fragment")
        }
    }

inline fun <reified T : Activity> ActivityResultCaller.startActivityForResult(vararg params: Pair<String, Any?>, crossinline callback: (ActivityResult) -> Unit) {
    val intent = context.createIntent<T>(*params)
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        callback(it)
    }.launch(intent)
}

inline fun <reified T : Activity> ActivityResultCaller.startActivityForResultOk(vararg params: Pair<String, Any?>, crossinline callback: (Intent?) -> Unit) {
    startActivityForResult<T>(*params) {
        if (it.resultCode == Activity.RESULT_OK) {
            callback(it.data)
        }
    }
}

inline fun ActivityResultCaller.requestPermission(permission: String, crossinline callback: (Boolean) -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        callback(it)
    }.launch(permission)
}

/**
 * 权限被同意
 */
inline fun ActivityResultCaller.requestPermissionGranted(permission: String, crossinline callback: () -> Unit) {
    requestPermission(permission) {
        if (it) {
            callback()
        }
    }
}

inline fun ActivityResultCaller.requestPermissions(vararg permissions: String, crossinline callback: (Map<String, Boolean>) -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        callback(it)
    }.launch(permissions)
}

/**
 * 所有权限都被同意
 */
inline fun ActivityResultCaller.requestPermissionsGranted(vararg permissions: String, crossinline callback: () -> Unit) {
    requestPermissions(*permissions) {
        if (it.values.all { it }) {
            callback()
        }
    }
}