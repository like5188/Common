package com.like.common.util

import java.util.regex.Pattern

fun String?.deleteLast(): String? {
    if (this.isNullOrEmpty()) {
        return this
    }
    return this.substring(0, this.length - 1)
}

fun String?.containsChinese(): Boolean {
    if (this.isNullOrEmpty()) {
        return false
    }
    return Pattern.compile("[\u4e00-\u9fa5]").matcher(this).find()
}