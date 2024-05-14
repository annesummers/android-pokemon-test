package com.giganticsheep.network.offline.responder.http

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.giganticsheep.network.FakeJsonUtilities
import com.giganticsheep.network.offline.FakeFileUtilities
import com.giganticsheep.network.offline.response.MockHttpRequest
import com.giganticsheep.network.offline.stub.RequestResponseCallMap
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.path
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.test.runTest
import kotlin.reflect.typeOf
import kotlin.test.Test

internal class HttpResponderTest {

    private val fakeJsonUtilities = FakeJsonUtilities()

    @Test
    fun `test file responder`() = runTest {
        val filename = "filename"
        val mockRequestHandleScope = MockRequestHandleScope(this@runTest.coroutineContext)

        val fakeFileUtilities = FakeFileUtilities(filename)

        val httpRequest = MockHttpRequest(
            _url = Url("url"),
            method = HttpMethod.Get,
            body = "body",
        ) { body, httpStatusCode -> mockRequestHandleScope.respond(body, httpStatusCode) }

        val responder = FileHttpResponder(
            status = HttpStatusCode.OK,
            filename = filename,
            fileUtilities = fakeFileUtilities,
        )

        val response = responder.respond(httpRequest)
        val checkResponse = mockRequestHandleScope.respondOk(fakeFileUtilities.fileContents)

        assertThat((response.httpResponseData.body as ByteReadChannel).readUTF8Line())
            .isEqualTo((checkResponse.body as ByteReadChannel).readUTF8Line())

        assertThat(response.httpResponseData.statusCode)
            .isEqualTo(checkResponse.statusCode)
    }

    @Test
    fun `test substitution file responder`() = runTest {
        val mockRequestHandleScope = MockRequestHandleScope(this@runTest.coroutineContext)

        val filename = "file[0-9]{4}name"
        val substitution = "[0-9]{4}"
        val path = "/my/url/1234"
        val subFilename = "file1234name"

        val fakeFileUtilities = FakeFileUtilities(subFilename)

        val httpRequest = MockHttpRequest(
            _url = Url(URLBuilder().apply { path(path) }),
            method = HttpMethod.Get,
            body = "body",
        ) { body, httpStatusCode -> mockRequestHandleScope.respond(body, httpStatusCode) }

        val responder = SubstitutionFileHttpResponder(
            status = HttpStatusCode.OK,
            filename = filename,
            fileUtilities = fakeFileUtilities,
            substitution = substitution,
        )

        val response = responder.respond(httpRequest)
        val checkResponse = mockRequestHandleScope.respondOk(fakeFileUtilities.fileContents)

        assertThat(response.httpResponseData.statusCode)
            .isEqualTo(checkResponse.statusCode)

        assertThat((response.httpResponseData.body as ByteReadChannel).readUTF8Line())
            .isEqualTo((checkResponse.body as ByteReadChannel).readUTF8Line())
    }

    @Test
    fun `test response code responder`() = runTest {
        val mockRequestHandleScope = MockRequestHandleScope(this@runTest.coroutineContext)

        val httpRequest = MockHttpRequest(
            _url = Url("url"),
            method = HttpMethod.Get,
            body = "body",
        ) { body, httpStatusCode -> mockRequestHandleScope.respond(body, httpStatusCode) }

        val responder = ResponseCodeHttpResponder(
            status = HttpStatusCode.OK,
        )

        val response = responder.respond(httpRequest)
        val checkResponse = mockRequestHandleScope.respondOk("")

        assertThat(response.httpResponseData.statusCode)
            .isEqualTo(checkResponse.statusCode)

        assertThat((response.httpResponseData.body as ByteReadChannel).readUTF8Line())
            .isEqualTo((checkResponse.body as ByteReadChannel).readUTF8Line())
    }

    @Test
    fun `test request to response responder`() = runTest {
        val mockRequestHandleScope = MockRequestHandleScope(this@runTest.coroutineContext)

        val httpRequest = MockHttpRequest(
            _url = Url("url"),
            method = HttpMethod.Get,
            body = fakeJsonUtilities.jsonString,
        ) { body, httpStatusCode -> mockRequestHandleScope.respond(body, httpStatusCode) }

        val responder = RequestToResponseHttpResponder(
            status = HttpStatusCode.OK,
            mapper = RequestResponseCallMap<FakeJsonUtilities.TestClass, FakeJsonUtilities.TestClass>(
                requestType = typeOf<FakeJsonUtilities.TestClass>(),
                responseType = typeOf<FakeJsonUtilities.TestClass>(),
                jsonUtilities = fakeJsonUtilities,
            ) {
                assertThat(it)
                    .isEqualTo(fakeJsonUtilities.jsonObject)

                fakeJsonUtilities.jsonObject
            },
        )

        val response = responder.respond(httpRequest)
        val checkResponse = mockRequestHandleScope.respondOk(fakeJsonUtilities.jsonString)

        assertThat(response.httpResponseData.statusCode)
            .isEqualTo(checkResponse.statusCode)

        assertThat((response.httpResponseData.body as ByteReadChannel).readUTF8Line())
            .isEqualTo((checkResponse.body as ByteReadChannel).readUTF8Line())
    }
}
