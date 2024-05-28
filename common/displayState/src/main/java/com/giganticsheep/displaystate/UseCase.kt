package com.giganticsheep.displaystate

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class ConditionalDataUseCase<T : Any>(
    dispatcher: CoroutineDispatcher,
    private val initialProvider: DisplayStateProvider<DisplayScreenState>,
    provider: DisplayStateProvider<DisplayDataState<T>>
) {
    private lateinit var initialJob: Job
    private val scope = CoroutineScope(dispatcher)

    val displayState: Flow<DisplayDataState<T>> = initialProvider.displayState.combine(
        provider.displayState
    ) { screenState, dataState -> screenState.mapToDataState(dataState) }

    protected fun onInitialProviderDefault(action: suspend () -> Unit) {
        initialJob = initialProvider.displayState
            .onEach {
                when (it) {
                    is DisplayScreenState.Default -> action()
                    else -> Unit
                }
            }
            .launchIn(scope)
    }
}

abstract class DataUseCase<T : Any>(
   provider: DisplayStateProvider<DisplayDataState<T>>
) {

    val displayState: Flow<DisplayDataState<T>> = provider.displayState
}

abstract class CompletableUseCase(
    provider: DisplayStateProvider<DisplayScreenState>
) {

    val displayState: Flow<DisplayScreenState> = provider.displayState
}