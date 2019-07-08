@file:Suppress("NOTHING_TO_INLINE")

package com.like.common.util

import java.text.DecimalFormat

/**
 * 转换为数据存储单位。KB、MB、GB
 */
inline fun Long.toDataStorageUnit(): String = DecimalFormat("#0.00").let {
    when {
        this <= 0 -> "0.00KB"
        this < 1048576 -> it.format(this.toDouble() / 1024) + "KB"
        this < 1073741824 -> it.format(this.toDouble() / 1048576) + "MB"
        else -> it.format(this.toDouble() / 1073741824) + "GB"
    }
}