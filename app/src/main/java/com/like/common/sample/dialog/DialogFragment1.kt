package com.like.common.sample.dialog

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.like.common.sample.R
import com.like.common.sample.databinding.DialogFragment1Binding

class DialogFragment1 : DialogFragment() {
    private var mBinding: DialogFragment1Binding? = null
    private var mTitle = ""
        set(value) {
            mBinding?.apply {
                tvTitle.text = value
            }
            field = value
        }
    private var mMessage = ""
        set(value) {
            mBinding?.apply {
                tvMessage.text = value
            }
            field = value
        }
    private var mWidth = WindowManager.LayoutParams.WRAP_CONTENT
    private var mHeight = WindowManager.LayoutParams.WRAP_CONTENT
    private var mGravity = Gravity.CENTER
    private var mDimAmount = 0.6f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_1, container, false)
        mBinding?.btnCancel?.setOnClickListener {
            dismiss()
        }
        mBinding?.btnConfirm?.setOnClickListener {
            setTitle("新的标题")
            setMessage("新的消息")
        }
        mBinding?.ivClose?.setOnClickListener {
            dismiss()
        }
        setTitle(mTitle)
        setMessage(mMessage)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 去除Dialog默认头部
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
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

    fun setTitle(title: String) {
        mTitle = title
    }

    fun setMessage(message: String) {
        mMessage = message
    }

}