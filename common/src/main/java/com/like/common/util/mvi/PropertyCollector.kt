package com.like.common.util.mvi

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty1

fun <UiState> Flow<UiState>.propertyCollector(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    action: PropertyCollector<UiState>.() -> Unit
) {
    PropertyCollector(lifecycleOwner, this, lifecycleState).action()
}

/**
 * 属性搜集器
 * 通过反射搜集[UiState]中的某个属性
 */
class PropertyCollector<UiState>(
    val lifecycleOwner: LifecycleOwner,
    val flow: Flow<UiState>,
    val lifecycleState: Lifecycle.State
) {

    /**
     * 搜集[UiState]中的某个属性的[Value]，当其改变后触发[onValueChanged]
     */
    inline fun <Value> collectProperty(
        property: KProperty1<UiState, Value>,
        crossinline onValueChanged: suspend (newValue: Value) -> Unit
    ) {
        collectWithLifecycle(
            property(property).distinctUntilChanged(),// 属性改变
            onValueChanged
        )
    }

    /**
     * 搜集[UiState]中的某个[Event]类型的属性的[Content]，当此[Event]未处理，则触发[onHandleEvent]去处理事件
     */
    inline fun <Content> collectEventProperty(
        property: KProperty1<UiState, Event<Content>?>,
        crossinline onHandleEvent: suspend (content: Content) -> Unit
    ) {
        collectWithLifecycle(
            property(property).mapNotNull { it?.getContentIfNotHandled() },// 事件属性未处理
            onHandleEvent
        )
    }

    inline fun <T> collectWithLifecycle(flow: Flow<T>, crossinline action: suspend (value: T) -> Unit) {
        lifecycleOwner.lifecycleScope.launch {
            flow.flowWithLifecycle(lifecycleOwner.lifecycle, lifecycleState)
                .collect(action)
        }
    }

    /**
     * 从[UiState]中获取某个属性的[Value]
     */
    fun <Value> property(property: KProperty1<UiState, Value>): Flow<Value> = flow.map { property.get(it) }

}
