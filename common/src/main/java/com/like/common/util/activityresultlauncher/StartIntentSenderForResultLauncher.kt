package com.like.common.util.activityresultlauncher

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat

class StartIntentSenderForResultLauncher(registry: ActivityResultRegistry) :
    BaseActivityResultLauncher<IntentSenderRequest, ActivityResult>(
        registry, ActivityResultContracts.StartIntentSenderForResult()
    )

suspend fun ComponentActivity.startIntentSenderForResult(
    intentSenderRequest: IntentSenderRequest,
    options: ActivityOptionsCompat? = null
): ActivityResult {
    return StartIntentSenderForResultLauncher(activityResultRegistry).launch(intentSenderRequest, options)
}

fun ComponentActivity.startIntentSenderForResult(
    intentSenderRequest: IntentSenderRequest,
    options: ActivityOptionsCompat? = null,
    callback: ActivityResultCallback<ActivityResult>
) {
    StartIntentSenderForResultLauncher(activityResultRegistry).launch(intentSenderRequest, options, callback)
}