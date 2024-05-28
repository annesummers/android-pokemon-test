package com.giganticsheep.pokemon.ui.home

import com.giganticsheep.displaystate.DisplayDataState
import com.giganticsheep.displaystate.DisplayScreenState
import com.giganticsheep.navigation.Navigator
import com.giganticsheep.pokemon.domain.pokemon.GetRandomPokemonUseCase
import com.giganticsheep.pokemon.domain.pokemon.SetupPokemonUseCase
import com.giganticsheep.pokemon.domain.pokemon.model.PokemonDisplay
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

internal class HomeViewModelTest {

    private val mockNavigator = mockk<Navigator>(relaxUnitFun = true)
    private val mockGetRandomPokemonUseCase = mockk<GetRandomPokemonUseCase>(relaxUnitFun = true) {
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

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        viewModel = HomeViewModel(
            mainNavigator = mockNavigator,
            backgroundDispatcher = testDispatcher,
            getRandomPokemonUseCase = mockGetRandomPokemonUseCase,
            setupUseCase = mockSetupPokemonUseCase,
        )
    }

    @Test
    fun `when init then setup and generate new pokemon`() = runTest {
        coEvery { mockSetupPokemonUseCase() } answers {
            every { mockSetupPokemonUseCase.displayState } returns flowOf(
                DisplayScreenState.Loading,
                DisplayScreenState.Default
            )
        }
        coEvery { mockGetRandomPokemonUseCase() } answers {
            every { mockGetRandomPokemonUseCase.displayState } returns flowOf(
                DisplayDataState.Loading(),
                DisplayDataState.Data(testPokemonDisplay)
            )
        }
        coVerify {
            mockSetupPokemonUseCase()
            mockGetRandomPokemonUseCase()
        }
    }

    @Test
    fun `when init then setup error`() = runTest {
        val error = "error"

        coEvery { mockSetupPokemonUseCase() } answers {
            every { mockSetupPokemonUseCase.displayState } returns flowOf(
                DisplayScreenState.Loading,
                DisplayScreenState.Error(error = error, title = null, onDismissed = {}),
            )
        }

        viewModel = HomeViewModel(
            mainNavigator = mockNavigator,
            backgroundDispatcher = testDispatcher,
            getRandomPokemonUseCase = mockGetRandomPokemonUseCase,
            setupUseCase = mockSetupPokemonUseCase,
        )

        coVerify(exactly = 2) {
            mockSetupPokemonUseCase()
            mockGetRandomPokemonUseCase()
        }
    }

    @Test
    fun `when generateNewPokemon then setup use case then fetch random pokemon`() = runTest {
        viewModel.generateNewPokemon()

        coVerify {
            mockSetupPokemonUseCase()
            mockGetRandomPokemonUseCase()
        }
    }

    @Test
    fun `when onPokemonClicked then navigate to Pokemon Screen with arguments`() = runTest {
        viewModel.onPokemonClicked(SPECIES_ID)

        coVerify {
            mockNavigator.navigate(
                HomeNavigation.Screen.Pokemon
                    .withArgs(HomeNavigation.pokemonId to SPECIES_ID.toString()),
            )
        }
    }

    @Test
    fun `when onBrowseByGenerationClicked then navigate to Generations screen`() = runTest {
        viewModel.onBrowseByGenerationClicked()

        coVerify {
            mockNavigator.navigate(
                HomeNavigation.Screen.Generations,
            )
        }
    }


    companion object {
        private const val SPECIES_ID = 1
        private const val SPECIES_DESCRIPTION = "SPECIES_DESCRIPTION"
        private const val SPECIES_DISPLAY_NAME = "SPECIES_DISPLAY_NAME"
        private const val SPECIES_IMAGE_URL = "SPECIES_IMAGE_URL"
    }
}
