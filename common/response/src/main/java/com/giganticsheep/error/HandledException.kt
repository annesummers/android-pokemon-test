package com.giganticsheep.error

open class HandledException(
    message: String = "",
    cause: Throwable? = null,
    val internalMessage: String,
) : Exception(
    message,
    cause,
) {
    val hasCause: Boolean
        get() = cause != null

    override fun toString() = "Error! | $internalMessage" + message?.let { " | $it" }
}
