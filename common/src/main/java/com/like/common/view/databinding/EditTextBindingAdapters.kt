package com.like.common.view.databinding

import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.like.common.util.toIntOrNull
import com.like.common.util.toStringOrEmpty

//用于对EditText进行非String类型字段的双向绑定
object EditTextBindingAdapters {

    @BindingAdapter("bindingInt")
    @JvmStatic
    fun setBindingInt(et: EditText, data: Int?) {
        et.setText(data.toStringOrEmpty())
    }

    @InverseBindingAdapter(attribute = "bindingInt")
    @JvmStatic
    fun getBindingInt(et: EditText): Int? {
        return et.text.toString().toIntOrNull()
    }

    @BindingAdapter("bindingDouble")
    @JvmStatic
    fun setBindingDouble(et: EditText, data: Double?) {
        et.setText(data.toStringOrEmpty())
    }

    @InverseBindingAdapter(attribute = "bindingDouble")
    @JvmStatic
    fun getBindingDouble(et: EditText): Double? {
        return et.text.toString().toDoubleOrNull()
    }

    @BindingAdapter("bindingLong")
    @JvmStatic
    fun setBindingLong(et: EditText, data: Long?) {
        et.setText(data.toStringOrEmpty())
    }

    @InverseBindingAdapter(attribute = "bindingLong")
    @JvmStatic
    fun getBindingLong(et: EditText): Long? {
        return et.text.toString().toLongOrNull()
    }

    @BindingAdapter("bindingFloat")
    @JvmStatic
    fun setBindingFloat(et: EditText, data: Float?) {
        et.setText(data.toStringOrEmpty())
    }

    @InverseBindingAdapter(attribute = "bindingFloat")
    @JvmStatic
    fun getBindingFloat(et: EditText): Float? {
        return et.text.toString().toFloatOrNull()
    }

    @BindingAdapter(
            "bindingIntAttrChanged", "bindingDoubleAttrChanged", "bindingLongAttrChanged", "bindingFloatAttrChanged",
            requireAll = false
    )
    @JvmStatic
    fun setBindingListener(
            et: EditText,
            listener0: InverseBindingListener?,
            listener1: InverseBindingListener?,
            listener2: InverseBindingListener?,
            listener3: InverseBindingListener?
    ) {
        var text = ""
        et.doAfterTextChanged {
            if (text != it.toString()) {
                // 会通知属性调用InverseBindingAdapter注解方法
                listener0?.onChange()
                listener1?.onChange()
                listener2?.onChange()
                listener3?.onChange()
                text = it.toString()
            }
            et.setSelection(text.length)
        }
    }
}