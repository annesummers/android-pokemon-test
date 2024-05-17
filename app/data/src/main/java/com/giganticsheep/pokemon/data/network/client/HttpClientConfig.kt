package com.giganticsheep.pokemon.data.network.client

import com.giganticsheep.logging.LoggerFactory
import com.giganticsheep.network.HttpClientLogger
import com.giganticsheep.network.JsonUtilities
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json

internal fun HttpClientConfig<*>.config(
    loggerFactory: LoggerFactory,
) {
    install(ContentNegotiation) {
        json(JsonUtilities.get().json)
    }

    install(HttpTimeout) {
        connectTimeoutMillis = TIMEOUT
        requestTimeoutMillis = TIMEOUT
        socketTimeoutMillis = TIMEOUT
    }

    install(Logging) {
        logger = HttpClientLogger(loggerFactory, OkHttp)
        level = LogLevel.ALL
    }
}

private const val TIMEOUT = 40_000L
