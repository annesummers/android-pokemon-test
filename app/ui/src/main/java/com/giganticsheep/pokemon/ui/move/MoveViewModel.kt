package com.giganticsheep.pokemon.ui.move

import androidx.lifecycle.ViewModel
import com.giganticsheep.pokemon.navigation.MainNavigator
import com.giganticsheep.navigation.Navigator
import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.ui.DisplayScreenStateProvided
import com.giganticsheep.ui.DisplayScreenStateProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
internal class MoveViewModel @Inject constructor(
    @MainNavigator val mainNavigator: Navigator,
    @BackgroundDispatcher val backgroundDispatcher: CoroutineDispatcher,
) : ViewModel(),
    DisplayScreenStateProvider by DisplayScreenStateProvided(backgroundDispatcher) {


}
