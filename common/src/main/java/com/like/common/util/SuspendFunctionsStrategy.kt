package com.like.common.util

import kotlinx.coroutines.*

/**
 * [launch]：未捕获异常，自动传播，直到根协程。
 * [async]：向用户暴露异常。会捕获所有异常并将其表示在结果 [Deferred] 对象中，用户最终通过 [await] 来消费异常。
 * [coroutineScope]：它的子协程会把异常委托给其父协程，这样一直向上直到根协程。
 * [supervisorScope]：它的子协程不会将异常传播到其父协程，需要自己处理。
 *
 * [CoroutineExceptionHandler]：
 * 1、只针对 [launch]。
 * 2、[coroutineScope]的根协程中，或者[supervisorScope]的子协程中。
 */
/**
 * 合并多个 suspend 方法：
 * ①、全部为 Success，则按[suspendFunctions]顺序返回结果集合。
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
 * ①、只要有一个 Success，则按[suspendFunctions]顺序返回结果集合。其中失败部分对应的结果为 Exception。
 * ②、全部为 Error，则抛出异常。
 */
@Throws(Exception::class)
suspend fun successIfOneSuccess(
    vararg suspendFunctions: suspend () -> Any
): List<Any> = supervisorScope {
    if (suspendFunctions.size < 2) {
        throw IllegalArgumentException("at least 2 suspend functions are required")
    }
    val result = mutableListOf<Any>()
    var exception: Throwable? = null
    suspendFunctions.map {
        async(Dispatchers.IO) {
            it()
        }
    }.forEach { deferred ->
        try {
            result.add(deferred.await())
        } catch (e: Exception) {
            result.add(e)
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