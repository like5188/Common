@file:Suppress("NOTHING_TO_INLINE")

package com.like.common.util

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

inline fun DialogFragment.show(activity: FragmentActivity) {
    show(activity.supportFragmentManager)
}

inline fun DialogFragment.show(fragment: Fragment) {
    fragment.fragmentManager?.apply {
        show(this)
    }
}

inline fun DialogFragment.show(fm: FragmentManager) {
    val tag = this::class.java.simpleName
    val fragment = fm.findFragmentByTag(tag)
    if (fragment == null || !fragment.isAdded || fragment.isHidden) {
        this.show(fm, tag)
    }
}

abstract class BaseDialogFragment<T : ViewDataBinding> : DialogFragment() {
    protected var mBinding: T? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Translucent_NoTitleBar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, getDialogFragmentLayoutResId(), container, false)
                ?: return null
        initData(arguments)
        return mBinding?.root
    }

    abstract fun getDialogFragmentLayoutResId(): Int

    open fun initData(arguments: Bundle?) {}
}