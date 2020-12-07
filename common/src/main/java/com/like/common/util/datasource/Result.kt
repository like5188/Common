package com.like.common.util.datasource

import com.google.gson.JsonParseException
import com.like.common.util.map
import com.like.datasource.RequestState
import com.like.datasource.RequestType
import com.like.datasource.Result
import com.like.datasource.ResultReport
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.model.*
import com.like.recyclerview.ui.DefaultEmptyItem
import com.like.recyclerview.ui.DefaultErrorItem
import com.like.recyclerview.ui.DefaultLoadMoreFooter
import com.like.recyclerview.ui.DefaultLoadMoreHeader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

//[com.like.datasource.Result] 扩展功能。

/**
 * 获取自定义的错误消息，用于显示给用户看。
 */
fun Throwable?.getCustomErrorMessage() =
        when (this) {
            is HttpException, is ConnectException, is SocketTimeoutException,
            is UnknownHostException, is JsonParseException, is JSONException,
            is SSLHandshakeException -> "无法连接到服务器"
            else -> "请求失败，${this?.message ?: "unknown error"}"
        }

/**
 * 开始搜集数据。
 *
 * @param onSuccess         成功回调
 * @param onFailed          失败回调
 */
suspend fun <ResultType> Result<ResultType>.collect(
        onFailed: ((RequestType, Throwable) -> Unit)? = null,
        onSuccess: ((RequestType, ResultType) -> Unit)? = null
) {
    resultReportFlow.collect { resultReport ->
        val state = resultReport.state
        val type = resultReport.type
        when (state) {
            is RequestState.Failed -> {
                onFailed?.invoke(type, Throwable(state.throwable.getCustomErrorMessage(), state.throwable.cause))
            }
            is RequestState.Success<ResultType> -> {
                onSuccess?.invoke(type, state.data)
            }
        }
    }
}

/**
 * 绑定进度条。
 * 初始化或者刷新时控制进度条的显示隐藏。
 *
 * @param show      初始化或者刷新开始时显示进度条
 * @param hide      初始化或者刷新成功或者失败时隐藏进度条
 */
internal fun <ResultType> Result<ResultType>.progress(
        show: () -> Unit,
        hide: () -> Unit
): Result<ResultType> {
    val newResultReportFlow = resultReportFlow.onEach { resultReport ->
        val state = resultReport.state
        val type = resultReport.type
        if (type is RequestType.Initial || type is RequestType.Refresh) {
            when (state) {
                is RequestState.Running -> {
                    show()
                }
                else -> {
                    hide()
                }
            }
        }
    }
    return updateResultReportFlow(newResultReportFlow)
}

/**
 * 绑定 [androidx.recyclerview.widget.RecyclerView]
 * 功能包括：Item数据的添加、空视图、错误视图、往后加载更多视图、往前加载更多视图
 *
 * @param transform         从 [ResultType] 中获取 List<ValueInList>? 类型的数据用于 RecyclerView 处理。
 * @param emptyItem         数据为空时显示的视图。[com.like.recyclerview.ui]库中默认实现了：[DefaultEmptyItem]
 * @param errorItem         失败时显示的视图。[com.like.recyclerview.ui]库中默认实现了：[DefaultErrorItem]
 * @param loadMoreFooter    往后加载更多的视图。[com.like.recyclerview.ui]库中默认实现了：[DefaultLoadMoreFooter]
 * @param loadMoreHeader    往前加载更多的视图。[com.like.recyclerview.ui]库中默认实现了：[DefaultLoadMoreHeader]
 */
internal fun <ResultType, ValueInList : IRecyclerViewItem> Result<ResultType>.recyclerView(
        adapter: BaseAdapter,
        transform: (ResultType) -> List<ValueInList>?,
        emptyItem: IEmptyItem? = null,
        errorItem: IErrorItem? = null,
        loadMoreFooter: ILoadMoreFooter? = null,
        loadMoreHeader: ILoadMoreHeader? = null
): Result<ResultType> {
    val newResultReportFlow = resultReportFlow.onEach { resultReport ->
        val state = resultReport.state
        val type = resultReport.type
        when {
            (type is RequestType.Initial || type is RequestType.Refresh) && state is RequestState.Success -> {
                val list = transform(state.data)
                if (list.isNullOrEmpty()) {
                    emptyItem?.let {
                        adapter.mAdapterDataManager.setEmptyItem(emptyItem)
                    }
                } else {
                    adapter.mAdapterDataManager.clearAndAddAll(list)
                    loadMoreFooter?.let {
                        loadMoreFooter.onLoading()
                        adapter.mAdapterDataManager.addFooterToEnd(loadMoreFooter)
                    }
                    loadMoreHeader?.let {
                        loadMoreHeader.onLoading()
                        adapter.mAdapterDataManager.addHeaderToStart(loadMoreHeader)
                    }
                }
            }
            type is RequestType.Initial && state is RequestState.Failed -> {
                errorItem?.let {
                    if (errorItem.errorMessage.isEmpty()) {
                        errorItem.errorMessage = state.throwable.getCustomErrorMessage()
                    }
                    adapter.mAdapterDataManager.setErrorItem(errorItem)
                }
            }
            type is RequestType.After && state is RequestState.Success -> {
                loadMoreFooter?.let {
                    val list = transform(state.data)
                    if (list.isNullOrEmpty()) {
                        // 到底了
                        loadMoreFooter.onEnd()
                    } else {
                        loadMoreFooter.onLoading()
                        adapter.mAdapterDataManager.addItemsToEnd(list.map())
                    }
                }
            }
            type is RequestType.After && state is RequestState.Failed -> {
                loadMoreFooter?.onError()
            }
            type is RequestType.Before && state is RequestState.Success -> {
                loadMoreHeader?.let {
                    val list = transform(state.data)
                    if (list.isNullOrEmpty()) {
                        // 到顶了
                        loadMoreHeader.onEnd()
                    } else {
                        loadMoreHeader.onLoading()
                        adapter.mAdapterDataManager.addItemsToStart(list.map())
                    }
                }
            }
            type is RequestType.Before && state is RequestState.Failed -> {
                loadMoreHeader?.onError()
            }
        }
    }
    return updateResultReportFlow(newResultReportFlow)
}

private fun <ResultType> Result<ResultType>.updateResultReportFlow(newResultReportFlow: Flow<ResultReport<ResultType>>): Result<ResultType> {
    return Result(newResultReportFlow, initial, refresh, retry, loadAfter, loadBefore)
}