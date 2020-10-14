package com.like.common.util.repository

data class ResultModelException(val code: Int, val errorMessage: String?) : RuntimeException(errorMessage)