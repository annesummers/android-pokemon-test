package com.giganticsheep.network.offline.response

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.giganticsheep.network.TestException
import com.giganticsheep.network.offline.responder.MatchBodyResponder
import com.giganticsheep.network.offline.responder.http.HttpResponder
import com.giganticsheep.network.offline.stub.HttpStubs
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpMethod
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.headersOf
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.toByteArray
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test

internal open class FakeHttpResponder(
    httpStatusCode: HttpStatusCode,
    private val response: (HttpResponseData) -> MockHttpResponse,
) : HttpResponder(httpStatusCode) {

    override fun respond(
        request: MockHttpRequest,
    ) = response(request.respond("", status))
}

internal class FakeHttpMatchBodyFileResponder(
    private val canMatch: Boolean,
    httpStatusCode: HttpStatusCode,
    response: (HttpResponseData) -> MockHttpResponse,
) : FakeHttpResponder(httpStatusCode, response), MatchBodyResponder {

    override val match: String = "match"

    override fun isMatch(
        requestBody: String,
    ) = canMatch
}

internal class HttpEndpointStubTest {

    @Test
    fun `test can respond`() = runTest {
        val myUrl = "my-url/with/query?Match23"

        val fakeResponder = FakeHttpResponder(
            httpStatusCode = HttpStatusCode.OK,
        ) { MockHttpResponse(it) }

        val endpointStub = HttpEndpointStub.create(
            call = getCallData(myUrl, "Match23"),
            responder = fakeResponder,
        )

        val request = MockHttpRequest(
            _url = Url(URLBuilder(myUrl)),
            method = HttpMethod.Get,
            body = "body",
        ) { content, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, content) }

        assertThat(endpointStub.canRespond(request))
            .isTrue()
    }

    @Test
    fun `test can respond match body`() = runTest {
        val myUrl = "my-url/with/query?Match23"

        val fakeResponder = FakeHttpMatchBodyFileResponder(
            httpStatusCode = HttpStatusCode.OK,
            canMatch = true,
        ) { MockHttpResponse(it) }

        val endpointStub = HttpEndpointStub.create(
            call = getCallData(myUrl, "Match23"),
            responder = fakeResponder,
        )

        val request = MockHttpRequest(
            _url = Url(URLBuilder(myUrl)),
            method = HttpMethod.Get,
            body = "body",
        ) { content, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, content) }

        assertThat(endpointStub.canRespond(request))
            .isTrue()
    }

    @Test
    fun `test cannot respond`() = runTest {
        val myUrl = "my-url/with/query?Match24"

        val fakeResponder = FakeHttpResponder(
            httpStatusCode = HttpStatusCode.OK,
        ) { MockHttpResponse(it) }

        val endpointStub = HttpEndpointStub.create(
            call = getCallData(myUrl, "Match23"),
            responder = fakeResponder,
        )

        val request = MockHttpRequest(
            _url = Url(URLBuilder(myUrl)),
            method = HttpMethod.Get,
            body = "body",
        ) { content, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, content) }

        assertThat(endpointStub.canRespond(request))
            .isFalse()
    }

    @Test
    fun `test cannot respond match body`() = runTest {
        val myUrl = "my-url/with/query?Match23"

        val fakeResponder = FakeHttpMatchBodyFileResponder(
            httpStatusCode = HttpStatusCode.OK,
            canMatch = false,
        ) { MockHttpResponse(it) }

        val endpointStub = HttpEndpointStub.create(
            call = getCallData(myUrl, "Match23"),
            responder = fakeResponder,
        )

        val request = MockHttpRequest(
            _url = Url(URLBuilder(myUrl)),
            method = HttpMethod.Get,
            body = "body",
        ) { content, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, content) }

        assertThat(endpointStub.canRespond(request))
            .isFalse()
    }

    @Test
    fun `test respond`() = runTest {
        val httpStatusCode = HttpStatusCode.OK
        val body = "body"

        val fakeResponder = FakeHttpResponder(
            httpStatusCode = httpStatusCode,
        ) { MockHttpResponse(it) }

        val endpointStub = HttpEndpointStub.create(
            call = getCallData(),
            responder = fakeResponder,
        )

        val checkResponse = getResponse(coroutineContext, httpStatusCode, body)

        val request = MockHttpRequest(
            _url = Url(URLBuilder("")),
            method = HttpMethod.Get,
            body = body,
        ) { _, _ -> checkResponse }

        val response = endpointStub.respond(request)

        assertThat(response.httpResponseData)
            .isEqualTo(checkResponse)
    }

    @Test
    fun `test respond error`() = runTest {
        val failedMessage = "Failed"
        val fakeResponder = FakeHttpResponder(
            httpStatusCode = HttpStatusCode.OK,
        ) { throw TestException(failedMessage) }

        val endpointStub = HttpEndpointStub.create(
            call = getCallData(),
            responder = fakeResponder,
        )

        val request = MockHttpRequest(
            _url = Url(URLBuilder()),
            method = HttpMethod.Get,
            body = "body",
        ) { content, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, content) }

        val response = endpointStub.respond(request).httpResponseData

        assertThat(response.statusCode)
            .isEqualTo(HttpStatusCode.BadRequest)

        val checkBody = (response.body as ByteReadChannel).readUTF8Line()

        assertThat(checkBody)
            .isEqualTo("Failed")
    }

    private fun getResponse(
        coroutineContext: CoroutineContext,
        statusCode: HttpStatusCode = HttpStatusCode.OK,
        content: String? = null,
    ) = HttpResponseData(
        statusCode = statusCode,
        requestTime = GMTDate(),
        headers = headersOf(),
        version = HttpProtocolVersion.HTTP_1_1,
        body = content?.let { ByteReadChannel(it.toByteArray(Charsets.UTF_8)) } ?: Any(),
        callContext = coroutineContext,
    )

    private fun getCallData(
        url: String = "",
        regexString: String = "",
    ) = object : HttpStubs.CallData {
        override val url = url
        override val urlRegex = Regex(regexString)
        override val key = "KEY"
        override val type = HttpMethod.Get
    }
}
