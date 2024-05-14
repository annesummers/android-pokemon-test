package com.giganticsheep.navigation

import android.app.Activity
import androidx.compose.runtime.Stable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Stable
class Navigator {

    private val _navigationEvents = Channel<Direction>(Channel.UNLIMITED)

    private val navigationEvents: Flow<Direction> = _navigationEvents.receiveAsFlow()

    private val navigationGraphs = NavigationGraphs()

    fun navigate(
        details: RequestedDestinationDetails,
        popUpTo: DestinationDetails? = null,
        popUpInclusive: Boolean = false,
    ) {
        _navigationEvents.trySend(Direction.Navigate(details, popUpTo, popUpInclusive))
    }

    fun navigate(
        details: PotentialDestinationDetails,
        popUpTo: DestinationDetails? = null,
        popUpInclusive: Boolean = false,
    ) {
        _navigationEvents.trySend(Direction.Navigate(details.asRequested, popUpTo, popUpInclusive))
    }

    fun navigateBack() {
        _navigationEvents.trySend(Direction.NavigateBack)
    }

    internal fun setupWithNavigationGraph(
        navigationGraph: InternalNavigationGraph,
    ) {
        navigationGraphs.add(navigationGraph)
    }

    internal fun startCollecting(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.apply { collectOnLifecycle() }
    }

    private fun LifecycleOwner.collectOnLifecycle() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                navigationEvents.collect { direction ->

                    when (direction) {
                        is Direction.Navigate ->
                            navigationGraphs.navigate(
                                direction.details,
                                direction.popUpTo,
                                direction.popUpInclusive
                            )

                        is Direction.NavigateBack ->
                            if (!navigationGraphs.popBackStack()) {
                                (this as? Activity)?.finish()
                            }
                    }
                }
            }
        }
    }

    internal class NavigationGraphs {

        private lateinit var currentDetails: DestinationDetails

        private val graphs: MutableSet<InternalNavigationGraph> = mutableSetOf()

        fun add(navigationGraph: InternalNavigationGraph) {
            graphs.add(navigationGraph)
        }

        fun navigate(
            details: RequestedDestinationDetails,
            popUpTo: DestinationDetails? = null,
            popUpInclusive: Boolean = false,
        ) {
            graphs
                .firstOrNull { it.navigate(details, popUpTo, popUpInclusive) }
                ?.also { currentDetails = details }
        }

        fun popBackStack() = graphs
            .firstOrNull { it.find(currentDetails)?.let { true } ?: false }
            ?.navHostController
            ?.popBackStack()
            ?: true // we can pretend we popped if we don't find our graph
    }

    internal sealed interface Direction {
        data class Navigate(
            val details: RequestedDestinationDetails,
            val popUpTo: DestinationDetails?,
            val popUpInclusive: Boolean,
        ) : Direction

        data object NavigateBack : Direction
    }
}
