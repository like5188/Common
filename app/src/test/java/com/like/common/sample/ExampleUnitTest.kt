package com.like.common.sample

import org.junit.Test
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.jvm.javaType

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        start("1", 2)
//        start(0.1)
    }

    companion object {

        fun start(c: Double?) {
            execute(*getParamPairs(c))
        }

        fun start(a: Double?, b: Int?) {
            execute(*getParamPairs(a, b))
        }

        fun start(a: String?, b: Int?) {
            executeProxy(a, b)
        }

        fun getParamPairs(vararg params: Any?): Array<Pair<String, Any?>> {
            if (params.isNullOrEmpty()) {
                return emptyArray()
            }
            val methodName = Thread.currentThread().stackTrace[3].methodName
            val parameters = findMethod(methodName, *params) ?: return emptyArray()
            return parameters.mapIndexed { index, kParameter -> Pair(kParameter.name ?: "", params[index]) }.toTypedArray()
        }

        /**
         * 查找方法
         * 1、方法名称相同
         * 2、方法的形参数个数和实参个数相同
         * 3、方法的每一个形参数据类型和实参数据类型都相同
         *
         * @return  找到方法就返回方法的形参列表
         */
        fun findMethod(methodName: String, vararg params: Any?): List<KParameter>? {
            loop@ for (kFunction in this::class.declaredMemberFunctions) {
                if (kFunction.name != methodName) {
                    continue
                }
                val realParameters = kFunction.parameters.filter { it.kind == KParameter.Kind.VALUE }
                if (realParameters.size != params.size) {
                    continue
                }
                for (i in realParameters.indices) {
                    val kParameter = realParameters[i]
                    val param = params[i]
                    if (kParameter.type.javaType.typeName != param?.javaClass?.typeName) {
                        continue@loop
                    }
                }
                return realParameters
            }
            return null
        }

        fun executeProxy(vararg params: Any?) {
            execute(*getParamPairs(*params))
        }

        fun execute(vararg params: Pair<String, Any?>) {
            params.forEach {
                println(it)
            }
        }
    }

}