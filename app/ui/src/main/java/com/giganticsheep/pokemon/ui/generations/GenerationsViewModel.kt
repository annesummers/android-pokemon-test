package com.giganticsheep.pokemon.ui.generations

import android.util.Log
import androidx.lifecycle.ViewModel
import com.giganticsheep.navigation.Navigator
import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.domain.generations.ShowGenerationsUseCase
import com.giganticsheep.pokemon.navigation.HomeNavigation
import com.giganticsheep.pokemon.navigation.MainNavigator
import com.giganticsheep.ui.DisplayScreenStateProvided
import com.giganticsheep.ui.DisplayScreenStateProvider
import com.giganticsheep.ui.launchWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
internal class GenerationsViewModel @Inject constructor(
    @MainNavigator val mainNavigator: Navigator,
    @BackgroundDispatcher val backgroundDispatcher: CoroutineDispatcher,
    private val showGenerationsUseCase: ShowGenerationsUseCase,
) : ViewModel() {

    val generations = showGenerationsUseCase.generations

    init {
        launchWith(backgroundDispatcher) {
            showGenerationsUseCase.fetchGenerationsForDisplay()
        }
    }

    fun onGenerationClicked(generationId: String) {
        mainNavigator.navigate(
            HomeNavigation.Screen
                .Generation
                .withArgs(HomeNavigation.generationId to generationId)
        )
    }
}
