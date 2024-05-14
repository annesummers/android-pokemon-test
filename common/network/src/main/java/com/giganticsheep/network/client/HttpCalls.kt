package com.giganticsheep.network.client

import com.giganticsheep.network.ErrorTransformer
import com.giganticsheep.network.ErrorTransformerBuilder
import com.giganticsheep.network.HttpResponse
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface HttpCalls {

    suspend fun get(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverRequest: HttpRequestBuilder.() -> Unit,
    ): HttpResponse

    suspend fun <B : Any> put(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverRequest: HttpRequestBuilder.() -> Unit,
    ): HttpResponse

    suspend fun <B : Any> post(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverRequest: HttpRequestBuilder.() -> Unit,
    ): HttpResponse

    suspend fun post(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverRequest: HttpRequestBuilder.() -> Unit,
    ): HttpResponse

    suspend fun delete(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverRequest: HttpRequestBuilder.() -> Unit,
    ): HttpResponse

    suspend fun <B : Any> delete(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverRequest: HttpRequestBuilder.() -> Unit,
    ): HttpResponse
}

internal class InternalHttpCalls(
    private val httpClient: io.ktor.client.HttpClient,
    private val universalErrorTransformers: ErrorTransformerBuilder.() -> Unit,
    private val coreErrorTransformer: ErrorTransformer,
) : HttpCalls {

    override suspend fun get(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverRequest: HttpRequestBuilder.() -> Unit,
    ) = HttpResponse(
        universalErrorTransformers = universalErrorTransformers,
        coreErrorTransformer = coreErrorTransformer,
        httpResponse = httpClient.get(path) {
            request()
            serverRequest()
            callRequest()
        },
    )

    override suspend fun <B : Any> put(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverRequest: HttpRequestBuilder.() -> Unit,
    ) = HttpResponse(
        universalErrorTransformers = universalErrorTransformers,
        coreErrorTransformer = coreErrorTransformer,
        httpResponse = httpClient.put(path) {
            request(body)
            serverRequest()
            callRequest()
        },
    )

    override suspend fun <B : Any> post(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverRequest: HttpRequestBuilder.() -> Unit,
    ) = HttpResponse(
        universalErrorTransformers = universalErrorTransformers,
        coreErrorTransformer = coreErrorTransformer,
        httpResponse = httpClient.post(path) {
            request(body)
            serverRequest()
            callRequest()
        },
    )

    override suspend fun post(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverRequest: HttpRequestBuilder.() -> Unit,
    ) = HttpResponse(
        universalErrorTransformers = universalErrorTransformers,
        coreErrorTransformer = coreErrorTransformer,
        httpResponse = httpClient.post(path) {
            request()
            serverRequest()
            callRequest()
        },
    )

    override suspend fun delete(
        path: String,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverRequest: HttpRequestBuilder.() -> Unit,
    ) = HttpResponse(
        universalErrorTransformers = universalErrorTransformers,
        coreErrorTransformer = coreErrorTransformer,
        httpResponse = httpClient.delete(path) {
            request()
            serverRequest()
            callRequest()
        },
    )

    override suspend fun <B : Any> delete(
        path: String,
        body: B,
        callRequest: HttpRequestBuilder.() -> Unit,
        serverRequest: HttpRequestBuilder.() -> Unit,
    ) = HttpResponse(
        universalErrorTransformers = universalErrorTransformers,
        coreErrorTransformer = coreErrorTransformer,
        httpResponse = httpClient.delete(path) {
            request(body)
            serverRequest()
            callRequest()
        },
    )

    private fun HttpRequestBuilder.request(
        body: Any? = null,
    ) {
        body?.let {
            setBody(it)
            contentType(ContentType.Application.Json)
        }
    }
}
