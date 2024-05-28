package com.giganticsheep.pokemon.display.generations

import com.giganticsheep.displaystate.DisplayDataStateSet
import com.giganticsheep.displaystate.DisplayDataStateSetter
import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.data.generations.GenerationsRepository
import com.giganticsheep.pokemon.data.generations.model.GenerationItem
import com.giganticsheep.pokemon.domain.generations.GenerationsDisplayProvider
import com.giganticsheep.pokemon.domain.generations.model.GenerationItemDisplay
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class InternalGenerationsDisplayProvider @Inject constructor(
    @BackgroundDispatcher dispatcher: CoroutineDispatcher,
    private val generationsRepository: GenerationsRepository
) : GenerationsDisplayProvider,
    DisplayDataStateSetter<List<GenerationItem>, ImmutableList<GenerationItemDisplay>> by DisplayDataStateSet(
        dispatcher
    ) {

    override suspend fun providesGenerations() {
        generationsRepository.fetchGenerations()

        generationsRepository.generations
            .onEach { state ->
                showResult(state) { generations ->
                    generations
                        .map { it.toDisplay() }
                        .toImmutableList()
                }
            }
            .collect()
    }
}
