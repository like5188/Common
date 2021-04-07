package com.like.common.util

import java.text.DecimalFormat

fun String?.toLongOrNull(): Long? {
    if (this.isNullOrEmpty()) {
        return null
    }
    return try {
        java.lang.Long.parseLong(this)
    } catch (e: Exception) {
        null
    }
}

fun String?.toLongOrDefault(default: Long = 0L): Long {
    if (this.isNullOrEmpty()) {
        return default
    }
    return try {
        java.lang.Long.parseLong(this)
    } catch (e: Exception) {
        default
    }
}

fun String?.toDoubleOrNull(): Double? {
    if (this.isNullOrEmpty()) {
        return null
    }
    return try {
        java.lang.Double.parseDouble(this)
    } catch (e: Exception) {
        null
    }
}

fun String?.toDoubleOrDefault(default: Double = 0.0): Double {
    if (this.isNullOrEmpty()) {
        return default
    }
    return try {
        java.lang.Double.parseDouble(this)
    } catch (e: Exception) {
        default
    }
}

fun String?.toIntOrNull(): Int? {
    if (this.isNullOrEmpty()) {
        return null
    }
    return try {
        java.lang.Integer.parseInt(this)
    } catch (e: Exception) {
        null
    }
}

fun String?.toIntOrDefault(default: Int = 0): Int {
    if (this.isNullOrEmpty()) {
        return default
    }
    return try {
        java.lang.Integer.parseInt(this)
    } catch (e: Exception) {
        default
    }
}

fun String?.toFloatOrNull(): Float? {
    if (this.isNullOrEmpty()) {
        return null
    }
    return try {
        java.lang.Float.parseFloat(this)
    } catch (e: Exception) {
        null
    }
}

fun String?.toFloatOrDefault(default: Float = 0f): Float {
    if (this.isNullOrEmpty()) {
        return default
    }
    return try {
        java.lang.Float.parseFloat(this)
    } catch (e: Exception) {
        default
    }
}

fun String?.toStringOrEmpty(): String {
    return this ?: ""
}

fun Int?.toStringOrEmpty(): String {
    return this?.toString() ?: ""
}

fun Double?.toStringOrEmpty(): String {
    return this?.toString() ?: ""
}

fun Float?.toStringOrEmpty(): String {
    return this?.toString() ?: ""
}

fun Long?.toStringOrEmpty(): String {
    return this?.toString() ?: ""
}

fun Float.maximumFractionDigits(maximumFractionDigits: Int): String {
    return toDouble().maximumFractionDigits(maximumFractionDigits)
}

fun Float.fractionDigits(fractionDigits: Int): String {
    return toDouble().fractionDigits(fractionDigits)
}

/**
 * 取最大位数的小数，小数末尾的0会被去掉。
 * @param maximumFractionDigits     最大小数位数
 */
fun Double.maximumFractionDigits(maximumFractionDigits: Int): String {
    val sb = StringBuilder("#")
    if (maximumFractionDigits > 0) {
        sb.append(".")
        (0 until maximumFractionDigits).forEach {
            sb.append("#")
        }
    }
    return DecimalFormat(sb.toString()).format(this)
}

/**
 * 取指定位数的小数，小数末尾的0会被保留。
 * @param fractionDigits     小数位数
 */
fun Double.fractionDigits(fractionDigits: Int): String {
    return String.format("%.${fractionDigits}f", this)
}