package com.like.common.base

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

abstract class BaseDialogFragment : DialogFragment() {
    private var mBinding: ViewDataBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Translucent_NoTitleBar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = getViewDataBinding(inflater, container, savedInstanceState, arguments)
        setCancelableOnClickViewOrBackKey(cancelable())
        return mBinding?.root
    }

    /**
     * 设置单击对话框或者返回键时隐藏对话框
     */
    private fun setCancelableOnClickViewOrBackKey(cancelable: Boolean) {
        if (cancelable) {
            // 单击对话框隐藏
            mBinding?.root?.setOnClickListener {
                this.dismissAllowingStateLoss()
            }
            // 单击返回键隐藏
            dialog?.setOnKeyListener { dialog, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    this.dismissAllowingStateLoss()
                    true
                } else {
                    false
                }
            }
        } else {
            // 屏蔽单击对话框
            mBinding?.root?.setOnClickListener(null)
            // 屏蔽返回键
            dialog?.setOnKeyListener { dialog, keyCode, event ->
                keyCode == KeyEvent.KEYCODE_BACK
            }
        }
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

    /**
     * 是否在单击对话框或者返回键时隐藏对话框
     */
    open fun cancelable() = false

    abstract fun getViewDataBinding(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
            args: Bundle?
    ): ViewDataBinding?

}