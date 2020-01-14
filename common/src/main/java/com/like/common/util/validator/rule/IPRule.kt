package com.like.common.util.validator.rule

import android.util.Patterns

class IPRule<TYPE> : AbstractRule<TYPE>() {
    override fun isValid(data: TYPE): Boolean {
        return if (data is CharSequence) {
            Patterns.IP_ADDRESS.matcher(data).matches()
        } else {
            false
        }
    }
}