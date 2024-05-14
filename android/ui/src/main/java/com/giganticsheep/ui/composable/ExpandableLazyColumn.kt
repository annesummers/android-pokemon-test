package com.giganticsheep.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Modifier

@Composable
private fun Header(
    modifier: Modifier,
    text: String,
    isExpanded: Boolean,
    onHeaderClicked: () -> Unit,
) {
    Row(modifier = modifier.clickable { onHeaderClicked() }) {
        Text(text = text)
        if (isExpanded)
            Icon(Icons.Filled.ExpandMore, contentDescription = "expand")
        else Icon(Icons.Filled.ExpandLess, contentDescription = "collapse")
    }
}

@Composable
private fun Item(
    modifier: Modifier,
    text: String,
    onItemClick: (String) -> Unit,
) {
    Text(
        modifier = modifier.clickable(onClick = { onItemClick(text) }),
        text = text,
    )
}

data class NestedList(
    val headerText: String,
    val items: List<String>,
    val onItemClick: (String) -> Unit,
)

@Composable
fun ExpandableNestedList(
    headerModifier: Modifier,
    itemModifier: Modifier,
    items: List<NestedList>,
) {
    val isExpandedMap = remember {
        List(items.size) { index: Int -> index to true }
            .toMutableStateMap() // TODO
    }

    LazyColumn(
        content = {
            items.onEachIndexed { index, sectionData ->
                sublist(
                    headerModifier = headerModifier,
                    itemModifier = itemModifier,
                    sectionData = sectionData,
                    isExpanded = isExpandedMap[index] ?: true,
                    onHeaderClick = { isExpandedMap[index] = !(isExpandedMap[index] ?: true) },
                    onItemClick = sectionData.onItemClick
                )
            }
        }
    )
}

fun LazyListScope.sublist(
    headerModifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
    sectionData: NestedList,
    isExpanded: Boolean,
    onHeaderClick: () -> Unit,
    onItemClick: (String) -> Unit,
) {
    item {
        Header(
            modifier = headerModifier,
            text = sectionData.headerText,
            isExpanded = isExpanded,
            onHeaderClicked = onHeaderClick
        )
    }

    if (isExpanded) {
        items(sectionData.items) {
            Item(modifier = itemModifier, text = it, onItemClick = onItemClick)
        }
    }
}