package com.giganticsheep.navigation

abstract class Navigation(name: String, args: List<String>) {

    val details = object : PotentialDestinationDetails {
        override val destinationName = name
        override val args: List<String> = args
    }
}
