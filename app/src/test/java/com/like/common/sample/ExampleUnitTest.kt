package com.like.common.sample

import org.junit.Test
import kotlin.reflect.full.declaredMemberFunctions

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
            execute(*getParamPairs(a, b).toTypedArray())
        }

        fun getParamPairs(vararg params: Any?): List<Pair<String, Any?>> {
            val methodName = Thread.currentThread().stackTrace[2].methodName
            val paramNames = getParamNames(methodName)
            return transformParamsToPairList(paramNames, *params)
        }

        fun getParamNames(methodName: String): List<String> {
            this::class.declaredMemberFunctions.forEach {
                if (it.name == methodName) {
                    val result = mutableListOf<String>()
                    it.parameters.forEach {
                        if (it.index > 0) {
                            result.add(it.name ?: "")
                        }
                    }
                    return result
                }
            }
            return emptyList()
        }

        fun transformParamsToPairList(paramNames: List<String>, vararg params: Any?): List<Pair<String, Any?>> {
            if (paramNames.isEmpty() || paramNames.size != params.size) {
                return emptyList()
            }
            val result = mutableListOf<Pair<String, Any?>>()
            paramNames.forEachIndexed { index, s ->
                result.add(Pair(s, params[index]))
            }
            return result
        }

        fun execute(vararg params: Pair<String, Any?>) {
            params.forEach {
                println(it)
            }
        }
    }

}