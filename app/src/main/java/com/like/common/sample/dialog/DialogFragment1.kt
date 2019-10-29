package com.like.common.sample.dialog

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.like.common.sample.R
import com.like.common.sample.databinding.DialogFragment1Binding
import org.jetbrains.anko.padding

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

    override fun onStart() {
        super.onStart()
        // 设置对话框宽高，必须放到 onStart() 里面才有效。
        dialog?.window?.attributes?.let {
            it.width = WindowManager.LayoutParams.MATCH_PARENT
            it.height = WindowManager.LayoutParams.WRAP_CONTENT
//            it.gravity = Gravity.BOTTOM
            dialog?.window?.attributes = it
        }
        // 设置背景透明，并去掉 dialog 默认的 padding ，默认是 24
//        dialog?.window?.setBackgroundDrawable(ColorDrawable())
    }

    fun setTitle(title: String) {
        mTitle = title
    }

    fun setMessage(message: String) {
        mMessage = message
    }

}