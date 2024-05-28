package com.giganticsheep.pokemon.domain.generations

import com.giganticsheep.displaystate.DisplayDataState
import com.giganticsheep.displaystate.DisplayStateProvider
import com.giganticsheep.pokemon.domain.generations.model.GenerationDisplay

interface GenerationDisplayProvider : DisplayStateProvider<DisplayDataState<GenerationDisplay>> {

    suspend fun providesGeneration(name: String)
}
