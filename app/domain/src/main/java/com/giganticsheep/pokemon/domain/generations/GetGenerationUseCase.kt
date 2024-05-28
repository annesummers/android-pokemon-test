package com.giganticsheep.pokemon.domain.generations

import com.giganticsheep.pokemon.domain.generations.model.GenerationDisplay
import com.giganticsheep.displaystate.DataUseCase
import javax.inject.Inject

class GetGenerationUseCase @Inject internal constructor(
    private val provider: GenerationDisplayProvider
): DataUseCase<GenerationDisplay>(provider) {

    suspend operator fun invoke(name: String) = provider.providesGeneration(name)
}
