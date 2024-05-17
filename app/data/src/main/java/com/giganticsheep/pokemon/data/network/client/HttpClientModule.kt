package com.giganticsheep.pokemon.data.network.client

import android.util.Log
import com.giganticsheep.logging.Logger
import com.giganticsheep.logging.LoggerFactory
import com.giganticsheep.network.client.HttpClientProvider
import com.giganticsheep.pokemon.data.network.environment.PokemonEnvironment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
