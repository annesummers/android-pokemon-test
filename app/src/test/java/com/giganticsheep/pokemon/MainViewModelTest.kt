package com.giganticsheep.pokemon

import com.giganticsheep.navigation.Navigator
import com.giganticsheep.pokemon.navigation.MainNavigation
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class MainViewModelTest {

    private val mockNavigator = mockk<Navigator>(relaxUnitFun = true)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(
            navigator = mockNavigator,
            dispatcher = testDispatcher,
        )
    }

    @Test
    fun `when init delay for splash delay then navigate to Home Screen`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            mockNavigator.navigate(
                MainNavigation.Graph.Home,
            )
        }
    }
}
