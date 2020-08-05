package com.like.common.view.callback

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class LiveDataCallback {
    companion object {
        private val TAG = LiveDataCallback::class.java.simpleName
    }

    private val mFragment: LiveDataCallbackFragment

    constructor(fragment: Fragment) {
        mFragment = getFragment(fragment.childFragmentManager)
    }

    constructor(fragmentActivity: FragmentActivity) {
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

    fun startActivityForResult(intent: Intent): LiveData<Callback> =
            MutableLiveData<Callback>().apply {
                mFragment.startActivityForResult(this, intent)
            }

}