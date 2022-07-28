package com.like.common.util.mvi

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty1

/**
 * 事件包装器（用于 MVI 架构）
 */
open class Event<out Content>(private val content: Content) {

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

/**
 * 没有内容的事件
 */
class UnitEvent : Event<Unit>(Unit)

fun <UiState, Value> Flow<UiState>.property(property: KProperty1<UiState, Value>): Flow<Value> =
    map { property.get(it) }.distinctUntilChanged()

fun <UiState, Content> Flow<UiState>.eventProperty(property: KProperty1<UiState, Event<Content?>?>): Flow<Content> =
    map { property.get(it)?.getContentIfNotHandled() }.filterNotNull()

inline fun <UiState, Value> Flow<UiState>.collectProperty(
    owner: LifecycleOwner,
    property: KProperty1<UiState, Value>,
    crossinline action: suspend (value: Value) -> Unit
) {
    owner.lifecycleScope.launch {
        this@collectProperty.property(property).flowWithLifecycle(owner.lifecycle, Lifecycle.State.STARTED).collect(action)
    }
}

inline fun <UiState, Content> Flow<UiState>.collectEventProperty(
    owner: LifecycleOwner,
    property: KProperty1<UiState, Event<Content?>?>,
    crossinline action: suspend (value: Content) -> Unit
) {
    owner.lifecycleScope.launch {
        this@collectEventProperty.eventProperty(property).flowWithLifecycle(owner.lifecycle, Lifecycle.State.STARTED).collect(action)
    }
}