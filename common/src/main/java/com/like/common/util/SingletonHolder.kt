package com.like.common.util

import kotlin.jvm.functions.FunctionN

/**
 * 为R这个类创建单例模式。
 * 一般用于构造函数中包含参数的情况，因为object创建的单例不能包含构造函数。
 * 使用的时候让伴生对象继承此类即可。
 */
/*
class SingletonHolderTest private constructor(val name: String, val age: Int) {
    companion object : SingletonHolder<SingletonHolderTest>(object : FunctionN<SingletonHolderTest> {
        override val arity: Int = 2 // number of arguments that must be passed to constructor

        override fun invoke(vararg args: Any?): SingletonHolderTest {
            return SingletonHolderTest(args[0] as String, args[1] as Int)
        }
    })
}
 */
open class SingletonHolderN<out R>(initializer: FunctionN<R>) {
    private var initializer: FunctionN<R>? = initializer

    @Volatile
    private var instance: R? = null

    fun getInstance(vararg args: Any?): R =
        instance ?: synchronized(this) {
            instance ?: initializer!!(*args).apply {
                instance = this
            }
        }
}

open class SingletonHolder1<in I, out R>(initializer: (I) -> R) {
    private var initializer: ((I) -> R)? = initializer

    @Volatile
    private var instance: R? = null

    fun getInstance(arg: I): R =
        instance ?: synchronized(this) {
            instance ?: initializer!!(arg).apply {
                instance = this
            }
        }

}

open class SingletonHolder2<in I1, in I2, out R>(initializer: (I1, I2) -> R) {
    private var initializer: ((I1, I2) -> R)? = initializer

    @Volatile
    private var instance: R? = null

    fun getInstance(arg1: I1, arg2: I2): R =
        instance ?: synchronized(this) {
            instance ?: initializer!!(arg1, arg2).apply {
                instance = this
            }
        }

}

open class SingletonHolder3<in I1, in I2, in I3, out R>(initializer: (I1, I2, I3) -> R) {
    private var initializer: ((I1, I2, I3) -> R)? = initializer

    @Volatile
    private var instance: R? = null

    fun getInstance(arg1: I1, arg2: I2, arg3: I3): R =
        instance ?: synchronized(this) {
            instance ?: initializer!!(arg1, arg2, arg3).apply {
                instance = this
            }
        }

}
