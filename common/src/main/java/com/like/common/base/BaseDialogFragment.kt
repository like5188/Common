package com.like.common.base

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager


abstract class BaseDialogFragment<T : ViewDataBinding> : DialogFragment() {
    private var mBinding: T? = null
    private var mWidth = WindowManager.LayoutParams.WRAP_CONTENT
    private var mHeight = WindowManager.LayoutParams.WRAP_CONTENT
    private var mGravity = Gravity.CENTER
    private var mDimAmount = 0.6f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutResId = getLayoutResId()
        if (layoutResId <= 0) return null
        mBinding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val b = mBinding
        val d = dialog
        if (b != null && d != null) {
            // 去除 Dialog 默认头部。不能放到 onStart() 方法中，因为 requestFeature() must be called before adding content
            d.requestWindowFeature(Window.FEATURE_NO_TITLE)
            onBindView(b, d)
        }
    }

    override fun onStart() {
        super.onStart()
        // 设置 window 相关，必须放到 onStart() 里面才有效。
        dialog?.window?.let {
            // 宽高
            val layoutParams = it.attributes
            layoutParams.width = mWidth
            layoutParams.height = mHeight
            // 位置
            layoutParams.gravity = mGravity
            // 透明度
            layoutParams.dimAmount = mDimAmount
            it.attributes = layoutParams
            // 设置背景透明，并去掉 dialog 默认的 padding ，默认是 24
            it.setBackgroundDrawable(ColorDrawable())
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

    fun getBinding() = mBinding

    fun setWidth(width: Int) {
        mWidth = width
    }

    fun setHeight(height: Int) {
        mHeight = height
    }

    fun setGravity(gravity: Int) {
        mGravity = gravity
    }

    fun setDimAmount(dimAmount: Float) {
        mDimAmount = dimAmount
    }

    abstract fun getLayoutResId(): Int
    abstract fun onBindView(binding: T, dialog: Dialog)
}