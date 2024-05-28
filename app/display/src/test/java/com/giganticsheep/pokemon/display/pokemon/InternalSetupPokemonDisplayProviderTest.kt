package com.giganticsheep.pokemon.display.pokemon

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.giganticsheep.displaystate.DisplayScreenState
import com.giganticsheep.error.HandledException
import com.giganticsheep.pokemon.data.species.PokemonRepository
import com.giganticsheep.response.CompletableResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class InternalSetupPokemonDisplayProviderTest {

    private class TestException : HandledException(internalMessage = "Test")

    private val mockRepository = mockk<PokemonRepository>()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private lateinit var displayProvider: InternalSetupPokemonDisplayProvider

    @Before
    fun setup() {
        displayProvider = InternalSetupPokemonDisplayProvider(
            testDispatcher,
            mockRepository,
        )
    }

    @Test
    fun `when getPokemon success then displayState emits Default`() = runTest {
        coEvery {
            mockRepository.setup()
        } returns CompletableResponse.Success

        displayProvider.displayState.test {
            assertThat(awaitItem())
                .isInstanceOf(DisplayScreenState.Uninitialised::class.java)

            displayProvider.providesSetup()

            val item = awaitItem()

            assertThat(item)
                .isInstanceOf(DisplayScreenState.Default::class.java)
        }
    }

    @Test
    fun `when getPokemon error then displayState emits Error`() = runTest {
        val testException = TestException()

        coEvery {
            mockRepository.setup()
        } returns CompletableResponse.Error(testException)

        displayProvider.displayState.test {
            assertThat(awaitItem())
                .isInstanceOf(DisplayScreenState.Uninitialised::class.java)

            displayProvider.providesSetup()

            val item = awaitItem()

            assertThat(item)
                .isInstanceOf(DisplayScreenState.Error::class.java)

            assertThat((item as DisplayScreenState.Error).error)
                .isEqualTo(testException.internalMessage)
        }
    }
}
