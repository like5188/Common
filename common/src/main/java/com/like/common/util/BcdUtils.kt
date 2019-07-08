package com.like.common.util

import kotlin.experimental.and

object BcdUtils {
    fun bcd2Str(i: Int): String =
        StringBuilder().append((i and 0xf0).ushr(4)).append(i and 0x0f).toString()

    fun bcd2Str(b: Byte): String =
        StringBuilder().append((b.toInt() and 0xf0).ushr(4)).append(b and 0x0f).toString()

    /**
     * @功能: BCD码转为10进制串(阿拉伯数据)
     * @参数: BCD码
     * @结果: 10进制串
     */
    fun bcd2Str(bytes: ByteArray): String {
        val temp = StringBuffer(bytes.size * 2)
        for (i in bytes.indices) {
            temp.append((bytes[i].toInt() and 0xf0).ushr(4))
            temp.append((bytes[i].toInt() and 0x0f))
        }
        return if (temp.toString().substring(0, 1).equals("0", ignoreCase = true))
            temp.toString().substring(1)
        else
            temp.toString()
    }

}
