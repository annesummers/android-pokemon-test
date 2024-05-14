package com.giganticsheep.network.offline.responder.http

import com.giganticsheep.network.offline.response.MockHttpRequest
import com.giganticsheep.network.offline.response.MockHttpResponse
import com.giganticsheep.network.offline.stub.CallMap
import io.ktor.http.HttpStatusCode

internal class RequestToResponseHttpResponder(
    status: HttpStatusCode,
    private val mapper: CallMap,
) : HttpResponder(status) {

    override fun respond(
        request: MockHttpRequest,
    ) = MockHttpResponse(
        request.respond(
            mapper.respond(request.body),
            status,
        ),
    )
}
