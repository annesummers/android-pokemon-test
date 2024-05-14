package com.giganticsheep.ui

import com.giganticsheep.response.CompletableResponse
import com.giganticsheep.response.CompletableResponseState
import com.giganticsheep.response.DataResponse
import com.giganticsheep.response.DataResponseState

sealed interface DisplayState {

    interface Uninitialised : DisplayState

    interface Loading : DisplayState

    interface Default : DisplayState

    interface Error : DisplayState {
        val error: String
        val title: String?
        val onDismissed: () -> Unit
    }
}

sealed class DisplayScreenState : DisplayState {

    object Uninitialised :
        DisplayScreenState(),
        DisplayState.Loading

    object Loading :
        DisplayScreenState(),
        DisplayState.Loading

    object Default :
        DisplayScreenState(),
        DisplayState.Default

    data class Error(
        override val error: String,
        override val title: String?,
        override val onDismissed: () -> Unit,
    ) : DisplayScreenState(),
        DisplayState.Error
}

sealed class DisplayDataState<T : Any> : DisplayState {

    class Uninitialised<T : Any> :
        DisplayDataState<T>(),
        DisplayState.Uninitialised

    class Loading<T : Any> :
        DisplayDataState<T>(),
        DisplayState.Loading

    class Default<T : Any>(
        val data: T,
    ) : DisplayDataState<T>(), DisplayState.Default

    data class Error<T : Any>(
        override val error: String,
        override val title: String?,
        override val onDismissed: () -> Unit,
    ) : DisplayDataState<T>(),
        DisplayState.Error
}


fun <T : Any, U : Any> DataResponse<T>.toDisplayState(
    onErrorDismissed: () -> Unit,
    map: (T) -> U,
): DisplayDataState<U> = when (this) {
    is DataResponse.Error<T> -> DisplayDataState.Error(
        error = error.internalMessage,
        title = null,// TODO
        onDismissed = onErrorDismissed,
    )

    is DataResponse.Success<T> -> DisplayDataState.Default(data = map(result))
}

fun <T : Any, U : Any> DataResponseState<T>.toDisplayState(
    onErrorDismissed: () -> Unit,
    map: (T) -> U,
): DisplayDataState<U> = when (this) {
    is DataResponseState.Error<T> -> DisplayDataState.Error(
        error = error.internalMessage,
        title = null,// TODO
        onDismissed = onErrorDismissed,
    )

    is DataResponseState.Success<T> -> DisplayDataState.Default(data = map(result))

    is DataResponseState.Loading<T> -> DisplayDataState.Loading()
    is DataResponseState.Empty<T> -> DisplayDataState.Uninitialised()
}

fun CompletableResponse.toDisplayState(onErrorDismissed: () -> Unit = {}): DisplayScreenState =
    when (this) {
        is CompletableResponse.Error -> DisplayScreenState.Error(
            error = error.internalMessage,
            title = null,// TODO
            onDismissed = onErrorDismissed,
        )

        CompletableResponse.Success -> DisplayScreenState.Default
    }

fun CompletableResponseState.toDisplayState(onErrorDismissed: () -> Unit = {}): DisplayScreenState =
    when (this) {
        is CompletableResponseState.Error -> DisplayScreenState.Error(
            error = error.internalMessage,
            title = null,// TODO
            onDismissed = onErrorDismissed,
        )

        CompletableResponseState.Loading -> DisplayScreenState.Loading
        CompletableResponseState.Success -> DisplayScreenState.Default
        CompletableResponseState.Empty -> DisplayScreenState.Uninitialised
    }