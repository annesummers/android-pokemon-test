package com.giganticsheep.pokemon.domain.generations

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.giganticsheep.error.HandledException
import com.giganticsheep.pokemon.data.generations.model.GenerationItem
import com.giganticsheep.pokemon.domain.generations.model.GenerationItemDisplay
import com.giganticsheep.response.DataResponseState
import com.giganticsheep.ui.DisplayDataState
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class GetGenerationsUseCaseTest {

    private class TestException : HandledException(internalMessage = "Test")

    private val mockGenerationsRepository = mockk<GenerationsRepository>(relaxUnitFun = true)

    private val testGenerationItem = GenerationItem(
        name = GENERATION_NAME,
    )

    private val testGenerationItemDisplay = GenerationItemDisplay(
        name = GENERATION_NAME,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private lateinit var useCase: GetGenerationsUseCase

    @Before
    fun setup() {
        useCase = GetGenerationsUseCase(
            testDispatcher,
            mockGenerationsRepository,
        )
    }

    @Test
    fun `when fetchGenerationsForDisplay success then generationDisplayState emits Loading then Data with GenerationDisplay`() =
        runTest {
            every { mockGenerationsRepository.fetchGenerations() } answers {
                every { mockGenerationsRepository.generations } returns flowOf(
                    DataResponseState.Loading(),
                    DataResponseState.Data(listOf(testGenerationItem)),
                )
            }

            useCase.generationsDisplayState.test {
                assertThat(awaitItem())
                    .isInstanceOf(DisplayDataState.Uninitialised::class.java)

                useCase.fetchGenerationsForDisplay()

                coVerify {
                    mockGenerationsRepository.fetchGenerations()
                }

                assertThat(awaitItem())
                    .isInstanceOf(DisplayDataState.Loading::class.java)

                val item = awaitItem()

                assertThat(item)
                    .isInstanceOf(DisplayDataState.Data::class.java)

                assertThat((item as DisplayDataState.Data<*>).data)
                    .isEqualTo(listOf(testGenerationItemDisplay))
            }
        }

    @Test
    fun `when fetchGenerationsForDisplay error then generationDisplayState emits Loading then Error`() =
        runTest {
            val testException = TestException()

            every { mockGenerationsRepository.fetchGenerations() } answers {
                every { mockGenerationsRepository.generations } returns flowOf(
                    DataResponseState.Loading(),
                    DataResponseState.Error(testException),
                )
            }

            useCase.generationsDisplayState.test {
                assertThat(awaitItem())
                    .isInstanceOf(DisplayDataState.Uninitialised::class.java)

                useCase.fetchGenerationsForDisplay()

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
        private const val GENERATION_NAME = "GENERATION_NAME"
    }
}
