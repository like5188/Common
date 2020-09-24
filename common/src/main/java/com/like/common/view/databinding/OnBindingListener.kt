package com.like.common.view.databinding

/**
 * 如果自定义 View 需要进行双向绑定，需要实现此接口
 */
interface OnBindingListener {
    var mOnValueChangedListener: OnValueChangedListener?
}