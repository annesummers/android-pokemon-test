package com.giganticsheep.network.error

import com.giganticsheep.error.HandledException

open class ConnectionException(
    message: String = "Connection Error",
    cause: Throwable?,
) : HandledException(message, cause, cause?.message ?: message)

class TimeoutException(
    message: String? = null,
    cause: Throwable? = null,
) : ConnectionException(
    message ?: "Timeout Error",
    cause,
)
