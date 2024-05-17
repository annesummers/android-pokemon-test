package com.giganticsheep.pokemon.ui.pokemon

import com.giganticsheep.error.HandledException
import com.giganticsheep.navigation.Navigator
import com.giganticsheep.pokemon.domain.pokemon.GetPokemonUseCase
import com.giganticsheep.pokemon.domain.pokemon.SetupPokemonUseCase
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

internal class PokemonViewModelTest {
    private class TestException : HandledException(internalMessage = "Test")

    private val mockNavigator = mockk<Navigator>(relaxUnitFun = true)
    private val mockGetPokemonUseCase = mockk<GetPokemonUseCase>(relaxUnitFun = true) {
        every { pokemonDisplayState } returns flowOf(DisplayDataState.Uninitialised())
    }

    private val mockSetupPokemonUseCase = mockk<SetupPokemonUseCase>(relaxUnitFun = true) {
        every { setupDisplayState } returns flowOf(DisplayScreenState.Uninitialised)
        coEvery { setup() } returns CompletableResponse.Success
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private lateinit var viewModel: PokemonViewModel

    @Before
    fun setup() {
        viewModel = PokemonViewModel(
            mainNavigator = mockNavigator,
            backgroundDispatcher = testDispatcher,
            getPokemonUseCase = mockGetPokemonUseCase,
            setupUseCase = mockSetupPokemonUseCase,
        )
    }

    @Test
    fun `when setup with pokemon name then setup and fetch pokemon`() = runTest {
        viewModel.setup(POKEMON_NAME)

        coVerify {
            mockSetupPokemonUseCase.setup()
            mockGetPokemonUseCase.fetchPokemon(POKEMON_NAME)
        }
    }

    @Test
    fun `when setup with pokemon name then setup error`() = runTest {
        val testException = TestException()

        coEvery { mockSetupPokemonUseCase.setup() } returns CompletableResponse.Error(testException)

        viewModel.setup(POKEMON_NAME)

        coVerify {
            mockSetupPokemonUseCase.setup()
        }

        coVerify(exactly = 0) { mockGetPokemonUseCase.fetchPokemon(POKEMON_NAME) }
    }

    @Test
    fun `when onUpClicked then navigate back`() = runTest {
        viewModel.onUpClicked()

        coVerify {
            mockNavigator.navigateBack()
        }
    }

    companion object {
        private const val POKEMON_NAME = "POKEMON_NAME"
    }
}
