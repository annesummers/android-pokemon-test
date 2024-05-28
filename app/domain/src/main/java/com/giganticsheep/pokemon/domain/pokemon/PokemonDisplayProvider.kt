package com.giganticsheep.pokemon.domain.pokemon

import com.giganticsheep.displaystate.DisplayDataState
import com.giganticsheep.displaystate.DisplayStateProvider
import com.giganticsheep.pokemon.domain.pokemon.model.PokemonDisplay

interface PokemonDisplayProvider : DisplayStateProvider<DisplayDataState<PokemonDisplay>> {

    suspend fun providesPokemon(nameOrId: String)

    suspend fun providesRandomPokemon()
}
