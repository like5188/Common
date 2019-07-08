package com.like.common.util.validator.rule

class NotEmptyRule<TYPE>(errorMessage: String) : AbstractRule<TYPE>(errorMessage) {

    override fun isValid(data: TYPE): Boolean {
        return when (data) {
            is CharSequence -> data.isNotEmpty()
            is Collection<*> -> data.isNotEmpty()
            is Map<*, *> -> data.isNotEmpty()
            is Array<*> -> data.isNotEmpty()
            else -> true
        }
    }

}