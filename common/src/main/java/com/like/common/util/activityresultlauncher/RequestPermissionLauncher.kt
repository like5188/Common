package com.like.common.util.activityresultlauncher

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat

class RequestPermissionLauncher(registry: ActivityResultRegistry) :
    BaseActivityResultLauncher<String, Boolean>(
        registry, ActivityResultContracts.RequestPermission()
    )

suspend fun ComponentActivity.requestPermission(permission: String, options: ActivityOptionsCompat? = null): Boolean {
    return RequestPermissionLauncher(activityResultRegistry).launch(permission, options)
}

fun ComponentActivity.requestPermission(
    permission: String,
    options: ActivityOptionsCompat? = null,
    callback: ActivityResultCallback<Boolean>
) {
    RequestPermissionLauncher(activityResultRegistry).launch(permission, options, callback)
}