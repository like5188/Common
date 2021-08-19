package com.like.common.util

import androidx.databinding.ObservableBoolean

/**
 * 复选控制器，默认不选择，用实体来控制唯一，所以实体必须实现equals()和hashCode()方法
 */
class CheckManager<T> {
    private val all = mutableMapOf<T, ObservableBoolean>()

    fun add(t: T) {
        all[t] = ObservableBoolean()
    }

    fun addAll(list: List<T>) {
        for (t in list) {
            all[t] = ObservableBoolean()
        }
    }

    fun remove(t: T) {
        all.remove(t)
    }

    fun removeAll(list: List<T>) {
        for (t in list) {
            all.remove(t)
        }
    }

    fun clear() {
        all.clear()
    }

    /**
     * 选中或者取消选中。没有选中就选中，选中了就取消选中
     */
    fun check(t: T) {
        val checked = all[t] ?: return
        checked.set(!checked.get())
    }

    /**
     * 全选
     */
    fun checkAll() {
        all.forEach {
            if (!it.value.get()) {
                it.value.set(true)
            }
        }
    }

    /**
     * 取消已经选中的
     */
    fun uncheckAll() {
        all.forEach {
            if (it.value.get()) {
                it.value.set(false)
            }
        }
    }

    /**
     * 反选
     */
    fun invertSelection() {
        all.forEach {
            it.value.set(!it.value.get())
        }
    }

    /**
     * 用于绑定选中、未选中的checkbox状态
     */
    operator fun get(t: T): ObservableBoolean? {
        return all[t]
    }

    fun getChecked(): Set<T> {
        return all.filter {
            it.value.get()
        }.keys
    }

    fun getUnchecked(): Set<T> {
        return all.filter {
            !it.value.get()
        }.keys
    }

}
