package com.giganticsheep.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation

@Composable
fun NestedNavHost(
    navigationGraph: NestedNavigationGraph,
    navigator: Navigator,
    paddingValues: PaddingValues,
) {
    navigator.startCollecting(LocalLifecycleOwner.current)

    NavHost(
        navController = navigationGraph.navHostController,
        startDestination = navigationGraph.startDestinationRoute,
        route = navigationGraph.routeString,
        modifier = Modifier.fillMaxSize(),
    ) { buildGraph(navigationGraph, navigator, paddingValues) }
}

@Composable
fun MainNavHost(
    navigationGraph: TopLevelNavigationGraph,
    navigator: Navigator,
) {
    navigator.startCollecting(LocalLifecycleOwner.current)

    NavHost(
        navController = navigationGraph.navHostController,
        startDestination = navigationGraph.startDestinationRoute,
        route = navigationGraph.routeString,
        modifier = Modifier.fillMaxSize(),
    ) { buildGraph(navigationGraph, navigator) }
}

private fun NavGraphBuilder.buildGraph(
    navigationGraph: NonNestedNavigationGraph,
    navigator: Navigator,
) {
    navigator.setupWithNavigationGraph(navigationGraph)

    navigationGraph.screens.forEach { destination ->
        composable(
            route = destination.routeString,
            arguments = if (destination.details.args.isEmpty()) {
                emptyList()
            } else {
                destination.details.args.map {
                    navArgument(it) {
                    }
                }
            },
        ) { backStackEntry ->
            val args = destination.details.args
                .mapNotNull { key ->
                    backStackEntry.arguments
                        ?.getString(key)
                        ?.let { Pair(key, it) }
                }
                .toMap()
            destination.composable(navigationGraph, args)
        }
    }

    navigationGraph.subGraphs.forEach { graph ->
        navigation(
            startDestination = graph.startDestinationRoute,
            route = graph.routeString,
        ) { buildGraph(graph, navigator) }
    }
}

private fun NavGraphBuilder.buildGraph(
    navigationGraph: NestedNavigationGraph,
    navigator: Navigator,
    paddingValues: PaddingValues,
) {
    navigator.setupWithNavigationGraph(navigationGraph)

    navigationGraph.screens.forEach { destination ->
        composable(route = destination.routeString) {
            destination.composable(navigationGraph, paddingValues)
        }
    }

    // we don't have sub graphs in a nested navigation graph
}
