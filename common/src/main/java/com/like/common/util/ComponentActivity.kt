package com.like.common.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts

inline fun ComponentActivity.startActivityForResultLauncher(crossinline callback: (Intent?) -> Unit): ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            callback(if (it.resultCode == Activity.RESULT_OK) {
                it.data
            } else {
                null
            })
        }

inline fun ComponentActivity.startIntentSenderForResultLauncher(crossinline callback: (Boolean) -> Unit): ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            callback(it.resultCode == Activity.RESULT_OK)
        }

inline fun ComponentActivity.requestPermissionLauncher(crossinline callback: (Boolean) -> Unit): ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            callback(it)
        }

inline fun ComponentActivity.requestPermissionsLauncher(crossinline callback: (Boolean) -> Unit): ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            callback(it.values.all { it })
        }