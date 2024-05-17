package com.giganticsheep.pokemon.ui.home

import com.giganticsheep.error.HandledException
import com.giganticsheep.navigation.Navigator
import com.giganticsheep.pokemon.domain.pokemon.GetRandomPokemonUseCase
import com.giganticsheep.pokemon.domain.pokemon.SetupPokemonUseCase
import com.giganticsheep.pokemon.navigation.HomeNavigation
import com.giganticsheep.response.CompletableResponse
import com.giganticsheep.ui.DisplayDataState
import com.giganticsheep.ui.DisplayScreenState
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
    private class TestException : HandledException(internalMessage = "Test")

    private val mockNavigator = mockk<Navigator>(relaxUnitFun = true)
    private val mockGetRandomPokemonUseCase = mockk<GetRandomPokemonUseCase>(relaxUnitFun = true) {
        every { pokemonDisplayState } returns flowOf(DisplayDataState.Uninitialised())
    }
    private val mockSetupPokemonUseCase = mockk<SetupPokemonUseCase>(relaxUnitFun = true) {
        every { setupDisplayState } returns flowOf(DisplayScreenState.Uninitialised)
        coEvery { setup() } returns CompletableResponse.Success
    }

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
        coVerify {
            mockSetupPokemonUseCase.setup()
            mockGetRandomPokemonUseCase.fetchRandomPokemon()
        }
    }

    @Test
    fun `when init then setup error`() = runTest {
        val testException = TestException()

        coEvery { mockSetupPokemonUseCase.setup() } returns CompletableResponse.Error(testException)

        viewModel = HomeViewModel(
            mainNavigator = mockNavigator,
            backgroundDispatcher = testDispatcher,
            getRandomPokemonUseCase = mockGetRandomPokemonUseCase,
            setupUseCase = mockSetupPokemonUseCase,
        )

        coVerify(exactly = 2) {
            mockSetupPokemonUseCase.setup()
        }

        coVerify(exactly = 1) { mockGetRandomPokemonUseCase.fetchRandomPokemon() }
    }

    @Test
    fun `when generateNewPokemon then setup use case then fetch random pokemon`() = runTest {
        viewModel.generateNewPokemon()

        coVerify(exactly = 2) { // also called as the viewmodel is created
            mockSetupPokemonUseCase.setup()
            mockGetRandomPokemonUseCase.fetchRandomPokemon()
        }
    }

    @Test
    fun `when onPokemonClicked then navigate to Pokemon Screen with arguments`() = runTest {
        viewModel.onPokemonClicked(POKEMON_ID)

        coVerify {
            mockNavigator.navigate(
                HomeNavigation.Screen.Pokemon
                    .withArgs(HomeNavigation.pokemonId to POKEMON_ID.toString()),
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
        private const val POKEMON_ID = 1
    }
}
