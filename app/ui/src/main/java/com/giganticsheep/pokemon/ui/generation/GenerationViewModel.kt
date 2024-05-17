package com.giganticsheep.pokemon.ui.generation

import androidx.lifecycle.ViewModel
import com.giganticsheep.navigation.Navigator
import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.domain.generations.GetGenerationUseCase
import com.giganticsheep.pokemon.navigation.HomeNavigation
import com.giganticsheep.pokemon.navigation.HomeNavigation.pokemonName
import com.giganticsheep.pokemon.navigation.MainNavigator
import com.giganticsheep.ui.launchWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
internal class GenerationViewModel @Inject constructor(
    @MainNavigator val mainNavigator: Navigator,
    @BackgroundDispatcher val backgroundDispatcher: CoroutineDispatcher,
    private val showGenerationUseCase: GetGenerationUseCase,
) : ViewModel() {

    val generationDisplayState = showGenerationUseCase.generationDisplayState

    fun setup(generationName: String) {
        launchWith(backgroundDispatcher) {
            showGenerationUseCase.fetchGenerationForDisplay(generationName)
        }
    }

    fun onUpClicked() {
        mainNavigator.navigateBack()
    }

    fun onMoveClicked(name: String) {
        // not implemented
    }

    fun onSpeciesClicked(name: String) {
        mainNavigator.navigate(
            HomeNavigation.Screen.Pokemon
                .withArgs(pokemonName to name),
        )
    }
}
