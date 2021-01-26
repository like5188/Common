package com.like.common.sample

import com.like.common.util.successIfAllSuccess
import com.like.common.util.successIfOneSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() = runBlocking {
        println(successIfOneSuccess(::a, ::b))
    }

    private suspend fun a(): String {
        delay(200)
//        throw RuntimeException("a error")
        return "a"
    }

    private suspend fun b(): String {
        delay(100)
//        throw RuntimeException("b error")
        return "b"
    }

}