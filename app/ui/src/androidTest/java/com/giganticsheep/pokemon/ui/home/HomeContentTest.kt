package com.giganticsheep.pokemon.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import assertk.assertThat
import assertk.assertions.isTrue
import com.giganticsheep.pokemon.domain.pokemon.model.PokemonDisplay
import com.giganticsheep.displaystate.DisplayDataState
import com.giganticsheep.displaystate.DisplayScreenState
import org.junit.Rule
import org.junit.Test

internal class HomeContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenHomeContentWithScreenStateDefaultAndDataStateDataThenShowPokemonAndButton() {
        val pokemonName = "name"

        composeTestRule.setContent {
            HomeContent(
                displayState = com.giganticsheep.displaystate.DisplayScreenState.Default,
                randomPokemonState = com.giganticsheep.displaystate.DisplayDataState.Data(
                    PokemonDisplay(
                        1,
                        pokemonName,
                        listOf(),
                        "url",
                    ),
                ),
                onPokemonClicked = { },
                onGenerationsClicked = { },
                generateNewPokemon = { },
            )
        }

        composeTestRule.onNodeWithText("Pokemon Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Show generations").assertIsDisplayed()
        composeTestRule.onNodeWithText(pokemonName).assertIsDisplayed()
    }

    @Test
    fun whenHomeContentWithScreenStateErrorThenShowError() {
        var dismissed = false
        val errorString = "error"

        composeTestRule.setContent {
            HomeContent(
                displayState = com.giganticsheep.displaystate.DisplayScreenState.Error(errorString, null) {
                    dismissed = true
                },
                randomPokemonState = com.giganticsheep.displaystate.DisplayDataState.Data(
                    PokemonDisplay(
                        1,
                        "name",
                        listOf(),
                        "url",
                    ),
                ),
                onPokemonClicked = { },
                onGenerationsClicked = { },
                generateNewPokemon = { },
            )
        }

        composeTestRule.onNodeWithText("Pokemon Home").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Show generations").assertIsNotDisplayed()
        composeTestRule.onNodeWithText(errorString).assertIsDisplayed()
        composeTestRule.onNodeWithText("Ok").assertIsDisplayed().performClick()

        assertThat(dismissed)
            .isTrue()
    }

    @Test
    fun whenHomeContentWithScreenStateDefaultAndDataStateErrorThenShowPokemonErrorAndButton() {
        val errorString = "error"

        composeTestRule.setContent {
            HomeContent(
                displayState = com.giganticsheep.displaystate.DisplayScreenState.Default,
                randomPokemonState = com.giganticsheep.displaystate.DisplayDataState.Error(errorString, null) { },
                onPokemonClicked = { },
                onGenerationsClicked = { },
                generateNewPokemon = { },
            )
        }

        composeTestRule.onNodeWithText("Pokemon Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Show generations").assertIsDisplayed()
        composeTestRule.onNodeWithText(errorString).assertIsDisplayed()
    }

    @Test
    fun whenShowGenerationsButtonAndButtonClickedThenAssertIsClicked() {
        var clicked = false
        val pokemonName = "name"

        composeTestRule.setContent {
            HomeContent(
                displayState = com.giganticsheep.displaystate.DisplayScreenState.Default,
                randomPokemonState = com.giganticsheep.displaystate.DisplayDataState.Data(
                    PokemonDisplay(
                        1,
                        pokemonName,
                        listOf(),
                        "url",
                    ),
                ),
                onPokemonClicked = { },
                onGenerationsClicked = { clicked = true },
                generateNewPokemon = { },
            )
        }

        composeTestRule.onNodeWithText("Show generations").assertIsDisplayed().performClick()

        assertThat(clicked)
            .isTrue()
    }

    @Test
    fun whenShowPokemonAndGenerateButtonClickedThenAssertIsClicked() {
        var clicked = false
        val pokemonName = "name"

        composeTestRule.setContent {
            HomeContent(
                displayState = com.giganticsheep.displaystate.DisplayScreenState.Default,
                randomPokemonState = com.giganticsheep.displaystate.DisplayDataState.Data(
                    PokemonDisplay(
                        1,
                        pokemonName,
                        listOf(),
                        "url",
                    ),
                ),
                onPokemonClicked = { },
                onGenerationsClicked = { },
                generateNewPokemon = { clicked = true },
            )
        }

        composeTestRule.onNodeWithText("Random Pokemon").assertIsDisplayed().performClick()

        assertThat(clicked)
            .isTrue()
    }

    @Test
    fun whenShowPokemonAndPokemonClickedThenAssertIsClicked() {
        var clicked = false
        val pokemonName = "name"

        composeTestRule.setContent {
            HomeContent(
                displayState = com.giganticsheep.displaystate.DisplayScreenState.Default,
                randomPokemonState = com.giganticsheep.displaystate.DisplayDataState.Data(
                    PokemonDisplay(
                        1,
                        pokemonName,
                        listOf(),
                        "url",
                    ),
                ),
                onPokemonClicked = { clicked = true },
                onGenerationsClicked = { },
                generateNewPokemon = { },
            )
        }

        composeTestRule.onNodeWithContentDescription(pokemonName).assertIsDisplayed().performClick()

        assertThat(clicked)
            .isTrue()
    }
}
