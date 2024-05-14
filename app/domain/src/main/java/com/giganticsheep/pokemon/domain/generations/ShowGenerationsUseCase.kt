package com.giganticsheep.pokemon.domain.generations

import androidx.compose.runtime.Stable
import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.data.generations.model.GenerationItem
import com.giganticsheep.ui.DisplayDataStateProvided
import com.giganticsheep.ui.DisplayDataStateProvider
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@Stable
data class GenerationItemDisplay(val name: String)

private fun GenerationItem.toDisplay() = GenerationItemDisplay(name)

class ShowGenerationsUseCase @Inject internal constructor(
    @BackgroundDispatcher val dispatcher: CoroutineDispatcher,
    private val generationsRepository: GenerationsRepository,
) {

    private val generationsModel = GenerationsModel()

    val generations: DisplayDataStateProvider<List<GenerationItem>, ImmutableList<GenerationItemDisplay>>
        get() = generationsModel

    private inner class GenerationsModel :
        DisplayDataStateProvider<List<GenerationItem>, ImmutableList<GenerationItemDisplay>> by DisplayDataStateProvided(
            backgroundContext = dispatcher,
        )

    suspend fun fetchGenerationsForDisplay() {
        generationsModel.showLoading()

        generationsRepository.fetchGenerations()

        generationsRepository.generations
            .onEach { state ->
                generationsModel.showResult(state) { generations ->
                    generations
                        .map { it.toDisplay() }
                        .toImmutableList()
                }
            }
            .collect()
    }
}
