package com.giganticsheep.pokemon.data.species

import com.giganticsheep.pokemon.data.network.client.PokemonHttpClient
import com.giganticsheep.pokemon.data.network.client.PokemonImageHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SpeciesModule {

    @Provides
    @Singleton
    fun providesSpeciesApi(
        httpClient: PokemonHttpClient,
        httpImageClient: PokemonImageHttpClient,
        endpointManager: SpeciesEndpointManager,
    ): SpeciesApi = InternalSpeciesApi(
        httpClient = httpClient,
        httpImageClient = httpImageClient,
        endpointManager = endpointManager,
    )
}
