package com.giganticsheep.pokemon.ui.pokemon

import androidx.lifecycle.ViewModel
import com.giganticsheep.pokemon.navigation.MainNavigator
import com.giganticsheep.navigation.Navigator
import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.domain.pokemon.ShowPokemonUseCase
import com.giganticsheep.ui.DisplayScreenStateProvided
import com.giganticsheep.ui.DisplayScreenStateProvider
import com.giganticsheep.ui.launchWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
internal class PokemonViewModel @Inject constructor(
    @MainNavigator val mainNavigator: Navigator,
    @BackgroundDispatcher val backgroundDispatcher: CoroutineDispatcher,
    private val showPokemonUseCase: ShowPokemonUseCase
) : ViewModel() {

    val pokemon = showPokemonUseCase.pokemon

    fun setup(pokemonNumber: String) {
        launchWith(backgroundDispatcher) {
            showPokemonUseCase.fetchPokemonForDisplay(pokemonNumber)
        }
    }
}
