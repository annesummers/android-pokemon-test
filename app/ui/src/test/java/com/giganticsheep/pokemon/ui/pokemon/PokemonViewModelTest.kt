package com.giganticsheep.pokemon.ui.pokemon

import com.giganticsheep.displaystate.DisplayDataState
import com.giganticsheep.displaystate.DisplayScreenState
import com.giganticsheep.navigation.Navigator
import com.giganticsheep.pokemon.domain.pokemon.GetPokemonUseCase
import com.giganticsheep.pokemon.domain.pokemon.SetupPokemonUseCase
import com.giganticsheep.pokemon.domain.pokemon.model.PokemonDisplay
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

internal class PokemonViewModelTest {

    private val mockNavigator = mockk<Navigator>(relaxUnitFun = true)
    private val mockGetPokemonUseCase = mockk<GetPokemonUseCase>(relaxUnitFun = true) {
        every { displayState } returns flowOf(DisplayDataState.Uninitialised())
    }

    private val mockSetupPokemonUseCase = mockk<SetupPokemonUseCase>(relaxUnitFun = true) {
        every { displayState } returns flowOf(DisplayScreenState.Uninitialised)
    }

    private val testPokemonDisplay = PokemonDisplay(
        id = SPECIES_ID,
        name = SPECIES_DISPLAY_NAME,
        descriptions = listOf(SPECIES_DESCRIPTION),
        imageUrl = SPECIES_IMAGE_URL,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private lateinit var viewModel: PokemonViewModel

    @Before
    fun setup() {
        viewModel = PokemonViewModel(
            mainNavigator = mockNavigator,
            backgroundDispatcher = testDispatcher,
            getPokemonUseCase = mockGetPokemonUseCase,
            setupPokemonUseCase = mockSetupPokemonUseCase,
        )
    }

    @Test
    fun `when setup with pokemon name then setup and fetch pokemon`() = runTest {
        coEvery { mockGetPokemonUseCase(SPECIES_NAME) } answers {
            every { mockGetPokemonUseCase.displayState } returns flowOf(
                DisplayDataState.Loading(),
                DisplayDataState.Data(testPokemonDisplay),
            )
        }

        viewModel.setup(SPECIES_NAME)

        coVerify {
            mockSetupPokemonUseCase()
            mockGetPokemonUseCase(SPECIES_NAME)
        }
    }

    @Test
    fun `when onUpClicked then navigate back`() = runTest {
        viewModel.onUpClicked()

        coVerify {
            mockNavigator.navigateBack()
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
