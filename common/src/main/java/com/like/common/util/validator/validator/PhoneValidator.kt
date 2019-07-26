package com.like.common.util.validator.validator

import com.like.common.util.validator.rule.NotEmptyRule
import com.like.common.util.validator.rule.NotNullRule
import com.like.common.util.validator.rule.PhoneRule

class PhoneValidator : BaseValidator<String?>() {

    init {
        addRules(
                NotNullRule("电话号码不能为null"),
                NotEmptyRule("电话号码不能为空"),
                PhoneRule("电话号码格式错误")
        )
    }
}