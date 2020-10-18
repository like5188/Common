package com.like.common.util.datasource

import android.graphics.Color
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.like.common.base.BaseDialogFragment
import com.like.datasource.Result
import com.like.datasource.ResultReport

/**
 * [Result]、[BaseDialogFragment] 组合
 */
suspend fun <ResultType> Result<ResultType>.collectWithProgress(
        lifecycleOwner: LifecycleOwner,
        progressDialogFragment: BaseDialogFragment,
        onFailed: ((ResultReport<Nothing>) -> Unit)? = null,
        onSuccess: ((ResultType) -> Unit)? = null
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
        onFailed: ((ResultReport<Nothing>) -> Unit)? = null,
        onSuccess: ((ResultType) -> Unit)? = null
) {
    swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW)
    swipeRefreshLayout.setOnRefreshListener {
        refresh()
    }
    progress(
            { swipeRefreshLayout.isRefreshing = true },
            { swipeRefreshLayout.isRefreshing = false }
    ).collect(onFailed, onSuccess)
}