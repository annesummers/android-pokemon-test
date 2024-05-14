package com.giganticsheep.pokemon.ui.generation

import androidx.lifecycle.ViewModel
import com.giganticsheep.pokemon.navigation.MainNavigator
import com.giganticsheep.navigation.Navigator
import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.domain.generations.ShowGenerationUseCase
import com.giganticsheep.pokemon.navigation.HomeNavigation
import com.giganticsheep.pokemon.navigation.HomeNavigation.moveId
import com.giganticsheep.pokemon.navigation.HomeNavigation.pokemonId
import com.giganticsheep.pokemon.navigation.MainNavigation
import com.giganticsheep.ui.DisplayScreenStateProvided
import com.giganticsheep.ui.DisplayScreenStateProvider
import com.giganticsheep.ui.launchWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
internal class GenerationViewModel @Inject constructor(
    @MainNavigator val mainNavigator: Navigator,
    @BackgroundDispatcher val backgroundDispatcher: CoroutineDispatcher,
    private val showGenerationUseCase: ShowGenerationUseCase
) : ViewModel() {

    val generation = showGenerationUseCase.generation

    fun setup(generationName: String) {
        launchWith(backgroundDispatcher) {
            showGenerationUseCase.fetchGenerationForDisplay(generationName)
        }
    }

    fun onMoveClicked(s: String) {
        mainNavigator.navigate(HomeNavigation.Screen.Move.withArgs(moveId to s))
    }


    fun onSpeciesClicked(s: String) {
        mainNavigator.navigate(HomeNavigation.Screen.Pokemon.withArgs(pokemonId to s))
    }
}
