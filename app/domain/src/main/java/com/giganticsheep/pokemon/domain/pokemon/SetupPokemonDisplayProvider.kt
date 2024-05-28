package com.giganticsheep.pokemon.domain.pokemon

import com.giganticsheep.displaystate.DisplayScreenState
import com.giganticsheep.displaystate.DisplayStateProvider

interface SetupPokemonDisplayProvider : DisplayStateProvider<DisplayScreenState> {

    suspend fun providesSetup()
}
