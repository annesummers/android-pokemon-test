package com.giganticsheep.pokemon.display.generations

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.giganticsheep.displaystate.DisplayDataState
import com.giganticsheep.error.HandledException
import com.giganticsheep.pokemon.data.generations.GenerationsRepository
import com.giganticsheep.pokemon.data.generations.model.Generation
import com.giganticsheep.pokemon.data.generations.model.RegionItem
import com.giganticsheep.pokemon.data.moves.model.MoveItem
import com.giganticsheep.pokemon.data.species.model.SpeciesItem
import com.giganticsheep.pokemon.domain.generations.model.GenerationDisplay
import com.giganticsheep.response.DataResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class InternalGenerationDisplayProviderTest {

    private class TestException : HandledException(internalMessage = "Test")

    private val mockGenerationsRepository = mockk<GenerationsRepository>(relaxUnitFun = true)

    private val testGeneration = Generation(
        name = GENERATION_NAME,
        moves = listOf(MoveItem(MOVE)),
        species = listOf(SpeciesItem(SPECIES)),
        id = GENERATION_ID,
        mainRegion = RegionItem(GENERATION_REGION),
    )

    private val testGenerationDisplay = GenerationDisplay(
        name = GENERATION_NAME,
        moves = listOf(MOVE),
        species = listOf(SPECIES),
        id = GENERATION_ID,
        region = GENERATION_REGION,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private lateinit var displayProvider: InternalGenerationDisplayProvider

    @Before
    fun setup() {
        displayProvider = InternalGenerationDisplayProvider(
            dispatcher = testDispatcher,
            generationsRepository = mockGenerationsRepository,
        )
    }

    @Test
    fun `when fetchGenerationForDisplay success then displayState emits Data with GenerationDisplay`() =
        runTest {
            coEvery {
                mockGenerationsRepository.getGeneration(GENERATION_NAME)
            } returns DataResponse.Success(testGeneration)

            displayProvider.displayState.test {
                assertThat(awaitItem())
                    .isInstanceOf(DisplayDataState.Uninitialised::class.java)

                displayProvider.providesGeneration(GENERATION_NAME)

                coVerify {
                    mockGenerationsRepository.getGeneration(GENERATION_NAME)
                }

                assertThat(awaitItem())
                    .isInstanceOf(DisplayDataState.Loading::class.java)

                val item = awaitItem()

                assertThat(item)
                    .isInstanceOf(DisplayDataState.Data::class.java)

                assertThat((item as DisplayDataState.Data<*>).data)
                    .isEqualTo(testGenerationDisplay)
            }
        }

    @Test
    fun `when fetchGenerationForDisplay error then displayState emits Error`() =
        runTest {
            val testException = TestException()

            coEvery {
                mockGenerationsRepository.getGeneration(GENERATION_NAME)
            } returns DataResponse.Error(testException)

            displayProvider.displayState.test {
                assertThat(awaitItem())
                    .isInstanceOf(DisplayDataState.Uninitialised::class.java)

                displayProvider.providesGeneration(GENERATION_NAME)

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
        private const val GENERATION_ID = 1
        private const val GENERATION_NAME = "GENERATION_NAME"
        private const val GENERATION_REGION = "GENERATION_REGION"
        private const val MOVE = "MOVE"
        private const val SPECIES = "SPECIES"
    }
}
