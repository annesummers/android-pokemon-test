package com.giganticsheep.pokemon.data.network.client

import com.giganticsheep.network.client.HttpClientProvider
import com.giganticsheep.pokemon.display.network.environment.PokemonEnvironment
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
    fun providesHttpClientProvider(
        httpClientFactory: StubClientFactory,
    ) = HttpClientProvider(
        mapOf(
            com.giganticsheep.pokemon.display.network.environment.PokemonEnvironment.Offline to httpClientFactory,
        ),
    )
}
