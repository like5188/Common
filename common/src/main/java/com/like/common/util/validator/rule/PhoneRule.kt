package com.like.common.util.validator.rule

import java.util.regex.Pattern

class PhoneRule<TYPE> : AbstractRule<TYPE>() {
    companion object {
        const val REG_PHONE = "(^1\\d{10}$)"
    }

    override fun isValid(data: TYPE): Boolean {
        return if (data is CharSequence) {
            Pattern.compile(REG_PHONE).matcher(data).matches()
        } else {
            false
        }
    }
}