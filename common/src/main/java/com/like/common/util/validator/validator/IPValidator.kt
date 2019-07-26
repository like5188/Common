package com.like.common.util.validator.validator

import com.like.common.util.validator.rule.NotEmptyRule
import com.like.common.util.validator.rule.NotNullRule
import com.like.common.util.validator.rule.PhoneRule

class IPValidator : BaseValidator<String?>() {

    init {
        addRules(
                NotNullRule("IP地址不能为null"),
                NotEmptyRule("IP地址不能为空"),
                PhoneRule("IP地址格式错误")
        )
    }
}