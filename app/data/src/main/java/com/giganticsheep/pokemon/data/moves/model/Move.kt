package com.giganticsheep.pokemon.data.moves.model

import com.giganticsheep.pokemon.data.generations.model.Item
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoveItem(
    @SerialName("name")
    override val name: String,
): Item

@Serializable
data class Move(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
)