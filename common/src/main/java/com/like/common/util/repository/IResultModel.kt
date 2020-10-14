package com.like.common.util.repository

interface IResultModel<out T> {
    fun code(): Int
    fun errorMessage(): String?
    fun data(): T?
    fun success(): Boolean
}