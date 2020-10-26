package com.like.common.util

import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce

/**
 * 防抖动搜索
 *
 * @param debounceTimeoutMillis     指定时间间隔内，[EditText] 中的数据没有变化，就会触发 [search] 进行搜索。默认 500 毫秒
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
fun EditText.search(debounceTimeoutMillis: Long = 500L): Flow<String?> {
    // stateFlow 的使用方式类似于 LiveData，用于替代 ConflatedBroadcastChannel
    val stateFlow = MutableStateFlow<String?>(null)
    this.doAfterTextChanged {
        stateFlow.value = it?.toString()
    }
    return stateFlow.debounce(debounceTimeoutMillis)
}