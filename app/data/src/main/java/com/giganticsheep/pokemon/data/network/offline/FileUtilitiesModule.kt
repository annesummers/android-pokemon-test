package com.giganticsheep.pokemon.data.network.offline

import android.content.Context
import com.giganticsheep.network.FileUtilities
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object FileUtilitiesModule {

    @Provides
    fun providesFileUtilities(
        @ApplicationContext context: Context,
    ) = FileUtilities.create(context)
}