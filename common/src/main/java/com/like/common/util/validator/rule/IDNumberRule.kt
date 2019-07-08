package com.like.common.util.validator.rule

import java.util.regex.Pattern

class IDNumberRule<TYPE>(errorMessage: String) : AbstractRule<TYPE>(errorMessage) {
    companion object {
        const val REG_ID_NUMBER_15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$"
        const val REG_ID_NUMBER_18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$"
    }

    override fun isValid(data: TYPE): Boolean {
        return if (data is CharSequence) {
            Pattern.compile(REG_ID_NUMBER_15).matcher(data).matches() || Pattern.compile(REG_ID_NUMBER_18).matcher(data).matches()
        } else {
            false
        }
    }
}