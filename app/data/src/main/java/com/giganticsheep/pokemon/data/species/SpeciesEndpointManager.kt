package com.giganticsheep.pokemon.data.species

import com.giganticsheep.pokemon.data.EndpointManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeciesEndpointManager @Inject constructor() : EndpointManager() {
    fun species(id: String) = "$species/$id"

    val species = "pokemon-species"
}