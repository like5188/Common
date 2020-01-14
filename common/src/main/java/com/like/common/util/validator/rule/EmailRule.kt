package com.like.common.util.validator.rule

import android.util.Patterns

class EmailRule<TYPE> : AbstractRule<TYPE>() {
    override fun isValid(data: TYPE): Boolean {
        return if (data is CharSequence) {
            Patterns.EMAIL_ADDRESS.matcher(data).matches()
        } else {
            false
        }
    }
}