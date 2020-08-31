package com.like.common.util

/**
 * 基于反射与注解实现页面跳转参数注入。配合[com.like.common.util.injectForIntentExtras]使用。
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class AutoWired