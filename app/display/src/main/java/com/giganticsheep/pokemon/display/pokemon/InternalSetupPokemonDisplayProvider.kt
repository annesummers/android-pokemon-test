package com.giganticsheep.pokemon.display.pokemon

import com.giganticsheep.displaystate.DisplayScreenStateSet
import com.giganticsheep.displaystate.DisplayScreenStateSetter
import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.data.species.PokemonRepository
import com.giganticsheep.pokemon.domain.pokemon.SetupPokemonDisplayProvider
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

internal class InternalSetupPokemonDisplayProvider @Inject constructor(
    @BackgroundDispatcher dispatcher: CoroutineDispatcher,
    private val pokemonRepository: PokemonRepository
) : SetupPokemonDisplayProvider,
    DisplayScreenStateSetter by DisplayScreenStateSet(dispatcher) {

     override suspend fun providesSetup() {
         showResult(pokemonRepository.setup())
    }
}
