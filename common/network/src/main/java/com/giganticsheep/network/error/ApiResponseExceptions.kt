package com.giganticsheep.network.error

import com.giganticsheep.error.HandledException
import io.ktor.http.HttpStatusCode

open class ApiResponseException(
    message: String,
    val statusCode: Int,
) : HandledException(
    message = message,
    internalMessage = "Api response error -\n\t$statusCode\n\t$message",
) {
    constructor(
        statusCode: HttpStatusCode,
        message: String = "",
    ) : this(
        message = message,
        statusCode = statusCode.value,
    )

    override fun toString() = internalMessage
}

class ForbiddenException(
    message: String?,
) : ApiResponseException(
    HttpStatusCode.Forbidden,
    message ?: "Forbidden Error",
)

class NotFoundException(
    message: String?,
) : ApiResponseException(
    HttpStatusCode.NotFound,
    message ?: "Not Found Error",
)

class ConflictException(
    message: String?,
) : ApiResponseException(
    HttpStatusCode.Conflict,
    message ?: "Conflict Error",
)
