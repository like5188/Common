package com.like.common.util.repository

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.like.common.base.BaseDialogFragment
import com.like.repository.RequestState
import com.like.repository.RequestType
import com.like.repository.Result

/**
 * 初始化或者刷新时控制进度条的显示隐藏。
 *
 * @param showProgress      初始化或者刷新开始时显示进度条
 * @param hideProgress      初始化或者刷新成功或者失败时隐藏进度条
 */
fun <ResultType> Result<ResultType>.bindProgress(
        lifecycleOwner: LifecycleOwner,
        showProgress: (() -> Unit)?,
        hideProgress: (() -> Unit)?
) {
    if (showProgress != null || hideProgress != null) {
        liveState.observe(lifecycleOwner) { stateReport ->
            val state = stateReport.state
            val type = stateReport.type
            if (type is RequestType.Initial || type is RequestType.Refresh) {
                when (state) {
                    is RequestState.Running -> {
                        showProgress?.invoke()
                    }
                    else -> {
                        hideProgress?.invoke()
                    }
                }
            }
        }
    }
}

/**
 * 把 [Result] 与 [com.like.common.base.BaseDialogFragment] 进行绑定
 */
fun <ResultType> Result<ResultType>.bindProgress(
        fragment: Fragment,
        progressDialogFragment: BaseDialogFragment
) {
    bindProgress(
            fragment,
            { progressDialogFragment.show(fragment) },
            { progressDialogFragment.dismiss() }
    )
}

/**
 * 把 [Result] 与 [com.like.common.base.BaseDialogFragment] 进行绑定
 */
fun <ResultType> Result<ResultType>.bindProgress(
        activity: FragmentActivity,
        progressDialogFragment: BaseDialogFragment
) {
    bindProgress(
            activity,
            { progressDialogFragment.show(activity) },
            { progressDialogFragment.dismiss() })
}

/**
 * 把 [Result] 与 [SwipeRefreshLayout] 进行绑定
 */
fun <ResultType> Result<ResultType>.bindProgress(
        lifecycleOwner: LifecycleOwner,
        swipeRefreshLayout: SwipeRefreshLayout,
        @ColorInt vararg colors: Int = intArrayOf(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW),
) {
    swipeRefreshLayout.setColorSchemeColors(*colors)

    swipeRefreshLayout.setOnRefreshListener {
        refresh()
    }

    bindProgress(
            lifecycleOwner,
            { swipeRefreshLayout.isRefreshing = true },
            { swipeRefreshLayout.isRefreshing = false }
    )
}