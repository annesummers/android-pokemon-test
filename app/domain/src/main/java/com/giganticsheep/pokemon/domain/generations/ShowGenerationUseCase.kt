package com.giganticsheep.pokemon.domain.generations

import androidx.compose.runtime.Stable
import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.data.generations.model.Generation
import com.giganticsheep.ui.DisplayDataStateProvided
import com.giganticsheep.ui.DisplayDataStateProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@Stable
data class GenerationDisplay(
    val id: Int,
    val region: String,
    val name: String,
    val species: List<String>,
    val moves: List<String>,
)

private fun Generation.toDisplay() = GenerationDisplay(
    id = id,
    region = mainRegion.name,
    name = name,
    species = species.map { it.name },
    moves = moves.map { it.name })

class ShowGenerationUseCase @Inject internal constructor(
    @BackgroundDispatcher val dispatcher: CoroutineDispatcher,
    private val generationsRepository: GenerationsRepository
) {
    private val generationModel = GenerationModel()

    val generation: DisplayDataStateProvider<Generation, GenerationDisplay>
        get() = generationModel

    private inner class GenerationModel :
        DisplayDataStateProvider<Generation, GenerationDisplay> by DisplayDataStateProvided(
            backgroundContext = dispatcher,
        )

    suspend fun fetchGenerationForDisplay(generationName: String) {
        generationModel.showLoading()

        generationsRepository.getGeneration(generationName)
            .onEach { state -> generationModel.showResult(state) { it.toDisplay() } }
            .collect()
    }
}