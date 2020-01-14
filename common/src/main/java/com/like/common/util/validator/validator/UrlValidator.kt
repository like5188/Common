package com.like.common.util.validator.validator

import com.like.common.util.validator.rule.NotEmptyRule
import com.like.common.util.validator.rule.NotNullRule
import com.like.common.util.validator.rule.UrlRule

class UrlValidator : BaseValidator<String?>() {

    init {
        addRules(
                NotNullRule(),
                NotEmptyRule(),
                UrlRule()
        )
    }
}