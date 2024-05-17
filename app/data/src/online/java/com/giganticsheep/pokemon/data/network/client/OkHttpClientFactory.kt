package com.giganticsheep.pokemon.data.network.client

import com.giganticsheep.logging.LoggerFactory
import com.giganticsheep.network.HttpClientFactory
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
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
