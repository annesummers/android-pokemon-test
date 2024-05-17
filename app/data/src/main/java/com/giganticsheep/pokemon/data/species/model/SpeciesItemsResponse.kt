package com.giganticsheep.pokemon.data.species.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpeciesItemsResponse(
    @SerialName("count")
    val count: Int,
    @SerialName("results")
    val results: List<SpeciesItem>,
)
