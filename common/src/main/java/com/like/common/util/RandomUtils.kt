package com.like.common.util

import java.util.*

object RandomUtils {
    /**
     * 产生一个随机的字符串
     *
     * @param length    字符串长度
     * @param base      字符串种子，即从中随机获取[length]个字符组成字符串。默认包括 a-z A-Z 0-9
     */
    fun getString(length: Int, base: String = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"): String {
        val sb = StringBuilder()
        for (i in 0 until length) {
            val index = getInt(base.length)
            sb.append(base[index])
        }
        return sb.toString()
    }

    /**
     * 产生一个随机的整数。
     *
     * @param bound the upper bound (exclusive).  Must be positive.
     * @throws IllegalArgumentException if bound is not positive
     */
    fun getInt(bound: Int): Int = Random().nextInt(bound)

}