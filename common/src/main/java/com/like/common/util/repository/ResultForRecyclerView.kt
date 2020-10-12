package com.like.common.util.repository

import androidx.lifecycle.LifecycleOwner
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

sealed class RecyclerViewLoadType {
    object NotPaging : RecyclerViewLoadType()
    object LoadAfter : RecyclerViewLoadType()
    object LoadBefore : RecyclerViewLoadType()
}

fun <ValueInList : IRecyclerViewItem> Result<List<ValueInList>?>.bindListResultToRecyclerView(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        type: RecyclerViewLoadType,
        onFailed: ((RequestType, Throwable) -> Unit)? = null,
        onSuccess: ((List<ValueInList>?) -> Unit)? = null,
        listener: OnItemClickListener? = null
) {
    bindResult(lifecycleOwner, onFailed, onSuccess)

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
            DefaultEmptyItem(),
            DefaultErrorItem(),
            loadMoreFooter,
            loadMoreHeader,
            listener
    )
}

fun <ValueInList : IRecyclerViewItem> Result<List<ValueInList>?>.bindListResultToRecyclerViewWithProgress(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        type: RecyclerViewLoadType,
        progressDialogFragment: BaseDialogFragment,
        onFailed: ((RequestType, Throwable) -> Unit)? = null,
        onSuccess: ((List<ValueInList>?) -> Unit)? = null,
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
            DefaultEmptyItem(),
            DefaultErrorItem(),
            loadMoreFooter,
            loadMoreHeader,
            listener
    )
}

fun <ValueInList : IRecyclerViewItem> Result<List<ValueInList>?>.bindListResultToRecyclerViewWithProgress(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        type: RecyclerViewLoadType,
        swipeRefreshLayout: SwipeRefreshLayout,
        onFailed: ((RequestType, Throwable) -> Unit)? = null,
        onSuccess: ((List<ValueInList>?) -> Unit)? = null,
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
            DefaultEmptyItem(),
            DefaultErrorItem(),
            loadMoreFooter,
            loadMoreHeader,
            listener
    )
}