package com.like.common.util.repository

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.like.common.util.shortToastCenter
import com.like.repository.RequestState
import com.like.repository.RequestType
import com.like.repository.Result

/**
 * 绑定成功失败回调。
 *
 * @param onSuccess         成功回调
 * @param onFailed          失败回调
 */
fun <ResultType> Result<ResultType>.bindResult(
        lifecycleOwner: LifecycleOwner,
        onSuccess: ((ResultType?) -> Unit)?,
        onFailed: ((RequestType, Throwable?) -> Unit)?
) {
    if (onSuccess != null) {
        liveValue.observe(lifecycleOwner) {
            val stateReport = liveState.value
            if (stateReport?.state is RequestState.Success) {
                onSuccess(it)
            }
        }
    }
    if (onFailed != null) {
        liveState.observe(lifecycleOwner) { stateReport ->
            val state = stateReport.state
            val type = stateReport.type
            if (state is RequestState.Failed) {
                onFailed(type, state.throwable)
            }
        }
    }
}

/**
 * 把 [Result] 与成功回调进行绑定。
 * 失败时进行 [android.widget.Toast] 提示
 */
fun <ResultType> Result<ResultType>.bindResult(
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
    bindResult(fragment, onSuccess, onFailed)
}

/**
 * 把 [Result] 与成功回调进行绑定。
 * 失败时进行 [android.widget.Toast] 提示
 */
fun <ResultType> Result<ResultType>.bindResult(
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
    bindResult(activity, onSuccess, onFailed)
}