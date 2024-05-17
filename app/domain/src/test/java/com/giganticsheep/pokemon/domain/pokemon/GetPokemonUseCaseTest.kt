package com.giganticsheep.pokemon.domain.pokemon

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.giganticsheep.error.HandledException
import com.giganticsheep.pokemon.domain.pokemon.model.PokemonDisplay
import com.giganticsheep.response.DataResponse
import com.giganticsheep.ui.DisplayDataState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class GetPokemonUseCaseTest {

    private class TestException : HandledException(internalMessage = "Test")

    private val mockPokemonRepository = mockk<PokemonRepository>()

    private val testPokemon = Pokemon(
        id = SPECIES_ID,
        name = SPECIES_DISPLAY_NAME,
        descriptions = listOf(SPECIES_DESCRIPTION),
        internalName = SPECIES_NAME,
        imageUrl = SPECIES_IMAGE_URL,
    )

    private val testPokemonDisplay = PokemonDisplay(
        id = SPECIES_ID,
        name = SPECIES_DISPLAY_NAME,
        descriptions = listOf(SPECIES_DESCRIPTION),
        imageUrl = SPECIES_IMAGE_URL,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private lateinit var useCase: GetPokemonUseCase

    @Before
    fun setup() {
        useCase = GetPokemonUseCase(
            testDispatcher,
            mockPokemonRepository,
        )
    }

    @Test
    fun `when getPokemon success then pokemonDisplayState emits Loading then Data with PokemonDisplay`() = runTest {
        coEvery {
            mockPokemonRepository.getPokemon(SPECIES_NAME)
        } returns DataResponse.Success(testPokemon)

        useCase.pokemonDisplayState.test {
            assertThat(awaitItem())
                .isInstanceOf(DisplayDataState.Uninitialised::class.java)

            useCase.fetchPokemon(SPECIES_NAME)

            assertThat(awaitItem())
                .isInstanceOf(DisplayDataState.Loading::class.java)

            val item = awaitItem()

            assertThat(item)
                .isInstanceOf(DisplayDataState.Data::class.java)

            assertThat((item as DisplayDataState.Data<*>).data)
                .isEqualTo(testPokemonDisplay)
        }
    }

    @Test
    fun `when getPokemon error then pokemonDisplayState emits Loading then Error`() = runTest {
        val testException = TestException()

        coEvery {
            mockPokemonRepository.getPokemon(SPECIES_NAME)
        } returns DataResponse.Error(testException)

        useCase.pokemonDisplayState.test {
            assertThat(awaitItem())
                .isInstanceOf(DisplayDataState.Uninitialised::class.java)

            useCase.fetchPokemon(SPECIES_NAME)

            assertThat(awaitItem())
                .isInstanceOf(DisplayDataState.Loading::class.java)

            val item = awaitItem()

            assertThat(item)
                .isInstanceOf(DisplayDataState.Error::class.java)

            assertThat((item as DisplayDataState.Error<*>).error)
                .isEqualTo(testException.internalMessage)
        }
    }

    companion object {
        private const val SPECIES_ID = 1
        private const val SPECIES_NAME = "SPECIES_NAME"
        private const val SPECIES_DESCRIPTION = "SPECIES_DESCRIPTION"
        private const val SPECIES_DISPLAY_NAME = "SPECIES_DISPLAY_NAME"
        private const val SPECIES_IMAGE_URL = "SPECIES_IMAGE_URL"
    }
}
