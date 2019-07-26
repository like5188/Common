package com.like.common.util.validator.validator

import com.like.common.util.validator.rule.EmailRule
import com.like.common.util.validator.rule.NotEmptyRule
import com.like.common.util.validator.rule.NotNullRule

class EmailValidator : BaseValidator<String?>() {

    init {
        addRules(
                NotNullRule("Email不能为null"),
                NotEmptyRule("Email不能为空"),
                EmailRule("Email格式错误")
        )
    }

}