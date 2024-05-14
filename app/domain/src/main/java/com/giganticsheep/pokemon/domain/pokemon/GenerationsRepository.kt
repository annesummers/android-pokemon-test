package com.giganticsheep.pokemon.domain.pokemon

import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.data.generations.model.GenerationItem
import com.giganticsheep.pokemon.data.species.SpeciesApi
import com.giganticsheep.pokemon.data.species.model.Species
import com.giganticsheep.response.DataResponseState
import com.giganticsheep.response.dataCallFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

internal interface PokemonRepository {
/*
    val generations: Flow<DataResponseState<List<GenerationItem>>>

    fun fetchData()*/

    suspend fun clearData()

    suspend fun getSpecies(pokemonNumber: String): Flow<DataResponseState<Species>>
}

@Singleton
internal class InternalPokemonRepository @Inject constructor(
    private val pokemonClient: SpeciesApi,
    @BackgroundDispatcher dispatcher: CoroutineDispatcher,
) : PokemonRepository {

    private val scope = CoroutineScope(dispatcher)

    private val _generations = MutableSharedFlow<DataResponseState<List<GenerationItem>>>(1)

   /* override val generations: Flow<DataResponseState<List<GenerationItem>>> =
        _generations.asSharedFlow()
*/
    init {
        scope.launch {
            clearData()
        }
    }
/*
    override fun fetchData() {
        dataCallFlow { generationsClient.getGenerations() }
            .onEach { _generations.emit(it) }
            .launchIn(scope)
    }*/

    override suspend fun clearData() {
        _generations.emit(DataResponseState.Empty())
    }

    override suspend fun getSpecies(pokemonNumber: String) = dataCallFlow { pokemonClient.getSpecies(pokemonNumber) }
        .apply { launchIn(scope) }
}

