package com.giganticsheep.pokemon.ui.pokemon

import androidx.lifecycle.ViewModel
import com.giganticsheep.navigation.Navigator
import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.domain.pokemon.GetPokemonUseCase
import com.giganticsheep.pokemon.domain.pokemon.SetupPokemonUseCase
import com.giganticsheep.pokemon.navigation.MainNavigator
import com.giganticsheep.ui.launchWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
internal class PokemonViewModel @Inject constructor(
    @MainNavigator val mainNavigator: Navigator,
    @BackgroundDispatcher val backgroundDispatcher: CoroutineDispatcher,
    private val getPokemonUseCase: GetPokemonUseCase,
    private val setupPokemonUseCase: SetupPokemonUseCase,
) : ViewModel() {

    val pokemonDisplayState = getPokemonUseCase.displayState

    fun setup(nameOrId: String) {
        launchWith(backgroundDispatcher) {
            setupPokemonUseCase()
            getPokemonUseCase(nameOrId)
        }
    }

    fun onUpClicked() {
        mainNavigator.navigateBack()
    }
}
