package com.giganticsheep.pokemon.ui.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.giganticsheep.pokemon.ui.theme.customColor1Dark

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PokemonTopAppBar() {
    TopAppBar(
        title = {
            Text(
                text = "Pokemon Navigator",
                style = MaterialTheme.typography.headlineMedium,
                color = customColor1Dark
            )
        }
    )
}