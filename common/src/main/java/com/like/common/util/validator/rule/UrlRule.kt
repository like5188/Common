package com.like.common.util.validator.rule

import android.util.Patterns

class UrlRule<TYPE> : AbstractRule<TYPE>() {
    override fun isValid(data: TYPE): Boolean {
        return if (data is CharSequence) {
            Patterns.WEB_URL.matcher(data).matches()
        } else {
            false
        }
    }
}