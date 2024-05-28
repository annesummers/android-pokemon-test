package com.giganticsheep.pokemon.domain.pokemon

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.giganticsheep.displaystate.DisplayDataState
import com.giganticsheep.displaystate.DisplayScreenState
import com.giganticsheep.pokemon.domain.pokemon.model.PokemonDisplay
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class GetPokemonUseCaseTest {

    private val testFlow =
        MutableStateFlow<DisplayDataState<PokemonDisplay>>(DisplayDataState.Uninitialised())

    private val setupTestFlow =
        MutableStateFlow<DisplayScreenState>(DisplayScreenState.Uninitialised)

    private val mockDisplayProvider = mockk<PokemonDisplayProvider> {
        every { displayState } returns testFlow
    }

    private val mockSetupDisplayProvider = mockk<SetupPokemonDisplayProvider> {
        every { displayState } returns setupTestFlow
    }

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
            dispatcher = testDispatcher,
            setupPokemonProvider = mockSetupDisplayProvider,
            pokemonDisplayProvider = mockDisplayProvider,
        )
    }

    @Test
    fun `when providesSetup success and providesPokemon success then displayState emits Data with PokemonDisplay`() =
        runTest {
            coEvery {
                mockSetupDisplayProvider.providesSetup()
            } coAnswers {
                setupTestFlow.emit(DisplayScreenState.Default)
            }

            coEvery {
                mockDisplayProvider.providesPokemon(SPECIES_NAME)
            } coAnswers {
                testFlow.emit(DisplayDataState.Data(testPokemonDisplay))
            }

            useCase.displayState.test {
                assertThat(awaitItem())
                    .isInstanceOf(DisplayDataState.Uninitialised::class.java)

                useCase(SPECIES_NAME)

                val item = awaitItem()

                assertThat(item)
                    .isInstanceOf(DisplayDataState.Data::class.java)

                assertThat((item as DisplayDataState.Data<*>).data)
                    .isEqualTo(testPokemonDisplay)
            }
        }

    @Test
    fun `when providesSetup success and providesPokemon error then displayState emits Error`() = runTest {
        val error = "error"

        coEvery {
            mockSetupDisplayProvider.providesSetup()
        } coAnswers {
            setupTestFlow.emit(DisplayScreenState.Default)
        }

        coEvery {
            mockDisplayProvider.providesPokemon(SPECIES_NAME)
        } coAnswers {
            testFlow.emit(DisplayDataState.Error(error = error, title = null, onDismissed = {}))
        }

        useCase.displayState.test {
            assertThat(awaitItem())
                .isInstanceOf(DisplayDataState.Uninitialised::class.java)

            useCase(SPECIES_NAME)

            val item = awaitItem()

            assertThat(item)
                .isInstanceOf(DisplayDataState.Error::class.java)

            assertThat((item as DisplayDataState.Error<*>).error)
                .isEqualTo(error)
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
