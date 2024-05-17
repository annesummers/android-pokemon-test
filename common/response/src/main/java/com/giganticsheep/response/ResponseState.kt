package com.giganticsheep.response

import com.giganticsheep.error.HandledException

sealed interface ResponseState {

    interface TerminalState : ResponseState {

        fun ignoreState(): Response
    }

    interface Empty : ResponseState

    interface Loading : ResponseState

    interface Success : TerminalState

    interface Error : TerminalState {
        val error: HandledException
    }
}

sealed interface CompletableResponseState : ResponseState {

    interface TerminalState : ResponseState.TerminalState {

        override fun ignoreState(): CompletableResponse
    }

    object Empty :
        CompletableResponseState,
        ResponseState.Empty {

        override suspend fun onSuccess(
            action: () -> Unit,
        ) = this

        override suspend fun onError(
            action: (HandledException) -> Unit,
        ) = this
    }

    object Loading :
        CompletableResponseState,
        ResponseState.Loading {

        override suspend fun onSuccess(
            action: () -> Unit,
        ) = this

        override suspend fun onError(
            action: (HandledException) -> Unit,
        ) = this
    }

    object Success :
        CompletableResponseState,
        TerminalState,
        ResponseState.Success {

        override suspend fun onSuccess(
            action: () -> Unit,
        ) = this.also { action() }

        override suspend fun onError(
            action: (HandledException) -> Unit,
        ) = this

        override fun ignoreState() = CompletableResponse.Success
    }

    class Error(
        override val error: HandledException,
    ) : CompletableResponseState,
        TerminalState,
        ResponseState.Error {

        override suspend fun onSuccess(
            action: () -> Unit,
        ) = this

        override suspend fun onError(
            action: (HandledException) -> Unit,
        ) = this.also { action(error) }

        override fun ignoreState() = CompletableResponse.Error(error)

        fun <E : HandledException> map(
            func: (HandledException) -> E,
        ): Error = Error(func(error))
    }

    suspend fun onSuccess(action: () -> Unit): CompletableResponseState

    suspend fun onError(action: (HandledException) -> Unit): CompletableResponseState
}

sealed interface DataResponseState<T : Any> : ResponseState {

    interface TerminalState<T : Any> : ResponseState.TerminalState {

        override fun ignoreState(): DataResponse<T>
    }

    class Empty<T : Any> :
        DataResponseState<T>,
        ResponseState.Empty {
        fun <R : Any> map(): Empty<R> = Empty()

        override suspend fun onSuccess(
            action: (T) -> Unit,
        ) = this

        override suspend fun onError(
            action: (HandledException) -> Unit,
        ) = this

        override fun asTerminalState(): TerminalState<T> = error("Not terminal")
    }

    class Loading<T : Any> :
        DataResponseState<T>,
        ResponseState.Loading {

        fun <R : Any> map(): Loading<R> = Loading()

        override suspend fun onSuccess(
            action: (T) -> Unit,
        ) = this

        override suspend fun onError(
            action: (HandledException) -> Unit,
        ) = this

        override fun asTerminalState(): TerminalState<T> = error("Not terminal")
    }

    class Data<T : Any>(
        val result: T,
    ) : DataResponseState<T>,
        TerminalState<T>,
        ResponseState.Success {

        fun <R : Any> map(
            func: (T) -> R,
        ): Data<R> = Data(func(result))

        override suspend fun onSuccess(
            action: (T) -> Unit,
        ) = this.also { action(result) }

        override suspend fun onError(
            action: (HandledException) -> Unit,
        ) = this

        override fun asTerminalState(): TerminalState<T> = this

        override fun ignoreState() = DataResponse.Success(result)
    }

    class Error<T : Any>(
        override val error: HandledException,
    ) : DataResponseState<T>,
        TerminalState<T>,
        ResponseState.Error {

        fun <R : Any, E : HandledException> map(
            func: (HandledException) -> E,
        ): Error<R> = Error(func(error))

        fun <R : Any> map(): Error<R> = Error(error)

        override suspend fun onSuccess(
            action: (T) -> Unit,
        ) = this

        override suspend fun onError(
            action: (HandledException) -> Unit,
        ) = this.also { action(error) }

        override fun asTerminalState(): TerminalState<T> = this

        override fun ignoreState() = DataResponse.Error<T>(error)
    }

    suspend fun onSuccess(action: (T) -> Unit): DataResponseState<T>

    suspend fun onError(action: (HandledException) -> Unit): DataResponseState<T>

    fun asTerminalState(): TerminalState<T>
}
