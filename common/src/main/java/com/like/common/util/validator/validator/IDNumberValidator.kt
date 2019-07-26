package com.like.common.util.validator.validator

import com.like.common.util.validator.rule.IDNumberRule
import com.like.common.util.validator.rule.NotEmptyRule
import com.like.common.util.validator.rule.NotNullRule

class IDNumberValidator : BaseValidator<String?>() {

    init {
        addRules(
                NotNullRule("身份证号码不能为null"),
                NotEmptyRule("身份证号码不能为空"),
                IDNumberRule("身份证号码格式错误")
        )
    }
}