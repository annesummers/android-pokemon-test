package com.giganticsheep.pokemon.display.generations

import com.giganticsheep.pokemon.domain.generations.GenerationDisplayProvider
import com.giganticsheep.pokemon.domain.generations.GenerationsDisplayProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ViewModelComponent::class)
internal abstract class GenerationsModule {

    @Binds
    abstract fun bindsGenerationsProvider(
        internalGenerationsProvider: InternalGenerationsDisplayProvider
    ): GenerationsDisplayProvider

    @Binds
    abstract fun bindsGenerationProvider(
        internalGenerationProvider: InternalGenerationDisplayProvider
    ): GenerationDisplayProvider

}
