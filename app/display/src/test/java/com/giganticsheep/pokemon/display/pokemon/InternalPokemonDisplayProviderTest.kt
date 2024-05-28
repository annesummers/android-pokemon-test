package com.giganticsheep.pokemon.display.pokemon

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.giganticsheep.displaystate.DisplayDataState
import com.giganticsheep.error.HandledException
import com.giganticsheep.pokemon.data.species.PokemonRepository
import com.giganticsheep.pokemon.data.species.model.Pokemon
import com.giganticsheep.pokemon.domain.pokemon.model.PokemonDisplay
import com.giganticsheep.response.DataResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class InternalPokemonDisplayProviderTest {

    private class TestException : HandledException(internalMessage = "Test")

    private val mockRepository = mockk<PokemonRepository>()

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

    private lateinit var displayProvider: InternalPokemonDisplayProvider

    @Before
    fun setup() {
        displayProvider = InternalPokemonDisplayProvider(
            dispatcher = testDispatcher,
            pokemonRepository = mockRepository,
        )
    }

    @Test
    fun `when providesRandomPokemon success then displayState emits Loading then Data with PokemonDisplay`() =
        runTest {
            coEvery {
                mockRepository.getRandomPokemon()
            } returns DataResponse.Success(testPokemon)

            displayProvider.displayState.test {
                assertThat(awaitItem())
                    .isInstanceOf(DisplayDataState.Uninitialised::class.java)

                displayProvider.providesRandomPokemon()

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
    fun `when providesRandomPokemon error then displayState emits Loading then Error`() =
        runTest {
            val testException = TestException()

            coEvery {
                mockRepository.getRandomPokemon()
            } returns DataResponse.Error(testException)

            displayProvider.displayState.test {
                assertThat(awaitItem())
                    .isInstanceOf(DisplayDataState.Uninitialised::class.java)

                displayProvider.providesRandomPokemon()

                assertThat(awaitItem())
                    .isInstanceOf(DisplayDataState.Loading::class.java)

                val item = awaitItem()

                assertThat(item)
                    .isInstanceOf(DisplayDataState.Error::class.java)

                assertThat((item as DisplayDataState.Error<*>).error)
                    .isEqualTo(testException.internalMessage)
            }
        }

    @Test
    fun `when providesPokemon success then displayState emits Loading then Data with PokemonDisplay`() =
        runTest {
            coEvery {
                mockRepository.getPokemon(SPECIES_NAME)
            } returns DataResponse.Success(testPokemon)

            displayProvider.displayState.test {
                assertThat(awaitItem())
                    .isInstanceOf(DisplayDataState.Uninitialised::class.java)

                displayProvider.providesPokemon(SPECIES_NAME)

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
    fun `when providesPokemon error then displayState emits Loading then Error`() =
        runTest {
            val testException = TestException()

            coEvery {
                mockRepository.getPokemon(SPECIES_NAME)
            } returns DataResponse.Error(testException)

            displayProvider.displayState.test {
                assertThat(awaitItem())
                    .isInstanceOf(DisplayDataState.Uninitialised::class.java)

                displayProvider.providesPokemon(SPECIES_NAME)

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
