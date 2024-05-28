package com.giganticsheep.pokemon.ui.generation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.giganticsheep.pokemon.domain.generations.model.GenerationDisplay
import com.giganticsheep.pokemon.ui.R
import com.giganticsheep.pokemon.ui.common.PokemonTopAppBar
import com.giganticsheep.pokemon.ui.home.HomeNavigationGraph
import com.giganticsheep.pokemon.ui.theme.Padding
import com.giganticsheep.pokemon.ui.theme.PokemonTheme
import com.giganticsheep.pokemon.ui.theme.TitleText
import com.giganticsheep.pokemon.ui.theme.screenPadding
import com.giganticsheep.displaystate.DisplayDataState
import com.giganticsheep.ui.HandleDisplayState
import com.giganticsheep.ui.collectDisplayDataStateAsState
import com.giganticsheep.ui.composable.ExpandableNestedList
import com.giganticsheep.ui.composable.NestedList

// TODO tests
@Composable
internal fun GenerationScreen(
    navigationGraph: HomeNavigationGraph,
    generationId: String,
    generationViewModel: GenerationViewModel = hiltViewModel(navigationGraph.graphNavEntry),
) {
    LaunchedEffect(generationViewModel) {
        generationViewModel.setup(generationId)
    }

    val generationState by generationViewModel.generationDisplayState.collectDisplayDataStateAsState()

    GenerationContent(
        generationState = generationState,
        onUpClicked = remember { generationViewModel::onUpClicked },
        onMoveClicked = remember { generationViewModel::onMoveClicked },
        onSpeciesClicked = remember { generationViewModel::onSpeciesClicked },
    )
}

@Composable
internal fun GenerationContent(
    generationState: com.giganticsheep.displaystate.DisplayDataState<GenerationDisplay>,
    onUpClicked: () -> Unit,
    onMoveClicked: (String) -> Unit,
    onSpeciesClicked: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            PokemonTopAppBar(
                title = if (generationState is com.giganticsheep.displaystate.DisplayDataState.Data) {
                    generationState.data.name
                } else {
                    "Generation"
                },
                onUpClicked = onUpClicked,
            )
        },
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
        TitleText(text = stringResource(R.string.label_region, generation.region))
        Spacer(modifier = Modifier.height(Padding.itemVerticalSpacing))
        ExpandableNestedList(
            startCollapsed = true,
            headerModifier = Modifier
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .padding(Padding.itemPadding),
            itemModifier = Modifier,
            items = listOf(
                NestedList(stringResource(R.string.header_species), generation.species, onSpeciesClicked),
                NestedList(stringResource(R.string.header_moves), generation.moves, onMoveClicked),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GenerationPreview() {
    PokemonTheme {
        GenerationContent(
            com.giganticsheep.displaystate.DisplayDataState.Data(
                GenerationDisplay(
                    1,
                    "kanto",
                    moves = listOf("move1", "move2"),
                    species = listOf("bulbasaur", "charmander"),
                    name = "generation-i",
                ),

            ),
            onMoveClicked = { },
            onSpeciesClicked = { },
            onUpClicked = {},
        )
    }
}
