package com.giganticsheep.pokemon.data.species

import com.giganticsheep.network.client.get
import com.giganticsheep.pokemon.data.EndpointManager
import com.giganticsheep.pokemon.data.network.client.PokemonHttpClient
import com.giganticsheep.pokemon.data.network.client.PokemonImageHttpClient
import com.giganticsheep.pokemon.data.species.model.Species
import com.giganticsheep.pokemon.data.species.model.SpeciesItemsResponse
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments

internal class InternalSpeciesApi(
    private val httpClient: PokemonHttpClient,
    private val httpImageClient: PokemonImageHttpClient,
    private val endpointManager: SpeciesEndpointManager,
) : SpeciesApi {

    override suspend fun getSpecies(
        page: Int,
        limit: Int,
    ) = httpClient.get<SpeciesItemsResponse>(
        path = endpointManager.species,
        query = mapOf(
            EndpointManager.OFFSET to page.toString(),
            EndpointManager.LIMIT to limit.toString(),
        ),
    )

    override suspend fun getSpecies(
        nameOrId: String,
    ) = httpClient.get<Species>(
        endpointManager.species(nameOrId),
    )

    override fun getSpeciesImageUrl(
        id: Int,
    ) = URLBuilder(httpImageClient.baseUrl)
        .appendPathSegments("$id.svg")
        .buildString()
}