package com.giganticsheep.pokemon.data.generations

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class GenerationEndpointManager @Inject constructor() {

    val generation = "generation"

    fun generation(name: String) = "$generation/$name"
}
