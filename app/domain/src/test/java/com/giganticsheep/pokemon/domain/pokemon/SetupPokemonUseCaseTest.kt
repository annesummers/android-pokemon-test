package com.giganticsheep.pokemon.domain.pokemon

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.giganticsheep.displaystate.DisplayScreenState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class SetupPokemonUseCaseTest {

    private val testFlow =
        MutableStateFlow<DisplayScreenState>(DisplayScreenState.Uninitialised)

    private val mockDisplayProvider = mockk<SetupPokemonDisplayProvider> {
        every { displayState } returns testFlow
    }

    private lateinit var useCase: SetupPokemonUseCase

    @Before
    fun setup() {
        useCase = SetupPokemonUseCase(
            provider = mockDisplayProvider,
        )
    }

    @Test
    fun `when providesSetup success then displayState emits Default`() =
        runTest {
            coEvery {
                mockDisplayProvider.providesSetup()
            } coAnswers {
                testFlow.emit(DisplayScreenState.Default)
            }

            useCase.displayState.test {
                assertThat(awaitItem())
                    .isInstanceOf(DisplayScreenState.Uninitialised::class.java)

                useCase()

                val item = awaitItem()

                assertThat(item)
                    .isInstanceOf(DisplayScreenState.Default::class.java)
            }
        }

    @Test
    fun `when providesSetup error then displayState emits Error`() = runTest {
        val error = "error"

        coEvery {
            mockDisplayProvider.providesSetup()
        } coAnswers {
            testFlow.emit(DisplayScreenState.Error(error = error, title = null, onDismissed = {}))
        }

        useCase.displayState.test {
            assertThat(awaitItem())
                .isInstanceOf(DisplayScreenState.Uninitialised::class.java)

            useCase()

            val item = awaitItem()

            assertThat(item)
                .isInstanceOf(DisplayScreenState.Error::class.java)

            assertThat((item as DisplayScreenState.Error).error)
                .isEqualTo(error)
        }
    }
}
