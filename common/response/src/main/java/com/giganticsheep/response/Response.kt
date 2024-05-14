package com.giganticsheep.response

import com.giganticsheep.error.HandledException

sealed interface Response {

    interface Success : Response

    interface Error : Response {
        val error: HandledException
    }

    fun toState(): ResponseState
}

sealed interface DataResponse<T : Any> : Response {

    class Success<T : Any>(
        val result: T,
    ) : DataResponse<T>,
        Response.Success {

        fun <R : Any> map(
            func: (T) -> R,
        ): Success<R> = Success(func(result))

        override suspend fun onSuccess(
            action: suspend (T) -> Unit,
        ) = this.also { action(result) }

        override suspend fun doOnSuccess(
            action: suspend (Success<T>) -> Unit,
        ) = this.also { action(this) }

        override suspend fun doOnError(
            action: suspend (Error<T>) -> Unit,
        ) = this

        override suspend fun onError(
            action: suspend (HandledException) -> Unit,
        ) = this

        override suspend fun flatMapError(
            action: suspend (Error<T>) -> DataResponse<T>,
        ) = this

        override fun ignoreResult() = CompletableResponse.Success

        override fun toState() = DataResponseState.Success(result)

        override suspend fun <U : Any> flatMap(
            next: suspend (T) -> DataResponse<U>,
        ): DataResponse<U> = next(result)

        override suspend fun flatMapCompletable(
            next: suspend (T) -> CompletableResponse,
        ): CompletableResponse = next(result)
    }

    class Error<T : Any>(
        override val error: HandledException,
    ) : DataResponse<T>,
        Response.Error {

        fun <R : Any, E : HandledException> map(
            func: (HandledException) -> E,
        ): Error<R> = Error(func(error))

        fun <R : Any> map(): Error<R> = Error(error)

        override suspend fun onSuccess(
            action: suspend (T) -> Unit,
        ) = this

        override suspend fun doOnSuccess(
            action: suspend (Success<T>) -> Unit,
        ) = this

        override suspend fun doOnError(
            action: suspend (Error<T>) -> Unit,
        ) = this.also { action(this) }

        override suspend fun onError(
            action: suspend (HandledException) -> Unit,
        ) = this.also { action(error) }

        override suspend fun flatMapError(
            action: suspend (Error<T>) -> DataResponse<T>,
        ) = action(this)

        override fun ignoreResult() = CompletableResponse.Error(error)

        override fun toState() = DataResponseState.Error<T>(error)

        override suspend fun <U : Any> flatMap(
            next: suspend (T) -> DataResponse<U>,
        ): DataResponse<U> = map()

        override suspend fun flatMapCompletable(
            next: suspend (T) -> CompletableResponse,
        ): CompletableResponse = ignoreResult()
    }

    suspend fun onSuccess(action: suspend (T) -> Unit): DataResponse<T>

    suspend fun onError(action: suspend (HandledException) -> Unit): DataResponse<T>

    suspend fun doOnSuccess(action: suspend (Success<T>) -> Unit): DataResponse<T>

    suspend fun doOnError(action: suspend (Error<T>) -> Unit): DataResponse<T>

    suspend fun flatMapError(action: suspend (Error<T>) -> DataResponse<T>): DataResponse<T>

    fun ignoreResult(): CompletableResponse

    override fun toState(): DataResponseState<T>

    suspend fun <U : Any> flatMap(next: suspend (T) -> DataResponse<U>): DataResponse<U>

    suspend fun flatMapCompletable(next: suspend (T) -> CompletableResponse): CompletableResponse
}

sealed interface CompletableResponse : Response {

    object Success :
        CompletableResponse,
        Response.Success {
        override fun toState() = CompletableResponseState.Success

        override suspend fun onSuccess(
            action: () -> Unit,
        ) = this.also { action() }

        override suspend fun onError(
            action: (HandledException) -> Unit,
        ) = this

        override suspend fun doOnSuccess(
            action: suspend (Success) -> Unit,
        ) = this.also { action(this) }

        override suspend fun doOnError(
            action: suspend (Error) -> Unit,
        ) = this

        override suspend fun onErrorFlatMap(
            action: suspend (Error) -> CompletableResponse,
        ) = this

        override suspend fun <T : Any> flatMapData(
            next: suspend () -> DataResponse<T>,
        ): DataResponse<T> = next()

        override suspend fun flatMap(
            next: suspend () -> CompletableResponse,
        ): CompletableResponse = next()
    }

    class Error(
        override val error: HandledException,
    ) : CompletableResponse,
        Response.Error {

        fun <E : HandledException> map(
            func: (HandledException) -> E,
        ): Error = Error(func(error))

        fun <T : Any> map() = DataResponse.Error<T>(error)

        override fun toState() = CompletableResponseState.Error(error)

        override suspend fun onSuccess(
            action: () -> Unit,
        ) = this

        override suspend fun doOnSuccess(
            action: suspend (Success) -> Unit,
        ) = this

        override suspend fun onError(
            action: (HandledException) -> Unit,
        ) = this.also { action(error) }

        override suspend fun doOnError(
            action: suspend (Error) -> Unit,
        ) = this.also { action(this) }

        override suspend fun onErrorFlatMap(
            action: suspend (Error) -> CompletableResponse,
        ) = action(this)

        override suspend fun <T : Any> flatMapData(
            next: suspend () -> DataResponse<T>,
        ): DataResponse<T> = map()

        override suspend fun flatMap(
            next: suspend () -> CompletableResponse,
        ): CompletableResponse = this
    }

    suspend fun onSuccess(action: () -> Unit): CompletableResponse

    suspend fun onError(action: (HandledException) -> Unit): CompletableResponse

    suspend fun doOnSuccess(action: suspend (Success) -> Unit): CompletableResponse

    suspend fun doOnError(action: suspend (Error) -> Unit): CompletableResponse

    override fun toState(): CompletableResponseState

    suspend fun flatMap(next: suspend () -> CompletableResponse): CompletableResponse

    suspend fun <T : Any> flatMapData(next: suspend () -> DataResponse<T>): DataResponse<T>

    suspend fun onErrorFlatMap(action: suspend (Error) -> CompletableResponse): CompletableResponse
}
