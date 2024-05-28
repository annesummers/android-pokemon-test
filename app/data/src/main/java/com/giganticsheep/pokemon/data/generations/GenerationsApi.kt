package com.giganticsheep.pokemon.data.generations

import com.giganticsheep.pokemon.data.EndpointManager
import com.giganticsheep.pokemon.data.generations.model.Generation
import com.giganticsheep.pokemon.data.generations.model.GenerationItemsResponse

internal interface GenerationsApi {

    suspend fun getGenerations(
        page: Int = EndpointManager.DEFAULT_OFFSET,
        batch: Int = EndpointManager.DEFAULT_LIMIT,
    ): GenerationItemsResponse

    suspend fun getGeneration(generationName: String): Generation
}


