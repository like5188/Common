package com.like.common.util.validator.validator

import com.like.common.util.validator.rule.NotEmptyRule
import com.like.common.util.validator.rule.NotNullRule
import com.like.common.util.validator.rule.UrlRule

class UrlValidator : BaseValidator<String?>() {

    init {
        addRules(
                NotNullRule("Url不能为null"),
                NotEmptyRule("Url不能为空"),
                UrlRule("Url格式错误")
        )
    }
}