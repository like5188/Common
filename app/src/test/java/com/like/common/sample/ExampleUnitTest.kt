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
            startActivityByApplicationByParameterNameKeyed(c)
        }

        fun start(a: Double?, b: Int?) {
            startActivityByApplicationByParameterNameKeyed(a, b)
        }

        fun start(activityResultCaller: ActivityResultCaller, a: String?, b: Int?, callback: (ActivityResultCaller) -> Unit) {
            activityResultCaller.startActivityForResultOkByParameterNameKeyed(callback, a, b)
        }

        /**
         * [execute]方法的加强版
         * 添加参数时不需要手动添加key，会通过反射自动把调用者方法的形参名作为key。
         *
         * @param params    实参列表，顺序必须和调用者方法的形参列表一致
         *
         * 必须在单独的方法中调用此方法，便于通过反射获取调用者方法的形参列表，如例子中的start()方法
         */
        fun startActivityByApplicationByParameterNameKeyed(vararg params: Any?) {
            execute(*transformParametersToPairs(*params) {
                return@transformParametersToPairs it
            })
        }

        /**
         * [ActivityResultCaller.execute]方法的加强版
         * 添加参数时不需要手动添加key，会通过反射自动把调用者方法的形参名作为key
         *
         * @param params    实参列表，顺序必须和调用者方法的形参列表一致
         *
         * 必须在单独的方法中调用此方法，便于通过反射获取调用者方法的形参列表，如例子中的start()方法
         */
        fun ActivityResultCaller.startActivityForResultOkByParameterNameKeyed(callback: (ActivityResultCaller) -> Unit, vararg params: Any?) {
            execute(*transformParametersToPairs(*params) {
                // 去掉第一个形参（activityResultCaller: ActivityResultCaller）
                // 去掉最后一个形参（callback: (ActivityResultCaller) -> Unit）
                return@transformParametersToPairs it.subList(1, it.size - 1)
            }, callback = callback)
        }

        /**
         * 把形参转换成 "name" to name 这样的键值对
         *
         * @param actualParameters          实参（需要注入的参数）
         * @param filterFormalParameters    过滤掉形参中的不需要注入的参数
         */
        fun transformParametersToPairs(
                vararg actualParameters: Any?,
                filterFormalParameters: (List<KParameter>) -> List<KParameter>
        ): Array<Pair<String, Any?>> {
            if (actualParameters.isNullOrEmpty()) {
                return emptyArray()
            }
            val stackTraceElement = Thread.currentThread().stackTrace[3]
            val callerMethodName = stackTraceElement.methodName
            val callerClass = Class.forName(stackTraceElement.className).kotlin
            // 查找调用者方法
            loop@ for (kFunction in callerClass.declaredMemberFunctions) {
                //方法名称相同
                if (kFunction.name != callerMethodName) {
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
                    if (formalParameter.type.javaType != actualParameter?.javaClass) {
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