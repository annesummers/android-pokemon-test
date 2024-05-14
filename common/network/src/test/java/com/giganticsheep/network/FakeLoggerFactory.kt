package com.giganticsheep.network

import com.giganticsheep.logging.Logger
import com.giganticsheep.logging.LoggerFactory

class FakeLoggerFactory : LoggerFactory {
    override fun logger(instance: Any) = object : Logger {
        override fun log(level: Logger.Level, message: String?, throwable: Throwable?) {
        }
    }
}
