package com.giganticsheep.pokemon.ui.generations

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import assertk.assertThat
import assertk.assertions.isTrue
import com.giganticsheep.pokemon.domain.generations.model.GenerationItemDisplay
import com.giganticsheep.displaystate.DisplayDataState
import kotlinx.collections.immutable.toImmutableList
import org.junit.Rule
import org.junit.Test

internal class GenerationsContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenGenerationsContentWithDataStateDataThenShowGenerations() {
        val generationName = "name"

        composeTestRule.setContent {
            GenerationsContent(
                generationsState = com.giganticsheep.displaystate.DisplayDataState.Data(
                    listOf(GenerationItemDisplay(generationName)).toImmutableList(),
                ),
                onUpClicked = { },
                onGenerationClicked = { },
            )
        }

        composeTestRule.onNodeWithText("Generations").assertIsDisplayed()
        composeTestRule.onNodeWithText(generationName).assertIsDisplayed()
    }

    @Test
    fun whenGenerationsContentWithDataStateErrorThenShowError() {
        val errorString = "error"

        composeTestRule.setContent {
            GenerationsContent(
                generationsState = com.giganticsheep.displaystate.DisplayDataState.Error(
                    errorString,
                    null,
                ) {},
                onUpClicked = { },
                onGenerationClicked = { },
            )
        }

        composeTestRule.onNodeWithText(errorString).assertIsDisplayed()
    }

    @Test
    fun whenShowGenerationsAndFirstGenerationClickedThenAssertClicked() {
        var clicked = false
        val generationName = "name"

        composeTestRule.setContent {
            GenerationsContent(
                generationsState = com.giganticsheep.displaystate.DisplayDataState.Data(
                    listOf(GenerationItemDisplay(generationName)).toImmutableList(),
                ),
                onUpClicked = { },
                onGenerationClicked = { clicked = true },
            )
        }

        composeTestRule.onNodeWithText(generationName).assertIsDisplayed().performClick()

        assertThat(clicked)
            .isTrue()
    }

    @Test
    fun whenShowGenerationsAndUpButtonClickedThenAssertIsClicked() {
        var clicked = false
        val generationName = "name"

        composeTestRule.setContent {
            GenerationsContent(
                generationsState = com.giganticsheep.displaystate.DisplayDataState.Data(
                    listOf(GenerationItemDisplay(generationName)).toImmutableList(),
                ),
                onUpClicked = { clicked = true },
                onGenerationClicked = { },
            )
        }

        composeTestRule.onNodeWithContentDescription("go up").assertIsDisplayed().performClick()

        assertThat(clicked)
            .isTrue()
    }
}
