package com.like.common.util

import kotlinx.coroutines.*
import java.util.concurrent.CountDownLatch

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
 * ②、全部为 Error，则抛出第一个异常。
 */
@Throws(Exception::class)
suspend fun <ResultType> successIfOneSuccess(
        vararg suspendFunctions: suspend () -> ResultType
): List<ResultType> = supervisorScope {
    if (suspendFunctions.size < 2) {
        throw IllegalArgumentException("at least 2 suspend functions are required")
    }
    val result = mutableListOf<ResultType>()
    val totalExceptionTimes = CountDownLatch(suspendFunctions.size)
    var firstException: Throwable? = null
    suspendFunctions.map {
        async(Dispatchers.IO) {
            it()
        }
    }.forEach { deferred ->
        try {
            result.add(deferred.await())
        } catch (e: Exception) {
            if (firstException == null) {
                firstException = e
            }
            totalExceptionTimes.countDown()
        }
    }
    if (totalExceptionTimes.count == 0L) {//全部失败
        throw firstException ?: RuntimeException("all suspend functions execute error")
    }
    result
}