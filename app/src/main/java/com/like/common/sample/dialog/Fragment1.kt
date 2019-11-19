package com.like.common.sample.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.like.common.sample.R
import com.like.common.sample.databinding.Fragment1Binding

class Fragment1 : Fragment() {
    private val mDialogFragment1: DialogFragment1 by lazy {
        DialogFragment1()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<Fragment1Binding>(inflater, R.layout.fragment_1, container, false)
        binding.btn.setOnClickListener {
            mDialogFragment1.setTitle("title")
            mDialogFragment1.setMessage("message")
            mDialogFragment1.show(childFragmentManager)
        }
        return binding.root
    }
}