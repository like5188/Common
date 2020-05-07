package com.like.common.util

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

fun String?.toStringOrEmpty(suffix: String = ""): String {
    if (this.isNullOrEmpty()) {
        return ""
    }
    return "$this$suffix"
}

fun Int?.toStringOrEmpty(suffix: String = ""): String {
    if (this == null) {
        return ""
    }
    return "$this$suffix"
}

fun Double?.toStringOrEmpty(suffix: String = ""): String {
    if (this == null) {
        return ""
    }
    return "$this$suffix"
}

fun Float?.toStringOrEmpty(suffix: String = ""): String {
    if (this == null) {
        return ""
    }
    return "$this$suffix"
}