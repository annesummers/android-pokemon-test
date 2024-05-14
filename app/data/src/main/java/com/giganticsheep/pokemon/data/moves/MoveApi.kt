package com.giganticsheep.pokemon.data.moves

import com.giganticsheep.pokemon.data.moves.model.Move
import com.giganticsheep.pokemon.data.network.client.PokemonHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class MoveEndpointManager @Inject constructor() {

    fun generation(name: String) = "$move/$name"

    val move = "move"

    val offset = "offset"
}

@Module
@InstallIn(SingletonComponent::class)
internal object MovesModule {

    @Provides
    @Singleton
    fun providesMovesApi(
        httpClient: PokemonHttpClient,
        endpointManager: MoveEndpointManager,
    ): MovesApi = InternalMovesApi(
        httpClient, endpointManager
    )
}

interface MovesApi {

  //  suspend fun getMoves(page: Int = 0): List<MoveItem>

    suspend fun getMove(generationName: String): Move
}

internal class InternalMovesApi(
    private val httpClient: PokemonHttpClient,
    private val endpointManager: MoveEndpointManager,
) : MovesApi {
/*
    override suspend fun getMoves(
        page: Int,
    ) = httpClient.get<MoveItemResponse>(
        path = endpointManager.generation,
        query = mapOf(endpointManager.offset to page.toString())
    ).results
*/

    override suspend fun getMove(
        generationName: String
    ) = httpClient.get<Move>(
        path = endpointManager.generation(generationName)
    )
}
