package com.giganticsheep.pokemon.data.species.model

data class Pokemon(
    val internalName: String,
    val id: Int,
    val imageUrl: String,
    val name: String,
    val descriptions: List<String>,
) {
    companion object {
        internal const val LANGUAGE = "en"
    }
}

fun Species.toPokemon(imageUrl: String) = Pokemon(
    internalName = internalName,
    id = id,
    imageUrl = imageUrl,
    name = names.find { it.language.name == Pokemon.LANGUAGE }?.name ?: internalName,
    descriptions = descriptions.filter { it.language.name == Pokemon.LANGUAGE }.map { it.description },
)
