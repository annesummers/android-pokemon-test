package com.giganticsheep.pokemon.data.network.client

import com.giganticsheep.logging.LoggerFactory
import com.giganticsheep.network.StubbedHttpClientFactory
import com.giganticsheep.network.offline.connection.StubProviders
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import javax.inject.Inject

internal class StubClientFactory @Inject constructor(
    private val loggerFactory: LoggerFactory,
    stubProviders: StubProviders,
) : StubbedHttpClientFactory(stubProviders) {

    override fun config(httpClientConfig: HttpClientConfig<HttpClientEngineConfig>) {
        httpClientConfig.config(loggerFactory)
    }
}
