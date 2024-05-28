package com.giganticsheep.ui

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import assertk.assertThat
import assertk.assertions.isTrue
import org.junit.Rule
import org.junit.Test

internal class DisplayStateTest {

    private data class TestClass(val id: String)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDisplayScreenStateDefault() {
        composeTestRule.setContent {
            HandleDisplayState(com.giganticsheep.displaystate.DisplayScreenState.Default) {
                Text("CONTENT")
            }
        }

        composeTestRule.onNodeWithText("CONTENT").assertIsDisplayed()
    }

    @Test
    fun testDisplayScreenStateDefaultError() {
        var dismissed = false

        composeTestRule.setContent {
            HandleDisplayState(
                com.giganticsheep.displaystate.DisplayScreenState.Error(
                    "error",
                    null,
                ) {
                    dismissed = true
                },
            ) {
                Text("CONTENT")
            }
        }

        composeTestRule.onNodeWithText("CONTENT").assertIsNotDisplayed()

        composeTestRule.onNodeWithText("error").assertIsDisplayed()
        composeTestRule.onNodeWithText("title").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Ok").assertIsDisplayed().performClick()

        assertThat(dismissed).isTrue()

        composeTestRule.onNodeWithText("error").assertIsNotDisplayed()
    }

    @Test
    fun testDisplayScreenStateDefaultErrorWithTitle() {
        var dismissed = false

        composeTestRule.setContent {
            HandleDisplayState(
                com.giganticsheep.displaystate.DisplayScreenState.Error(
                    "error",
                    "title",
                ) {
                    dismissed = true
                },
            ) {
                Text("CONTENT")
            }
        }

        composeTestRule.onNodeWithText("CONTENT").assertIsNotDisplayed()

        composeTestRule.onNodeWithText("error").assertIsDisplayed()
        composeTestRule.onNodeWithText("title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ok").assertIsDisplayed().performClick()

        assertThat(dismissed).isTrue()

        composeTestRule.onNodeWithText("error").assertIsNotDisplayed()
    }

    @Test
    fun testDisplayScreenStateCustomError() {
        composeTestRule.setContent {
            HandleDisplayState(
                com.giganticsheep.displaystate.DisplayScreenState.Error("error", null) {},
                onError = { error, _, _ -> Text(error) },
            ) {
                Text("CONTENT")
            }
        }

        composeTestRule.onNodeWithText("CONTENT").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ok").assertIsNotDisplayed()
    }

    @Test
    fun testDisplayScreenStateDefaultLoading() {
        composeTestRule.setContent {
            HandleDisplayState(com.giganticsheep.displaystate.DisplayScreenState.Loading) {
                Text("CONTENT")
            }
        }

        composeTestRule.onNodeWithText("CONTENT").assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescription("Loading").assertIsDisplayed()
    }

    @Test
    fun testDisplayScreenStateCustomLoading() {
        composeTestRule.setContent {
            HandleDisplayState(
                com.giganticsheep.displaystate.DisplayScreenState.Loading,
                onLoading = { Text("LOADING") },
            ) {
                Text("CONTENT")
            }
        }

        composeTestRule.onNodeWithText("CONTENT").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("LOADING").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Loading").assertIsNotDisplayed()
    }

    @Test
    fun testDisplayDataStateDefault() {
        val data = TestClass("id")

        composeTestRule.setContent {
            HandleDisplayState(com.giganticsheep.displaystate.DisplayDataState.Data(data)) {
                Text(it.id)
            }
        }

        composeTestRule.onNodeWithText("id").assertIsDisplayed()
    }

    @Test
    fun testDisplayDataStateDefaultError() {
        var dismissed = false

        composeTestRule.setContent {
            HandleDisplayState(
                com.giganticsheep.displaystate.DisplayDataState.Error(
                    "error",
                    "title",
                ) {
                    dismissed = true
                },
            ) {
                Text("CONTENT")
            }
        }

        composeTestRule.onNodeWithText("CONTENT").assertIsNotDisplayed()

        composeTestRule.onNodeWithText("error").assertIsDisplayed()
        composeTestRule.onNodeWithText("title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ok").assertIsDisplayed().performClick()

        assertThat(dismissed).isTrue()

        composeTestRule.onNodeWithText("error").assertIsNotDisplayed()
    }

    @Test
    fun testDisplayDataStateDefaultErrorWithTitle() {
        var dismissed = false

        composeTestRule.setContent {
            HandleDisplayState(
                com.giganticsheep.displaystate.DisplayDataState.Error(
                    "error",
                    "title",
                ) {
                    dismissed = true
                },
            ) {
                Text("CONTENT")
            }
        }

        composeTestRule.onNodeWithText("CONTENT").assertIsNotDisplayed()

        composeTestRule.onNodeWithText("error").assertIsDisplayed()
        composeTestRule.onNodeWithText("title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ok").assertIsDisplayed().performClick()

        assertThat(dismissed).isTrue()

        composeTestRule.onNodeWithText("error").assertIsNotDisplayed()
    }

    @Test
    fun testDisplayDataStateDefaultLoading() {
        composeTestRule.setContent {
            HandleDisplayState(com.giganticsheep.displaystate.DisplayDataState.Loading()) {
                Text("CONTENT")
            }
        }

        composeTestRule.onNodeWithText("CONTENT").assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescription("Loading").assertIsDisplayed()
    }
}
