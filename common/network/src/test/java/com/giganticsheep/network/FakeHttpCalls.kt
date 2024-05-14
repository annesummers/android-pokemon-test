package com.giganticsheep.network

import com.giganticsheep.network.client.HttpCalls
import io.ktor.client.request.HttpRequestBuilder

class FakeGetHttpCalls(
    private val fakeResponse: (String) -> HttpResponse,
) : HttpCalls {

    override suspend fun get(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = fakeResponse(path)

    override suspend fun <B : Any> put(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun <B : Any> post(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun post(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun delete(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun <B : Any> delete(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")
}

class FakePutHttpCalls(
    private val fakeResponse: (String) -> HttpResponse,
) : HttpCalls {

    override suspend fun get(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun <B : Any> put(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = fakeResponse(path)

    override suspend fun <B : Any> post(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun post(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun delete(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun <B : Any> delete(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")
}

class FakePostHttpCalls(
    private val fakeResponse: (String) -> HttpResponse,
) : HttpCalls {

    override suspend fun get(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun <B : Any> put(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun <B : Any> post(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = fakeResponse(path)

    override suspend fun post(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun delete(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun <B : Any> delete(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")
}

class FakePostNoBodyHttpCalls(
    private val fakeResponse: (String) -> HttpResponse,
) : HttpCalls {

    override suspend fun get(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun <B : Any> put(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun <B : Any> post(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun post(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = fakeResponse(path)

    override suspend fun delete(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun <B : Any> delete(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")
}

class FakeDeleteHttpCalls(
    private val fakeResponse: (String) -> HttpResponse,
) : HttpCalls {

    override suspend fun get(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun <B : Any> put(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun <B : Any> post(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun post(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun delete(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun <B : Any> delete(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = fakeResponse(path)
}

class FakeDeleteNoBodyHttpCalls(
    private val fakeResponse: (String) -> HttpResponse,
) : HttpCalls {

    override suspend fun get(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun <B : Any> put(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun <B : Any> post(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun post(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")

    override suspend fun delete(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = fakeResponse(path)

    override suspend fun <B : Any> delete(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverReqeust: HttpRequestBuilder.() -> Unit,
    ) = error("")
}
