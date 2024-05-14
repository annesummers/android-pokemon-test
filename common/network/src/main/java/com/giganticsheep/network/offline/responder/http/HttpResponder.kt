package com.giganticsheep.network.offline.responder.http

import com.giganticsheep.network.offline.responder.Responder
import com.giganticsheep.network.offline.response.MockHttpRequest
import com.giganticsheep.network.offline.response.MockHttpResponse
import io.ktor.http.HttpStatusCode

abstract class HttpResponder(
    protected val status: HttpStatusCode,
) : Responder<MockHttpRequest, MockHttpResponse>
