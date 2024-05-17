package com.giganticsheep.pokemon

import androidx.compose.runtime.Immutable
import androidx.navigation.NavHostController
import com.giganticsheep.navigation.DestinationDetails
import com.giganticsheep.navigation.ScreenDestination
import com.giganticsheep.navigation.TopLevelNavigationGraph
import com.giganticsheep.pokemon.navigation.MainNavigation
import com.giganticsheep.pokemon.splash.SplashScreen
import com.giganticsheep.pokemon.ui.home.HomeNavigationGraph

@Immutable
class MainNavigationGraph(
    navHostController: NavHostController,
) : TopLevelNavigationGraph(
    navigation = MainNavigation,
    navHostController = navHostController,
) {
    override val startDestination: DestinationDetails = MainNavigation.Screen.Splash

    override val screens = MainNavigation.Screen.entries.map { screen ->
        ScreenDestination(
            details = screen,
            graph = this@MainNavigationGraph,
            composable = when (screen) {
                MainNavigation.Screen.Splash -> { graph, _ -> SplashScreen(graph) }
            },
        )
    }

    override val subGraphs = listOf(
        HomeNavigationGraph(this),
    )
}
