package com.like.common.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope
import java.util.concurrent.CountDownLatch

/**
 * 合并多个 suspend 方法：
 * ①、全部为 Success，则按顺序组合所有成功的结果并返回。
 * ②、只要有一个 Error，则抛出异常。
 */
@Throws(Exception::class)
suspend fun <ResultType> List<suspend () -> ResultType>.successIfAllSuccess(): List<ResultType> = coroutineScope {
    if (this@successIfAllSuccess.size < 2) {
        throw IllegalArgumentException("at least 2 suspend functions are required")
    }
    val result = mutableListOf<ResultType>()
    this@successIfAllSuccess.map {
        this.async(Dispatchers.IO) {
            it()
        }
    }.forEach { deferred ->
        deferred.await()?.let {
            result.add(it)
        }
    }
    result
}

/**
 * 合并多个 suspend 方法：
 * ①、只要有一个 Success，则按顺序组合所有成功的结果并返回。
 * ②、全部为 Error，则抛出第一个异常。
 */
@Throws(Exception::class)
suspend fun <ResultType> List<suspend () -> ResultType>.successIfOneSuccess(): List<ResultType> = supervisorScope {
    if (this@successIfOneSuccess.size < 2) {
        throw IllegalArgumentException("at least 2 suspend functions are required")
    }
    val result = mutableListOf<ResultType>()
    val totalExceptionTimes = CountDownLatch(this@successIfOneSuccess.size)
    var firstException: Throwable? = null
    this@successIfOneSuccess.map {
        this.async(Dispatchers.IO) {
            it()
        }
    }.forEach { deferred ->
        try {
            deferred.await()?.let {
                result.add(it)
            }
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