package com.like.common.util.validator.validator

import com.like.common.util.validator.rule.EmailRule
import com.like.common.util.validator.rule.NotEmptyRule
import com.like.common.util.validator.rule.NotNullRule

class EmailValidator : BaseValidator<String?>() {

    init {
        addRules(
                NotNullRule(),
                NotEmptyRule(),
                EmailRule()
        )
    }

}