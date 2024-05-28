package com.giganticsheep.pokemon.ui.generation

import com.giganticsheep.displaystate.DisplayDataState
import com.giganticsheep.navigation.Navigator
import com.giganticsheep.pokemon.domain.generations.GetGenerationUseCase
import com.giganticsheep.pokemon.domain.generations.model.GenerationDisplay
import com.giganticsheep.pokemon.navigation.HomeNavigation
import io.mockk.coEvery
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

internal class GenerationViewModelTest {

    private val mockNavigator = mockk<Navigator>(relaxUnitFun = true)
    private val mockGetGenerationUseCase = mockk<GetGenerationUseCase>(relaxUnitFun = true) {
        every { displayState } returns flowOf(DisplayDataState.Uninitialised())
    }

    private val testGenerationDisplay = GenerationDisplay(
        name = GENERATION_NAME,
        moves = listOf(MOVE),
        species = listOf(SPECIES),
        id = GENERATION_ID,
        region = GENERATION_REGION,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private lateinit var viewModel: GenerationViewModel

    @Before
    fun setup() {
        viewModel = GenerationViewModel(
            mockNavigator,
            testDispatcher,
            mockGetGenerationUseCase,
        )
    }

    @Test
    fun `when setup with generationName then fetch generation`() = runTest {
        coEvery { mockGetGenerationUseCase(GENERATION_NAME) } answers {
            every { mockGetGenerationUseCase.displayState } returns flowOf(
                DisplayDataState.Loading(),
                DisplayDataState.Data(testGenerationDisplay)
            )
        }

        viewModel.setup(GENERATION_NAME)

        coVerify {
            mockGetGenerationUseCase(GENERATION_NAME)
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
    fun `when onSpeciesClicked then navigate to Pokemon Screen with argument`() = runTest {
        viewModel.onSpeciesClicked(SPECIES)

        coVerify {
            mockNavigator.navigate(
                HomeNavigation.Screen.Pokemon
                    .withArgs(HomeNavigation.pokemonName to SPECIES)
            )
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
