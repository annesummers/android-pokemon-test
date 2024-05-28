package com.giganticsheep.pokemon.data.generations

import com.giganticsheep.pokemon.data.EndpointManager
import com.giganticsheep.pokemon.data.generations.model.Generation
import com.giganticsheep.pokemon.data.generations.model.GenerationItemsResponse
import com.giganticsheep.pokemon.data.network.client.PokemonHttpClient
import com.giganticsheep.network.client.get

internal class InternalGenerationsApi(
    private val httpClient: PokemonHttpClient,
    private val endpointManager: GenerationEndpointManager,
) : GenerationsApi {

    override suspend fun getGenerations(
        page: Int,
        batch: Int,
    ) = httpClient.get<GenerationItemsResponse>(
        path = endpointManager.generation,
        query = mapOf(
            EndpointManager.OFFSET to page.toString(),
            EndpointManager.LIMIT to batch.toString(),
        ),
    )

    override suspend fun getGeneration(
        generationName: String,
    ) = httpClient.get<Generation>(
        path = endpointManager.generation(generationName),
    )
}
