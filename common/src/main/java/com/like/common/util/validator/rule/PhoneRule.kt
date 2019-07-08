package com.like.common.util.validator.rule

import java.util.regex.Pattern

class PhoneRule<TYPE>(errorMessage: String) : AbstractRule<TYPE>(errorMessage) {
    companion object {
        const val REG_PHONE = "(^1[3,4,5,7,8]\\d{9}$)"
    }

    override fun isValid(data: TYPE): Boolean {
        return if (data is CharSequence) {
            Pattern.compile(REG_PHONE).matcher(data).matches()
        } else {
            false
        }
    }
}