package com.giganticsheep.pokemon.data.network.client

import com.giganticsheep.logging.LoggerFactory
import com.giganticsheep.network.HttpClientFactory
import com.giganticsheep.network.HttpClientLogger
import com.giganticsheep.network.JsonUtilities
import com.giganticsheep.network.StubbedHttpClientFactory
import com.giganticsheep.network.offline.connection.StubProviders
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Inject

internal class OkHttpClientFactory @Inject constructor(
    private val loggerFactory: LoggerFactory,
) : HttpClientFactory<OkHttpConfig>() {

    override val httpEngineFactory = OkHttp

    override fun config(httpClientConfig: HttpClientConfig<OkHttpConfig>) {
        httpClientConfig.engine {
            config {}
        }
        httpClientConfig.config(loggerFactory)
    }
}

internal class StubClientFactory @Inject constructor(
    private val loggerFactory: LoggerFactory,
    stubProviders: StubProviders,
) : StubbedHttpClientFactory(stubProviders) {

    override fun config(httpClientConfig: HttpClientConfig<HttpClientEngineConfig>) {
        httpClientConfig.config(loggerFactory)
    }
}

private fun HttpClientConfig<*>.config(
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
