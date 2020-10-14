package com.like.common.util.repository

interface IPagingModel<ValueInList> {
    fun list(): List<ValueInList>?
}