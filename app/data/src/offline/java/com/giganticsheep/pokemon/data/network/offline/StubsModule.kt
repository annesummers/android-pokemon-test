package com.giganticsheep.pokemon.data.network.offline

import com.giganticsheep.network.FileUtilities
import com.giganticsheep.network.offline.connection.HttpStubProvider
import com.giganticsheep.network.offline.connection.StubProviders
import com.giganticsheep.pokemon.data.generations.DefaultGenerationsStubs
import com.giganticsheep.pokemon.data.species.DefaultSpeciesStubs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object StubsModule {

    @Provides
    @Singleton
    fun providesStubProviders(
        fileUtilities: FileUtilities,
    ) = StubProviders(
        HttpStubProvider(
            DefaultGenerationsStubs(fileUtilities),
            DefaultSpeciesStubs(fileUtilities),
        ),
    )
}
