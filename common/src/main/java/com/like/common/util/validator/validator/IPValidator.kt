package com.like.common.util.validator.validator

import com.like.common.util.validator.rule.AbstractRule
import com.like.common.util.validator.rule.NotEmptyRule
import com.like.common.util.validator.rule.NotNullRule
import com.like.common.util.validator.rule.PhoneRule

class IPValidator : AbstractValidator<String?>() {
    override fun getRules(): List<AbstractRule<String?>> {
        return listOf(
                NotNullRule("IP地址不能为null"),
                NotEmptyRule("IP地址不能为空"),
                PhoneRule("IP地址格式错误")
        )
    }
}