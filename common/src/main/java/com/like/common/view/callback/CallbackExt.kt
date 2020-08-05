package com.like.common.view.callback

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import io.reactivex.Observable

suspend fun Fragment.startActivityForResult(intent: Intent): Callback =
        CoroutinesCallback(this).startActivityForResult(intent)

suspend fun FragmentActivity.startActivityForResult(intent: Intent): Callback =
        CoroutinesCallback(this).startActivityForResult(intent)

fun Fragment.startActivityForLiveDataResult(intent: Intent): LiveData<Callback> =
        LiveDataCallback(this).startActivityForResult(intent)

fun FragmentActivity.startActivityForLiveDataResult(intent: Intent): LiveData<Callback> =
        LiveDataCallback(this).startActivityForResult(intent)

fun Fragment.startActivityForObservableResult(intent: Intent): Observable<Callback> =
        RxCallback(this).startActivityForResult(intent)

fun FragmentActivity.startActivityForObservableResult(intent: Intent): Observable<Callback> =
        RxCallback(this).startActivityForResult(intent)