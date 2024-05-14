package com.giganticsheep.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal abstract class DisplayStateHandler<S : DisplayState>(
    backgroundContext: CoroutineContext,
    protected val onErrorDismissed: () -> Unit,
) {

    protected val scope = CoroutineScope(backgroundContext)

    protected abstract val _displayState: MutableSharedFlow<S>

    val displayState: Flow<S> by lazy { _displayState }

    abstract fun setLoading()

    abstract fun setError(
        title: String? = null,
        error: String,
        onDismissed: (() -> Unit)? = null,
    )
}

internal class DisplayDataStateHandler<T : Any>(
    backgroundContext: CoroutineContext,
    onErrorDismissed: () -> Unit,
) : DisplayStateHandler<DisplayDataState<T>>(
    backgroundContext,
    onErrorDismissed
) {

    override val _displayState = MutableSharedFlow<DisplayDataState<T>>(1)
        .apply {
            scope.launch {
                emit(DisplayDataState.Uninitialised())
            }
        }

    fun setDefault(data: T) = set(DisplayDataState.Default(data))

    override fun setLoading() = set(DisplayDataState.Loading())

    override fun setError(
        title: String?,
        error: String,
        onDismissed: (() -> Unit)?,
    ) = set(
        DisplayDataState.Error(
            title = title,
            error = error,
            onDismissed = onDismissed ?: onErrorDismissed,
        ),
    )

    fun set(displayState: DisplayDataState<T>) {
        scope.launch {
            _displayState.emit(displayState)
        }
    }
}

internal class DisplayScreenStateHandler(
    backgroundContext: CoroutineContext,
    onErrorDismissed: () -> Unit = {},
) : DisplayStateHandler<DisplayScreenState>(
    backgroundContext,
    onErrorDismissed,
) {
    override val _displayState = MutableSharedFlow<DisplayScreenState>(1)
        .apply {
            scope.launch {
                emit(DisplayScreenState.Uninitialised)
            }
        }

    fun setDefault() = set(DisplayScreenState.Default)

    override fun setLoading() = set(DisplayScreenState.Loading)

    override fun setError(
        title: String?,
        error: String,
        onDismissed: (() -> Unit)?,
    ) = set(
        DisplayScreenState.Error(
            title = title,
            error = error,
            onDismissed = onDismissed ?: onErrorDismissed,
        ),
    )

    fun set(displayState: DisplayScreenState) {
        scope.launch {
            _displayState.emit(displayState)
        }
    }
}