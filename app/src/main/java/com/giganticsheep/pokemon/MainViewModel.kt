package com.giganticsheep.pokemon

import androidx.lifecycle.ViewModel
import com.giganticsheep.navigation.Navigator
import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.navigation.MainNavigation
import com.giganticsheep.pokemon.navigation.MainNavigator
import com.giganticsheep.ui.launchWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @MainNavigator val navigator: Navigator,
    @BackgroundDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    init {
        launchWith(dispatcher) {
            delay(SPLASH_DELAY)
            navigator.navigate(MainNavigation.Graph.Home)
        }
    }

    companion object {
        private const val SPLASH_DELAY = 3000L
    }
}
