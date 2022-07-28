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

/**
 * 从[UiState]中获取某个属性
 */
fun <UiState, Value> Flow<UiState>.property(property: KProperty1<UiState, Value>): Flow<Value> =
    map { property.get(it) }.distinctUntilChanged()

/**
 * 从[UiState]中获取某个事件属性
 */
fun <UiState, Content> Flow<UiState>.eventProperty(property: KProperty1<UiState, Event<Content>?>): Flow<Content> =
    map { property.get(it)?.getContentIfNotHandled() }.filterNotNull()

/**
 * 搜集[UiState]中的某个属性，当此属性改变后触发[onValueChanged]
 */
inline fun <UiState, Value> Flow<UiState>.collectProperty(
    owner: LifecycleOwner,
    property: KProperty1<UiState, Value>,
    crossinline onValueChanged: suspend (newValue: Value) -> Unit
) {
    owner.lifecycleScope.launch {
        this@collectProperty.property(property).flowWithLifecycle(owner.lifecycle, Lifecycle.State.STARTED).collect(onValueChanged)
    }
}

/**
 * 搜集[UiState]中的某个事件属性，当此事件属性未处理，则触发[onHandleEvent]去处理事件
 */
inline fun <UiState, Content> Flow<UiState>.collectEventProperty(
    owner: LifecycleOwner,
    property: KProperty1<UiState, Event<Content>?>,
    crossinline onHandleEvent: suspend (content: Content) -> Unit
) {
    owner.lifecycleScope.launch {
        this@collectEventProperty.eventProperty(property).flowWithLifecycle(owner.lifecycle, Lifecycle.State.STARTED).collect(onHandleEvent)
    }
}