package com.giganticsheep.pokemon

import androidx.lifecycle.ViewModel
import com.giganticsheep.navigation.Navigator
import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.navigation.MainNavigation
import com.giganticsheep.pokemon.navigation.MainNavigator
import com.giganticsheep.ui.DisplayScreenStateProvided
import com.giganticsheep.ui.DisplayScreenStateProvider
import com.giganticsheep.ui.launchWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @MainNavigator val navigator: Navigator,
    @BackgroundDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel(),
    DisplayScreenStateProvider by DisplayScreenStateProvided(dispatcher) {

    init {
        launchWith(dispatcher) {
            showDefault()
            delay(5000)
            navigator.navigate(MainNavigation.Graph.Home)
        }
    }
}
