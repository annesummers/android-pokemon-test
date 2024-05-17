package com.giganticsheep.pokemon.data.moves.model

import com.giganticsheep.pokemon.data.Item
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoveItem(
    @SerialName("name")
    override val name: String,
) : Item
