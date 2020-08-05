package com.like.common.view.callback

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class CoroutinesCallback {
    companion object {
        private val TAG = CoroutinesCallback::class.java.simpleName
    }

    private val mFragment: LiveDataCallbackFragment
    private val mLifecycleOwner: LifecycleOwner

    constructor(fragment: Fragment) {
        mLifecycleOwner = fragment
        mFragment = getFragment(fragment.childFragmentManager)
    }

    constructor(fragmentActivity: FragmentActivity) {
        mLifecycleOwner = fragmentActivity
        mFragment = getFragment(fragmentActivity.supportFragmentManager)
    }

    private fun getFragment(fm: FragmentManager): LiveDataCallbackFragment {
        var fragment = fm.findFragmentByTag(TAG) as? LiveDataCallbackFragment
        if (fragment == null) {
            fragment = LiveDataCallbackFragment()
            fm.beginTransaction().add(fragment, TAG).commitNow()
        }
        return fragment
    }

    suspend fun startActivityForResult(intent: Intent): Callback = suspendCancellableCoroutine { con ->
        val liveData = MutableLiveData<Callback>()
        val observer = Observer<Callback> {
            con.resume(it)
        }

        con.invokeOnCancellation {
            liveData.removeObserver(observer)
        }

        mFragment.startActivityForResult(liveData, intent)
        liveData.observe(mLifecycleOwner, observer)
    }

}