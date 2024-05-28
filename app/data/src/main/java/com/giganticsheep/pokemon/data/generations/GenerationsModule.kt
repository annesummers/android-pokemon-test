package com.giganticsheep.pokemon.data.generations

import com.giganticsheep.pokemon.data.network.client.PokemonHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object GenerationsModule {

    @Provides
    @Singleton
    fun providesGenerationsRepository(
        generationsRepository: InternalGenerationsRepository,
    ): GenerationsRepository = generationsRepository

    @Provides
    @Singleton
    fun providesGenerationsApi(
        httpClient: PokemonHttpClient,
        endpointManager: GenerationEndpointManager,
    ): GenerationsApi = InternalGenerationsApi(
        httpClient = httpClient,
        endpointManager = endpointManager,
    )
}
