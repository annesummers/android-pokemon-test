package com.giganticsheep.pokemon.data.species

import com.giganticsheep.network.FileUtilities
import com.giganticsheep.network.offline.stub.EndpointStubs
import com.giganticsheep.network.offline.stub.HttpStubs
import io.ktor.http.HttpMethod

abstract class SpeciesStubs(
    fileUtilities: FileUtilities,
) : HttpStubs("SPECIES", fileUtilities) {

    final override val calls = Call.entries

    enum class Call(
        override val type: HttpMethod,
        override val url: String,
    ) : CallData {
        SPECIES_LIST(HttpMethod.Get, "pokemon-species?offset=$offset&limit=$limit"),
        SPECIES_BY_NUMBER(HttpMethod.Get, "pokemon-species/$number"),
        SPECIES_BY_NAME(HttpMethod.Get, "pokemon-species/$name"),
        ;

        override val key: String = name
    }

    companion object {
        internal const val number = "[0-9]+"
        internal const val name = "[a-z-]+"
        internal const val offset = "[0-9]+"
        internal const val limit = "[0-9]+"
    }
}

internal class DefaultSpeciesStubs(
    fileUtilities: FileUtilities,
) : SpeciesStubs(fileUtilities) {

    override fun getStub(
        callData: EndpointStubs.CallData,
    ) = when (callData as Call) {
        Call.SPECIES_LIST -> stub(callData) {
            filename = "species-list.json"
        }
        Call.SPECIES_BY_NUMBER -> stub(callData) {
            filename = "species-$number.json"
            regexSubstitution = number
        }
        Call.SPECIES_BY_NAME -> stub(callData) {
            filename = "species-$name.json"
            trailingSubstitution = name
        }
    }
}
