package com.like.common.util.repository

import android.graphics.Color
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.like.common.base.BaseDialogFragment
import com.like.repository.RequestType
import com.like.repository.Result

/**
 * [Result]、[BaseDialogFragment] 组合
 */
fun <ResultType> Result<ResultType>.bindResultWithProgress(
        lifecycleOwner: LifecycleOwner,
        progressDialogFragment: BaseDialogFragment,
        onFailed: ((RequestType, Throwable) -> Unit)? = null,
        onSuccess: ((ResultType?) -> Unit)? = null
) {
    bindResult(lifecycleOwner, onFailed, onSuccess)
    bindProgress(
            lifecycleOwner,
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
    )
}

/**
 * [Result]、[SwipeRefreshLayout] 组合
 */
fun <ResultType> Result<ResultType>.bindResultWithProgress(
        lifecycleOwner: LifecycleOwner,
        swipeRefreshLayout: SwipeRefreshLayout,
        onFailed: ((RequestType, Throwable) -> Unit)? = null,
        onSuccess: ((ResultType?) -> Unit)? = null
) {
    bindResult(lifecycleOwner, onFailed, onSuccess)
    swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW)
    swipeRefreshLayout.setOnRefreshListener {
        refresh()
    }
    bindProgress(
            lifecycleOwner,
            { swipeRefreshLayout.isRefreshing = true },
            { swipeRefreshLayout.isRefreshing = false }
    )
}