package com.like.common.util

/**
 * 判断两个集合的所有 item 是否相等。
 */
fun <T : Comparable<T>> Iterable<T>?.equalsItems(other: Iterable<T>?): Boolean {
    return when {
        this == null && other == null -> true
        this != null && other != null -> this.sorted().toString() == other.sorted().toString()
        else -> false
    }
}