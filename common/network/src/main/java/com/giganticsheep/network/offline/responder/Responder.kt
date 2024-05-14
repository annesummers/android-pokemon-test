package com.giganticsheep.network.offline.responder

import com.giganticsheep.network.offline.response.MockRequest
import com.giganticsheep.network.offline.response.MockResponse

interface Responder<Request : MockRequest, Response : MockResponse> {

    fun respond(
        request: Request,
    ): Response
}

interface FileResponder<Request : MockRequest, Response : MockResponse> : Responder<Request, Response>

interface MatchBodyResponder {
    val match: String

    fun isMatch(
        requestBody: String,
    ): Boolean
}

class MatchBodyResponded(
    override val match: String,
) : MatchBodyResponder {

    override fun isMatch(
        requestBody: String,
    ) = if (requestBody.isEmpty()) {
        false
    } else {
        requestBody.indexOf(match) != -1
    }
}
