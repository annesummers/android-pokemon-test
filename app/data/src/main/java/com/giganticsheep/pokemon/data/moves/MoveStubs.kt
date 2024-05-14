package com.giganticsheep.pokemon.data.moves

import com.giganticsheep.network.FileUtilities
import com.giganticsheep.network.offline.stub.EndpointStubs
import com.giganticsheep.network.offline.stub.HttpStubs
import io.ktor.http.HttpMethod

//TODO
internal class DefaultMoveStubs(
    fileUtilities: FileUtilities,
) : MoveStubs(fileUtilities) {

    override fun getStub(
        callData: EndpointStubs.CallData
    ) = when (callData as Call) {
        Call.LOGIN -> TODO()
    }
}

abstract class MoveStubs(
    fileUtilities: FileUtilities,
) : HttpStubs("GENERATIONS", fileUtilities) {

    final override val calls = Call.entries

    enum class Call(
        override val type: HttpMethod,
        override val url: String,
    ) : CallData {
        LOGIN(HttpMethod.Post, "login");

        override val key: String = name
    }
}
