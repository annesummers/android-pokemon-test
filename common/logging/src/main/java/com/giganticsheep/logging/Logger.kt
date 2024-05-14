package com.giganticsheep.logging

interface LoggerFactory {

    fun logger(instance: Any): Logger
}

interface Logger {

    fun log(level: Level, message: String? = null, throwable: Throwable? = null)

    enum class Level {
        Verbose,
        Debug,
        Info,
        Warning,
        Error,
        Fatal,
    }
}
