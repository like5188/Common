package com.like.common.util.datasource

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.like.common.base.BaseDialogFragment
import com.like.datasource.RequestType
import com.like.datasource.Result
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.ui.DefaultEmptyItem
import com.like.recyclerview.ui.DefaultErrorItem
import com.like.recyclerview.ui.DefaultLoadMoreFooter
import com.like.recyclerview.ui.DefaultLoadMoreHeader

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
suspend fun <ResultType, ValueInList : IRecyclerViewItem> Result<ResultType>.collectForRecyclerView(
        adapter: BaseAdapter,
        type: RecyclerViewLoadType,
        transform: (ResultType) -> List<ValueInList>?,
        onFailed: ((RequestType, Throwable) -> Unit)? = null,
        onSuccess: ((RequestType, ResultType) -> Unit)? = null
) {
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
    recyclerView(adapter, transform, DefaultEmptyItem(), DefaultErrorItem(), loadMoreFooter, loadMoreHeader)
            .collect(onFailed, onSuccess)
}

/**
 * [Result]、[RecyclerView]、[BaseDialogFragment] 组合
 */
suspend fun <ResultType, ValueInList : IRecyclerViewItem> Result<ResultType>.collectWithProgressForRecyclerView(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        type: RecyclerViewLoadType,
        progressDialogFragment: BaseDialogFragment,
        transform: (ResultType) -> List<ValueInList>?,
        onFailed: ((RequestType, Throwable) -> Unit)? = null,
        onSuccess: ((RequestType, ResultType) -> Unit)? = null
) {
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
    recyclerView(adapter, transform, DefaultEmptyItem(), DefaultErrorItem(), loadMoreFooter, loadMoreHeader)
            .collectWithProgress(lifecycleOwner, progressDialogFragment, onFailed, onSuccess)
}

/**
 * [Result]、[RecyclerView]、[SwipeRefreshLayout] 组合
 */
suspend fun <ResultType, ValueInList : IRecyclerViewItem> Result<ResultType>.collectWithProgressForRecyclerView(
        adapter: BaseAdapter,
        type: RecyclerViewLoadType,
        swipeRefreshLayout: SwipeRefreshLayout,
        transform: (ResultType) -> List<ValueInList>?,
        onFailed: ((RequestType, Throwable) -> Unit)? = null,
        onSuccess: ((RequestType, ResultType) -> Unit)? = null
) {
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
    recyclerView(adapter, transform, DefaultEmptyItem(), DefaultErrorItem(), loadMoreFooter, loadMoreHeader)
            .collectWithProgress(swipeRefreshLayout, onFailed, onSuccess)
}