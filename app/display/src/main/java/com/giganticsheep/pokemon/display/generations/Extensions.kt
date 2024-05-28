package com.giganticsheep.pokemon.display.generations

import com.giganticsheep.pokemon.data.generations.model.Generation
import com.giganticsheep.pokemon.data.generations.model.GenerationItem
import com.giganticsheep.pokemon.domain.generations.model.GenerationDisplay
import com.giganticsheep.pokemon.domain.generations.model.GenerationItemDisplay

internal fun Generation.toDisplay() = GenerationDisplay(
    id = id,
    region = mainRegion.name,
    name = name,
    species = species.map { it.name },
    moves = moves.map { it.name },
)

internal fun GenerationItem.toDisplay() = GenerationItemDisplay(name)