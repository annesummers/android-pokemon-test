package com.giganticsheep.pokemon.ui.generations

import androidx.lifecycle.ViewModel
import com.giganticsheep.navigation.Navigator
import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.domain.generations.GetGenerationsUseCase
import com.giganticsheep.pokemon.navigation.HomeNavigation
import com.giganticsheep.pokemon.navigation.MainNavigator
import com.giganticsheep.ui.launchWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
internal class GenerationsViewModel @Inject constructor(
    @MainNavigator val mainNavigator: Navigator,
    @BackgroundDispatcher val backgroundDispatcher: CoroutineDispatcher,
    private val getGenerationsUseCase: GetGenerationsUseCase,
) : ViewModel() {

    val generationsDisplayState = getGenerationsUseCase.displayState

    init {
        launchWith(backgroundDispatcher) {
            getGenerationsUseCase()
        }
    }

    fun onUpClicked() {
        mainNavigator.navigateBack()
    }

    fun onGenerationClicked(generationId: String) {
        mainNavigator.navigate(
            HomeNavigation.Screen
                .Generation
                .withArgs(HomeNavigation.generationId to generationId),
        )
    }
}
