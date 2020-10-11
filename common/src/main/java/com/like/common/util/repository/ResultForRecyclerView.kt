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

/**
 * 把 [Result] 与 [androidx.recyclerview.widget.RecyclerView] 进行绑定，不分页
 * 包括空视图、错误视图、点击监听及Item数据的自动添加
 *
 * @param errorItem         失败时显示的视图。默认为：[DefaultErrorItem]
 * @param emptyItem         数据为空时显示的视图。默认为：[DefaultEmptyItem]
 * @param listener          item点击监听
 */
fun <T : IRecyclerViewItem> Result<List<T>?>.bindRecyclerViewForNotPaging(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        emptyItem: IEmptyItem? = DefaultEmptyItem(),
        errorItem: IErrorItem? = DefaultErrorItem(),
        listener: OnItemClickListener? = null
) {
    bindRecyclerView(
            lifecycleOwner,
            adapter,
            emptyItem,
            errorItem,
            null,
            null,
            listener
    )
}

/**
 * 把 [Result] 与 [androidx.recyclerview.widget.RecyclerView] 进行绑定，往后加载更多分页
 * 包括空视图、错误视图、往后加载更多视图、点击监听及Item数据的自动添加
 *
 * @param errorItem         失败时显示的视图。默认为：[DefaultErrorItem]
 * @param emptyItem         数据为空时显示的视图。默认为：[DefaultEmptyItem]
 * @param loadMoreFooter    往后加载更多的视图。默认为：[DefaultLoadMoreFooter]
 * @param listener          item点击监听
 */
fun <T : IRecyclerViewItem> Result<List<T>?>.bindRecyclerViewForLoadAfterPaging(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        emptyItem: IEmptyItem? = DefaultEmptyItem(),
        errorItem: IErrorItem? = DefaultErrorItem(),
        loadMoreFooter: ILoadMoreFooter? = DefaultLoadMoreFooter { this.retry() },
        listener: OnItemClickListener? = null
) {
    bindRecyclerView(
            lifecycleOwner,
            adapter,
            emptyItem,
            errorItem,
            loadMoreFooter,
            null,
            listener
    )
}

/**
 * 把 [Result] 与 [androidx.recyclerview.widget.RecyclerView] 进行绑定，往前加载更多分页
 * 包括空视图、错误视图、往前加载更多视图、点击监听及Item数据的自动添加
 *
 * @param errorItem         失败时显示的视图。默认为：[DefaultErrorItem]
 * @param emptyItem         数据为空时显示的视图。默认为：[DefaultEmptyItem]
 * @param loadMoreHeader    往前加载更多的视图。默认为：[DefaultLoadMoreHeader]
 * @param listener          item点击监听
 */
fun <T : IRecyclerViewItem> Result<List<T>?>.bindRecyclerViewForLoadBeforePaging(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        emptyItem: IEmptyItem? = DefaultEmptyItem(),
        errorItem: IErrorItem? = DefaultErrorItem(),
        loadMoreHeader: ILoadMoreHeader? = DefaultLoadMoreHeader { this.retry() },
        listener: OnItemClickListener? = null
) {
    bindRecyclerView(
            lifecycleOwner,
            adapter,
            emptyItem,
            errorItem,
            null,
            loadMoreHeader,
            listener
    )
}

/**
 * 把 [Result] 与 [androidx.recyclerview.widget.RecyclerView] 进行绑定
 * 功能包括：Item数据的添加、空视图、错误视图、往后加载更多视图、往前加载更多视图、点击监听
 *
 * @param errorItem         失败时显示的视图。
 * @param emptyItem         数据为空时显示的视图。
 * @param loadMoreFooter    往后加载更多的视图。
 * @param loadMoreHeader    往前加载更多的视图。
 * @param listener          item点击监听
 */
private fun <T : IRecyclerViewItem> Result<List<T>?>.bindRecyclerView(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        emptyItem: IEmptyItem?,
        errorItem: IErrorItem?,
        loadMoreFooter: ILoadMoreFooter?,
        loadMoreHeader: ILoadMoreHeader?,
        listener: OnItemClickListener?
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
    liveValue.observe(lifecycleOwner) { list ->
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