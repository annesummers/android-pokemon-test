package com.giganticsheep.pokemon.ui.generations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.giganticsheep.pokemon.domain.generations.model.GenerationItemDisplay
import com.giganticsheep.pokemon.ui.common.PokemonTopAppBar
import com.giganticsheep.pokemon.ui.home.HomeNavigationGraph
import com.giganticsheep.pokemon.ui.theme.Padding
import com.giganticsheep.pokemon.ui.theme.PokemonTheme
import com.giganticsheep.pokemon.ui.theme.screenPadding
import com.giganticsheep.ui.DisplayDataState
import com.giganticsheep.ui.HandleDisplayState
import com.giganticsheep.ui.collectDisplayDataStateAsState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun GenerationsScreen(
    navigationGraph: HomeNavigationGraph,
    generationsViewModel: GenerationsViewModel = hiltViewModel(navigationGraph.graphNavEntry),
) {
    val generationsState by generationsViewModel.generationsDisplayState.collectDisplayDataStateAsState()

    GenerationsContent(
        generationsState = generationsState,
        onUpClicked = remember { generationsViewModel::onUpClicked },
        onGenerationClicked = remember { generationsViewModel::onGenerationClicked },
    )
}

@Composable
internal fun GenerationsContent(
    generationsState: DisplayDataState<ImmutableList<GenerationItemDisplay>>,
    onUpClicked: () -> Unit,
    onGenerationClicked: (generationId: String) -> Unit,
) {
    Scaffold(
        topBar = {
            PokemonTopAppBar(
                title = "Generations",
                onUpClicked = onUpClicked,
            )
        },
    ) {
        HandleDisplayState(
            displayState = generationsState,
            onError = { error, _, _ ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .screenPadding(it),
                    contentAlignment = Alignment.Center,
                ) { Text(error, color = MaterialTheme.colorScheme.primary) }
            },
        ) { item ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .screenPadding(it),
            ) {
                LazyColumn {
                    items(item.size) { index ->
                        GenerationItem(item[index], onGenerationClicked)
                    }
                }
            }
        }
    }
}

@Composable
internal fun GenerationItem(
    generation: GenerationItemDisplay,
    onGenerationClicked: (String) -> Unit,
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Padding.itemPadding)
            .clickable(onClick = { onGenerationClicked(generation.name) }),
        text = generation.name,
    )
}

@Preview(showBackground = true)
@Composable
fun GenerationsPreview() {
    PokemonTheme {
        GenerationsContent(
            DisplayDataState.Data(
                listOf(
                    GenerationItemDisplay(
                        "Kanto",
                    ),
                    GenerationItemDisplay(
                        "Jhoto",
                    ),
                ).toImmutableList(),
            ),
            onGenerationClicked = {},
            onUpClicked = {},
        )
    }
}
