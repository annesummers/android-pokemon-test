package com.giganticsheep.ui

import androidx.lifecycle.SavedStateHandle

interface SavedStateData {
    val key: String
}

class StateFlowUtils(
    private val savedStateHandle: SavedStateHandle,
) {
    operator fun <T : Any> get(
        data: SavedStateData,
        initialValue: T,
    ) = savedStateHandle.getStateFlow(data.key, initialValue)

    operator fun <T : Any> set(data: SavedStateData, value: T) {
        savedStateHandle[data.key] = value
    }
}
