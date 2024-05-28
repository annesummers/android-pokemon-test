package com.giganticsheep.pokemon.display.generations

import com.giganticsheep.displaystate.DisplayDataStateSet
import com.giganticsheep.displaystate.DisplayDataStateSetter
import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.data.generations.GenerationsRepository
import com.giganticsheep.pokemon.data.generations.model.Generation
import com.giganticsheep.pokemon.domain.generations.GenerationDisplayProvider
import com.giganticsheep.pokemon.domain.generations.model.GenerationDisplay
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

internal class InternalGenerationDisplayProvider @Inject constructor(
    @BackgroundDispatcher dispatcher: CoroutineDispatcher,
    private val generationsRepository: GenerationsRepository
) : GenerationDisplayProvider,
    DisplayDataStateSetter<Generation, GenerationDisplay> by DisplayDataStateSet(
        dispatcher
    ) {

    override suspend fun providesGeneration(name: String) {
        showLoading()

        showResult(generationsRepository.getGeneration(name)) { generation ->
            generation.toDisplay()
        }
    }
}