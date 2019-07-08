package com.like.common.util.validator.validator

import com.like.common.util.validator.rule.AbstractRule

/**
 * 验证器基类
 */
abstract class AbstractValidator<TYPE> {
    /**
     * 验证
     */
    fun validate(data: TYPE) = getRules().any {
        !it.isValid(data)
    }

    /**
     * 验证，如果失败会返回失败的[AbstractRule]集合
     */
    fun validate(data: TYPE, success: () -> Unit, failure: (List<AbstractRule<TYPE>>) -> Unit) {
        val result = getRules().filter {
            !it.isValid(data)
        }
        if (result.isEmpty()) {
            success()
        } else {
            failure(result)
        }
    }

    /**
     * 为验证器添加[AbstractRule]
     */
    abstract fun getRules(): List<AbstractRule<TYPE>>
}