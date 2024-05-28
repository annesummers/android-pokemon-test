package com.giganticsheep.pokemon.domain.generations

import com.giganticsheep.displaystate.DisplayDataState
import com.giganticsheep.displaystate.DisplayStateProvider
import com.giganticsheep.pokemon.domain.generations.model.GenerationItemDisplay
import kotlinx.collections.immutable.ImmutableList

interface GenerationsDisplayProvider : DisplayStateProvider<DisplayDataState<ImmutableList<GenerationItemDisplay>>> {

    suspend fun providesGenerations()
}
