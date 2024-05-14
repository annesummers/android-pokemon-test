package com.giganticsheep.navigation

import com.giganticsheep.navigation.PotentialDestinationDetails

abstract class Navigation(name: String, args: List<String>) {

    val details = object : PotentialDestinationDetails {
        override val destinationName = name
        override val args: List<String> = args
    }
}
