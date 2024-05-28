package com.giganticsheep.pokemon.data.species

import com.giganticsheep.error.HandledException
import com.giganticsheep.pokemon.data.species.model.toPokemon
import com.giganticsheep.response.DataResponse
import com.giganticsheep.response.completableCall
import com.giganticsheep.response.dataCall
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

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
        getPokemon((Random.nextInt(speciesCount) + 1).toString())
    } else {
        DataResponse.Error(HandledException(internalMessage = "Not ready")) // this is a programmer error as setup should always be called before this
    }
}