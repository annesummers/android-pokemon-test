package com.giganticsheep.pokemon.domain.pokemon.model

import androidx.compose.runtime.Stable

@Stable
data class PokemonDisplay(
    val id: Int,
    val name: String,
    val descriptions: List<String>,
    val imageUrl: String,
)
