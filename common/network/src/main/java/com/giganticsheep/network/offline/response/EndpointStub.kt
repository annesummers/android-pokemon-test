package com.giganticsheep.network.offline.response

interface MockRequest {
    val url: String
    val body: String
}

interface MockResponse

interface EndpointStub<Request : MockRequest, Response : MockResponse> {

    fun canRespond(
        request: Request,
    ): Boolean

    fun respond(
        request: Request,
    ): Response
}
