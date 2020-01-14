package com.like.common.util.validator.rule

import java.util.regex.Pattern

class RegExpRule<TYPE>(private val pattern: String) : AbstractRule<TYPE>() {
    override fun isValid(data: TYPE): Boolean {
        return if (data is CharSequence) {
            Pattern.compile(pattern).matcher(data).matches()
        } else {
            false
        }
    }
}