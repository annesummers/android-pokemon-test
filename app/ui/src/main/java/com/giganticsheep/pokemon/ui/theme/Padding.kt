package com.giganticsheep.pokemon.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


object Padding {
    val screenPaddingHorizontal = 12.dp
    val screenPaddingVertical = 12.dp

    val cardSpacing = 16.dp

    val headerSpacing = 20.dp

    val itemVerticalSpacing = 12.dp
    val itemHorizontalSpacing = 6.dp

    val itemPaddingHorizontal = 12.dp
    val itemPaddingVertical = 12.dp

    val cardPaddingHorizontal = 12.dp
    val cardPaddingVertical = 12.dp

    val dialogPaddingHorizontal = 24.dp
    val dialogPaddingVertical = 24.dp

    val dialogPadding = PaddingValues(
        horizontal = dialogPaddingHorizontal,
        vertical = dialogPaddingVertical,
    )

    val cardPadding = PaddingValues(
        horizontal = cardPaddingHorizontal,
        vertical = cardPaddingVertical,
    )

    val itemPadding = PaddingValues(
        horizontal = itemPaddingHorizontal,
        vertical = itemPaddingVertical,
    )
}

fun Modifier.screenPadding(scaffoldPaddingValues: PaddingValues) = this then padding(
    top = Padding.screenPaddingVertical + scaffoldPaddingValues.calculateTopPadding(),
    bottom = Padding.screenPaddingVertical + scaffoldPaddingValues.calculateBottomPadding(),
    start = Padding.screenPaddingHorizontal,
    end = Padding.screenPaddingHorizontal,
)
