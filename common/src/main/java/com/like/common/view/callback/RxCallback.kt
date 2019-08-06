package com.like.common.view.callback

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.like.common.util.SingletonHolder
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlin.jvm.functions.FunctionN

class RxCallback private constructor() {
    companion object : SingletonHolder<RxCallback>(object : FunctionN<RxCallback> {
        override val arity: Int = 0 // number of arguments that must be passed to constructor

        override fun invoke(vararg args: Any?): RxCallback {
            return RxCallback()
        }
    }) {
        private val TAG = RxCallback::class.java.simpleName
    }

    private var mFragment: RxCallbackFragment? = null

    fun init(fragment: androidx.fragment.app.Fragment): RxCallback {
        mFragment = getRxCallbackFragment(fragment.childFragmentManager)
        return this
    }

    fun init(fragmentActivity: androidx.fragment.app.FragmentActivity): RxCallback {
        mFragment = getRxCallbackFragment(fragmentActivity.supportFragmentManager)
        return this
    }

    private fun getRxCallbackFragment(fm: androidx.fragment.app.FragmentManager): RxCallbackFragment {
        var rxCallbackFragment = findRxCallbackFragment(fm)
        if (rxCallbackFragment == null) {
            rxCallbackFragment = RxCallbackFragment()
            fm.beginTransaction().add(rxCallbackFragment, TAG).commitNow()
        }
        return rxCallbackFragment
    }

    private fun findRxCallbackFragment(fm: androidx.fragment.app.FragmentManager): RxCallbackFragment? {
        return fm.findFragmentByTag(TAG) as RxCallbackFragment?
    }

    fun startActivityForResult(intent: Intent): Observable<Callback> {
        if (mFragment == null) {
            throw RuntimeException("you must call init() first")
        }
        val subject = PublishSubject.create<Callback>()
        mFragment?.startActivityForResult(subject, intent)
        return subject
    }

}