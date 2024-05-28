package com.giganticsheep.pokemon.display.pokemon

import com.giganticsheep.pokemon.domain.pokemon.PokemonDisplayProvider
import com.giganticsheep.pokemon.domain.pokemon.SetupPokemonDisplayProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal abstract class PokemonModule {

    @Binds
    abstract fun bindsSetupPokemonDisplayProvider(
        internalSetupPokemonProvider: InternalSetupPokemonDisplayProvider
    ): SetupPokemonDisplayProvider

    @Binds
    abstract fun bindsGetPokemonDisplayProvider(
        internalGetPokemonProvider: InternalPokemonDisplayProvider
    ): PokemonDisplayProvider
}
