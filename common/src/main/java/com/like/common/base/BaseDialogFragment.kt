package com.like.common.base

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

abstract class BaseDialogFragment<T : ViewDataBinding> : DialogFragment() {
    protected var mBinding: T? = null
    private var cancelableOnClickViewOrBackKey = false
    private var animStyleId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Translucent_NoTitleBar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<T>(inflater, getLayoutId(), container, false)
                ?: return null
        mBinding = binding
        dialog?.let {
            cancelableOnClickViewOrBackKey(binding, it)
            anim(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initData()
    }

    /**
     * 设置单击对话框或者返回键时隐藏对话框
     */
    fun setCancelableOnClickViewOrBackKey(cancelable: Boolean) {
        cancelableOnClickViewOrBackKey = cancelable
        val b = mBinding ?: return
        val d = dialog ?: return
        cancelableOnClickViewOrBackKey(b, d)
    }

    private fun cancelableOnClickViewOrBackKey(binding: T, dialog: Dialog) {
        if (cancelableOnClickViewOrBackKey) {
            // 单击对话框隐藏
            binding.root.setOnClickListener {
                onClickViewOrBackKey()
            }
            // 单击返回键隐藏
            dialog.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    onClickViewOrBackKey()
                    true
                } else {
                    false
                }
            }
        } else {
            // 屏蔽单击对话框
            binding.root.setOnClickListener(null)
            // 屏蔽返回键
            dialog.setOnKeyListener { _, keyCode, _ ->
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
     * 设置显示隐藏的动画
     */
    fun setAnim(animStyleId: Int) {
        this.animStyleId = animStyleId
        if (animStyleId <= 0) return
        dialog?.let {
            anim(it)
        }
    }

    private fun anim(dialog: Dialog) {
        dialog.window?.attributes?.let {
            it.windowAnimations = animStyleId
        }
    }

    open fun onClickViewOrBackKey() {
        this.dismiss()
    }

    abstract fun getLayoutId(): Int

    abstract fun initData()

}