package com.giganticsheep.network.offline.stub

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.giganticsheep.network.FileUtilities
import com.giganticsheep.network.offline.FakeFileUtilities
import com.giganticsheep.network.offline.response.MockHttpRequest
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpMethod
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.headersOf
import io.ktor.http.path
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.toByteArray
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test

private const val id = "[0-9]{8}"

internal class HttpStubsTest {

    private class TestHttpStubs(
        fileUtilities: FileUtilities,
    ) : HttpStubs(
        "TEST",
        fileUtilities,
    ) {
        override val calls: List<EndpointStubs.CallData> = Call.entries

        override fun getStub(
            callData: EndpointStubs.CallData
        ) = when (callData as Call) {
            Call.TEST_GET -> stub(callData) {
                filename = "get_filename"
            }
            Call.TEST_GET2 ->  stub(callData) {
                filename = "get_filename2"
                status = HttpStatusCode.OK
            }
            Call.TEST_GET3 -> stub(callData) {
                filename = "get_filename_$id"
                status = HttpStatusCode.OK
                substitution = id
            }
            Call.TEST_PUT -> stub(callData) {
                filename = "put_filename"
                status = HttpStatusCode.OK
                bodyMatch = "body"
            }
            Call.TEST_PUT_NO_RESPONSE -> stub(callData) {
                status = HttpStatusCode.OK
            }
            Call.TEST_POST -> stub(callData) {
                filename = "post_filename"
                status = HttpStatusCode.OK
                bodyMatch = "body"
            }
            Call.TEST_POST_NO_RESPONSE -> stub(callData) {
                status = HttpStatusCode.Created
            }
            Call.TEST_DELETE -> stub(callData) {
                filename = "delete_filename"
                status = HttpStatusCode.OK
                bodyMatch = "body"
            }
            Call.TEST_DELETE_NO_RESPONSE -> stub(callData) {
                status = HttpStatusCode.OK
            }
        }

        enum class Call(
            override val type: HttpMethod,
            override val url: String,
        ) : CallData {
            TEST_GET(HttpMethod.Get, "test/get"),
            TEST_GET2(HttpMethod.Get, "test/get2"),
            TEST_GET3(HttpMethod.Get, "test/get3/$id"),
            TEST_PUT(HttpMethod.Put, "test/put"),
            TEST_PUT_NO_RESPONSE(HttpMethod.Put, "test/put/noresponse"),
            TEST_POST(HttpMethod.Post, "test/post"),
            TEST_POST_NO_RESPONSE(HttpMethod.Post, "test/post/noresponse"),
            TEST_DELETE(HttpMethod.Delete, "test/delete"),
            TEST_DELETE_NO_RESPONSE(HttpMethod.Delete, "test/delete/noresponse"),
            ;

            override val key: String = name
        }
    }

    private lateinit var httpStubs: TestHttpStubs

    @Test
    fun `test get 1`() = runTest {
        val fakeFileUtilities = FakeFileUtilities("get_filename")

        val request = MockHttpRequest(
            _url = Url(URLBuilder().apply { path("my/url/test/get") }),
            method = HttpMethod.Get,
            body = "",
        ) { body, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, body) }

        httpStubs = TestHttpStubs(fakeFileUtilities)

        val requestResponse = httpStubs.stubs
            .find { it.call.key == "TEST|${TestHttpStubs.Call.TEST_GET.name}" }

        assertThat(requestResponse)
            .isNotNull()

        assertThat(requestResponse!!.canRespond(request))
            .isTrue()

        val response = requestResponse.respond(request)

        assertThat(response.httpResponseData.statusCode)
            .isEqualTo(HttpStatusCode.OK)

        assertThat((response.httpResponseData.body as ByteReadChannel).readUTF8Line())
            .isEqualTo(fakeFileUtilities.fileContents)
    }

    @Test
    fun `test get 2`() = runTest {
        val fakeFileUtilities = FakeFileUtilities("get_filename2")

        val request = MockHttpRequest(
            _url = Url(URLBuilder().apply { path("my/url/test/get2") }),
            method = HttpMethod.Get,
            body = "",
        ) { body, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, body) }

        httpStubs = TestHttpStubs(fakeFileUtilities)

        val requestResponse = httpStubs.stubs
            .groupBy { it.call.type }[request.method]
            ?.find { it.call.key == "TEST|${TestHttpStubs.Call.TEST_GET2.name}" }

        assertThat(requestResponse)
            .isNotNull()

        assertThat(requestResponse!!.canRespond(request))
            .isTrue()

        val response = requestResponse.respond(request)

        assertThat(response.httpResponseData.statusCode)
            .isEqualTo(HttpStatusCode.OK)

        assertThat((response.httpResponseData.body as ByteReadChannel).readUTF8Line())
            .isEqualTo(fakeFileUtilities.fileContents)
    }

    @Test
    fun `test get 3`() = runTest {
        val fakeFileUtilities = FakeFileUtilities("get_filename_12345678")

        val request = MockHttpRequest(
            _url = Url(URLBuilder().apply { path("my/url/test/get3/12345678") }),
            method = HttpMethod.Get,
            body = "",
        ) { body, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, body) }

        httpStubs = TestHttpStubs(fakeFileUtilities)

        val requestResponse = httpStubs.stubs
            .groupBy { it.call.type }[request.method]
            ?.find { it.call.key == "TEST|${TestHttpStubs.Call.TEST_GET3.name}" }

        assertThat(requestResponse)
            .isNotNull()

        assertThat(requestResponse!!.canRespond(request))
            .isTrue()

        val response = requestResponse.respond(request)

        assertThat(response.httpResponseData.statusCode)
            .isEqualTo(HttpStatusCode.OK)

        assertThat((response.httpResponseData.body as ByteReadChannel).readUTF8Line())
            .isEqualTo(fakeFileUtilities.fileContents)
    }

    @Test
    fun `test put body matches`() = runTest {
        val fakeFileUtilities = FakeFileUtilities("put_filename")

        val request = MockHttpRequest(
            _url = Url(URLBuilder().apply { path("my/url/test/put") }),
            method = HttpMethod.Put,
            body = "body",
        ) { body, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, body) }

        httpStubs = TestHttpStubs(fakeFileUtilities)

        val requestResponse = httpStubs.stubs
            .groupBy { it.call.type }[request.method]
            ?.find { it.call.key == "TEST|${TestHttpStubs.Call.TEST_PUT.name}" }

        assertThat(requestResponse)
            .isNotNull()

        assertThat(requestResponse!!.canRespond(request))
            .isTrue()

        val response = requestResponse.respond(request)

        assertThat(response.httpResponseData.statusCode)
            .isEqualTo(HttpStatusCode.OK)

        assertThat((response.httpResponseData.body as ByteReadChannel).readUTF8Line())
            .isEqualTo(fakeFileUtilities.fileContents)
    }

    @Test
    fun `test put body does not match`() = runTest {
        val fakeFileUtilities = FakeFileUtilities("put_filename")

        val request = MockHttpRequest(
            _url = Url(URLBuilder().apply { path("my/url/test/put") }),
            method = HttpMethod.Put,
            body = "random",
        ) { body, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, body) }

        httpStubs = TestHttpStubs(fakeFileUtilities)

        val requestResponse = httpStubs.stubs
            .groupBy { it.call.type }[request.method]
            ?.find { it.call.key == "TEST|${TestHttpStubs.Call.TEST_PUT.name}" }

        assertThat(requestResponse)
            .isNotNull()

        assertThat(requestResponse!!.canRespond(request))
            .isFalse()
    }

    @Test
    fun `test put no response`() = runTest {
        val fakeFileUtilities = FakeFileUtilities("")

        val request = MockHttpRequest(
            _url = Url(URLBuilder().apply { path("my/url/test/put/noresponse") }),
            method = HttpMethod.Put,
            body = "body",
        ) { body, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, body) }

        httpStubs = TestHttpStubs(fakeFileUtilities)

        val requestResponse = httpStubs.stubs
            .groupBy { it.call.type }[request.method]
            ?.find { it.call.key == "TEST|${TestHttpStubs.Call.TEST_PUT_NO_RESPONSE.name}" }

        assertThat(requestResponse)
            .isNotNull()

        assertThat(requestResponse!!.canRespond(request))
            .isTrue()

        val response = requestResponse.respond(request)

        assertThat(response.httpResponseData.statusCode)
            .isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `test post body matches`() = runTest {
        val fakeFileUtilities = FakeFileUtilities("post_filename")

        val request = MockHttpRequest(
            _url = Url(URLBuilder().apply { path("my/url/test/post") }),
            method = HttpMethod.Post,
            body = "body",
        ) { body, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, body) }

        httpStubs = TestHttpStubs(fakeFileUtilities)

        val requestResponse = httpStubs.stubs
            .groupBy { it.call.type }[request.method]
            ?.find { it.call.key == "TEST|${TestHttpStubs.Call.TEST_POST.name}" }

        assertThat(requestResponse)
            .isNotNull()

        assertThat(requestResponse!!.canRespond(request))
            .isTrue()

        val response = requestResponse.respond(request)

        assertThat(response.httpResponseData.statusCode)
            .isEqualTo(HttpStatusCode.OK)

        assertThat((response.httpResponseData.body as ByteReadChannel).readUTF8Line())
            .isEqualTo(fakeFileUtilities.fileContents)
    }

    @Test
    fun `test post body does not match`() = runTest {
        val fakeFileUtilities = FakeFileUtilities("post_filename")

        val request = MockHttpRequest(
            _url = Url(URLBuilder().apply { path("my/url/test/post") }),
            method = HttpMethod.Post,
            body = "random",
        ) { body, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, body) }

        httpStubs = TestHttpStubs(fakeFileUtilities)

        val requestResponse = httpStubs.stubs
            .groupBy { it.call.type }[request.method]
            ?.find { it.call.key == "TEST|${TestHttpStubs.Call.TEST_POST.name}" }

        assertThat(requestResponse)
            .isNotNull()

        assertThat(requestResponse!!.canRespond(request))
            .isFalse()
    }

    @Test
    fun `test post no response`() = runTest {
        val fakeFileUtilities = FakeFileUtilities("")

        val request = MockHttpRequest(
            _url = Url(URLBuilder().apply { path("my/url/test/post/noresponse") }),
            method = HttpMethod.Post,
            body = "body",
        ) { body, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, body) }

        httpStubs = TestHttpStubs(fakeFileUtilities)

        val requestResponse = httpStubs.stubs
            .groupBy { it.call.type }[request.method]
            ?.find { it.call.key == "TEST|${TestHttpStubs.Call.TEST_POST_NO_RESPONSE.name}" }

        assertThat(requestResponse)
            .isNotNull()

        assertThat(requestResponse!!.canRespond(request))
            .isTrue()

        val response = requestResponse.respond(request)

        assertThat(response.httpResponseData.statusCode)
            .isEqualTo(HttpStatusCode.Created)
    }

    @Test
    fun `test delete body matches`() = runTest {
        val fakeFileUtilities = FakeFileUtilities("delete_filename")

        val request = MockHttpRequest(
            _url = Url(URLBuilder().apply { path("my/url/test/delete") }),
            method = HttpMethod.Delete,
            body = "body",
        ) { body, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, body) }

        httpStubs = TestHttpStubs(fakeFileUtilities)

        val requestResponse = httpStubs.stubs
            .groupBy { it.call.type }[request.method]
            ?.find { it.call.key == "TEST|${TestHttpStubs.Call.TEST_DELETE.name}" }

        assertThat(requestResponse)
            .isNotNull()

        assertThat(requestResponse!!.canRespond(request))
            .isTrue()

        val response = requestResponse.respond(request)

        assertThat(response.httpResponseData.statusCode)
            .isEqualTo(HttpStatusCode.OK)

        assertThat((response.httpResponseData.body as ByteReadChannel).readUTF8Line())
            .isEqualTo(fakeFileUtilities.fileContents)
    }

    @Test
    fun `test delete body does not match`() = runTest {
        val fakeFileUtilities = FakeFileUtilities("delete_filename")

        val request = MockHttpRequest(
            _url = Url(URLBuilder().apply { path("my/url/test/delete") }),
            method = HttpMethod.Delete,
            body = "random",
        ) { body, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, body) }

        httpStubs = TestHttpStubs(fakeFileUtilities)

        val requestResponse = httpStubs.stubs
            .groupBy { it.call.type }[request.method]
            ?.find { it.call.key == "TEST|${TestHttpStubs.Call.TEST_DELETE.name}" }

        assertThat(requestResponse)
            .isNotNull()

        assertThat(requestResponse!!.canRespond(request))
            .isFalse()
    }

    @Test
    fun `test delete no response`() = runTest {
        val fakeFileUtilities = FakeFileUtilities("")

        val request = MockHttpRequest(
            _url = Url(URLBuilder().apply { path("my/url/test/delete/noresponse") }),
            method = HttpMethod.Delete,
            body = "body",
        ) { body, httpStatusCode -> getResponse(coroutineContext, httpStatusCode, body) }

        httpStubs = TestHttpStubs(fakeFileUtilities)

        val requestResponse = httpStubs.stubs
            .groupBy { it.call.type }[request.method]
            ?.find { it.call.key == "TEST|${TestHttpStubs.Call.TEST_DELETE_NO_RESPONSE.name}" }

        assertThat(requestResponse)
            .isNotNull()

        assertThat(requestResponse!!.canRespond(request))
            .isTrue()

        val response = requestResponse.respond(request)

        assertThat(response.httpResponseData.statusCode)
            .isEqualTo(HttpStatusCode.OK)
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
