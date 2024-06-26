package com.giganticsheep.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.giganticsheep.android.ui.R
import com.giganticsheep.displaystate.DisplayDataState
import com.giganticsheep.displaystate.DisplayScreenState
import com.giganticsheep.displaystate.DisplayState
import kotlinx.coroutines.flow.Flow

@Composable
fun Flow<DisplayScreenState>.collectDisplayScreenStateAsState(): State<DisplayScreenState> =
    collectAsState(initial = DisplayScreenState.Uninitialised)

@Composable
fun <T : Any> Flow<DisplayDataState<T>>.collectDisplayDataStateAsState(): State<DisplayDataState<T>> =
    collectAsState(initial = DisplayDataState.Uninitialised())

@Composable
fun <T : Any> ColumnScope.HandleDisplayState(
    displayState: DisplayDataState<T>,
    onLoading: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1F),
            contentAlignment = Alignment.Center,
        ) { ShowLoading() }
    },
    onError: @Composable (String, String?, () -> Unit) -> Unit = { error, title, onDismissed ->
        ShowError(
            error = error,
            title = title,
            onErrorDismissed = onDismissed,
        )
    },
    content: @Composable ColumnScope.(T) -> Unit,
) {
    when (displayState) {
        is DisplayState.Default -> content((displayState as DisplayDataState.Data).data)
        is DisplayState.Error -> onError(
            displayState.error,
            displayState.title,
            displayState.onDismissed,
        )

        is DisplayState.Loading -> onLoading()

        else -> Unit
    }
}

@Composable
fun <T : Any> BoxScope.HandleDisplayState(
    displayState: DisplayDataState<T>,
    onLoading: @Composable () -> Unit = { ShowLoading() },
    onError: @Composable (String, String?, () -> Unit) -> Unit = { error, title, onDismissed ->
        ShowError(
            error = error,
            title = title,
            onErrorDismissed = onDismissed,
        )
    },
    content: @Composable BoxScope.(T) -> Unit,
) {
    when (displayState) {
        is DisplayState.Default -> content((displayState as DisplayDataState.Data).data)
        is DisplayState.Error -> onError(
            displayState.error,
            displayState.title,
            displayState.onDismissed,
        )

        is DisplayState.Loading -> onLoading()

        else -> Unit
    }
}

@Composable
fun <T : Any> HandleDisplayState(
    displayState: DisplayDataState<T>,
    onLoading: @Composable () -> Unit = { ShowLoading() },
    onError: @Composable (String, String?, () -> Unit) -> Unit = { error, title, onDismissed ->
        ShowError(
            error = error,
            title = title,
            onErrorDismissed = onDismissed,
        )
    },
    content: @Composable (T) -> Unit,
) {
    when (displayState) {
        is DisplayState.Default -> content((displayState as DisplayDataState.Data).data)
        is DisplayState.Error -> onError(
            displayState.error,
            displayState.title,
            displayState.onDismissed,
        )

        is DisplayState.Loading -> onLoading()

        else -> Unit
    }
}

@Composable
fun HandleDisplayState(
    displayState: DisplayScreenState,
    onLoading: @Composable () -> Unit = { ShowLoading(Modifier.fillMaxSize()) },
    onError: @Composable (String, String?, () -> Unit) -> Unit = { error, title, onDismissed ->
        ShowError(
            error = error,
            title = title,
            onErrorDismissed = onDismissed,
        )
    },
    content: @Composable () -> Unit,
) {
    when (displayState) {
        is DisplayState.Default -> content()
        is DisplayState.Error -> onError(
            displayState.error,
            displayState.title,
            displayState.onDismissed,
        )

        is DisplayState.Loading -> onLoading()

        else -> Unit
    }
}

@Composable
private fun ShowLoading(modifier: Modifier = Modifier) {
    val loadingString = stringResource(R.string.loading_description)

    Box(
        modifier = modifier
            .semantics { contentDescription = loadingString },
        contentAlignment = Alignment.Center,
    ) { CircularProgressIndicator() }
}

@Composable
private fun ShowError(
    error: String,
    title: String?,
    onErrorDismissed: () -> Unit,
) {
    var shown by remember { mutableStateOf(true) }

    if (shown) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.background,
            onDismissRequest = {
                onErrorDismissed()
                shown = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onErrorDismissed()
                        shown = false
                    },
                ) {
                    Text(
                        text = stringResource(R.string.error_ok),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            title = title?.let { { Text(text = it) } },
            text = { Text(text = error) },
        )
    }
}
