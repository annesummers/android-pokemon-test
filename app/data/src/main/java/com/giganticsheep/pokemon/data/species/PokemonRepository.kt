package com.giganticsheep.pokemon.data.species

import com.giganticsheep.pokemon.data.species.model.Pokemon
import com.giganticsheep.response.CompletableResponse
import com.giganticsheep.response.DataResponse

interface PokemonRepository {

    suspend fun setup(): CompletableResponse

    suspend fun getPokemon(nameOrId: String): DataResponse<Pokemon>

    suspend fun getRandomPokemon(): DataResponse<Pokemon>
}

