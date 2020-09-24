package com.like.common.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

/**
 * 把 LiveData<T> 转换成 LiveData<R>
 */
fun <T, R> LiveData<T>.transform(transform: (T) -> R): LiveData<R> {
    val finalLiveValue: MediatorLiveData<R> = MediatorLiveData()
    finalLiveValue.addSource(this) {
        finalLiveValue.value = transform(it)
    }
    return finalLiveValue
}