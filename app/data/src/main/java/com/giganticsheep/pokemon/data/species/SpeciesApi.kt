package com.giganticsheep.pokemon.data.species

import com.giganticsheep.pokemon.data.network.client.PokemonHttpClient
import com.giganticsheep.pokemon.data.network.client.PokemonImageHttpClient
import com.giganticsheep.pokemon.data.species.model.Species
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeciesEndpointManager @Inject constructor() {
    fun species(id: String) = "$species/$id"

    val species = "pokemon-species"

    val offset = "offset"
}

@Module
@InstallIn(SingletonComponent::class)
internal object SpeciesModule {

    @Provides
    @Singleton
    fun providesSpeciesClient(
        httpClient: PokemonHttpClient,
        httpImageClient: PokemonImageHttpClient,
        endpointManager: SpeciesEndpointManager,
    ): SpeciesApi = InternalSpeciesApi(
        httpClient,
        httpImageClient,
        endpointManager
    )
}

interface SpeciesApi {

    suspend fun getSpecies(page: Int = 0): List<Species>

    suspend fun getSpecies(id: String): Species

    suspend fun getSpeciesImageUrl(id: String): Url
}

internal class InternalSpeciesApi(
    private val httpClient: PokemonHttpClient,
    private val httpImageClient: PokemonImageHttpClient,
    private val endpointManager: SpeciesEndpointManager,
) : SpeciesApi {

    override suspend fun getSpecies(
        page: Int,
    ) = httpClient.get<List<Species>>(
        path = endpointManager.species,
        query = mapOf(endpointManager.offset to page.toString())
    )

    override suspend fun getSpecies(
        id: String,
    ) = httpClient.get<Species>(
        endpointManager.species(id)
    )

    override suspend fun getSpeciesImageUrl(
        id: String,
    ) = URLBuilder(httpImageClient.baseUrl)
        .appendPathSegments(id)
        .build()
}
