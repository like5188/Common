package com.like.common.util.repository

import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.like.common.base.BaseDialogFragment
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.listener.OnItemClickListener
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.repository.Result

fun <ValueInList : IRecyclerViewItem> Result<IPagingModel<ValueInList>?>.bindSuccessPagingModelResultToRecyclerView(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        type: RecyclerViewLoadType,
        onSuccess: ((IPagingModel<ValueInList>?) -> Unit)? = null,
        listener: OnItemClickListener? = null
) {
    bindListResultToRecyclerView(
            lifecycleOwner,
            adapter,
            type,
            {
                it?.list()
            },
            onFailed(lifecycleOwner),
            onSuccess,
            listener
    )
}

fun <ValueInList : IRecyclerViewItem> Result<IPagingModel<ValueInList>?>.bindSuccessPagingModelResultToRecyclerViewWithProgress(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        type: RecyclerViewLoadType,
        progressDialogFragment: BaseDialogFragment,
        onSuccess: ((IPagingModel<ValueInList>?) -> Unit)? = null,
        listener: OnItemClickListener? = null
) {
    bindListResultToRecyclerViewWithProgress(
            lifecycleOwner,
            adapter,
            type,
            progressDialogFragment,
            {
                it?.list()
            },
            onFailed(lifecycleOwner),
            onSuccess,
            listener
    )
}

fun <ValueInList : IRecyclerViewItem> Result<IPagingModel<ValueInList>?>.bindSuccessPagingModelResultToRecyclerViewWithProgress(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        type: RecyclerViewLoadType,
        swipeRefreshLayout: SwipeRefreshLayout,
        onSuccess: ((IPagingModel<ValueInList>?) -> Unit)? = null,
        listener: OnItemClickListener? = null
) {
    bindListResultToRecyclerViewWithProgress(
            lifecycleOwner,
            adapter,
            type,
            swipeRefreshLayout,
            {
                it?.list()
            },
            onFailed(lifecycleOwner),
            onSuccess,
            listener
    )
}