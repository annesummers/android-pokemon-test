package com.giganticsheep.ui

import com.giganticsheep.android.ui.BuildConfig
import com.giganticsheep.error.HandledException
import com.giganticsheep.response.CompletableResponse
import com.giganticsheep.response.CompletableResponseState
import com.giganticsheep.response.DataResponse
import com.giganticsheep.response.DataResponseState
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

interface DisplayStateProvider<S : DisplayState> {
    val displayState: Flow<S>

    fun showLoading()

    fun showError(
        error: Throwable,
        onDismissed: (() -> Unit)? = null,
    )

    fun showError(
        title: String? = null,
        error: String,
        onDismissed: (() -> Unit)? = null,
    )

    fun onErrorDismissed()
}

interface DisplayScreenStateProvider : DisplayStateProvider<DisplayScreenState> {

    fun showDefault()

    fun showResult(response: CompletableResponse)

    fun showResult(responseState: CompletableResponseState)
}

interface DisplayDataStateProvider<T : Any, U : Any> : DisplayStateProvider<DisplayDataState<U>> {

    fun showDefault(data: U)

    fun showResult(response: DataResponse<T>, map: (T) -> U)

    fun showResult(responseState: DataResponseState<T>, map: (T) -> U)
}

class DisplayScreenStateProvided(
    backgroundContext: CoroutineContext,
) : DisplayStateProvided<DisplayScreenState>(),
    DisplayScreenStateProvider {

    override val screenStateHandler = DisplayScreenStateHandler(backgroundContext)

    override val displayState: Flow<DisplayScreenState> = screenStateHandler.displayState

    override fun onErrorDismissed() = showDefault()

    override fun showDefault() = screenStateHandler.setDefault()

    override fun showResult(response: CompletableResponse) =
        screenStateHandler.set(response.toDisplayState())

    override fun showResult(responseState: CompletableResponseState) =
        screenStateHandler.set(responseState.toDisplayState())
}

class DisplayDataStateProvided<T : Any, U : Any>(
    backgroundContext: CoroutineContext,
    onErrorDismissed: () -> Unit = {},
) : DisplayStateProvided<DisplayDataState<U>>(onErrorDismissed),
    DisplayDataStateProvider<T, U> {

    override val screenStateHandler = DisplayDataStateHandler<U>(backgroundContext, onErrorDismissed)

    override val displayState: Flow<DisplayDataState<U>> = screenStateHandler.displayState

    override fun showDefault(data: U) = screenStateHandler.setDefault(data)

    override fun showResult(response: DataResponse<T>, map: (T) -> U) =
        screenStateHandler.set(
            response.toDisplayState(
                map = map,
                onErrorDismissed = ::onErrorDismissed,
            ),
        )

    override fun showResult(responseState: DataResponseState<T>, map: (T) -> U) =
        screenStateHandler.set(
            responseState.toDisplayState(
                map = map,
                onErrorDismissed = ::onErrorDismissed,
            ),
        )
}

abstract class DisplayStateProvided<S : DisplayState> internal constructor(
    private val _onErrorDismissed: () -> Unit = {},
) : DisplayStateProvider<S> {

    internal abstract val screenStateHandler: DisplayStateHandler<*>

    override fun showLoading() = screenStateHandler.setLoading()

    override fun showError(
        error: Throwable,
        onDismissed: (() -> Unit)?,
    ) = screenStateHandler.setError(
        null,
        if (BuildConfig.DEBUG && error is HandledException) {
            error.internalMessage
        } else if (error.message.isNullOrBlank()) {
            "An error has occurred"
        } else {
            error.message!!
        }, // TODO default error message
        onDismissed ?: ::onErrorDismissed,
    )

    override fun showError(
        title: String?,
        error: String,
        onDismissed: (() -> Unit)?,
    ) = screenStateHandler.setError(
        title,
        error,
        onDismissed ?: ::onErrorDismissed,
    )

    override fun onErrorDismissed() = _onErrorDismissed()
}
