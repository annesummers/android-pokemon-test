package com.giganticsheep.pokemon.ui.generation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import assertk.assertThat
import assertk.assertions.isTrue
import com.giganticsheep.pokemon.domain.generations.model.GenerationDisplay
import com.giganticsheep.ui.DisplayDataState
import org.junit.Rule
import org.junit.Test

internal class GenerationContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenGenerationContentWithDataStateDataThenShowGeneration() {
        val regionName = "name"
        val generationName = "generationName"
        val speciesName = "speciesName"
        val movesName = "name"

        composeTestRule.setContent {
            GenerationContent(
                generationState = DisplayDataState.Data(
                    GenerationDisplay(
                        id = 1,
                        region = regionName,
                        name = generationName,
                        species = listOf(speciesName),
                        moves = listOf(movesName),
                    ),
                ),
                onUpClicked = { },
                onMoveClicked = {},
                onSpeciesClicked = { },
            )
        }

        composeTestRule.onNodeWithText(generationName).assertIsDisplayed()
        composeTestRule.onNodeWithText("Region : $regionName").assertIsDisplayed()
        composeTestRule.onNodeWithText("Species").assertIsDisplayed()
        composeTestRule.onNodeWithText("Moves").assertIsDisplayed()
    }

    @Test
    fun whenGenerationContentWithDataStateErrorThenShowError() {
        val errorString = "error"

        composeTestRule.setContent {
            GenerationContent(
                generationState = DisplayDataState.Error(
                    errorString,
                    null,
                ) {},
                onUpClicked = { },
                onMoveClicked = {},
                onSpeciesClicked = { },
            )
        }

        composeTestRule.onNodeWithText(errorString).assertIsDisplayed()
    }

    @Test
    fun whenShowGenerationAndFirstSpeciesClickedThenAssertClicked() {
        var clicked = false
        val regionName = "name"
        val generationName = "generationName"
        val speciesName = "speciesName"
        val movesName = "name"

        composeTestRule.setContent {
            GenerationContent(
                generationState = DisplayDataState.Data(
                    GenerationDisplay(
                        id = 1,
                        region = regionName,
                        name = generationName,
                        species = listOf(speciesName),
                        moves = listOf(movesName),
                    ),
                ),
                onUpClicked = { },
                onMoveClicked = { },
                onSpeciesClicked = { clicked = true },
            )
        }

        composeTestRule.onNodeWithText("Species").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText(speciesName).assertIsDisplayed().performClick()

        assertThat(clicked)
            .isTrue()
    }

    @Test
    fun whenShowGenerationAndFirstMoveClickedThenAssertClicked() {
        var clicked = false
        val regionName = "name"
        val generationName = "generationName"
        val speciesName = "speciesName"
        val movesName = "name"

        composeTestRule.setContent {
            GenerationContent(
                generationState = DisplayDataState.Data(
                    GenerationDisplay(
                        id = 1,
                        region = regionName,
                        name = generationName,
                        species = listOf(speciesName),
                        moves = listOf(movesName),
                    ),
                ),
                onUpClicked = { },
                onMoveClicked = { clicked = true },
                onSpeciesClicked = { },
            )
        }

        composeTestRule.onNodeWithText("Moves").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText(movesName).assertIsDisplayed().performClick()

        assertThat(clicked)
            .isTrue()
    }

    @Test
    fun whenShowGenerationAndUpButtonClickedThenAssertIsClicked() {
        var clicked = false
        val regionName = "name"
        val generationName = "generationName"
        val speciesName = "speciesName"
        val movesName = "name"

        composeTestRule.setContent {
            GenerationContent(
                generationState = DisplayDataState.Data(
                    GenerationDisplay(
                        id = 1,
                        region = regionName,
                        name = generationName,
                        species = listOf(speciesName),
                        moves = listOf(movesName),
                    ),
                ),
                onUpClicked = { clicked = true },
                onMoveClicked = {},
                onSpeciesClicked = { },
            )
        }

        composeTestRule.onNodeWithContentDescription("go up").assertIsDisplayed().performClick()

        assertThat(clicked)
            .isTrue()
    }
}
