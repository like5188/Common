package com.like.common.util.mvi

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty1

/**
 * UiState 搜集器
 */
class UiStateCollector<UiState>(
    val lifecycleOwner: LifecycleOwner,
    val flow: Flow<UiState>,
    val lifecycleState: Lifecycle.State
) {

    /**
     * 搜集[UiState]中的某个属性，当此属性改变后触发[onValueChanged]
     */
    inline fun <Value> collectProperty(
        property: KProperty1<UiState, Value>,
        crossinline onValueChanged: suspend (newValue: Value) -> Unit
    ) {
        lifecycleOwner.lifecycleScope.launch {
            property(property)
                .distinctUntilChanged()// 属性改变
                .flowWithLifecycle(lifecycleOwner.lifecycle, lifecycleState)
                .collect(onValueChanged)
        }
    }

    /**
     * 搜集[UiState]中的某个事件属性，当此事件属性未处理，则触发[onHandleEvent]去处理事件
     */
    inline fun <Content> collectEventProperty(
        property: KProperty1<UiState, Event<Content>?>,
        crossinline onHandleEvent: suspend (content: Content) -> Unit
    ) {
        lifecycleOwner.lifecycleScope.launch {
            eventProperty(property)
                .filterNotNull()// 事件属性未处理
                .flowWithLifecycle(lifecycleOwner.lifecycle, lifecycleState)
                .collect(onHandleEvent)
        }
    }

    /**
     * 从[UiState]中获取某个属性
     */
    fun <Value> property(property: KProperty1<UiState, Value>): Flow<Value> =
        flow.map { property.get(it) }

    /**
     * 从[UiState]中获取某个事件属性
     */
    fun <Content> eventProperty(property: KProperty1<UiState, Event<Content>?>): Flow<Content?> =
        flow.map { property.get(it)?.getContentIfNotHandled() }

}
