package com.giganticsheep.pokemon.data.network.client

import android.util.Log
import com.giganticsheep.logging.Logger
import com.giganticsheep.logging.LoggerFactory
import com.giganticsheep.network.HttpClientFactory
import com.giganticsheep.network.HttpClientLogger
import com.giganticsheep.network.JsonUtilities
import com.giganticsheep.network.StubbedHttpClientFactory
import com.giganticsheep.network.client.HttpClientProvider
import com.giganticsheep.network.offline.connection.StubProviders
import com.giganticsheep.pokemon.data.network.environment.PokemonEnvironment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object HttpClientModule {

    @Provides
    @Singleton
    fun providesLoggerFactory() = object : LoggerFactory {
        override fun logger(instance: Any) = object : Logger {

            private val tag: String = (instance as? String) ?: instance.javaClass.simpleName

            override fun log(
                level: Logger.Level,
                message: String?,
                throwable: Throwable?,
            ) {
                if (throwable == null) {
                    when (level) {
                        Logger.Level.Verbose -> Log.v(tag, message.orEmpty())
                        Logger.Level.Debug -> Log.d(tag, message.orEmpty())
                        Logger.Level.Info -> Log.i(tag, message.orEmpty())
                        Logger.Level.Warning -> Log.w(tag, message.orEmpty())
                        Logger.Level.Error -> Log.e(tag, message.orEmpty())
                        Logger.Level.Fatal -> Log.wtf(tag, message.orEmpty())
                    }
                } else {
                    when (level) {
                        Logger.Level.Verbose -> Log.v(tag, message.orEmpty(), throwable)
                        Logger.Level.Debug -> Log.d(tag, message.orEmpty(), throwable)
                        Logger.Level.Info -> Log.i(tag, message.orEmpty(), throwable)
                        Logger.Level.Warning -> Log.w(tag, message.orEmpty(), throwable)
                        Logger.Level.Error -> Log.e(tag, message.orEmpty(), throwable)
                        Logger.Level.Fatal -> Log.wtf(tag, message.orEmpty(), throwable)
                    }
                }
            }
        }
    }

    @Provides
    @Singleton
    fun providesHttpClientProvider(
        httpClientFactory: OkHttpClientFactory,
        stubClientFactory: StubClientFactory,
    ) = HttpClientProvider(
        mapOf(
            PokemonEnvironment.Online to httpClientFactory,
            PokemonEnvironment.Offline to stubClientFactory,
        ),
    )
}

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
