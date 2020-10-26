package com.like.common.util

import android.util.Log
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

/**
 * 防抖动搜索
 *
 * @param debounceTimeoutMillis     指定时间间隔内，[EditText] 中的数据没有变化，就会触发 [search] 进行搜索。默认 500 毫秒
 * @param filter                    过滤条件。参数为非空字符串。默认返回 true。返回 true 表示放行；false 表示拦截。
 * @param search                    搜索的方法。参数为非空字符串。
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
suspend fun EditText.search(
        debounceTimeoutMillis: Long = 500L,
        filter: (String) -> Boolean = { true },
        search: (String) -> Unit
) {
    // stateFlow 的使用方式类似于 LiveData，用于替代 ConflatedBroadcastChannel
    val stateFlow = MutableStateFlow<String?>(null)
    this.doAfterTextChanged {
        stateFlow.value = it?.toString()
    }
    stateFlow.debounce(debounceTimeoutMillis)
            .filter {
                return@filter !it.isNullOrEmpty() && filter(it)
            }
            .onEach {
                // 只显示最后一次搜索的结果，忽略之前的请求
                // 网络请求，这里替换自己的实现即可
                search(it!!)
            }
            .catch { throwable ->
                //  异常捕获
                throwable.printStackTrace()
                Log.e("EditText", "search error $throwable")
            }
            .flowOn(Dispatchers.IO)
            .collect()
}