package com.giganticsheep.pokemon.data.generations.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenerationItemsResponse(
    @SerialName("count")
    val count: Int,
    @SerialName("results")
    val results: List<GenerationItem>,
)
