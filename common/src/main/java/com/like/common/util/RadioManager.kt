package com.like.common.util

import androidx.databinding.ObservableField

/**
 * 单选控制器，默认不选择，用实体来控制唯一，所以实体必须实现equals()和hashCode()方法
 */
class RadioManager<T> {
    private val mPreChecked = ObservableField<T>()
    private val mCurChecked = ObservableField<T>()

    fun check(t: T) {
        mPreChecked.set(mCurChecked.get())
        mCurChecked.set(t)
    }

    fun delete(t: T) {
        if (mPreChecked.get() == t) {
            mPreChecked.set(null)
        }
        if (mCurChecked.get() == t) {
            mCurChecked.set(null)
        }
    }

    fun clear() {
        mPreChecked.set(null)
        mCurChecked.set(null)
    }

    /**
     * 是否有选中的
     */
    fun isChecked(): Boolean {
        return mCurChecked.get() != null
    }

    /**
     * 用于异步请求失败时还原到上次的选中状态。只支持还原一次
     */
    fun restore() {
        mCurChecked.set(mPreChecked.get())
        mPreChecked.set(null)
    }

    /**
     * 获取当前选中项
     */
    fun getCurChecked(): ObservableField<T> {
        return mCurChecked
    }

    fun getPreChecked(): ObservableField<T> {
        return mPreChecked
    }
}