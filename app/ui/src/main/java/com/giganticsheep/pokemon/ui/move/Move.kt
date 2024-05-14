package com.giganticsheep.pokemon.ui.move

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.giganticsheep.navigation.Navigator
import com.giganticsheep.pokemon.ui.common.PokemonTopAppBar
import com.giganticsheep.pokemon.ui.home.HomeNavigationGraph
import com.giganticsheep.pokemon.ui.theme.screenPadding
import com.giganticsheep.ui.DisplayScreenState
import com.giganticsheep.ui.HandleDisplayState
import com.giganticsheep.ui.collectDisplayScreenStateAsState

@Composable
internal fun MoveScreen(
    navigationGraph: HomeNavigationGraph,
    moveId: String,
    moveViewModel: MoveViewModel = hiltViewModel(navigationGraph.graphNavEntry),
) {
    val displayState by moveViewModel.displayState.collectDisplayScreenStateAsState()
/*

    LaunchedEffect(pokemonViewModel) {
        pokemonViewModel.onStart(pokemonId)
    }
*/

    MoveContent(
        navigator = moveViewModel.mainNavigator,
        displayState = displayState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MoveContent(
    navigator: Navigator,
    displayState: DisplayScreenState,
) {
    HandleDisplayState(
        displayState,
        onError = { error, _, _ ->
            Scaffold(
                topBar = { PokemonTopAppBar() },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .screenPadding(it),
                    contentAlignment = Alignment.Center,
                ) { Text(error, color = MaterialTheme.colorScheme.primary) }
            }
        },
    ) {
        Scaffold(
            topBar = { PokemonTopAppBar() },
        ) {
        }
    }
}
