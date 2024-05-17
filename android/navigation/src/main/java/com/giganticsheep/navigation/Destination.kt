package com.giganticsheep.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions

interface PotentialDestinationDetails : DestinationDetails {

    override val asRoute: String
        get() = args.fold("/$destinationName") { route, arg -> route.plus("/{$arg}") }

    val args: List<String>

    fun withArgs(
        vararg args: Pair<String, String?>,
    ) = withArgs(args.toMap())

    fun withArgs(
        args: Map<String, String?>,
    ) = RequestedDestinationDetails(this, args.toMap())

    val asRequested
        get() = RequestedDestinationDetails(this, mapOf())
}

interface DestinationDetails {
    val destinationName: String

    val asRoute: String
}

data class RequestedDestinationDetails(
    private val details: DestinationDetails,
    val args: Map<String, String?>,
) : DestinationDetails {

    override val destinationName = details.destinationName
    override val asRoute = details.asRoute
}

interface Destination {
    val details: DestinationDetails

    val routeString: String
}

interface PotentialDestination : Destination {
    val navOptions: NavOptions.Builder

    override val details: PotentialDestinationDetails

    fun request(
        popUpTo: DestinationDetails? = null,
        popUpInclusive: Boolean = false,
        args: Map<String, String?> = mapOf(),
    ): RequestedDestination
}

interface NavigationDestination : PotentialDestination {
    val startDestination: DestinationDetails
}

interface NavigationGraph : NavigationDestination {
    val navHostController: NavHostController
    val isCurrentScreen: Boolean

    val graphNavEntry: NavBackStackEntry
}

interface InternalNavigationGraph : NavigationGraph {
    val startDestinationRoute: String

    fun find(details: DestinationDetails): Destination?

    fun findRoute(details: DestinationDetails): String?

    fun navigate(
        details: RequestedDestinationDetails,
        popUpTo: DestinationDetails? = null,
        popUpInclusive: Boolean = false,
    ): Boolean
}

class RequestedDestination(
    destination: PotentialDestination,
    popUpToString: String?,
    popUpInclusive: Boolean,
    args: Map<String, String?>,
) : Destination {

    override val details = destination.details.withArgs(args)

    val navOptions = (
        popUpToString
            ?.let {
                destination.navOptions
                    .setPopUpTo(popUpToString, popUpInclusive)
            }
            ?: destination.navOptions
        )
        .build()

    override val routeString: String = if (details.args.isEmpty()) {
        destination.routeString
    } else {
        details.args.entries.fold(destination.routeString) { route, entry ->
            entry.value?.let { route.replace("{${entry.key}}", it) } ?: route
        }
    }
}
