package com.like.common.sample.dialog

import android.os.Bundle
import com.like.common.base.BaseDialogFragment
import com.like.common.sample.R
import com.like.common.sample.databinding.DialogFragment1Binding

class DialogFragment1 : BaseDialogFragment<DialogFragment1Binding>() {
    private var mTitle = ""
        set(value) {
            getBinding()?.apply {
                tvTitle.text = value
            }
            field = value
        }
    private var mMessage = ""
        set(value) {
            getBinding()?.apply {
                tvMessage.text = value
            }
            field = value
        }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_1
    }

    override fun initView(binding: DialogFragment1Binding, savedInstanceState: Bundle?) {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnConfirm.setOnClickListener {
            setTitle("新的标题")
            setMessage("新的消息")
        }
        binding.ivClose.setOnClickListener {
            dismiss()
        }
        setTitle(mTitle)
        setMessage(mMessage)
        resources.displayMetrics?.widthPixels?.let { screenWidth ->
            setWidth((screenWidth * 0.9).toInt())
        }
        savedInstanceState?.let {
            setTitle(it.getString("mTitle") ?: "")
            setMessage(it.getString("mMessage") ?: "")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("mTitle", mTitle)
        outState.putString("mMessage", mMessage)
    }

    fun setTitle(title: String) {
        mTitle = title
    }

    fun setMessage(message: String) {
        mMessage = message
    }

}