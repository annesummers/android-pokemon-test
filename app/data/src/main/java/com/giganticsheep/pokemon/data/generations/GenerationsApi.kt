package com.giganticsheep.pokemon.data.generations

import com.giganticsheep.pokemon.data.generations.model.Generation
import com.giganticsheep.pokemon.data.generations.model.GenerationItem
import com.giganticsheep.pokemon.data.generations.model.GenerationItemResponse
import com.giganticsheep.pokemon.data.network.client.PokemonHttpClient
import javax.inject.Inject
import javax.inject.Singleton

interface GenerationsApi {

    suspend fun getGenerations(page: Int = 0, batch: Int = 20): List<GenerationItem>

    suspend fun getGeneration(generationName: String): Generation
}

internal class InternalGenerationsApi(
    private val httpClient: PokemonHttpClient,
    private val endpointManager: GenerationEndpointManager,
) : GenerationsApi {
    override suspend fun getGenerations(
        page: Int,
        batch: Int,
    ) = httpClient.get<GenerationItemResponse>(
        path = endpointManager.generation,
        query = mapOf(
            endpointManager.offset to page.toString(),
            endpointManager.limit to batch.toString()
        )
    ).results

    override suspend fun getGeneration(
        generationName: String,
    ) = httpClient.get<Generation>(
        path = endpointManager.generation(generationName)
    )
}

@Singleton
internal class GenerationEndpointManager @Inject constructor() {

    val generation = "generation"

    val offset = "offset"
    val limit = "limit"

    fun generation(name: String) = "$generation/$name"
}

