package com.giganticsheep.pokemon.navigation

import com.giganticsheep.navigation.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {

    @Provides
    @MainNavigator
    @Singleton
    fun providesMainNavigator() = Navigator()
}

@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Qualifier
annotation class MainNavigator
