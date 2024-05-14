package com.giganticsheep.pokemon.domain.pokemon

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PokemonModule {

    @Binds
    abstract fun providesGenerationsRepository(
        generationsRepository: InternalPokemonRepository
    ): PokemonRepository
}