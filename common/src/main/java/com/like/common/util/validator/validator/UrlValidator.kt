package com.like.common.util.validator.validator

import com.like.common.util.validator.rule.AbstractRule
import com.like.common.util.validator.rule.NotEmptyRule
import com.like.common.util.validator.rule.NotNullRule
import com.like.common.util.validator.rule.UrlRule

class UrlValidator : AbstractValidator<String?>() {
    override fun getRules(): List<AbstractRule<String?>> {
        return listOf(
                NotNullRule("Url不能为null"),
                NotEmptyRule("Url不能为空"),
                UrlRule("Url格式错误")
        )
    }
}