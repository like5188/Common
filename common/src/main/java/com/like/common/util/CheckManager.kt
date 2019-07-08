package com.like.common.util

import android.databinding.ObservableBoolean

/**
 * 复选控制器，默认不选择，用实体来控制唯一，所以实体必须实现equals()和hashCode()方法
 */
class CheckManager<T> {
    private val all = mutableMapOf<T, ObservableBoolean>()
    private var checkedSet = mutableSetOf<T>()
    private var uncheckedSet = mutableSetOf<T>()

    fun add(t: T) {
        uncheckedSet.add(t)
        all[t] = ObservableBoolean()
    }

    fun addAll(list: List<T>) {
        uncheckedSet.addAll(list)
        for (t in list) {
            all[t] = ObservableBoolean()
        }
    }

    fun delete(t: T) {
        all.remove(t)
        checkedSet.remove(t)
        uncheckedSet.remove(t)
    }

    fun deleteAll(list: List<T>) {
        for (t in list) {
            all.remove(t)
        }
        checkedSet.removeAll(list)
        uncheckedSet.removeAll(list)
    }

    fun clear() {
        all.clear()
        checkedSet.clear()
        uncheckedSet.clear()
    }

    /**
     * 选中或者取消选中。没有选中就选中，选中了就取消选中
     */
    fun check(t: T) {
        if (uncheckedSet.contains(t)) {// 没有选中就选中
            uncheckedSet.remove(t)
            checkedSet.add(t)
            all[t]?.set(true)
        } else if (checkedSet.contains(t)) {// 选中了就取消选中
            checkedSet.remove(t)
            uncheckedSet.add(t)
            all[t]?.set(false)
        }
    }

    /**
     * 全选
     */
    fun checkAll() {
        for (t in uncheckedSet) {
            all[t]?.set(true)
        }
        checkedSet.addAll(uncheckedSet)
        uncheckedSet.clear()
    }

    /**
     * 取消已经选中的
     */
    fun uncheckAll() {
        for (t in checkedSet) {
            all[t]?.set(false)
        }
        uncheckedSet.addAll(checkedSet)
        checkedSet.clear()
    }

    /**
     * 反选
     */
    fun invertSelection() {
        for (t in checkedSet) {
            all[t]?.set(false)
        }
        for (t in uncheckedSet) {
            all[t]?.set(true)
        }

        val temp = mutableListOf<T>()
        temp.addAll(checkedSet)
        checkedSet.clear()
        checkedSet.addAll(uncheckedSet)
        uncheckedSet.clear()
        uncheckedSet.addAll(temp)
    }

    /**
     * 用于绑定选中、未选中的checkbox状态
     */
    operator fun get(t: T): ObservableBoolean? {
        return all[t]
    }

    fun getChecked(): Set<T> {
        return checkedSet
    }

    fun getUnchecked(): Set<T> {
        return uncheckedSet
    }
}