package com.giganticsheep.pokemon.domain.generations.model

import androidx.compose.runtime.Stable

@Stable
data class GenerationDisplay(
    val id: Int,
    val region: String,
    val name: String,
    val species: List<String>,
    val moves: List<String>,
)
