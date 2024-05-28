package com.giganticsheep.pokemon.domain.generations

import com.giganticsheep.pokemon.domain.generations.model.GenerationItemDisplay
import com.giganticsheep.displaystate.DataUseCase
import kotlinx.collections.immutable.ImmutableList
import javax.inject.Inject

class GetGenerationsUseCase @Inject internal constructor(
    private val provider: GenerationsDisplayProvider
): DataUseCase<ImmutableList<GenerationItemDisplay>>(provider) {

    // Any logic applied to the returned object is done here
    suspend operator fun invoke() = provider.providesGenerations()
}
