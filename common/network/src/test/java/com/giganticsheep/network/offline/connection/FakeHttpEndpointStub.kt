package com.giganticsheep.network.offline.connection

import com.giganticsheep.network.offline.response.HttpEndpointStub
import com.giganticsheep.network.offline.response.MockHttpRequest
import com.giganticsheep.network.offline.response.MockHttpResponse
import com.giganticsheep.network.offline.stub.HttpStubs
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

class FakeHttpEndpointStub(
    private val _type: HttpMethod,
    private val canRespond: Boolean,
    private val body: String,
    private val status: HttpStatusCode,
) : HttpEndpointStub {

    override val call = object : HttpStubs.CallData {
        override val url = "url"
        override val key = "KEY"
        override val type = _type
    }

    override fun canRespond(request: MockHttpRequest) = canRespond

    override fun respond(
        request: MockHttpRequest,
    ) = MockHttpResponse(request.respond(body, status))
}
