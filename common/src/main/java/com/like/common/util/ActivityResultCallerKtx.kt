package com.like.common.util

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts

/*
 * startActivityForResult
 * 权限请求
 */

inline fun ActivityResultCaller.startActivityForResult(intent: Intent, crossinline callback: (ActivityResult) -> Unit) {
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        callback(it)
    }.launch(intent)
}

inline fun ActivityResultCaller.startActivityForResultOk(intent: Intent, crossinline callback: (Intent?) -> Unit) {
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            callback(it.data)
        }
    }.launch(intent)
}

inline fun ActivityResultCaller.requestPermission(permission: String, crossinline callback: (Boolean) -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        callback(it)
    }.launch(permission)
}

inline fun ActivityResultCaller.requestPermissionTrue(permission: String, crossinline callback: () -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            callback()
        }
    }.launch(permission)
}

inline fun ActivityResultCaller.requestPermissions(vararg permissions: String, crossinline callback: (Map<String, Boolean>) -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        callback(it)
    }.launch(permissions)
}

inline fun ActivityResultCaller.requestPermissionsTrue(vararg permissions: String, crossinline callback: () -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it.values.all { it }) {
            callback()
        }
    }.launch(permissions)
}