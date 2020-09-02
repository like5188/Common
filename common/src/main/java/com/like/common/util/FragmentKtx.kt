package com.like.common.util

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.fragment.app.Fragment

/*
 * startActivity
 * startActivityForResult
 */

inline fun <reified T : Activity> Fragment.startActivity(vararg params: Pair<String, Any?>) {
    val act = activity ?: throw IllegalStateException("Fragment $this not attached to Activity")
    val intent = act.createIntent<T>(*params)
    startActivity(intent)
}

inline fun <reified T : Activity> Fragment.startActivityForResult(vararg params: Pair<String, Any?>, crossinline callback: (ActivityResult) -> Unit) {
    val act = activity ?: throw IllegalStateException("Fragment $this not attached to Activity")
    val intent = act.createIntent<T>(*params)
    startActivityForResult(intent, callback)
}

inline fun <reified T : Activity> Fragment.startActivityForResultOk(vararg params: Pair<String, Any?>, crossinline callback: (Intent?) -> Unit) {
    val act = activity ?: throw IllegalStateException("Fragment $this not attached to Activity")
    val intent = act.createIntent<T>(*params)
    startActivityForResultOk(intent, callback)
}