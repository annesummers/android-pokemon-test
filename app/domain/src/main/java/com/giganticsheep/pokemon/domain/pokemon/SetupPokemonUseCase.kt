package com.giganticsheep.pokemon.domain.pokemon

import com.giganticsheep.displaystate.CompletableUseCase
import javax.inject.Inject

class SetupPokemonUseCase @Inject internal constructor(
    private val provider: SetupPokemonDisplayProvider,
): CompletableUseCase(provider) {

    suspend operator fun invoke() = provider.providesSetup()
}
