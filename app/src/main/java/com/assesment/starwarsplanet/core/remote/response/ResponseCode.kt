package com.assesment.starwarsplanet.core.remote.response

import com.assesment.starwarsplanet.core.remote.api.APIResult

fun httpError(responseMessage: String, httpCode: Int) = when (httpCode) {
    400 -> APIResult.Error(Throwable("Bad Request: $responseMessage")) // 400 error
    401 -> APIResult.Error(Throwable("Unauthorized: $responseMessage")) // 401 error
    403 -> APIResult.Error(Throwable("Forbidden: $responseMessage")) // 403 error
    404 -> APIResult.Error(Throwable("Planets Not Found")) // 404 error
    429 -> APIResult.Error(Throwable("Too Many Requests: $responseMessage")) // 429 error
    500 -> APIResult.Error(Throwable("Server Error")) // 500 error
    503 -> APIResult.Error(Throwable("Service Unavailable: $responseMessage")) // 503 error
    else -> APIResult.Error(Throwable("Something went wrong: $responseMessage")) // Default err
}