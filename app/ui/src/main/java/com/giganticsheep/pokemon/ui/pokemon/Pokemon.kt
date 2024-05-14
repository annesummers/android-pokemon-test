package com.giganticsheep.pokemon.ui.pokemon

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.giganticsheep.pokemon.domain.pokemon.SpeciesDisplay
import com.giganticsheep.pokemon.ui.common.PokemonTopAppBar
import com.giganticsheep.pokemon.ui.home.HomeNavigationGraph
import com.giganticsheep.pokemon.ui.theme.screenPadding
import com.giganticsheep.ui.DisplayDataState
import com.giganticsheep.ui.HandleDisplayState
import com.giganticsheep.ui.collectDisplayDataStateAsState

@Composable
internal fun PokemonScreen(
    navigationGraph: HomeNavigationGraph,
    pokemonId: String,
    pokemonViewModel: PokemonViewModel = hiltViewModel(navigationGraph.graphNavEntry),
) {
    LaunchedEffect(pokemonViewModel) {
        pokemonViewModel.setup(pokemonId)
    }

    val pokemonState by pokemonViewModel.pokemon.displayState.collectDisplayDataStateAsState()

    PokemonContent(
        pokemonState = pokemonState
    )
}

@Composable
internal fun PokemonContent(
    pokemonState: DisplayDataState<SpeciesDisplay>,
) {
    Scaffold(
        topBar = { PokemonTopAppBar() },
    ) {
        HandleDisplayState(
            displayState = pokemonState,
            onError = { error, _, _ ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .screenPadding(it),
                    contentAlignment = Alignment.Center,
                ) { Text(error, color = MaterialTheme.colorScheme.primary) }
            },
        ) { species ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .screenPadding(it),
            ) { Text(text = species.name) }
        }
    }
}
