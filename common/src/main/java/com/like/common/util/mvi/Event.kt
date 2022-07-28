package com.like.common.util.mvi

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.Flow

/**
 * 事件包装器（用于 MVI 架构）
 */
class Event<out Content>(private val content: Content) {
    companion object {
        /**
         * 通知事件，没有内容
         */
        val NOTIFICATION = Event(Unit)
    }

    private var hasBeenHandled = false

    /**
     * 如果没有处理过该事件，则返回内容；如果处理过了，则返回 null。
     */
    fun getContentIfNotHandled(): Content? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * 不管事件是否处理过，都返回内容
     */
    fun peekContent(): Content = content
}

fun <UiState> Flow<UiState>.collectUiState(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    action: UiStateCollector<UiState>.() -> Unit
) {
    UiStateCollector(lifecycleOwner, this, lifecycleState).action()
}
