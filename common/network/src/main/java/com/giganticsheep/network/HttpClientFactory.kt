package com.giganticsheep.network

import com.giganticsheep.logging.Logger
import com.giganticsheep.logging.LoggerFactory
import com.giganticsheep.network.offline.connection.HttpStubCollector
import com.giganticsheep.network.offline.connection.HttpStubbedConnection
import com.giganticsheep.network.offline.connection.StubProviders
import com.giganticsheep.network.offline.response.MockHttpRequest
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.mock.MockEngine

class HttpClientLogger(
    loggerFactory: LoggerFactory,
    key: Any,
) : io.ktor.client.plugins.logging.Logger {

    private val okHttpLogger = loggerFactory.logger(key)

    override fun log(message: String) {
        okHttpLogger.log(
            level = Logger.Level.Debug,
            message = message,
        )
    }
}

abstract class HttpClientFactory<T : HttpClientEngineConfig> {

    abstract val httpEngineFactory: HttpClientEngineFactory<T>

    fun createHttpClient() = HttpClient(httpEngineFactory) {
        config(this)
    }

    abstract fun config(httpClientConfig: HttpClientConfig<T>)
}

abstract class StubbedHttpClientFactory protected constructor(
    stubProviders: StubProviders,
) : HttpClientFactory<HttpClientEngineConfig>() {

    private val stubbedConnection = stubProviders
        .stubs<HttpStubCollector>()
        .let { HttpStubbedConnection(it) }

    final override val httpEngineFactory
        get() = object : HttpClientEngineFactory<HttpClientEngineConfig> {
            override fun create(
                block: HttpClientEngineConfig.() -> Unit,
            ) = MockEngine {
                stubbedConnection
                    .respond(MockHttpRequest(request = it, mockRequestHandleScope = this))
                    .httpResponseData
            }
        }
}
