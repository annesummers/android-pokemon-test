package com.giganticsheep.pokemon.data.species.model

import com.giganticsheep.pokemon.data.Item
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpeciesItem(
    @SerialName("name")
    override val name: String,
) : Item

@Serializable
data class Species(
    @SerialName("name")
    val internalName: String,
    @SerialName("id")
    val id: Int,
    @SerialName("names")
    val names: List<Name>,
    @SerialName("flavor_text_entries")
    val descriptions: List<FlavorText>,
) {
    @Serializable
    data class Name(
        @SerialName("language")
        val language: Language,
        @SerialName("name")
        val name: String,
    )

    @Serializable
    data class FlavorText(
        @SerialName("language")
        val language: Language,
        @SerialName("flavor_text")
        val description: String,
    )
}

@Serializable
data class Language(
    @SerialName("name")
    val name: String,
)
