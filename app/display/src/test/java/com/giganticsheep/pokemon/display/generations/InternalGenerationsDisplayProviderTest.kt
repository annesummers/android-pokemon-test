package com.giganticsheep.pokemon.display.generations

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.giganticsheep.error.HandledException
import com.giganticsheep.pokemon.data.generations.GenerationsRepository
import com.giganticsheep.pokemon.data.generations.model.GenerationItem
import com.giganticsheep.pokemon.domain.generations.model.GenerationItemDisplay
import com.giganticsheep.response.DataResponseState
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

internal class InternalGenerationsDisplayProviderTest {

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

    private lateinit var displayProvider: InternalGenerationsDisplayProvider

    @Before
    fun setup() {
        displayProvider = InternalGenerationsDisplayProvider(
            dispatcher = testDispatcher,
            generationsRepository = mockGenerationsRepository,
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

            displayProvider.displayState.test {
                assertThat(awaitItem())
                    .isInstanceOf(com.giganticsheep.displaystate.DisplayDataState.Uninitialised::class.java)

                displayProvider.providesGenerations()

                coVerify {
                    mockGenerationsRepository.fetchGenerations()
                }

                assertThat(awaitItem())
                    .isInstanceOf(com.giganticsheep.displaystate.DisplayDataState.Loading::class.java)

                val item = awaitItem()

                assertThat(item)
                    .isInstanceOf(com.giganticsheep.displaystate.DisplayDataState.Data::class.java)

                assertThat((item as com.giganticsheep.displaystate.DisplayDataState.Data<*>).data)
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

            displayProvider.displayState.test {
                assertThat(awaitItem())
                    .isInstanceOf(com.giganticsheep.displaystate.DisplayDataState.Uninitialised::class.java)

                displayProvider.providesGenerations()

                assertThat(awaitItem())
                    .isInstanceOf(com.giganticsheep.displaystate.DisplayDataState.Loading::class.java)

                val item = awaitItem()

                assertThat(item)
                    .isInstanceOf(com.giganticsheep.displaystate.DisplayDataState.Error::class.java)

                assertThat((item as com.giganticsheep.displaystate.DisplayDataState.Error<*>).error)
                    .isEqualTo(testException.internalMessage)
            }
        }

    companion object {
        private const val GENERATION_NAME = "GENERATION_NAME"
    }
}
