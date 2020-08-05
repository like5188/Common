package com.like.common.view.callback

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal class CoroutinesCallback {
    private val mFragment: LiveDataCallbackFragment
    private val mLifecycleOwner: LifecycleOwner

    constructor(fragment: Fragment) {
        mLifecycleOwner = fragment
        mFragment = LiveDataCallbackFragment.getOrCreateIfAbsent(fragment.childFragmentManager)
    }

    constructor(fragmentActivity: FragmentActivity) {
        mLifecycleOwner = fragmentActivity
        mFragment = LiveDataCallbackFragment.getOrCreateIfAbsent(fragmentActivity.supportFragmentManager)
    }

    suspend fun startActivityForResult(intent: Intent): Callback = suspendCancellableCoroutine { con ->
        val liveData = MutableLiveData<Callback>()

        con.invokeOnCancellation {
            liveData.removeObservers(mLifecycleOwner)
        }

        liveData.observe(mLifecycleOwner, Observer<Callback> {
            con.resume(it)
        })

        mFragment.startActivityForResult(liveData, intent)
    }

}