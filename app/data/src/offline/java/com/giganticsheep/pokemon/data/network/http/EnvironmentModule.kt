package com.giganticsheep.pokemon.data.network.http

import com.giganticsheep.network.environment.Environment
import com.giganticsheep.pokemon.data.network.environment.PokemonEnvironment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EnvironmentModule {

    @Provides
    @Singleton
    fun providesEnvironment(): Environment = PokemonEnvironment.Offline
}
