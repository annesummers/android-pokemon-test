package com.giganticsheep.pokemon.domain.pokemon

import com.giganticsheep.error.HandledException
import com.giganticsheep.pokemon.data.species.SpeciesApi
import com.giganticsheep.pokemon.data.species.model.Species
import com.giganticsheep.pokemon.domain.pokemon.Pokemon.Companion.LANGUAGE
import com.giganticsheep.response.CompletableResponse
import com.giganticsheep.response.DataResponse
import com.giganticsheep.response.completableCall
import com.giganticsheep.response.dataCall
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

fun Species.toPokemon(imageUrl: String) = Pokemon(
    internalName = internalName,
    id = id,
    imageUrl = imageUrl,
    name = names.find { it.language.name == LANGUAGE }?.name ?: internalName,
    descriptions = descriptions.filter { it.language.name == LANGUAGE }.map { it.description },
)

data class Pokemon(
    val internalName: String,
    val id: Int,
    val imageUrl: String,
    val name: String,
    val descriptions: List<String>,
) {
    companion object {
        internal const val LANGUAGE = "en"
    }
}

internal interface PokemonRepository {

    suspend fun setup(): CompletableResponse

    suspend fun getPokemon(nameOrId: String): DataResponse<Pokemon>

    suspend fun getRandomPokemon(): DataResponse<Pokemon>
}

@Singleton
internal class InternalPokemonRepository @Inject constructor(
    private val speciesApi: SpeciesApi,
) : PokemonRepository {

    private var speciesCount: Int = 0

    override suspend fun setup() = if (speciesCount == 0) {
        dataCall { speciesApi.getSpecies(0, 0) }
            .doOnSuccess { response -> speciesCount = response.result.count }
            .ignoreResult()
    } else {
        completableCall { }
    }

    override suspend fun getPokemon(
        nameOrId: String,
    ) = dataCall { speciesApi.getSpecies(nameOrId) }
        .flatMap { species ->
            dataCall { speciesApi.getSpeciesImageUrl(species.id) }
                .flatMap { DataResponse.Success(species.toPokemon(it)) }
        }

    override suspend fun getRandomPokemon() = if (speciesCount > 0) {
        getPokemon((Random.Default.nextInt(speciesCount) + 1).toString())
    } else {
        DataResponse.Error(HandledException(internalMessage = "Not ready")) // this is a programmer error as setup should always be called before this
    }
}
