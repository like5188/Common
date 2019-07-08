package com.like.common.util.validator.rule

class NotNullRule<TYPE>(errorMessage: String) : AbstractRule<TYPE>(errorMessage) {

    override fun isValid(data: TYPE): Boolean {
        return data != null
    }

}