package com.like.common.util.repository

import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.like.common.base.BaseDialogFragment
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.listener.OnItemClickListener
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.repository.Result

fun <ValueInList : IRecyclerViewItem> Result<List<ValueInList>?>.bindSuccessListResultToRecyclerView(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        type: RecyclerViewLoadType,
        onSuccess: ((List<ValueInList>?) -> Unit)? = null,
        listener: OnItemClickListener? = null
) {
    bindListResultToRecyclerView(
            lifecycleOwner,
            adapter,
            type,
            {
                it
            },
            onFailed(lifecycleOwner),
            onSuccess,
            listener
    )
}

fun <ValueInList : IRecyclerViewItem> Result<List<ValueInList>?>.bindSuccessListResultToRecyclerViewWithProgress(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        type: RecyclerViewLoadType,
        progressDialogFragment: BaseDialogFragment,
        onSuccess: ((List<ValueInList>?) -> Unit)? = null,
        listener: OnItemClickListener? = null
) {
    bindListResultToRecyclerViewWithProgress(
            lifecycleOwner,
            adapter,
            type,
            progressDialogFragment,
            {
                it
            },
            onFailed(lifecycleOwner),
            onSuccess,
            listener
    )
}

fun <ValueInList : IRecyclerViewItem> Result<List<ValueInList>?>.bindSuccessListResultToRecyclerViewWithProgress(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        type: RecyclerViewLoadType,
        swipeRefreshLayout: SwipeRefreshLayout,
        onSuccess: ((List<ValueInList>?) -> Unit)? = null,
        listener: OnItemClickListener? = null
) {
    bindListResultToRecyclerViewWithProgress(
            lifecycleOwner,
            adapter,
            type,
            swipeRefreshLayout,
            {
                it
            },
            onFailed(lifecycleOwner),
            onSuccess,
            listener
    )
}
