package com.giganticsheep.pokemon.ui.generations

import com.giganticsheep.navigation.Navigator
import com.giganticsheep.pokemon.domain.generations.GetGenerationsUseCase
import com.giganticsheep.pokemon.domain.generations.model.GenerationItemDisplay
import com.giganticsheep.pokemon.navigation.HomeNavigation
import com.giganticsheep.ui.DisplayDataState
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class GenerationsViewModelTest {

    private val mockNavigator = mockk<Navigator>(relaxUnitFun = true)
    private val mockGetGenerationsUseCase = mockk<GetGenerationsUseCase>(relaxUnitFun = true) {
        every { generationsDisplayState } returns flowOf(DisplayDataState.Uninitialised<ImmutableList<GenerationItemDisplay>>())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private lateinit var viewModel: GenerationsViewModel

    @Before
    fun setup() {
        viewModel = GenerationsViewModel(
            mockNavigator,
            testDispatcher,
            mockGetGenerationsUseCase,
        )
    }

    @Test
    fun `when init then fetch generations`() = runTest {
        coVerify {
            mockGetGenerationsUseCase.fetchGenerationsForDisplay()
        }
    }

    @Test
    fun `when onUpClicked then navigate back`() = runTest {
        viewModel.onUpClicked()

        coVerify {
            mockNavigator.navigateBack()
        }
    }

    @Test
    fun `when onGenerationClicked then navigate to Generation Screen with argument`() = runTest {
        viewModel.onGenerationClicked(GENERATION)

        coVerify {
            mockNavigator.navigate(
                HomeNavigation.Screen.Generation.withArgs(HomeNavigation.generationId to GENERATION),
            )
        }
    }

    companion object {
        private const val GENERATION = "GENERATION"
    }
}
