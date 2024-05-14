package com.giganticsheep.pokemon.ui.home

import androidx.compose.runtime.Immutable
import androidx.navigation.NavOptions
import com.giganticsheep.navigation.ChildNavigationGraph
import com.giganticsheep.navigation.DestinationDetails
import com.giganticsheep.navigation.NavigationGraph
import com.giganticsheep.navigation.ScreenDestination
import com.giganticsheep.pokemon.navigation.HomeNavigation
import com.giganticsheep.pokemon.navigation.HomeNavigation.generationId
import com.giganticsheep.pokemon.navigation.HomeNavigation.moveId
import com.giganticsheep.pokemon.navigation.HomeNavigation.pokemonId
import com.giganticsheep.pokemon.ui.generation.GenerationScreen
import com.giganticsheep.pokemon.ui.generations.GenerationsScreen
import com.giganticsheep.pokemon.ui.move.MoveScreen
import com.giganticsheep.pokemon.ui.pokemon.PokemonScreen

@Immutable
class HomeNavigationGraph(
    parent: NavigationGraph,
) : ChildNavigationGraph(HomeNavigation, parent) {

    override val startDestination: DestinationDetails = HomeNavigation.Screen.Home

    override val screens = HomeNavigation.Screen.entries.map { screen ->
        when (screen) {
            HomeNavigation.Screen.Home -> ScreenDestination(
                details = screen,
                graph = this@HomeNavigationGraph
            ) { graph, _ ->
                HomeScreen(
                    navigationGraph = graph as HomeNavigationGraph,
                )
            }

            HomeNavigation.Screen.Generations ->
                ScreenDestination(
                    details = screen,
                    graph = this@HomeNavigationGraph
                ) { graph, _ ->
                    GenerationsScreen(
                        navigationGraph = graph as HomeNavigationGraph
                    )
                }

            HomeNavigation.Screen.Generation ->
                ScreenDestination(
                    details = screen,
                    graph = this@HomeNavigationGraph
                ) { graph, args ->
                    GenerationScreen(
                        navigationGraph = graph as HomeNavigationGraph,
                        generationId = args[generationId] as String
                    )
                }

            HomeNavigation.Screen.Pokemon ->
                ScreenDestination(
                    details = screen,
                    graph = this@HomeNavigationGraph
                ) { graph, args ->
                    PokemonScreen(
                        navigationGraph = graph as HomeNavigationGraph,
                        pokemonId = args[pokemonId] as String
                    )
                }

            HomeNavigation.Screen.Move ->
                ScreenDestination(
                    details = screen,
                    graph = this@HomeNavigationGraph
                ) { graph, args ->
                    MoveScreen(
                        navigationGraph = graph as HomeNavigationGraph,
                        moveId = args[moveId] as String
                    )
                }
        }
    }

    override val navOptions = NavOptions.Builder()
        .setPopUpTo(parentGraph.routeString, false)
}
