package com.like.common.util.activityresultlauncher

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

open class BaseActivityResultLauncher<I, O>(
    private val registry: ActivityResultRegistry,
    private val contract: ActivityResultContract<I, O>
) {

    suspend fun launch(input: I, options: ActivityOptionsCompat? = null): O =
        suspendCancellableCoroutine { continuation ->
            launch(input, options) {
                continuation.resume(it)
            }
        }

    fun launch(input: I, options: ActivityOptionsCompat? = null, callback: ActivityResultCallback<O>) {
        val key = "com.like.common.util.activityresult.launcher.BaseActivityResultLauncher.${hashCode()}"
        var launcher: ActivityResultLauncher<I>? = null
        launcher = registry.register(key, contract) { result ->
            callback.onActivityResult(result)
            launcher?.unregister()
        }.apply {
            launch(input, options)
        }
    }

}
