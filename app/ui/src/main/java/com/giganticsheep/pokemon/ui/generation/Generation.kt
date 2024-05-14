package com.giganticsheep.pokemon.ui.generation

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.giganticsheep.pokemon.domain.generations.GenerationDisplay
import com.giganticsheep.pokemon.ui.common.PokemonTopAppBar
import com.giganticsheep.pokemon.ui.home.HomeNavigationGraph
import com.giganticsheep.pokemon.ui.theme.Padding
import com.giganticsheep.pokemon.ui.theme.PokemonTheme
import com.giganticsheep.pokemon.ui.theme.screenPadding
import com.giganticsheep.ui.DisplayDataState
import com.giganticsheep.ui.HandleDisplayState
import com.giganticsheep.ui.collectDisplayDataStateAsState
import com.giganticsheep.ui.composable.ExpandableNestedList
import com.giganticsheep.ui.composable.NestedList

@Composable
internal fun GenerationScreen(
    navigationGraph: HomeNavigationGraph,
    generationId: String,
    generationViewModel: GenerationViewModel = hiltViewModel(navigationGraph.graphNavEntry),
) {
    LaunchedEffect(generationViewModel) {
        generationViewModel.setup(generationId)
    }

    val generationState by generationViewModel.generation.displayState.collectDisplayDataStateAsState()

    GenerationContent(
        generationState = generationState,
        onMoveClicked = remember { generationViewModel::onMoveClicked },
        onSpeciesClicked = remember { generationViewModel::onSpeciesClicked }
    )
}

@Composable
internal fun GenerationContent(
    generationState: DisplayDataState<GenerationDisplay>,
    onMoveClicked: (String) -> Unit,
    onSpeciesClicked: (String) -> Unit,
) {
    Scaffold(
        topBar = { PokemonTopAppBar() },
    ) {
        HandleDisplayState(
            displayState = generationState,
        ) { item ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .screenPadding(it),
            ) { Generation(item, onMoveClicked, onSpeciesClicked) }
        }
    }
}

@Composable
fun Generation(
    generation: GenerationDisplay,
    onMoveClicked: (String) -> Unit,
    onSpeciesClicked: (String) -> Unit,
) {
    Column {
        Text(generation.region)
        Text(generation.name)
        ExpandableNestedList(
            headerModifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(Padding.itemPadding),
            itemModifier = Modifier,
            items = listOf(
                NestedList("Moves", generation.moves, onMoveClicked),
                NestedList("Species", generation.species, onSpeciesClicked)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GenerationPreview() {
    PokemonTheme {
        GenerationContent(
            DisplayDataState.Default(
                GenerationDisplay(
                    1,
                    "kanto",
                    moves = listOf("move1","move2"),
                    species = listOf("bulbasaur","charmander"),
                    name = "generation-i"
                ),

                ),
            onMoveClicked = { },
            onSpeciesClicked = { },
        )
    }
}

