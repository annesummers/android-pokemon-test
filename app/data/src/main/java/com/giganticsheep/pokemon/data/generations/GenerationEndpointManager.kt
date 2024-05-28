package com.giganticsheep.pokemon.data.generations

@javax.inject.Singleton
internal class GenerationEndpointManager @javax.inject.Inject constructor() {

    val generation = "generation"

    fun generation(name: String) = "$generation/$name"
}