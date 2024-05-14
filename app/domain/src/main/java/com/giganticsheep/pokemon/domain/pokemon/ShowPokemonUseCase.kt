package com.giganticsheep.pokemon.domain.pokemon

import androidx.compose.runtime.Stable
import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.data.species.model.Species
import com.giganticsheep.ui.DisplayDataStateProvided
import com.giganticsheep.ui.DisplayDataStateProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@Stable
data class SpeciesDisplay(
    val id: Int,
    val name: String,
)

private fun Species.toDisplay() = SpeciesDisplay(
    id = id,
    name = name,
)

class ShowPokemonUseCase @Inject internal constructor(
    @BackgroundDispatcher val dispatcher: CoroutineDispatcher,
    private val pokemonRepository: PokemonRepository,
) {

    private val pokemonModel = PokemonModel()

    val pokemon: DisplayDataStateProvider<Species, SpeciesDisplay>
        get() = pokemonModel

    private inner class PokemonModel :
        DisplayDataStateProvider<Species, SpeciesDisplay> by DisplayDataStateProvided(
            backgroundContext = dispatcher,
        )

    suspend fun fetchPokemonForDisplay(pokemonNumber: String) {
        pokemonModel.showLoading()

        pokemonRepository.getSpecies(pokemonNumber)
            .onEach { state -> pokemonModel.showResult(state) { it.toDisplay() } }
            .collect()
    }
}
