package com.giganticsheep.pokemon.display.pokemon

import com.giganticsheep.pokemon.data.species.model.Pokemon
import com.giganticsheep.pokemon.domain.pokemon.model.PokemonDisplay


internal fun Pokemon.toDisplay() = PokemonDisplay(
    id = id,
    name = name,
    imageUrl = imageUrl,
    descriptions = descriptions,
)
