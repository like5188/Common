package com.like.common.util.repository

import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.like.common.base.BaseDialogFragment
import com.like.repository.RequestType
import com.like.repository.Result

fun <ResultType> Result<ResultType>.bindSuccessResult(
        lifecycleOwner: LifecycleOwner,
        onSuccess: ((ResultType?) -> Unit)? = null
) {
    bindResult(lifecycleOwner, onFailed(lifecycleOwner), onSuccess)
}

fun <ResultType> Result<ResultType>.bindSuccessResultWithProgress(
        lifecycleOwner: LifecycleOwner,
        progressDialogFragment: BaseDialogFragment,
        onSuccess: ((ResultType?) -> Unit)? = null
) {
    bindResultWithProgress(lifecycleOwner, progressDialogFragment, onFailed(lifecycleOwner), onSuccess)
}

fun <ResultType> Result<ResultType>.bindSuccessResultWithProgress(
        lifecycleOwner: LifecycleOwner,
        swipeRefreshLayout: SwipeRefreshLayout,
        onSuccess: ((ResultType?) -> Unit)? = null
) {
    bindResultWithProgress(lifecycleOwner, swipeRefreshLayout, onFailed(lifecycleOwner), onSuccess)
}