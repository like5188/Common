package com.like.common.view.callback

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

internal class RxCallback {
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

    fun startActivityForResult(intent: Intent): Observable<Callback> {
        val liveData = MutableLiveData<Callback>()
        val publishSubject = PublishSubject.create<Callback>()

        liveData.observe(mLifecycleOwner, Observer {
            publishSubject.onNext(it)
            publishSubject.onComplete()
        })

        mFragment.startActivityForResult(liveData, intent)
        return publishSubject
    }


}