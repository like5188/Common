package com.like.common.sample.dialog

import android.app.Dialog
import com.like.common.base.BaseDialogFragment
import com.like.common.sample.R
import com.like.common.sample.databinding.DialogFragment1Binding

class DialogFragment1 : BaseDialogFragment<DialogFragment1Binding>() {
    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_1
    }

    override fun initView(binding: DialogFragment1Binding, dialog: Dialog) {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnConfirm.setOnClickListener {
            dismiss()
        }
        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    fun setTitle(title: String) {
        getBinding()?.apply {
            tvTitle.text = title
        }
    }

    fun setMessage(msg: String) {
        getBinding()?.apply {
            tvMessage.text = msg
        }
    }

}