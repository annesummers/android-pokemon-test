package com.giganticsheep.network.offline.connection

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.giganticsheep.network.FakeJsonUtilities
import com.giganticsheep.network.offline.FakeFileUtilities
import com.giganticsheep.network.offline.response.HttpEndpointStub
import com.giganticsheep.network.offline.response.MockHttpRequest
import com.giganticsheep.network.offline.stub.EndpointStubs
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

internal class HttpStubbedConnectionTest {

    abstract class TestHttpStubs(
        name: String,
        override val calls: List<CallData>,
    ) : HttpStubs(
        name,
        FakeFileUtilities(""),
        FakeJsonUtilities(),
    )

    private val bodies = listOf(
        "body",
        "body2",
        "body3",
        "body4",
        "body5",
        "body6",
        "body7",
        "body8",
    )

    private val stubProviders = StubProviders(
        HttpStubProvider(
            object : TestHttpStubs(
                "TEST",
                listOf(
                    object : CallData {
                        override val url: String
                            get() = TODO("Not yet implemented")
                        override val key: String
                            get() = "key"
                        override val type: HttpMethod
                            get() = TODO("Not yet implemented")
                    },
                    object : CallData {
                        override val url: String
                            get() = TODO("Not yet implemented")
                        override val key: String
                            get() = "key2"
                        override val type: HttpMethod
                            get() = TODO("Not yet implemented")
                    },
                ),
            ) {
                override fun getStub(
                    callData: EndpointStubs.CallData,
                ): HttpEndpointStub = when (callData.key) {
                    "key" -> FakeHttpEndpointStub(
                        _type = HttpMethod.Get,
                        canRespond = false,
                        body = bodies[0],
                        status = HttpStatusCode.OK,
                    )

                    "key2" -> FakeHttpEndpointStub(
                        _type = HttpMethod.Put,
                        canRespond = true,
                        body = bodies[1],
                        status = HttpStatusCode.OK,
                    )

                    else -> error("")
                }
            },
            object : TestHttpStubs(
                "TEST2",
                listOf(
                    object : CallData {
                        override val url: String
                            get() = TODO("Not yet implemented")
                        override val key: String
                            get() = "key"
                        override val type: HttpMethod
                            get() = TODO("Not yet implemented")
                    },
                    object : CallData {
                        override val url: String
                            get() = TODO("Not yet implemented")
                        override val key: String
                            get() = "key2"
                        override val type: HttpMethod
                            get() = TODO("Not yet implemented")
                    },
                    object : CallData {
                        override val url: String
                            get() = TODO("Not yet implemented")
                        override val key: String
                            get() = "key3"
                        override val type: HttpMethod
                            get() = TODO("Not yet implemented")
                    },
                ),
            ) {
                override fun getStub(
                    callData: EndpointStubs.CallData,
                ): HttpEndpointStub = when (callData.key) {
                    "key" -> FakeHttpEndpointStub(
                        _type = HttpMethod.Get,
                        canRespond = true,
                        body = bodies[2],
                        status = HttpStatusCode.OK,
                    )

                    "key2" -> FakeHttpEndpointStub(
                        _type = HttpMethod.Post,
                        canRespond = false,
                        body = bodies[3],
                        status = HttpStatusCode.OK,
                    )

                    "key3" -> FakeHttpEndpointStub(
                        _type = HttpMethod.Put,
                        canRespond = false,
                        body = bodies[4],
                        status = HttpStatusCode.OK,
                    )

                    else -> error("")
                }
            },
        ),
        HttpStubProvider(
            object : TestHttpStubs(
                "TEST3",
                listOf(
                    object : CallData {
                        override val url: String
                            get() = TODO("Not yet implemented")
                        override val key: String
                            get() = "key"
                        override val type: HttpMethod
                            get() = TODO("Not yet implemented")
                    },
                    object : CallData {
                        override val url: String
                            get() = TODO("Not yet implemented")
                        override val key: String
                            get() = "key2"
                        override val type: HttpMethod
                            get() = TODO("Not yet implemented")
                    },
                    object : CallData {
                        override val url: String
                            get() = TODO("Not yet implemented")
                        override val key: String
                            get() = "key3"
                        override val type: HttpMethod
                            get() = TODO("Not yet implemented")
                    },
                ),
            ) {
                override fun getStub(
                    callData: EndpointStubs.CallData,
                ): HttpEndpointStub = when (callData.key) {
                    "key" -> FakeHttpEndpointStub(
                        _type = HttpMethod.Delete,
                        canRespond = false,
                        body = bodies[5],
                        status = HttpStatusCode.OK,
                    )

                    "key2" -> FakeHttpEndpointStub(
                        _type = HttpMethod.Post,
                        canRespond = true,
                        body = bodies[6],
                        status = HttpStatusCode.OK,
                    )

                    "key3" -> FakeHttpEndpointStub(
                        _type = HttpMethod.Delete,
                        canRespond = true,
                        body = bodies[7],
                        status = HttpStatusCode.OK,
                    )

                    else -> error("")
                }
            },
        ),
    )

    private val stubbedConnection = HttpStubbedConnection(stubProviders.stubs<HttpStubCollector>())

    @Test
    fun `test find response for get`() = runTest {
        val httpRequest = MockHttpRequest(
            _url = Url(URLBuilder()),
            method = HttpMethod.Get,
            body = "",
        ) { body, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, body) }

        assertThat((stubbedConnection.respond(httpRequest).httpResponseData.body as ByteReadChannel).readUTF8Line())
            .isEqualTo(bodies[2])
    }

    @Test
    fun `test find response for put`() = runTest {
        val httpRequest = MockHttpRequest(
            _url = Url(URLBuilder()),
            method = HttpMethod.Put,
            body = "",
        ) { body, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, body) }

        assertThat((stubbedConnection.respond(httpRequest).httpResponseData.body as ByteReadChannel).readUTF8Line())
            .isEqualTo(bodies[1])
    }

    @Test
    fun `test find response for post`() = runTest {
        val httpRequest = MockHttpRequest(
            _url = Url(URLBuilder()),
            method = HttpMethod.Post,
            body = "",
        ) { body, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, body) }

        assertThat((stubbedConnection.respond(httpRequest).httpResponseData.body as ByteReadChannel).readUTF8Line())
            .isEqualTo(bodies[6])
    }

    @Test
    fun `test find response for delete`() = runTest {
        val httpRequest = MockHttpRequest(
            _url = Url(URLBuilder()),
            method = HttpMethod.Delete,
            body = "",
        ) { body, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, body) }

        assertThat((stubbedConnection.respond(httpRequest).httpResponseData.body as ByteReadChannel).readUTF8Line())
            .isEqualTo(bodies[7])
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
}
