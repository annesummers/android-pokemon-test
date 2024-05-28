package com.giganticsheep.pokemon.data.generations

import com.giganticsheep.pokemon.data.generations.model.Generation
import com.giganticsheep.pokemon.data.generations.model.GenerationItem
import com.giganticsheep.response.CompletableResponse
import com.giganticsheep.response.DataResponse
import com.giganticsheep.response.DataResponseState
import kotlinx.coroutines.flow.Flow

interface GenerationsRepository {

    val generations: Flow<DataResponseState<List<GenerationItem>>>

    fun fetchGenerations()

    suspend fun clearData(): CompletableResponse

    suspend fun getGeneration(generationName: String): DataResponse<Generation>
}
