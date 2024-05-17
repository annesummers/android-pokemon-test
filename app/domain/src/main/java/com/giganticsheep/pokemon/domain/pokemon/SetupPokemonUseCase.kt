package com.giganticsheep.pokemon.domain.pokemon

import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.ui.DisplayScreenStateProvided
import com.giganticsheep.ui.DisplayScreenStateProvider
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SetupPokemonUseCase @Inject internal constructor(
    @BackgroundDispatcher val dispatcher: CoroutineDispatcher,
    private val pokemonRepository: PokemonRepository,
) {

    private val setupModel = SetupModel()

    val setupDisplayState = setupModel.displayState

    suspend fun setup() = pokemonRepository.setup()
        .also { setupModel.showResult(it) }

    private inner class SetupModel :
        DisplayScreenStateProvider by DisplayScreenStateProvided(
            backgroundContext = dispatcher,
        )
}
