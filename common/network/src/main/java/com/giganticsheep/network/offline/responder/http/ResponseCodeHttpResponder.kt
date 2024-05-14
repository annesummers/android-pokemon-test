package com.giganticsheep.network.offline.responder.http

import com.giganticsheep.network.offline.response.MockHttpRequest
import com.giganticsheep.network.offline.response.MockHttpResponse
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode

internal class ResponseCodeHttpResponder(
    status: HttpStatusCode,
) : HttpResponder(status) {

    override fun respond(
        request: MockHttpRequest,
    ) = MockHttpResponse(
        request.respond("", status),
    )
}
