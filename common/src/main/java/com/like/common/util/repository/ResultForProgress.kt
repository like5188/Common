package com.like.common.util.repository

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.like.common.base.BaseDialogFragment
import com.like.common.util.shortToastCenter
import com.like.repository.RequestState
import com.like.repository.RequestType
import com.like.repository.Result

/**
 * 把 [Result] 与成功回调进行绑定。
 * 失败时进行 [android.widget.Toast] 提示
 */
fun <ResultType> Result<ResultType>.bind(
        fragment: Fragment,
        onSuccess: ((ResultType?) -> Unit)? = null
) {
    val onFailed: ((RequestType, Throwable?) -> Unit)? = { requestType, throwable ->
        val errorMsg = if (throwable?.message.isNullOrEmpty()) {
            "unknown error"
        } else {
            throwable?.message
        }
        fragment.context?.shortToastCenter(errorMsg)
    }
    bind(fragment, onSuccess, onFailed, null, null)
}

/**
 * 把 [Result] 与成功回调进行绑定。
 * 失败时进行 [android.widget.Toast] 提示
 */
fun <ResultType> Result<ResultType>.bind(
        activity: FragmentActivity,
        onSuccess: ((ResultType?) -> Unit)? = null
) {
    val onFailed: ((RequestType, Throwable?) -> Unit)? = { requestType, throwable ->
        val errorMsg = if (throwable?.message.isNullOrEmpty()) {
            "unknown error"
        } else {
            throwable?.message
        }
        activity.shortToastCenter(errorMsg)
    }
    bind(activity, onSuccess, onFailed, null, null)
}

/**
 * 把 [Result] 与 [com.like.common.base.BaseDialogFragment] 进行绑定
 * 根据 [Result.liveState] 的值，在初始化或者刷新时控制进度条的显示隐藏，错误时进行 [android.widget.Toast] 提示
 */
fun <ResultType> Result<ResultType>.bindProgress(
        fragment: Fragment,
        progressDialogFragment: BaseDialogFragment,
        onSuccess: ((ResultType?) -> Unit)? = null
) {
    bind(fragment, onSuccess,
            { requestType, throwable ->
                val errorMsg = if (throwable?.message.isNullOrEmpty()) {
                    "unknown error"
                } else {
                    throwable?.message
                }
                fragment.context?.shortToastCenter(errorMsg)
            },
            { progressDialogFragment.show(fragment) },
            { progressDialogFragment.dismiss() }
    )
}

/**
 * 把 [Result] 与 [com.like.common.base.BaseDialogFragment] 进行绑定
 * 根据 [Result.liveState] 的值，在初始化或者刷新时控制进度条的显示隐藏，错误时进行 [android.widget.Toast] 提示
 */
fun <ResultType> Result<ResultType>.bindProgress(
        activity: FragmentActivity,
        progressDialogFragment: BaseDialogFragment,
        onSuccess: ((ResultType?) -> Unit)? = null
) {
    bind(activity, onSuccess,
            { requestType, throwable ->
                val errorMsg = if (throwable?.message.isNullOrEmpty()) {
                    "unknown error"
                } else {
                    throwable?.message
                }
                activity.shortToastCenter(errorMsg)
            },
            { progressDialogFragment.show(activity) },
            { progressDialogFragment.dismiss() })
}

/**
 * 把 [Result] 与 [SwipeRefreshLayout] 进行绑定
 * 根据 [Result.liveState] 的值，在初始化或者刷新时控制进度条的显示隐藏，错误时进行 [android.widget.Toast] 提示
 */
fun <ResultType> Result<ResultType>.bindProgress(
        lifecycleOwner: LifecycleOwner,
        swipeRefreshLayout: SwipeRefreshLayout,
        @ColorInt vararg colors: Int = intArrayOf(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW),
        onSuccess: ((ResultType?) -> Unit)? = null
) {
    swipeRefreshLayout.setColorSchemeColors(*colors)

    swipeRefreshLayout.setOnRefreshListener {
        refresh()
    }

    bind(lifecycleOwner, onSuccess,
            { requestType, throwable ->
                val errorMsg = if (throwable?.message.isNullOrEmpty()) {
                    "unknown error"
                } else {
                    throwable?.message
                }
                swipeRefreshLayout.context?.shortToastCenter(errorMsg)
            },
            { swipeRefreshLayout.isRefreshing = true },
            { swipeRefreshLayout.isRefreshing = false }
    )
}

/**
 * 绑定成功失败回调、控制进度条的显示隐藏。
 *
 * @param onSuccess         成功回调
 * @param onFailed          失败回调
 * @param showProgress      初始化或者刷新时显示进度条
 * @param hideProgress      初始化或者刷新时隐藏进度条
 */
private fun <ResultType> Result<ResultType>.bind(
        lifecycleOwner: LifecycleOwner,
        onSuccess: ((ResultType?) -> Unit)?,
        onFailed: ((RequestType, Throwable?) -> Unit)?,
        showProgress: (() -> Unit)?,
        hideProgress: (() -> Unit)?
) {
    if (onSuccess != null) {
        liveValue.observe(lifecycleOwner) {
            val stateReport = liveState.value
            if (stateReport?.state is RequestState.Success) {
                onSuccess.invoke(it)
            }
        }
    }
    if (onFailed != null || showProgress != null || hideProgress != null) {
        liveState.observe(lifecycleOwner) { stateReport ->
            val state = stateReport.state
            val type = stateReport.type
            if (state is RequestState.Failed) {
                onFailed?.invoke(type, state.throwable)
            }

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