package com.giganticsheep.pokemon.data.species

import com.giganticsheep.pokemon.data.EndpointManager.Companion.DEFAULT_LIMIT
import com.giganticsheep.pokemon.data.EndpointManager.Companion.DEFAULT_OFFSET
import com.giganticsheep.pokemon.data.species.model.Species
import com.giganticsheep.pokemon.data.species.model.SpeciesItemsResponse

interface SpeciesApi {

    suspend fun getSpecies(
        page: Int = DEFAULT_OFFSET,
        limit: Int = DEFAULT_LIMIT,
    ): SpeciesItemsResponse

    suspend fun getSpecies(nameOrId: String): Species

    fun getSpeciesImageUrl(id: Int): String
}

