package com.giganticsheep.pokemon.navigation

import com.giganticsheep.navigation.Navigation
import com.giganticsheep.navigation.PotentialDestinationDetails

object MainNavigation : Navigation("Root", listOf()) {

    enum class Screen(
        override val destinationName: String,
        override val args: List<String> = listOf(),
    ) : PotentialDestinationDetails {
        Splash("Splash"),
    }

    enum class Graph(
        override val destinationName: String,
        override val args: List<String>,
    ) : PotentialDestinationDetails {
        Home(HomeNavigation.details.destinationName, listOf()),
    }
}

object HomeNavigation : Navigation("Home", listOf()) {

    const val generationId = "generationId"
    const val pokemonName = "pokemonName"
    const val pokemonId = "pokemonId"

    enum class Screen(
        override val destinationName: String,
        override val args: List<String> = listOf(),
    ) : PotentialDestinationDetails {
        Home("Home"),
        Generations("Generations"),
        Generation("Generation", listOf(generationId)),
        Pokemon("Pokemon", listOf(pokemonId, pokemonName)),
    }
}
