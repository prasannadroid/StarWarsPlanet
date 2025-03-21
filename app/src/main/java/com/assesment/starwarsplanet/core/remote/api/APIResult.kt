package com.assesment.starwarsplanet.core.remote.api

sealed class APIResult<out T> {
    data class Success<out T>(val data: T) : APIResult<T>()
    data class Error(val exception: Throwable) : APIResult<Nothing>()
}