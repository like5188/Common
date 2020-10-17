package com.like.common.util

import android.util.Log
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

/**
 * 防抖动搜索
 *
 * @param debounceTimeoutMillis     指定时间间隔内，[EditText] 中的数据没有变化，就会触发 [search] 进行搜索。
 * @param search                    搜索的方法。参数为非空字符串。
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
fun <T> EditText.search(debounceTimeoutMillis: Long = 500L, search: suspend (String) -> T): LiveData<T> {
    // stateFlow 类似于 LiveData，用于替代 ConflatedBroadcastChannel
    val stateFlow = MutableStateFlow<String?>(null)
    this.doAfterTextChanged {
        stateFlow.value = it?.toString()
    }
    return stateFlow.debounce(debounceTimeoutMillis)
            .filter {
                return@filter !it.isNullOrEmpty()
            }
            .flatMapLatest {
                // 只显示最后一次搜索的结果，忽略之前的请求
                // 网络请求，这里替换自己的实现即可
                flow {
                    Log.d("EditText", "flatMapLatest 1 $it")
                    emit(search(it!!))
                    Log.d("EditText", "flatMapLatest 2 $it")
                }
            }
            .catch { throwable ->
                //  异常捕获
                Log.e("EditText", "search error $throwable")
            }
            .asLiveData()
}