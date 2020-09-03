package com.like.common.sample

import org.junit.Test
import kotlin.reflect.KFunction

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        start("1", 2)
    }

    companion object {

        fun start(a: String?, b: Int?) {
            b(*::start.transformParamsToPairList(a, b).toTypedArray())
            b("a" to a, "b" to b)
        }

        fun KFunction<*>.transformParamsToPairList(vararg params: Any?): List<Pair<String, Any?>> {
            val paramsNames = parameters.map { it.name ?: "" }
            if (paramsNames.isEmpty() || paramsNames.size != params.size) {
                return emptyList()
            }
            val result = mutableListOf<Pair<String, Any?>>()
            paramsNames.forEachIndexed { index, s ->
                result.add(Pair(s, params[index]))
            }
            return result
        }

        fun b(vararg params: Pair<String, Any?>) {
            params.forEach {
                println(it)
            }
        }
    }

}