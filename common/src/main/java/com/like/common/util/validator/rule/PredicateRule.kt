package com.like.common.util.validator.rule

class PredicateRule<TYPE>(private val predicate: (TYPE) -> Boolean, errorMessage: String) : AbstractRule<TYPE>(errorMessage) {
    override fun isValid(data: TYPE): Boolean {
        return predicate(data)
    }
}