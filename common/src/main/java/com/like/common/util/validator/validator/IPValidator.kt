package com.like.common.util.validator.validator

import com.like.common.util.validator.rule.NotEmptyRule
import com.like.common.util.validator.rule.NotNullRule
import com.like.common.util.validator.rule.PhoneRule

class IPValidator : BaseValidator<String?>() {

    init {
        addRules(
                NotNullRule(),
                NotEmptyRule(),
                PhoneRule()
        )
    }
}