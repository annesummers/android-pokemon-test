package com.giganticsheep.pokemon.data.generations

import com.giganticsheep.pokemon.common.BackgroundDispatcher
import com.giganticsheep.pokemon.data.generations.model.GenerationItem
import com.giganticsheep.response.DataResponseState
import com.giganticsheep.response.completableCall
import com.giganticsheep.response.dataCall
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
            .onEach {
                when (it) {
                    is DataResponseState.Empty -> Unit
                    is DataResponseState.Error -> _generations.emit(it.map())
                    is DataResponseState.Loading -> _generations.emit(it.map())
                    is DataResponseState.Data -> _generations.emit(it.map { state -> state.results })
                }
            }
            .launchIn(scope)
    }

    override suspend fun clearData() = completableCall {
        _generations.emit(DataResponseState.Empty())
    }

    override suspend fun getGeneration(
        generationName: String,
    ) = dataCall { generationsApi.getGeneration(generationName) }
}