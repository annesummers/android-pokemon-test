package com.giganticsheep.pokemon.data.generations.model

import com.giganticsheep.pokemon.data.moves.model.MoveItem
import com.giganticsheep.pokemon.data.species.model.SpeciesItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenerationItemResponse(
    @SerialName("count")
    val count: Int,
    @SerialName("results")
    val results: List<GenerationItem>
)

interface Item {
    val name: String
}

@Serializable
data class GenerationItem(
    @SerialName("name")
    override val name: String,
): Item

@Serializable
data class RegionItem(
    @SerialName("name")
    override val name: String,
): Item

@Serializable
data class Generation(
    @SerialName("name")
    val name: String,
    @SerialName("id")
    val id: Int,
    @SerialName("main_region")
    val mainRegion: RegionItem,
    @SerialName("moves")
    val moves: List<MoveItem>,
    @SerialName("pokemon_species")
    val species: List<SpeciesItem>,
)
