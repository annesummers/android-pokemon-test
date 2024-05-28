package com.giganticsheep.pokemon.ui.pokemon

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import assertk.assertThat
import assertk.assertions.isTrue
import com.giganticsheep.pokemon.domain.pokemon.model.PokemonDisplay
import com.giganticsheep.displaystate.DisplayDataState
import org.junit.Rule
import org.junit.Test

internal class PokemonContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenPokemonContentWithDataStateDataThenShowPokemon() {
        val pokemonName = "pokemonName"
        val description = "description"

        composeTestRule.setContent {
            PokemonContent(
                pokemonState = com.giganticsheep.displaystate.DisplayDataState.Data(
                    PokemonDisplay(
                        id = 1,
                        name = pokemonName,
                        descriptions = listOf(description),
                        imageUrl = "url",
                    ),
                ),
                onUpClicked = { },
            )
        }

        composeTestRule.onNodeWithText(pokemonName).assertIsDisplayed()
        composeTestRule.onNodeWithText(description).assertIsDisplayed()
    }

    @Test
    fun whenPokemonContentWithDataStateErrorThenShowError() {
        val errorString = "error"

        composeTestRule.setContent {
            PokemonContent(
                pokemonState = com.giganticsheep.displaystate.DisplayDataState.Error(
                    errorString,
                    null,
                ) {},
                onUpClicked = { },
            )
        }

        composeTestRule.onNodeWithText(errorString).assertIsDisplayed()
    }

    @Test
    fun whenShowPokemonAndUpButtonClickedThenAssertIsClicked() {
        var clicked = false
        val pokemonName = "pokemonName"
        val description = "description"

        composeTestRule.setContent {
            PokemonContent(
                pokemonState = com.giganticsheep.displaystate.DisplayDataState.Data(
                    PokemonDisplay(
                        id = 1,
                        name = pokemonName,
                        descriptions = listOf(description),
                        imageUrl = "url",
                    ),
                ),
                onUpClicked = { clicked = true },
            )
        }

        composeTestRule.onNodeWithContentDescription("go up").assertIsDisplayed().performClick()

        assertThat(clicked)
            .isTrue()
    }
}
