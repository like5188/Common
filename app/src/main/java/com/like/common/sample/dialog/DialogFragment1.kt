package com.like.common.sample.dialog

import android.app.Dialog
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

    override fun initView(binding: DialogFragment1Binding, dialog: Dialog) {
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
        dialog.window?.attributes?.windowAnimations = R.style.dialogFragment_anim_bottom_in_bottom_out
        isCancelable = false
//        dialog.setOnKeyListener { dialog, keyCode, event ->
//            if (keyCode == KeyEvent.KEYCODE_BACK) {
//                shortToastCenter("back 键被点击")
//                true
//            } else {
//                false
//            }
//        }
        setTitle(mTitle)
        setMessage(mMessage)
    }

    fun setTitle(title: String) {
        mTitle = title
    }

    fun setMessage(message: String) {
        mMessage = message
    }

}