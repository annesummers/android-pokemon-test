package com.giganticsheep.pokemon.data.generations

import com.giganticsheep.network.FileUtilities
import com.giganticsheep.network.offline.stub.EndpointStubs
import com.giganticsheep.network.offline.stub.HttpStubs
import io.ktor.http.HttpMethod

// TODO
internal class DefaultGenerationsStubs(
    fileUtilities: FileUtilities,
) : GenerationsStubs(fileUtilities) {

    override fun getStub(
        callData: EndpointStubs.CallData,
    ) = when (callData as Call) {
        Call.GENERATION_LIST -> stub(callData) {
            filename = "generations.json"
        }
        Call.GENERATION -> stub(callData) {
            filename = "generation_$id.json"
        }
    }
}

abstract class GenerationsStubs(
    fileUtilities: FileUtilities,
) : HttpStubs("GENERATIONS", fileUtilities) {

    final override val calls = Call.entries

    enum class Call(
        override val type: HttpMethod,
        override val url: String,
    ) : CallData {
        GENERATION_LIST(HttpMethod.Get, "generation"),
        GENERATION(HttpMethod.Get, "generation/$id"),
        ;

        override val key: String = name
    }

    companion object {
        internal val id = "[a-z-]*"
    }
}
