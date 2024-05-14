package com.giganticsheep.response

import com.giganticsheep.error.HandledException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.cancellation.CancellationException

fun completableCallFlow(
    call: suspend () -> Unit,
): Flow<CompletableResponseState> = flow {
    emit(CompletableResponseState.Loading)

    emit(completableCall(call).toState())
}

inline fun <reified R : Any> dataCallFlow(
    crossinline call: suspend () -> R,
): Flow<DataResponseState<R>> = flow {
    emit(DataResponseState.Loading())

    emit(dataCall(call).toState())
}

suspend fun completableCall(
    call: suspend () -> Unit,
) = try {
    call()

    CompletableResponse.Success
} catch (e: HandledException) {
    CompletableResponse.Error(e)
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    throw RuntimeException(e)
}

suspend inline fun <reified R : Any> dataCall(
    crossinline call: suspend () -> R,
): DataResponse<R> = try {
    DataResponse.Success(call())
} catch (e: HandledException) {
    DataResponse.Error(e)
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    throw RuntimeException(e)
}
