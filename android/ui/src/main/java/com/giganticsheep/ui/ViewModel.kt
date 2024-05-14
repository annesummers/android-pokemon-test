package com.giganticsheep.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun ViewModel.launchWith(
    coroutineContext: CoroutineContext,
    block: suspend CoroutineScope.() -> Unit,
) = viewModelScope.launch(context = viewModelScope.coroutineContext + coroutineContext, block = block)

fun CoroutineScope.launchWithMainContext(
    block: suspend CoroutineScope.() -> Unit,
) = launch(context = Dispatchers.Main, block = block)
