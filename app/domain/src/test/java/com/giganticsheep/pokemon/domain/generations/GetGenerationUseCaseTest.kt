package com.giganticsheep.pokemon.domain.generations

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.giganticsheep.error.HandledException
import com.giganticsheep.pokemon.data.generations.model.Generation
import com.giganticsheep.pokemon.data.generations.model.RegionItem
import com.giganticsheep.pokemon.data.moves.model.MoveItem
import com.giganticsheep.pokemon.data.species.model.SpeciesItem
import com.giganticsheep.pokemon.domain.generations.model.GenerationDisplay
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

internal class GetGenerationUseCaseTest {

    private class TestException : HandledException(internalMessage = "Test")

    private val mockGenerationsRepository = mockk<GenerationsRepository>()

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

    private lateinit var useCase: GetGenerationUseCase

    @Before
    fun setup() {
        useCase = GetGenerationUseCase(
            testDispatcher,
            mockGenerationsRepository,
        )
    }

    @Test
    fun `when fetchGenerationsForDisplay success then generationDisplayState emits Loading then Data with GenerationDisplay`() = runTest {
        coEvery {
            mockGenerationsRepository.getGeneration(GENERATION_NAME)
        } returns DataResponse.Success(testGeneration)

        useCase.generationDisplayState.test {
            assertThat(awaitItem())
                .isInstanceOf(DisplayDataState.Uninitialised::class.java)

            useCase.fetchGenerationForDisplay(GENERATION_NAME)

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
    fun `when fetchGenerationsForDisplay error then generationDisplayState emits Loading then Error`() = runTest {
        val testException = TestException()

        coEvery {
            mockGenerationsRepository.getGeneration(GENERATION_NAME)
        } returns DataResponse.Error(testException)

        useCase.generationDisplayState.test {
            assertThat(awaitItem())
                .isInstanceOf(DisplayDataState.Uninitialised::class.java)

            useCase.fetchGenerationForDisplay(GENERATION_NAME)

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
