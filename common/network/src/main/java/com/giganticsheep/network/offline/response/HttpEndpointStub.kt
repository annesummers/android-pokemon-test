package com.giganticsheep.network.offline.response

import com.giganticsheep.network.offline.responder.MatchBodyResponder
import com.giganticsheep.network.offline.responder.http.HttpResponder
import com.giganticsheep.network.offline.stub.HttpStubs
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.utils.EmptyContent
import io.ktor.content.ByteArrayContent
import io.ktor.content.TextContent
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.content.OutgoingContent
import org.jetbrains.annotations.VisibleForTesting
import kotlin.coroutines.cancellation.CancellationException

@VisibleForTesting
class MockHttpRequest(
    private val _url: Url,
    internal val method: HttpMethod,
    override val body: String,
    val respond: (String, HttpStatusCode) -> HttpResponseData,
) : MockRequest {

    constructor(
        request: HttpRequestData,
        mockRequestHandleScope: MockRequestHandleScope,
    ) : this(
        _url = request.url,
        method = request.method,
        body = when (request.body) {
            is ByteArrayContent ->
                (request.body as ByteArrayContent).bytes().decodeToString()

            is TextContent ->
                (request.body as TextContent).text

            is OutgoingContent.ByteArrayContent ->
                (request.body as OutgoingContent.ByteArrayContent).bytes().decodeToString()

            is EmptyContent ->
                ""

            else ->
                error("Unknown body type")
        },
        respond = { body, httpStatusCode -> mockRequestHandleScope.respond(body, httpStatusCode) },
    )

    override val url: String
        get() = _url.toString()

    internal val path: String
        get() = _url.encodedPathAndQuery
}

class MockHttpResponse(
    val httpResponseData: HttpResponseData,
) : MockResponse

interface HttpEndpointStub :
    EndpointStub<MockHttpRequest, MockHttpResponse> {

    val call: HttpStubs.CallData

    companion object {

        internal fun create(
            call: HttpStubs.CallData,
            responder: HttpResponder,
        ): HttpEndpointStub = InternalHttpEndpointStub(
            call = call,
            responder = responder,
        )
    }
}

private class InternalHttpEndpointStub(
    override val call: HttpStubs.CallData,
    private val responder: HttpResponder,
) : HttpEndpointStub {

    override fun canRespond(
        request: MockHttpRequest,
    ) = call.urlRegex.containsMatchIn(request.path) &&
        (responder !is MatchBodyResponder || responder.isMatch(request.body))

    override fun respond(
        request: MockHttpRequest,
    ) = try {
        responder.respond(request)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        MockHttpResponse(
            httpResponseData = request.respond(
                e.message ?: "Mock response failed",
                HttpStatusCode.BadRequest,
            ),
        )
    }
}
