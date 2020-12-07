package com.like.common.util.datasource

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.like.common.base.BaseDialogFragment
import com.like.datasource.RequestType
import com.like.datasource.Result

/**
 * [Result]、[BaseDialogFragment] 组合
 */
suspend fun <ResultType> Result<ResultType>.collectWithProgress(
        lifecycleOwner: LifecycleOwner,
        progressDialogFragment: BaseDialogFragment,
        onFailed: ((RequestType, Throwable) -> Unit)? = null,
        onSuccess: ((RequestType, ResultType) -> Unit)? = null
) {
    progress(
            {
                when (lifecycleOwner) {
                    is FragmentActivity -> {
                        progressDialogFragment.show(lifecycleOwner)
                    }
                    is Fragment -> {
                        progressDialogFragment.show(lifecycleOwner)
                    }
                }
            },
            { progressDialogFragment.dismiss() }
    ).collect(onFailed, onSuccess)
}

/**
 * [Result]、[SwipeRefreshLayout] 组合
 */
suspend fun <ResultType> Result<ResultType>.collectWithProgress(
        swipeRefreshLayout: SwipeRefreshLayout,
        onFailed: ((RequestType, Throwable) -> Unit)? = null,
        onSuccess: ((RequestType, ResultType) -> Unit)? = null
) {
    swipeRefreshLayout.setOnRefreshListener {
        refresh()
    }
    progress(
            { swipeRefreshLayout.isRefreshing = true },
            { swipeRefreshLayout.isRefreshing = false }
    ).collect(onFailed, onSuccess)
}