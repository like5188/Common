package com.like.common.sample

import com.like.common.util.fractionDigits
import com.like.common.util.maximumFractionDigits
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        println(13.4049.maximumFractionDigits(0))
        println(13.5049.maximumFractionDigits(1))
        println(13.5049.maximumFractionDigits(2))
        println(13.5049.maximumFractionDigits(3))
        println(13.5049.maximumFractionDigits(4))
        println(13.5049.maximumFractionDigits(5))
        println("-----------------------------------------------------")
        println(13.4049.fractionDigits(0))
        println(13.5049.fractionDigits(1))
        println(13.5049.fractionDigits(2))
        println(13.5049.fractionDigits(3))
        println(13.5049.fractionDigits(4))
        println(13.5049.fractionDigits(5))
    }

}