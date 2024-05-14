package com.giganticsheep.pokemon.domain.generations

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class GenerationsModule {

    @Binds
    abstract fun providesGenerationsRepository(
        generationsRepository: InternalGenerationsRepository
    ): GenerationsRepository
}