package com.giganticsheep.pokemon.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.giganticsheep.pokemon.domain.pokemon.model.PokemonDisplay
import com.giganticsheep.pokemon.ui.R
import com.giganticsheep.pokemon.ui.common.PokemonImage
import com.giganticsheep.pokemon.ui.common.PokemonTopAppBar
import com.giganticsheep.pokemon.ui.theme.Padding
import com.giganticsheep.pokemon.ui.theme.PokemonTheme
import com.giganticsheep.pokemon.ui.theme.screenPadding
import com.giganticsheep.displaystate.DisplayDataState
import com.giganticsheep.displaystate.DisplayScreenState
import com.giganticsheep.ui.HandleDisplayState
import com.giganticsheep.ui.collectDisplayDataStateAsState
import com.giganticsheep.ui.collectDisplayScreenStateAsState

@Composable
internal fun HomeScreen(
    navigationGraph: HomeNavigationGraph,
    homeViewModel: HomeViewModel = hiltViewModel(navigationGraph.graphNavEntry),
) {
    val displayState by homeViewModel.setupDisplayState.collectDisplayScreenStateAsState()
    val randomPokemon by homeViewModel.randomPokemonDisplayState.collectDisplayDataStateAsState()

    HomeContent(
        displayState = displayState,
        randomPokemonState = randomPokemon,
        onPokemonClicked = remember { homeViewModel::onPokemonClicked },
        onGenerationsClicked = remember { homeViewModel::onBrowseByGenerationClicked },
        generateNewPokemon = remember { homeViewModel::generateNewPokemon },
    )
}

@Composable
internal fun HomeContent(
    displayState: com.giganticsheep.displaystate.DisplayScreenState,
    randomPokemonState: com.giganticsheep.displaystate.DisplayDataState<PokemonDisplay>,
    onPokemonClicked: (Int) -> Unit,
    onGenerationsClicked: () -> Unit,
    generateNewPokemon: () -> Unit,
) {
    HandleDisplayState(
        displayState,
    ) {
        Scaffold(
            topBar = { PokemonTopAppBar(stringResource(R.string.title_home)) },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .screenPadding(it),
            ) {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Card(
                        modifier = Modifier
                            .padding(Padding.cardPadding),
                        colors = CardDefaults.cardColors()
                            .copy(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(5.dp),
                    ) {
                        HandleDisplayState(
                            displayState = randomPokemonState,
                            onError = { error, _, _ ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1F),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .padding(Padding.itemPadding),
                                        text = error,
                                    )
                                }
                            },
                        ) { pokemon ->
                            PokemonImage(pokemon, onPokemonClicked)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(Padding.itemPadding),
                                    text = pokemon.name,
                                )

                                Button(
                                    modifier = Modifier.padding(Padding.itemPadding),
                                    onClick = generateNewPokemon,
                                ) { Text(stringResource(R.string.button_random_pokemon)) }
                            }
                        }
                    }
                }

                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        modifier = Modifier.padding(Padding.itemPadding),
                        onClick = onGenerationsClicked,
                    ) {
                        Text(stringResource(R.string.button_show_generations))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    PokemonTheme {
        HomeContent(
            com.giganticsheep.displaystate.DisplayScreenState.Default,
            com.giganticsheep.displaystate.DisplayDataState.Data(
                PokemonDisplay(
                    id = 1,
                    name = "Bulbasaur",
                    imageUrl = "https://unpkg.com/pokeapi-sprites@2.0.2/sprites/pokemon/other/dream-world/1.svg",
                    descriptions = listOf("It can go for days\\nwithout eating a\\nsingle morsel.\\u000cIn the bulb on\\nits back, it\\nstores energy."),
                ),
            ),
            onPokemonClicked = {},
            onGenerationsClicked = {
            },
            generateNewPokemon = { },
        )
    }
}
