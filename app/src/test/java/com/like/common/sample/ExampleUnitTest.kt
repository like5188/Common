package com.like.common.sample

import kotlinx.coroutines.*
import org.junit.Test
import kotlin.system.measureTimeMillis

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        runBlocking {
            isActive
            val time = measureTimeMillis {
                // 我们可以在协程外面启动异步执行
                val one = somethingUsefulOneAsync()
                val two = somethingUsefulTwoAsync()
                // 但是等待结果必须调用其它的挂起或者阻塞
                // 当我们等待结果的时候，这里我们使用 `runBlocking { …… }` 来阻塞主线程
                runBlocking {
                    println("The answer is ${one.await() + two.await()}")
                }
            }
            println("Completed in $time ms")
        }
    }

    fun somethingUsefulOneAsync() = GlobalScope.async {
        doSomethingUsefulOne()
    }

    fun somethingUsefulTwoAsync() = GlobalScope.async {
        doSomethingUsefulTwo()
    }

    suspend fun doSomethingUsefulOne(): Int {
        throw RuntimeException("12312321")
    }

    suspend fun doSomethingUsefulTwo(): Int {
        delay(1000L) // 假设我们在这里也做了些有用的事
        println("2")
        return 29
    }
}