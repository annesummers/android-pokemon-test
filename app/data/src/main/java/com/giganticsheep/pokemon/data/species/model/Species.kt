package com.giganticsheep.pokemon.data.species.model

import com.giganticsheep.pokemon.data.generations.model.Item
import com.giganticsheep.pokemon.data.generations.model.RegionItem
import com.giganticsheep.pokemon.data.moves.model.MoveItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpeciesItemResponse(
    @SerialName("count")
    val count: Int,
    @SerialName("results")
    val results: List<SpeciesItem>
)

@Serializable
data class SpeciesItem(
    @SerialName("name")
    override val name: String,
): Item

@Serializable
data class Species(
    @SerialName("name")
    val name: String,
    @SerialName("id")
    val id: Int,
)
