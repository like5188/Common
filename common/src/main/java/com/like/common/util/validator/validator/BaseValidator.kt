package com.like.common.util.validator.validator

import com.like.common.util.validator.rule.AbstractRule

/**
 * 验证器基类
 */
open class BaseValidator<TYPE> {
    private val mRules = mutableListOf<AbstractRule<TYPE>>()

    /**
     * 克隆一个验证器，有相同的规则
     */
    fun clone(): BaseValidator<TYPE> = BaseValidator<TYPE>().apply {
        this.mRules.addAll(mRules)
    }

    /**
     * 删除满足条件的规则
     */
    fun removeIf(predicate: (AbstractRule<TYPE>) -> Boolean): BaseValidator<TYPE> {
        val listIterator = mRules.listIterator()
        listIterator.forEach {
            if (predicate(it)) {
                listIterator.remove()
            }
        }
        return this
    }

    /**
     * 为验证器添加[AbstractRule]
     */
    fun addRules(vararg rules: AbstractRule<TYPE>): BaseValidator<TYPE> {
        mRules.addAll(rules)
        return this
    }

    /**
     * 验证数据是否符合本规则[AbstractRule]
     *
     * @param data      验证的数据
     * @param success   验证成功要做的事
     * @param failure   验证失败要做的事，其中返回了失败的[AbstractRule]集合
     * @return          验证成功：true；验证失败：false；
     */
    @JvmOverloads
    fun validate(
            data: TYPE,
            success: (() -> Unit)? = null,
            failure: ((List<AbstractRule<TYPE>>) -> Unit)? = null
    ): Boolean {
        val failureRules = mRules.filter {
            !it.isValid(data)
        }
        return if (failureRules.isEmpty()) {
            success?.invoke()
            true
        } else {
            failure?.invoke(failureRules)
            false
        }
    }
}