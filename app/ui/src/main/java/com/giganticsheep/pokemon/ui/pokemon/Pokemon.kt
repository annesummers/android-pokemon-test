package com.giganticsheep.pokemon.ui.pokemon

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.giganticsheep.pokemon.domain.pokemon.model.PokemonDisplay
import com.giganticsheep.pokemon.ui.R
import com.giganticsheep.pokemon.ui.common.PokemonImage
import com.giganticsheep.pokemon.ui.common.PokemonTopAppBar
import com.giganticsheep.pokemon.ui.home.HomeNavigationGraph
import com.giganticsheep.pokemon.ui.theme.Padding
import com.giganticsheep.pokemon.ui.theme.PokemonTheme
import com.giganticsheep.pokemon.ui.theme.screenPadding
import com.giganticsheep.ui.DisplayDataState
import com.giganticsheep.ui.HandleDisplayState
import com.giganticsheep.ui.collectDisplayDataStateAsState

@Composable
internal fun PokemonScreen(
    navigationGraph: HomeNavigationGraph,
    pokemonNameOrId: String,
    pokemonViewModel: PokemonViewModel = hiltViewModel(navigationGraph.graphNavEntry),
) {
    LaunchedEffect(pokemonViewModel) {
        pokemonViewModel.setup(pokemonNameOrId)
    }

    val pokemonState by pokemonViewModel.pokemonDisplayState.collectDisplayDataStateAsState()

    PokemonContent(
        pokemonState = pokemonState,
        onUpClicked = remember { pokemonViewModel::onUpClicked },
    )
}

@Composable
internal fun PokemonContent(
    pokemonState: DisplayDataState<PokemonDisplay>,
    onUpClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            PokemonTopAppBar(
                title = if (pokemonState is DisplayDataState.Data) {
                    pokemonState.data.name
                } else {
                    stringResource(R.string.title_pokemon)
                },
                onUpClicked = onUpClicked,
            )
        },
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
        ) { pokemon ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .screenPadding(it),
            ) {
                Column {
                    PokemonImage(pokemon)

                    Text(
                        modifier = Modifier.padding(Padding.itemPadding),
                        text = pokemon.descriptions.first(),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonPreview() {
    PokemonTheme {
        PokemonContent(
            DisplayDataState.Data(
                PokemonDisplay(
                    id = 1,
                    name = "Bulbasaur",
                    imageUrl = "https://unpkg.com/pokeapi-sprites@2.0.2/sprites/pokemon/other/dream-world/1.svg",
                    descriptions = listOf("It can go for days\\nwithout eating a\\nsingle morsel.\\u000cIn the bulb on\\nits back, it\\nstores energy."),
                ),
            ),
        ) {}
    }
}
