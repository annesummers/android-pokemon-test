package com.giganticsheep.pokemon.domain.generations

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.giganticsheep.displaystate.DisplayDataState
import com.giganticsheep.pokemon.domain.generations.model.GenerationDisplay
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class GetGenerationUseCaseTest {

    private val testFlow = MutableStateFlow<DisplayDataState<GenerationDisplay>>(DisplayDataState.Uninitialised())

    private val mockDisplayProvider = mockk<GenerationDisplayProvider>(relaxUnitFun = true) {
        every { displayState } returns testFlow
    }

    private val testGenerationDisplay = GenerationDisplay(
        name = GENERATION_NAME,
        moves = listOf(MOVE),
        species = listOf(SPECIES),
        id = GENERATION_ID,
        region = GENERATION_REGION,
    )

    private lateinit var useCase: GetGenerationUseCase

    @Before
    fun setup() {
        useCase = GetGenerationUseCase(
            provider = mockDisplayProvider
        )
    }

    @Test
    fun `when fetchGenerationsForDisplay success then generationDisplayState emits Loading then Data with GenerationDisplay`() = runTest {
        coEvery {
            mockDisplayProvider.providesGeneration(GENERATION_NAME)
        } coAnswers  {
            testFlow.emit(DisplayDataState.Loading())
            testFlow.emit(DisplayDataState.Data(testGenerationDisplay))
        }

        useCase.displayState.test {
            assertThat(awaitItem())
                .isInstanceOf(DisplayDataState.Uninitialised::class.java)

            useCase(GENERATION_NAME)

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
        val error = "error"

        coEvery {
            mockDisplayProvider.providesGeneration(GENERATION_NAME)
        } coAnswers  {
            testFlow.emit(DisplayDataState.Loading())
            testFlow.emit(DisplayDataState.Error(error = error, title = null, onDismissed = {}))
        }

        useCase.displayState.test {
            assertThat(awaitItem())
                .isInstanceOf(DisplayDataState.Uninitialised::class.java)

            useCase(GENERATION_NAME)

            assertThat(awaitItem())
                .isInstanceOf(DisplayDataState.Loading::class.java)

            val item = awaitItem()

            assertThat(item)
                .isInstanceOf(DisplayDataState.Error::class.java)

            assertThat((item as DisplayDataState.Error<*>).error)
                .isEqualTo(error)
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
