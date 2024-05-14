package com.giganticsheep.pokemon.data.network.environment

import com.giganticsheep.network.environment.Endpoint
import com.giganticsheep.network.environment.Environment
import com.giganticsheep.network.environment.Host

sealed interface PokemonEndpoint : Endpoint {
    data object Data : PokemonEndpoint

    data object Images : PokemonEndpoint
}

sealed class PokemonEnvironment(
    hosts: Map<Endpoint, Host>,
) : Environment(hosts) {

    data object Offline : PokemonEnvironment(
        mapOf(
            PokemonEndpoint.Data to Host(
                host = "offline",
                path = "",
            ),
            PokemonEndpoint.Images to Host(
                host = "offline",
                path = "",
            ),
        ),
    )

    data object Online : PokemonEnvironment(
        mapOf(
            PokemonEndpoint.Data to Host(
                host = "pokeapi.co",
                path = "api/v2",
            ),
            PokemonEndpoint.Images to Host(
                host = "unpkg.com",
                path = "pokeapi-sprites@2.0.2/sprites/pokemon/other/dream-world",
            ),
        ),
    )
}
