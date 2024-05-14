package com.giganticsheep.pokemon.common

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @BackgroundDispatcher
    @Provides
    fun providesBackgroundDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
