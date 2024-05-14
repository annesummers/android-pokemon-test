package com.giganticsheep.pokemon.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.giganticsheep.pokemon.ui.common.PokemonTopAppBar
import com.giganticsheep.pokemon.ui.theme.PokemonTheme
import com.giganticsheep.pokemon.ui.theme.customColor1Dark
import com.giganticsheep.pokemon.ui.theme.screenPadding
import com.giganticsheep.ui.DisplayScreenState
import com.giganticsheep.ui.HandleDisplayState
import com.giganticsheep.ui.collectDisplayScreenStateAsState

@Composable
internal fun HomeScreen(
    navigationGraph: HomeNavigationGraph,
    homeViewModel: HomeViewModel = hiltViewModel(navigationGraph.graphNavEntry),
) {
    val displayState by homeViewModel.displayState.collectDisplayScreenStateAsState()

    HomeContent(
        displayState = displayState,
        onButtonClicked = remember { homeViewModel::onBrowseByGenerationClicked }
    )
}

@Composable
internal fun HomeContent(
    displayState: DisplayScreenState,
    onButtonClicked: () -> Unit,
) {
    Log.d("HomeContent", "displayState is $displayState")

    HandleDisplayState(
        displayState = displayState,
        onError = { error, _, _ ->
            Scaffold(
                topBar = { PokemonTopAppBar() }
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
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                Button(modifier = Modifier.padding(it), onClick = onButtonClicked) {
                    Text("Show generations")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    PokemonTheme {
        HomeContent(DisplayScreenState.Default) {

        }
    }
}
