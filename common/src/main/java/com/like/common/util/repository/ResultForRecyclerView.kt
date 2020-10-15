package com.like.common.util.repository

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.like.common.base.BaseDialogFragment
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.listener.OnItemClickListener
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.ui.DefaultEmptyItem
import com.like.recyclerview.ui.DefaultErrorItem
import com.like.recyclerview.ui.DefaultLoadMoreFooter
import com.like.recyclerview.ui.DefaultLoadMoreHeader
import com.like.repository.RequestType
import com.like.repository.Result

/**
 * [RecyclerView] 的加载类型
 */
sealed class RecyclerViewLoadType {
    /**
     * 不分页
     */
    object NotPaging : RecyclerViewLoadType()

    /**
     * 往后加载更多
     */
    object LoadAfter : RecyclerViewLoadType()

    /**
     * 往前加载更多
     */
    object LoadBefore : RecyclerViewLoadType()
}

/**
 * [Result]、[RecyclerView] 组合
 */
fun <ResultType, ValueInList : IRecyclerViewItem> Result<ResultType>.bindResultToRecyclerView(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        type: RecyclerViewLoadType,
        transform: (ResultType) -> List<ValueInList>?,
        onFailed: ((RequestType, Throwable) -> Unit)? = null,
        onSuccess: ((ResultType?) -> Unit)? = null,
        listener: OnItemClickListener? = null
) {
    bind(lifecycleOwner, onFailed, onSuccess)

    val loadMoreFooter = if (type is RecyclerViewLoadType.LoadAfter) {
        DefaultLoadMoreFooter { this.retry() }
    } else {
        null
    }
    val loadMoreHeader = if (type is RecyclerViewLoadType.LoadBefore) {
        DefaultLoadMoreHeader { this.retry() }
    } else {
        null
    }
    bindRecyclerView(
            lifecycleOwner,
            adapter,
            transform,
            DefaultEmptyItem(),
            DefaultErrorItem(),
            loadMoreFooter,
            loadMoreHeader,
            listener
    )
}

/**
 * [Result]、[RecyclerView]、[BaseDialogFragment] 组合
 */
fun <ResultType, ValueInList : IRecyclerViewItem> Result<ResultType>.bindResultToRecyclerViewWithProgress(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        type: RecyclerViewLoadType,
        progressDialogFragment: BaseDialogFragment,
        transform: (ResultType) -> List<ValueInList>?,
        onFailed: ((RequestType, Throwable) -> Unit)? = null,
        onSuccess: ((ResultType?) -> Unit)? = null,
        listener: OnItemClickListener? = null
) {
    bindResultWithProgress(lifecycleOwner, progressDialogFragment, onFailed, onSuccess)

    val loadMoreFooter = if (type is RecyclerViewLoadType.LoadAfter) {
        DefaultLoadMoreFooter { this.retry() }
    } else {
        null
    }
    val loadMoreHeader = if (type is RecyclerViewLoadType.LoadBefore) {
        DefaultLoadMoreHeader { this.retry() }
    } else {
        null
    }
    bindRecyclerView(
            lifecycleOwner,
            adapter,
            transform,
            DefaultEmptyItem(),
            DefaultErrorItem(),
            loadMoreFooter,
            loadMoreHeader,
            listener
    )
}

/**
 * [Result]、[RecyclerView]、[SwipeRefreshLayout] 组合
 */
fun <ResultType, ValueInList : IRecyclerViewItem> Result<ResultType>.bindResultToRecyclerViewWithProgress(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        type: RecyclerViewLoadType,
        swipeRefreshLayout: SwipeRefreshLayout,
        transform: (ResultType) -> List<ValueInList>?,
        onFailed: ((RequestType, Throwable) -> Unit)? = null,
        onSuccess: ((ResultType?) -> Unit)? = null,
        listener: OnItemClickListener? = null
) {
    bindResultWithProgress(lifecycleOwner, swipeRefreshLayout, onFailed, onSuccess)

    val loadMoreFooter = if (type is RecyclerViewLoadType.LoadAfter) {
        DefaultLoadMoreFooter { this.retry() }
    } else {
        null
    }
    val loadMoreHeader = if (type is RecyclerViewLoadType.LoadBefore) {
        DefaultLoadMoreHeader { this.retry() }
    } else {
        null
    }
    bindRecyclerView(
            lifecycleOwner,
            adapter,
            transform,
            DefaultEmptyItem(),
            DefaultErrorItem(),
            loadMoreFooter,
            loadMoreHeader,
            listener
    )
}