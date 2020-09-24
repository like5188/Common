package com.like.common.view.databinding

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener

//用于自定义View的双向绑定
object CustomViewBindingAdapters {
    @BindingAdapter(value = ["onValueChanged", "selectedValueAttrChanged"], requireAll = false)
    @JvmStatic
    fun setOnValueChangedListener(
            view: View,
            valueChangedListener: OnValueChangedListener?,
            bindingListener: InverseBindingListener?
    ) {
        if (valueChangedListener == null && bindingListener == null) {
            return
        }
        if (view is OnBindingListener) {
            view.mOnValueChangedListener = OnValueChangedListener {
                bindingListener?.onChange()
                valueChangedListener?.onValueChanged()
            }
        }
    }

}