package com.giganticsheep.displaystate

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

sealed interface DisplayScreenState : DisplayState {

    sealed interface MappableScreenState : DisplayScreenState {

        fun <T : Any> map(): DisplayDataState<T>
    }

    object Uninitialised :
        MappableScreenState,
        DisplayState.Uninitialised {

        override fun <T : Any> map() = DisplayDataState.Uninitialised<T>()
    }

    object Loading :
        MappableScreenState,
        DisplayState.Loading {

        override fun <T : Any> map() = DisplayDataState.Loading<T>()
    }

    data class Error(
        override val error: String,
        override val title: String?,
        override val onDismissed: () -> Unit,
    ) : MappableScreenState,
        DisplayState.Error {

        override fun <T : Any> map() = DisplayDataState.Error<T>(
            error = error,
            title = title,
            onDismissed = onDismissed
        )
    }

    object Default :
        DisplayScreenState,
        DisplayState.Default

    fun <T : Any> mapToDataState(dataState: DisplayDataState<T>) = when (this) {
        Default -> dataState
        is MappableScreenState -> map()
    }

}

sealed class DisplayDataState<T : Any> : DisplayState {

    class Uninitialised<T : Any> :
        DisplayDataState<T>(),
        DisplayState.Uninitialised

    class Loading<T : Any> :
        DisplayDataState<T>(),
        DisplayState.Loading

    data class Data<T : Any>(
        val data: T,
    ) : DisplayDataState<T>(),
        DisplayState.Default

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
        title = null, // TODO
        onDismissed = onErrorDismissed,
    )

    is DataResponse.Success<T> -> DisplayDataState.Data(
        data = map(
            result
        )
    )
}

fun <T : Any, U : Any> DataResponseState<T>.toDisplayState(
    onErrorDismissed: () -> Unit,
    map: (T) -> U,
): DisplayDataState<U> = when (this) {
    is DataResponseState.Error<T> -> DisplayDataState.Error(
        error = error.internalMessage,
        title = null, // TODO
        onDismissed = onErrorDismissed,
    )

    is DataResponseState.Data<T> -> DisplayDataState.Data(
        data = map(
            result
        )
    )

    is DataResponseState.Loading<T> -> DisplayDataState.Loading()
    is DataResponseState.Empty<T> -> DisplayDataState.Uninitialised()
}

fun CompletableResponse.toDisplayState(onErrorDismissed: () -> Unit = {}): DisplayScreenState =
    when (this) {
        is CompletableResponse.Error -> DisplayScreenState.Error(
            error = error.internalMessage,
            title = null, // TODO
            onDismissed = onErrorDismissed,
        )

        CompletableResponse.Success -> DisplayScreenState.Default
    }

fun CompletableResponseState.toDisplayState(onErrorDismissed: () -> Unit = {}): DisplayScreenState =
    when (this) {
        is CompletableResponseState.Error -> DisplayScreenState.Error(
            error = error.internalMessage,
            title = null, // TODO
            onDismissed = onErrorDismissed,
        )

        CompletableResponseState.Loading -> DisplayScreenState.Loading
        CompletableResponseState.Success -> DisplayScreenState.Default
        CompletableResponseState.Empty -> DisplayScreenState.Uninitialised
    }
