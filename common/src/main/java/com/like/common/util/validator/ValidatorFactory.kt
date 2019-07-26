package com.like.common.util.validator

import com.like.common.util.validator.validator.*

/**
 * 验证器工厂，创建了默认实现的几个验证器。
 * 如果需要自定义其它验证器，请集成自[BaseValidator]，然后添加[AbstractRule]。
 * 其中[AbstractRule]默认实现了：[PredicateRule]、[RegExpRule]、[NotNullRule]、[NotEmptyRule]、[EmailRule]、[IDNumberRule]、[IPRule]、[PhoneRule]、[UrlRule]
 */
object ValidatorFactory {
    fun createPhoneValidator() = PhoneValidator()
    fun createEmailValidator() = EmailValidator()
    fun createIPValidator() = IPValidator()
    fun createUrlValidator() = UrlValidator()
    fun createIDNumberValidator() = IDNumberValidator()
}