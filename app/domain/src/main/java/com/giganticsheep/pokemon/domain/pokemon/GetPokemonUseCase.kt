package com.giganticsheep.pokemon.domain.pokemon

import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.domain.pokemon.model.PokemonDisplay
import com.giganticsheep.pokemon.domain.pokemon.model.toDisplay
import com.giganticsheep.ui.DisplayDataStateProvided
import com.giganticsheep.ui.DisplayDataStateProvider
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetPokemonUseCase @Inject internal constructor(
    @BackgroundDispatcher val dispatcher: CoroutineDispatcher,
    private val pokemonRepository: PokemonRepository,
) {

    private val pokemonModel = PokemonModel()

    val pokemonDisplayState = pokemonModel.displayState

    suspend fun fetchPokemon(nameOrId: String) {
        pokemonModel.showLoading()

        pokemonModel.showResult(pokemonRepository.getPokemon(nameOrId)) { species ->
            species.toDisplay()
        }
    }

    private inner class PokemonModel :
        DisplayDataStateProvider<Pokemon, PokemonDisplay> by DisplayDataStateProvided(
            backgroundContext = dispatcher,
        )
}
