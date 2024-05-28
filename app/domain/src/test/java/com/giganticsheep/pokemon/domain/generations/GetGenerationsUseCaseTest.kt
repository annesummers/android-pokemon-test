package com.giganticsheep.pokemon.domain.generations

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.giganticsheep.displaystate.DisplayDataState
import com.giganticsheep.pokemon.domain.generations.model.GenerationItemDisplay
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class GetGenerationsUseCaseTest {

    private val testFlow =
        MutableStateFlow<DisplayDataState<ImmutableList<GenerationItemDisplay>>>(DisplayDataState.Uninitialised())

    private val mockDisplayProvider = mockk<GenerationsDisplayProvider>(relaxUnitFun = true) {
        every { displayState } returns testFlow
    }

    private val testGenerationItemDisplay = GenerationItemDisplay(
        name = GENERATION_NAME,
    )

    private lateinit var useCase: GetGenerationsUseCase

    @Before
    fun setup() {
        useCase = GetGenerationsUseCase(
            provider = mockDisplayProvider
        )
    }

    @Test
    fun `when providesGenerations success then displayState emits Loading then Data with GenerationDisplay`() =
        runTest {
            coEvery {
                mockDisplayProvider.providesGenerations()
            } coAnswers {
                testFlow.emit(DisplayDataState.Loading())
                testFlow.emit(
                    DisplayDataState.Data(listOf(testGenerationItemDisplay).toImmutableList()),
                )
            }

            useCase.displayState.test {
                assertThat(awaitItem())
                    .isInstanceOf(DisplayDataState.Uninitialised::class.java)

                useCase()

                coVerify {
                    mockDisplayProvider.providesGenerations()
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
    fun `when providesGenerations error then displayState emits Loading then Error`() =
        runTest {
            val error = "error"

            coEvery {
                mockDisplayProvider.providesGenerations()
            } coAnswers {
                testFlow.emit(DisplayDataState.Loading())
                testFlow.emit(DisplayDataState.Error(error = error, title = null, onDismissed = {}))
            }

            useCase.displayState.test {
                assertThat(awaitItem())
                    .isInstanceOf(DisplayDataState.Uninitialised::class.java)

                useCase()

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
        private const val GENERATION_NAME = "GENERATION_NAME"
    }
}
