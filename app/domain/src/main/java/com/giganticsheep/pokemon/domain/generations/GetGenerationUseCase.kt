package com.giganticsheep.pokemon.domain.generations

import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.data.generations.model.Generation
import com.giganticsheep.pokemon.domain.generations.model.GenerationDisplay
import com.giganticsheep.ui.DisplayDataStateProvided
import com.giganticsheep.ui.DisplayDataStateProvider
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

private fun Generation.toDisplay() = GenerationDisplay(
    id = id,
    region = mainRegion.name,
    name = name,
    species = species.map { it.name },
    moves = moves.map { it.name },
)

class GetGenerationUseCase @Inject internal constructor(
    @BackgroundDispatcher val dispatcher: CoroutineDispatcher,
    private val generationsRepository: GenerationsRepository,
) {
    private val generationModel = GenerationModel()

    val generationDisplayState = generationModel.displayState

    private inner class GenerationModel :
        DisplayDataStateProvider<Generation, GenerationDisplay> by DisplayDataStateProvided(
            backgroundContext = dispatcher,
        )

    suspend fun fetchGenerationForDisplay(generationName: String) {
        generationModel.showLoading()

        generationModel.showResult(
            generationsRepository.getGeneration(generationName),
        ) { it.toDisplay() }
    }
}
