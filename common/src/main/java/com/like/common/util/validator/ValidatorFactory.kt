package com.like.common.util.validator

import com.like.common.util.validator.validator.*

/**
 * 验证器工厂
 */
object ValidatorFactory {
    fun createPhoneValidator() = PhoneValidator()
    fun createEmailValidator() = EmailValidator()
    fun createIPValidator() = IPValidator()
    fun createUrlValidator() = UrlValidator()
    fun createIDNumberValidator() = IDNumberValidator()
}