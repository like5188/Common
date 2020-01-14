package com.like.common.util.validator.validator

import com.like.common.util.validator.rule.IDNumberRule
import com.like.common.util.validator.rule.NotEmptyRule
import com.like.common.util.validator.rule.NotNullRule

class IDNumberValidator : BaseValidator<String?>() {

    init {
        addRules(
                NotNullRule(),
                NotEmptyRule(),
                IDNumberRule()
        )
    }
}