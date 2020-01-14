package com.like.common.util.validator.rule

/**
 * 验证规则基类
 */
abstract class AbstractRule<TYPE> {
    abstract fun isValid(data: TYPE): Boolean
}
