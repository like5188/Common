package com.like.common.view.callback

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class RxCallback {
    companion object {
        private val TAG = RxCallback::class.java.simpleName
    }

    private val mFragment: RxCallbackFragment

    constructor(fragment: Fragment) {
        mFragment = getRxCallbackFragment(fragment.childFragmentManager)
    }

    constructor(fragmentActivity: FragmentActivity) {
        mFragment = getRxCallbackFragment(fragmentActivity.supportFragmentManager)
    }

    private fun getRxCallbackFragment(fm: FragmentManager): RxCallbackFragment {
        var rxCallbackFragment = findRxCallbackFragment(fm)
        if (rxCallbackFragment == null) {
            rxCallbackFragment = RxCallbackFragment()
            fm.beginTransaction().add(rxCallbackFragment, TAG).commitNow()
        }
        return rxCallbackFragment
    }

    private fun findRxCallbackFragment(fm: FragmentManager): RxCallbackFragment? {
        return fm.findFragmentByTag(TAG) as RxCallbackFragment?
    }

    fun startActivityForResult(intent: Intent): Observable<Callback> {
        val subject = PublishSubject.create<Callback>()
        mFragment.startActivityForResult(subject, intent)
        return subject
    }

}