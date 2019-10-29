package com.like.common.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 * 背景透明、去掉了自带的标题栏
 */
abstract class BaseDialogFragment<T : ViewDataBinding> : DialogFragment() {
    private var mBinding: T? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Translucent_NoTitleBar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutResId = getLayoutResId()
        if (layoutResId <= 0) return null
        mBinding = DataBindingUtil.inflate(inflater, layoutResId, container, false) ?: return null
        val b = mBinding
        val d = dialog
        if (b != null && d != null) {
            initView(b, d)
        }
        return mBinding?.root
    }

    fun show(activity: FragmentActivity) {
        show(activity.supportFragmentManager)
    }

    fun show(fragment: Fragment) {
        fragment.fragmentManager?.apply {
            show(this)
        }
    }

    fun show(fragmentManager: FragmentManager) {
        val tag = this::class.java.simpleName
        val fragment = fragmentManager.findFragmentByTag(tag)
        if (fragment == null || !fragment.isAdded || fragment.isHidden) {
            this.show(fragmentManager, tag)
        }
    }

    fun getBinding() = mBinding

    abstract fun getLayoutResId(): Int
    abstract fun initView(binding: T, dialog: Dialog)
}