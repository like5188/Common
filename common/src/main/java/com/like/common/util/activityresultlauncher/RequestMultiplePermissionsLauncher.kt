package com.like.common.util.activityresultlauncher

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat

class RequestMultiplePermissionsLauncher(registry: ActivityResultRegistry) :
    BaseActivityResultLauncher<Array<String>, Map<String, Boolean>>(
        registry, ActivityResultContracts.RequestMultiplePermissions()
    )

suspend fun ComponentActivity.requestMultiplePermissions(
    vararg permissions: String,
    options: ActivityOptionsCompat? = null
): Map<String, Boolean> {
    return RequestMultiplePermissionsLauncher(activityResultRegistry).launch(arrayOf(*permissions), options)
}

fun ComponentActivity.requestMultiplePermissions(
    vararg permissions: String,
    options: ActivityOptionsCompat? = null,
    callback: ActivityResultCallback<Map<String, Boolean>>
) {
    RequestMultiplePermissionsLauncher(activityResultRegistry).launch(arrayOf(*permissions), options, callback)
}