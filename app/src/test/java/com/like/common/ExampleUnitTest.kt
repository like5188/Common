package com.like.common

import kotlinx.coroutines.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        Dispatchers.Default
        runBlocking<Unit> {
            launch { // 运行在父协程的上下文中，即 runBlocking 主协程
                println("main runBlocking      : I'm working in thread ${Thread.currentThread().name}")
            }
            launch(Dispatchers.Unconfined) { // 不受限的——将工作在主线程中
                println("Unconfined            : I'm working in thread ${Thread.currentThread().name}")
            }
            launch(Dispatchers.Default) { // 将会获取默认调度器
                println("Default               : I'm working in thread ${Thread.currentThread().name}")
            }
            launch(newSingleThreadContext("MyOwnThread")) { // 将使它获得一个新的线程
                println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
            }
        }
    }

    suspend fun concurrentSum(): Int = coroutineScope {
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }
        one.await() + two.await()
    }

    suspend fun doSomethingUsefulOne(): Int {
        delay(1000L) // 假设我们在这里做了一些有用的事
        return 13
    }

    suspend fun doSomethingUsefulTwo(): Int {
        delay(1000L) // 假设我们在这里也做了一些有用的事
        return 29
    }
}