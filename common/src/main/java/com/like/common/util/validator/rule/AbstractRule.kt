package com.like.common.util.validator.rule

/**
 * 验证规则基类
 */
abstract class AbstractRule<TYPE>(val errorMessage: String) {
    abstract fun isValid(data: TYPE): Boolean

    override fun toString(): String {
        return "AbstractRule(errorMessage='$errorMessage')"
    }
}
