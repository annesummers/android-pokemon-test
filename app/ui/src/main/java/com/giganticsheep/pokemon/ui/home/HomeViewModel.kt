package com.giganticsheep.pokemon.ui.home

import androidx.lifecycle.ViewModel
import com.giganticsheep.navigation.Navigator
import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.domain.pokemon.GetRandomPokemonUseCase
import com.giganticsheep.pokemon.domain.pokemon.SetupPokemonUseCase
import com.giganticsheep.pokemon.navigation.HomeNavigation
import com.giganticsheep.pokemon.navigation.HomeNavigation.pokemonId
import com.giganticsheep.pokemon.navigation.MainNavigator
import com.giganticsheep.ui.launchWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    @MainNavigator val mainNavigator: Navigator,
    @BackgroundDispatcher val backgroundDispatcher: CoroutineDispatcher,
    private val getRandomPokemonUseCase: GetRandomPokemonUseCase,
    private val setupUseCase: SetupPokemonUseCase,
) : ViewModel() {

    val setupDisplayState = setupUseCase.setupDisplayState
    val randomPokemonDisplayState = getRandomPokemonUseCase.pokemonDisplayState

    init {
        generateNewPokemon()
    }

    fun onPokemonClicked(id: Int) {
        mainNavigator.navigate(
            HomeNavigation.Screen.Pokemon
                .withArgs(pokemonId to id.toString()),
        )
    }

    fun generateNewPokemon() {
        launchWith(backgroundDispatcher) {
            setupUseCase.setup()
                .doOnSuccess { getRandomPokemonUseCase.fetchRandomPokemon() }
        }
    }

    fun onBrowseByGenerationClicked() {
        mainNavigator.navigate(HomeNavigation.Screen.Generations)
    }
}
