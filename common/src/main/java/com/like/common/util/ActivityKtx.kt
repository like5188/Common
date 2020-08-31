package com.like.common.util

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

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