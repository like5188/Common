@file:Suppress("NOTHING_TO_INLINE")

package com.like.common.util

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

inline fun androidx.fragment.app.DialogFragment.show(activity: androidx.fragment.app.FragmentActivity) {
    show(activity.supportFragmentManager)
}

inline fun androidx.fragment.app.DialogFragment.show(fragment: androidx.fragment.app.Fragment) {
    fragment.fragmentManager?.apply {
        show(this)
    }
}

inline fun androidx.fragment.app.DialogFragment.show(fm: androidx.fragment.app.FragmentManager) {
    val tag = this::class.java.simpleName
    val fragment = fm.findFragmentByTag(tag)
    if (fragment == null || !fragment.isAdded || fragment.isHidden) {
        this.show(fm, tag)
    }
}

abstract class BaseDialogFragment<T : ViewDataBinding> : androidx.fragment.app.DialogFragment() {
    protected var mBinding: T? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(androidx.fragment.app.DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Translucent_NoTitleBar)
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