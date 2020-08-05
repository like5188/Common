package com.like.common.view.callback

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.like.common.util.Logger

internal class LiveDataCallback {
    private val mFragment: LiveDataCallbackFragment

    constructor(fragment: Fragment) {
        mFragment = LiveDataCallbackFragment.getOrCreateIfAbsent(fragment.childFragmentManager)
    }

    constructor(fragmentActivity: FragmentActivity) {
        mFragment = LiveDataCallbackFragment.getOrCreateIfAbsent(fragmentActivity.supportFragmentManager)
    }

    fun startActivityForResult(intent: Intent): LiveData<Callback> =
            MutableLiveData<Callback>().apply {
                Logger.w(mFragment)
                mFragment.startActivityForResult(this, intent)
            }

}