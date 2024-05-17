package com.giganticsheep.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavOptions

class ScreenDestination(
    details: PotentialDestinationDetails,
    singleTop: Boolean = true,
    popUpTo: DestinationDetails? = null,
    popUpInclusive: Boolean = false,
    override val graph: NonNestedNavigationGraph,
    val composable: @Composable (NonNestedNavigationGraph, Map<String, String?>) -> Unit,
) : BaseScreenDestination(
    details = details,
    singleTop = singleTop,
    popUpTo = popUpTo,
    popUpInclusive = popUpInclusive,
    graph = graph,
)

abstract class BaseScreenDestination internal constructor(
    final override val details: PotentialDestinationDetails,
    val singleTop: Boolean,
    open val popUpTo: DestinationDetails?,
    val popUpInclusive: Boolean,
    open val graph: NavigationGraph,
) : PotentialDestination {

    private val internalGraph
        get() = graph as InternalNavigationGraph

    override val navOptions by lazy {
        NavOptions.Builder()
            .setLaunchSingleTop(singleTop)
            .setPopUpTo(popUpTo?.let { internalGraph.findRoute(it) }, popUpInclusive)
    }

    override val routeString
        get() = "${internalGraph.routeString}${details.asRoute}"

    override fun request(
        popUpTo: DestinationDetails?,
        popUpInclusive: Boolean,
        args: Map<String, String?>,
    ) = RequestedDestination(
        destination = this,
        popUpToString = popUpTo?.let { internalGraph.findRoute(it) },
        popUpInclusive = popUpInclusive,
        args = args,
    )
}
