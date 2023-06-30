package com.like.common.util.mvi

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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
     * 搜集[UiState]中的某个属性的[Value]，不管它是否改变，都会触发[onValueCollected]
     */
    inline fun <Value> collectProperty(
        property: KProperty1<UiState, Value>,
        crossinline onValueCollected: suspend (newValue: Value) -> Unit
    ) {
        flow.property(property)
            .collectWithLifecycle(lifecycleOwner, lifecycleState, onValueCollected)
    }

    /**
     * 搜集[UiState]中的某个[Event]类型的属性的[Content]，不管[Event]是否处理，都会触发[onEventCollected]去处理事件
     */
    inline fun <Content> collectEventProperty(
        property: KProperty1<UiState, Event<Content>?>,
        crossinline onEventCollected: suspend (content: Content) -> Unit
    ) {
        flow.property(property)
            .toContent()
            .collectWithLifecycle(lifecycleOwner, lifecycleState, onEventCollected)
    }

    /**
     * 搜集[UiState]中的某个属性的[Value]，当其改变后触发[onValueChanged]
     */
    inline fun <Value> collectDistinctProperty(
        property: KProperty1<UiState, Value>,
        crossinline onValueChanged: suspend (newValue: Value) -> Unit
    ) {
        flow.property(property)
            .distinctUntilChanged()
            .collectWithLifecycle(lifecycleOwner, lifecycleState, onValueChanged)
    }

    /**
     * 搜集[UiState]中的某个[Event]类型的属性的[Content]，当此[Event]未处理，则触发[onNotHandledEventCollected]去处理事件
     */
    inline fun <Content> collectNotHandledEventProperty(
        property: KProperty1<UiState, Event<Content>?>,
        crossinline onNotHandledEventCollected: suspend (content: Content) -> Unit
    ) {
        flow.property(property)
            .toNotHandledContent()
            .collectWithLifecycle(lifecycleOwner, lifecycleState, onNotHandledEventCollected)
    }

}

inline fun <T> Flow<T>.collectWithLifecycle(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State,
    crossinline action: suspend (value: T) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        flowWithLifecycle(lifecycleOwner.lifecycle, lifecycleState).collect(action)
    }
}

/**
 * 从一个流 Flow<T> 中获取某个属性的流 Flow<R>
 */
fun <T, R> Flow<T>.property(property: KProperty1<T, R>): Flow<R> = map { property.get(it) }
