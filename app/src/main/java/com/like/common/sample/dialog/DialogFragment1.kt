package com.like.common.sample.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.like.common.base.BaseDialogFragment
import com.like.common.sample.R
import com.like.common.sample.databinding.DialogFragment1Binding

class DialogFragment1 : BaseDialogFragment() {
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
        val binding = DataBindingUtil.inflate<DialogFragment1Binding>(LayoutInflater.from(context), R.layout.dialog_fragment_1, null, false)
        mBinding = binding
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
        if (savedInstanceState != null) {
            setTitle(savedInstanceState.getString("mTitle") ?: "")
            setMessage(savedInstanceState.getString("mMessage") ?: "")
        } else {
            setTitle(mTitle)
            setMessage(mMessage)
        }
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("mTitle", mTitle)
        outState.putString("mMessage", mMessage)
    }

    override fun initLayoutParams(layoutParams: WindowManager.LayoutParams) {
        super.initLayoutParams(layoutParams)
        resources.displayMetrics?.widthPixels?.let {
            layoutParams.width = (it * 0.9).toInt()
        }
    }

    fun setTitle(title: String) {
        mTitle = title
    }

    fun setMessage(message: String) {
        mMessage = message
    }

}