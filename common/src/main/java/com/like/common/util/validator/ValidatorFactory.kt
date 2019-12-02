package com.like.common.util.validator

import com.like.common.util.validator.validator.*

/**
 * 验证器工厂，创建了默认实现的几个验证器。
 * 如果需要自定义其它验证器，请继承自[BaseValidator]，然后添加[com.like.common.util.validator.rule.AbstractRule]。
 * 其中[BaseValidator]的默认实现有：
 * [com.like.common.util.validator.validator.EmailValidator]、
 * [com.like.common.util.validator.validator.IDNumberValidator]、
 * [com.like.common.util.validator.validator.IPValidator]、
 * [com.like.common.util.validator.validator.PhoneValidator]、
 * [com.like.common.util.validator.validator.UrlValidator]
 * 其中[com.like.common.util.validator.rule.AbstractRule]的默认实现有：
 * [com.like.common.util.validator.rule.PredicateRule]、
 * [com.like.common.util.validator.rule.RegExpRule]、
 * [com.like.common.util.validator.rule.NotNullRule]、
 * [com.like.common.util.validator.rule.NotEmptyRule]、
 * [com.like.common.util.validator.rule.EmailRule]、
 * [com.like.common.util.validator.rule.IDNumberRule]、
 * [com.like.common.util.validator.rule.IPRule]、
 * [com.like.common.util.validator.rule.PhoneRule]、
 * [com.like.common.util.validator.rule.UrlRule]
 */
object ValidatorFactory {
    fun createPhoneValidator() = PhoneValidator()
    fun createEmailValidator() = EmailValidator()
    fun createIPValidator() = IPValidator()
    fun createUrlValidator() = UrlValidator()
    fun createIDNumberValidator() = IDNumberValidator()
}