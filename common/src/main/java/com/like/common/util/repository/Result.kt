package com.like.common.util.repository

import androidx.lifecycle.LifecycleOwner
import com.like.common.util.map
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.listener.OnItemClickListener
import com.like.recyclerview.model.*
import com.like.recyclerview.ui.DefaultEmptyItem
import com.like.recyclerview.ui.DefaultErrorItem
import com.like.recyclerview.ui.DefaultLoadMoreFooter
import com.like.recyclerview.ui.DefaultLoadMoreHeader
import com.like.repository.RequestState
import com.like.repository.RequestType
import com.like.repository.Result

//[com.like.repository.Result]相关的基础绑定操作
/**
 * 绑定成功失败回调。
 *
 * @param onSuccess         成功回调
 * @param onFailed          失败回调
 */
internal fun <ResultType> Result<ResultType>.bind(
        lifecycleOwner: LifecycleOwner,
        onFailed: ((RequestType, Throwable) -> Unit)? = null,
        onSuccess: ((ResultType?) -> Unit)? = null
) {
    if (onFailed != null) {
        liveState.observe(lifecycleOwner) { stateReport ->
            val state = stateReport.state
            val type = stateReport.type
            if (state is RequestState.Failed) {
                onFailed(type, state.throwable)
            }
        }
    }
    if (onSuccess != null) {
        liveValue.observe(lifecycleOwner) {
            val stateReport = liveState.value
            if (stateReport?.state is RequestState.Success) {
                onSuccess(it)
            }
        }
    }
}

/**
 * 初始化或者刷新时控制进度条的显示隐藏。
 *
 * @param show      初始化或者刷新开始时显示进度条
 * @param hide      初始化或者刷新成功或者失败时隐藏进度条
 */
internal fun <ResultType> Result<ResultType>.bindProgress(
        lifecycleOwner: LifecycleOwner,
        show: () -> Unit,
        hide: () -> Unit
) {
    liveState.observe(lifecycleOwner) { stateReport ->
        val state = stateReport.state
        val type = stateReport.type
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
}

/**
 * 把 [Result] 与 [androidx.recyclerview.widget.RecyclerView] 进行绑定
 * 功能包括：Item数据的添加、空视图、错误视图、往后加载更多视图、往前加载更多视图、点击监听
 *
 * @param transform         从 [ResultType] 中获取 List<ValueInList>? 类型的数据用于 RecyclerView 处理。
 * @param emptyItem         数据为空时显示的视图。[com.like.recyclerview.ui]库中默认实现了：[DefaultEmptyItem]
 * @param errorItem         失败时显示的视图。[com.like.recyclerview.ui]库中默认实现了：[DefaultErrorItem]
 * @param loadMoreFooter    往后加载更多的视图。[com.like.recyclerview.ui]库中默认实现了：[DefaultLoadMoreFooter]
 * @param loadMoreHeader    往前加载更多的视图。[com.like.recyclerview.ui]库中默认实现了：[DefaultLoadMoreHeader]
 * @param listener          item点击监听
 */
internal fun <ResultType, ValueInList : IRecyclerViewItem> Result<ResultType>.bindRecyclerView(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        transform: (ResultType) -> List<ValueInList>?,
        emptyItem: IEmptyItem? = null,
        errorItem: IErrorItem? = null,
        loadMoreFooter: ILoadMoreFooter? = null,
        loadMoreHeader: ILoadMoreHeader? = null,
        listener: OnItemClickListener? = null
) {
    liveState.observe(lifecycleOwner) { stateReport ->
        stateReport ?: return@observe
        val state = stateReport.state
        val type = stateReport.type
        when {
            (type is RequestType.Initial || type is RequestType.Refresh) && state is RequestState.Success -> {
                // 恢复加载更多视图的状态，避免使用同一个加载更多视图导致状态不对。
                loadMoreFooter?.let {
                    adapter.mAdapterDataManager.getFooters().forEach { iFooter ->
                        if (iFooter is ILoadMoreFooter) {
                            iFooter.onLoading()
                        }
                    }
                }
                loadMoreHeader?.let {
                    adapter.mAdapterDataManager.getHeaders().forEach { iHeader ->
                        if (iHeader is ILoadMoreHeader) {
                            iHeader.onLoading()
                        }
                    }
                }
            }
            type is RequestType.Initial && state is RequestState.Failed -> {
                errorItem?.let {
                    if (errorItem.errorMessage.isEmpty()) {
                        errorItem.errorMessage = state.throwable.message ?: "unknown error"
                    }
                    adapter.mAdapterDataManager.setErrorItem(errorItem)
                }
                listener?.let {
                    adapter.removeOnItemClickListener(listener)
                }
            }
            type is RequestType.After && state is RequestState.Failed -> {
                loadMoreFooter?.let {
                    adapter.mAdapterDataManager.getFooters().forEach { iFooter ->
                        if (iFooter is ILoadMoreFooter) {
                            iFooter.onError()
                        }
                    }
                }
            }
            type is RequestType.Before && state is RequestState.Failed -> {
                loadMoreHeader?.let {
                    adapter.mAdapterDataManager.getHeaders().forEach { iHeader ->
                        if (iHeader is ILoadMoreHeader) {
                            iHeader.onError()
                        }
                    }
                }
            }
        }
    }
    liveValue.observe(lifecycleOwner) { data ->
        val list = transform(data)
        val stateReport = liveState.value ?: return@observe
        val state = stateReport.state
        val type = stateReport.type
        when {
            (type is RequestType.Initial || type is RequestType.Refresh) && state is RequestState.Success -> {
                if (list.isNullOrEmpty()) {
                    emptyItem?.let {
                        adapter.mAdapterDataManager.setEmptyItem(emptyItem)
                    }
                    listener?.let {
                        adapter.removeOnItemClickListener(listener)
                    }
                } else {
                    adapter.mAdapterDataManager.clearAndAddAll(list)
                    loadMoreFooter?.let {
                        adapter.mAdapterDataManager.addFooterToEnd(loadMoreFooter)
                    }
                    loadMoreHeader?.let {
                        adapter.mAdapterDataManager.addHeaderToStart(loadMoreHeader)
                    }
                    listener?.let {
                        adapter.addOnItemClickListener(listener)
                    }
                }
            }
            type is RequestType.After && state is RequestState.Success -> {
                // 因为 footer 的状态和数据有关，所以放到 liveValue 的监听里面来。
                loadMoreFooter?.let {
                    if (list.isNullOrEmpty()) {
                        // 到底了
                        adapter.mAdapterDataManager.getFooters().forEach { iFooter ->
                            if (iFooter is ILoadMoreFooter) {
                                iFooter.onEnd()
                            }
                        }
                    } else {
                        adapter.mAdapterDataManager.getFooters().forEach { iFooter ->
                            if (iFooter is ILoadMoreFooter) {
                                iFooter.onLoading()
                            }
                        }
                        adapter.mAdapterDataManager.addItemsToEnd(list.map())
                    }
                }
            }
            type is RequestType.Before && state is RequestState.Success -> {
                // 因为 header 的状态和数据有关，所以放到 liveValue 的监听里面来。
                loadMoreHeader?.let {
                    if (list.isNullOrEmpty()) {
                        // 到顶了
                        adapter.mAdapterDataManager.getHeaders().forEach { iHeader ->
                            if (iHeader is ILoadMoreHeader) {
                                iHeader.onEnd()
                            }
                        }
                    } else {
                        adapter.mAdapterDataManager.getHeaders().forEach { iHeader ->
                            if (iHeader is ILoadMoreHeader) {
                                iHeader.onLoading()
                            }
                        }
                        adapter.mAdapterDataManager.addItemsToStart(list.map())
                    }
                }
            }
        }
    }
}