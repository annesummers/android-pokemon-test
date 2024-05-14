package com.giganticsheep.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptions

abstract class NonNestedNavigationGraph protected constructor(
    navigation: Navigation,
    navHostController: NavHostController,
) : BaseNavigationGraph<ScreenDestination>(navigation, navHostController) {

    override val subGraphs: List<NonNestedNavigationGraph> = listOf()

    final override val graphNavEntry
        get() = navHostController.getBackStackEntry(routeString)
}

abstract class TopLevelNavigationGraph protected constructor(
    navigation: Navigation,
    navHostController: NavHostController,
) : NonNestedNavigationGraph(navigation, navHostController) {

    final override val routeString = details.asRoute

    final override val navOptions = NavOptions.Builder()
}

abstract class ChildNavigationGraph protected constructor(
    navigation: Navigation,
    val parentGraph: NavigationGraph,
) : NonNestedNavigationGraph(navigation, parentGraph.navHostController) {

    final override val routeString = "${parentGraph.routeString}${details.asRoute}"
}

abstract class NestedNavigationGraph protected constructor(
    navigation: Navigation,
    navHostController: NavHostController,
    private val parentGraph: NavigationGraph,
) : BaseNavigationGraph<NestedScreenDestination>(navigation, navHostController) {

    final override val subGraphs: List<NestedNavigationGraph> = listOf()

    final override val graphNavEntry
        get() = parentGraph.navHostController.currentBackStackEntry!!

    final override val routeString = details.asRoute

    final override val navOptions = NavOptions.Builder()
}

abstract class BaseNavigationGraph<S : BaseScreenDestination> internal constructor(
    navigation: Navigation,
    override val navHostController: NavHostController,
) : InternalNavigationGraph {

    override val details = navigation.details

    private val screenLookup: Map<String, S> by lazy {
        screens.associateBy { it.details.destinationName }
    }

    private val graphLookup: Map<String, NavigationDestination> by lazy {
        subGraphs.associateBy { it.details.destinationName }
    }

    override val startDestinationRoute by lazy {
        screenLookup[startDestination.destinationName]
            ?.routeString
            ?: "Start destination does not exist in this graph"
    }

    override fun navigate(
        details: RequestedDestinationDetails,
        popUpTo: DestinationDetails?,
        popUpInclusive: Boolean,
    ) = find(details)
        ?.let { potential ->
            potential.request(popUpTo, popUpInclusive, details.args)
                .also { request ->
                    navHostController.navigate(
                        route = request.routeString,
                        navOptions = request.navOptions,
                    )
                }

            true
        }
        ?: false

    override fun find(
        details: DestinationDetails,
    ) = screenLookup[details.destinationName]
        ?: graphLookup[details.destinationName]

    override fun findRoute(
        details: DestinationDetails,
    ) = find(details)?.routeString

    override fun request(
        popUpTo: DestinationDetails?,
        popUpInclusive: Boolean,
        args: Map<String, String>,
    ) = RequestedDestination(
        destination = this,
        popUpToString = popUpTo?.let { find(it)?.routeString },
        popUpInclusive = popUpInclusive,
        args = args
    )

    override val isCurrentScreen: Boolean
        get() {
            val currentScreen =
                navHostController.currentBackStackEntry?.destination?.route == routeString
            return currentScreen
        }

    abstract override val startDestination: DestinationDetails

    abstract val screens: List<S>

    open val subGraphs: List<NavigationDestination> = listOf()
}
