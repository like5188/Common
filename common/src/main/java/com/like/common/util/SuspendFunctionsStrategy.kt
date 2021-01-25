package com.like.common.util

import kotlinx.coroutines.*

/**
 * 在 launch 中，异常一旦发生就会立马被抛出 。因此，你可以使用 try/catch 包裹会发生异常的代码。
 *
 * 当 async 在根协程 (CoroutineScope 实例或者 supervisorJob 的直接子协程) 使用时，异常不会被自动抛出，而是直到你调用 .await() 时才抛出。
 *
 * CoroutineExceptionHandler:
 * 如果满足以下要求，异常将会被捕获：
 * 何时：是被可以自动抛异常的协程抛出的（launch，而不是 async）
 * 何地：在 CoroutineScope 或者根协程的协程上下文中（CoroutineScope 的直接子协程或者 supervisorScope）
 */
/**
 * 合并多个 suspend 方法：
 * ①、全部为 Success，则按顺序组合所有成功的结果并返回。
 * ②、只要有一个 Error，则抛出异常。
 */
@Throws(Exception::class)
suspend fun <ResultType> successIfAllSuccess(
        vararg suspendFunctions: suspend () -> ResultType
): List<ResultType> = coroutineScope {
    if (suspendFunctions.size < 2) {
        throw IllegalArgumentException("at least 2 suspend functions are required")
    }
    val result = mutableListOf<ResultType>()
    suspendFunctions.map {
        async(Dispatchers.IO) {
            it()
        }
    }.forEach { deferred ->
        result.add(deferred.await())
    }
    result
}

/**
 * 合并多个 suspend 方法：
 * ①、只要有一个 Success，则按顺序组合所有成功的结果并返回。
 * ②、全部为 Error，则抛出异常。
 */
@Throws(Exception::class)
suspend fun <ResultType> successIfOneSuccess(
        vararg suspendFunctions: suspend () -> ResultType
): List<ResultType> = supervisorScope {
    if (suspendFunctions.size < 2) {
        throw IllegalArgumentException("at least 2 suspend functions are required")
    }
    val result = mutableListOf<ResultType>()
    var exception: Throwable? = null
    suspendFunctions.map {
        async(Dispatchers.IO) {
            it()
        }
    }.forEach { deferred ->
        try {
            result.add(deferred.await())
        } catch (e: Exception) {
            if (exception == null) {
                exception = e
            } else {
                exception?.addSuppressed(e)
            }
        }
    }
    exception?.let {
        if (it.suppressed.size + 1 == suspendFunctions.size) {//全部失败
            throw exception!!
        }
    }
    result
}