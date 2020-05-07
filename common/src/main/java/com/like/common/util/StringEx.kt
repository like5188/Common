package com.like.common.util

fun String?.deleteLast(): String? {
    if (this.isNullOrEmpty()) {
        return this
    }
    return this.substring(0, this.length - 1)
}