package com.giganticsheep.pokemon.data.species

import com.giganticsheep.network.FileUtilities
import com.giganticsheep.network.offline.stub.EndpointStubs
import com.giganticsheep.network.offline.stub.HttpStubs
import com.giganticsheep.pokemon.data.generations.GenerationsStubs
import io.ktor.http.HttpMethod

//TODO
internal class DefaultSpeciesStubs(
    fileUtilities: FileUtilities,
) : SpeciesStubs(fileUtilities) {

    override fun getStub(
        callData: EndpointStubs.CallData
    ) = when (callData as Call) {
        Call.SPECIES_LIST -> stub(callData) {
            filename = "species-list.json"
        }
        Call.SPECIES -> stub(callData) {
            filename = "species_$id.json"
            substitution = id
        }
    }
}

abstract class SpeciesStubs(
    fileUtilities: FileUtilities,
) : HttpStubs("SPECIES", fileUtilities) {

    final override val calls = Call.entries

    enum class Call(
        override val type: HttpMethod,
        override val url: String,
    ) : CallData {
        SPECIES_LIST(HttpMethod.Get, "pokemon-species"),
        SPECIES(HttpMethod.Get, "pokemon-species/$id");

        override val key: String = name
    }

    companion object {
        const val id = "[a-z-]*"
    }
}
