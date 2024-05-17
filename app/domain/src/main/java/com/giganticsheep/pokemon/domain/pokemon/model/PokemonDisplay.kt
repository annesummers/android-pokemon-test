package com.giganticsheep.pokemon.domain.pokemon.model

import androidx.compose.runtime.Stable
import com.giganticsheep.pokemon.domain.pokemon.Pokemon

internal fun Pokemon.toDisplay() = PokemonDisplay(
    id = id,
    name = name,
    imageUrl = imageUrl,
    descriptions = descriptions,
)

@Stable
data class PokemonDisplay(
    val id: Int,
    val name: String,
    val descriptions: List<String>,
    val imageUrl: String,
)
