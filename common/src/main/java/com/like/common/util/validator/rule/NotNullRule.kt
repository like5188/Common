package com.like.common.util.validator.rule

class NotNullRule<TYPE> : AbstractRule<TYPE>() {

    override fun isValid(data: TYPE): Boolean {
        return data != null
    }

}