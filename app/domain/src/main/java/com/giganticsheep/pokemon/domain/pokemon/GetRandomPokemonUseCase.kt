package com.giganticsheep.pokemon.domain.pokemon

import com.giganticsheep.displaystate.ConditionalDataUseCase
import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.domain.pokemon.model.PokemonDisplay
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetRandomPokemonUseCase @Inject internal constructor(
    @BackgroundDispatcher dispatcher: CoroutineDispatcher,
    private val setupPokemonProvider: SetupPokemonDisplayProvider,
    private val pokemonDisplayProvider: PokemonDisplayProvider,
) : ConditionalDataUseCase<PokemonDisplay>(
    dispatcher = dispatcher,
    initialProvider = setupPokemonProvider,
    provider = pokemonDisplayProvider
) {

    suspend operator fun invoke() {
        onInitialProviderDefault { pokemonDisplayProvider.providesRandomPokemon() }

        setupPokemonProvider.providesSetup()
    }
}
