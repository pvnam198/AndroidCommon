package com.lingvo.base_common.model

sealed class Result<out T> {

    data object Loading : Result<Nothing>()

    data class Success<out T>(val data: T) : Result<T>()

    data class Error(val exception: Exception? = null) : Result<Nothing>()

}