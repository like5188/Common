package com.like.common.util

import kotlinx.coroutines.*

/**
 * [launch]：未捕获异常，自动传播，直到根协程。
 * [async]：向用户暴露异常。会捕获所有异常并将其表示在结果 [Deferred] 对象中，用户最终通过 [await] 来消费异常。
 * [CoroutineExceptionHandler]：
 * 1、只针对 [launch]。
 * 2、当使用 [coroutineScope] 时：用于根协程。
 * 当使用 [supervisorScope] 时：由于它的子协程不会将异常传播到其父协程，所以可用 [CoroutineExceptionHandler] 处理自身异常。
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