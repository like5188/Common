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
        start(0.1)
        start(0.2, 2)
        val caller = AAA()
        start(caller, "3", 3) {

        }
    }

    companion object {

        fun start(c: Double?) {
            startActivityProxy(c)
        }

        fun start(a: Double?, b: Int?) {
            startActivityProxy(a, b)
        }

        fun start(activityResultCaller: ActivityResultCaller, a: String?, b: Int?, callback: (ActivityResultCaller) -> Unit) {
            startActivityForResultProxy(activityResultCaller, callback, a, b)
        }

        /**
         * 把参数转换成 "name" to name 这样的键值对
         *
         * @param actualParameters          实参（需要注入的参数）
         * @param filterFormalParameters    过滤掉形参中的不需要注入的参数
         */
        fun transformParametersToPairs(vararg actualParameters: Any?, filterFormalParameters: (List<KParameter>) -> List<KParameter>): Array<Pair<String, Any?>> {
            if (actualParameters.isNullOrEmpty()) {
                return emptyArray()
            }
            // 查找调用者方法
            loop@ for (kFunction in this::class.declaredMemberFunctions) {
                //方法名称相同
                if (kFunction.name != Thread.currentThread().stackTrace[3].methodName) {
                    continue
                }
                //方法的形参数个数和实参个数相同
                var realFormalParameters = kFunction.parameters.filter { it.kind == KParameter.Kind.VALUE }
                realFormalParameters = filterFormalParameters(realFormalParameters)
                if (realFormalParameters.size != actualParameters.size) {
                    continue
                }
                //方法的每一个形参数据类型和实参数据类型都相同
                for (i in realFormalParameters.indices) {
                    val formalParameter = realFormalParameters[i]
                    val actualParameter = actualParameters[i]
                    if (formalParameter.type.javaType.typeName != actualParameter?.javaClass?.typeName) {
                        continue@loop
                    }
                }

                // 找到了方法，就把它的形参列表和实参列表组合成Pair列表
                return realFormalParameters.mapIndexed { index, kParameter ->
                    Pair(kParameter.name ?: "", actualParameters[index])
                }.toTypedArray()
            }
            return emptyArray()
        }

        fun startActivityProxy(vararg actualParameters: Any?) {
            execute(*transformParametersToPairs(*actualParameters) {
                return@transformParametersToPairs it
            })
        }

        fun startActivityForResultProxy(activityResultCaller: ActivityResultCaller, callback: (ActivityResultCaller) -> Unit, vararg actualParameters: Any?) {
            activityResultCaller.execute(*transformParametersToPairs(*actualParameters) {
                return@transformParametersToPairs it.subList(1, it.size - 1)//去掉第一个和最后一个形参。
            }, callback = callback)
        }

        fun execute(vararg params: Pair<String, Any?>) {
            params.forEach {
                println(it)
            }
        }

        fun ActivityResultCaller.execute(vararg params: Pair<String, Any?>, callback: (ActivityResultCaller) -> Unit) {
            params.forEach {
                println(it)
            }
        }
    }

    open class ActivityResultCaller
    class AAA : ActivityResultCaller()

}