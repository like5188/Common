package com.like.common.util.mvi

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
 * 通知事件，没有内容
 */
class NotificationEvent : Event<Unit>(Unit)
