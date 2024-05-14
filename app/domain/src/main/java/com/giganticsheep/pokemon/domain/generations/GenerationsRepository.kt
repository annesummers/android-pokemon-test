package com.giganticsheep.pokemon.domain.generations

import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.data.generations.GenerationsApi
import com.giganticsheep.pokemon.data.generations.model.Generation
import com.giganticsheep.pokemon.data.generations.model.GenerationItem
import com.giganticsheep.response.DataResponseState
import com.giganticsheep.response.dataCallFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

internal interface GenerationsRepository {

    val generations: Flow<DataResponseState<List<GenerationItem>>>

    fun fetchGenerations()

    suspend fun clearData()

    suspend fun getGeneration(generationName: String): Flow<DataResponseState<Generation>>
}

@Singleton
internal class InternalGenerationsRepository @Inject constructor(
    private val generationsApi: GenerationsApi,
    @BackgroundDispatcher dispatcher: CoroutineDispatcher,
) : GenerationsRepository {

    private val scope = CoroutineScope(dispatcher)

    private val _generations = MutableSharedFlow<DataResponseState<List<GenerationItem>>>(1)

    override val generations: Flow<DataResponseState<List<GenerationItem>>> =
        _generations.asSharedFlow()

    init {
        scope.launch {
            clearData()
        }
    }

    override fun fetchGenerations() {
        dataCallFlow { generationsApi.getGenerations() }
            .onEach { _generations.emit(it) }
            .launchIn(scope)
    }

    override suspend fun clearData() {
        _generations.emit(DataResponseState.Empty())
    }

    override suspend fun getGeneration(
        generationName: String,
    ) = dataCallFlow { generationsApi.getGeneration(generationName) }
        .apply { launchIn(scope) }
}

