package com.giganticsheep.pokemon.display.pokemon

import com.giganticsheep.displaystate.DisplayDataStateSet
import com.giganticsheep.displaystate.DisplayDataStateSetter
import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.data.species.PokemonRepository
import com.giganticsheep.pokemon.data.species.model.Pokemon
import com.giganticsheep.pokemon.domain.pokemon.PokemonDisplayProvider
import com.giganticsheep.pokemon.domain.pokemon.model.PokemonDisplay
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

internal class InternalPokemonDisplayProvider @Inject constructor(
    @BackgroundDispatcher dispatcher: CoroutineDispatcher,
    private val pokemonRepository: PokemonRepository
) : PokemonDisplayProvider,
    DisplayDataStateSetter<Pokemon, PokemonDisplay> by DisplayDataStateSet(dispatcher) {

     override suspend fun providesPokemon(nameOrId: String) {
         showLoading()

         showResult(pokemonRepository.getPokemon(nameOrId)) { pokemon ->
             pokemon.toDisplay()
         }
    }

    override suspend fun providesRandomPokemon() {
        showLoading()

        showResult(pokemonRepository.getRandomPokemon()) { pokemon ->
            pokemon.toDisplay()
        }
    }
}
