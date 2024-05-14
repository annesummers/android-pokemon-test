package com.giganticsheep.network.client

import com.giganticsheep.error.HandledException
import com.giganticsheep.logging.Logger
import com.giganticsheep.logging.LoggerFactory
import com.giganticsheep.network.ErrorTransformer
import com.giganticsheep.network.ErrorTransformerBuilder
import com.giganticsheep.network.ErrorTypeTransformer
import com.giganticsheep.network.HttpClientFactory
import com.giganticsheep.network.HttpResponse
import com.giganticsheep.network.JsonUtilities
import com.giganticsheep.network.environment.Endpoint
import com.giganticsheep.network.environment.Environment
import com.giganticsheep.network.error.ApiResponseException
import com.giganticsheep.network.error.ConnectionException
import com.giganticsheep.network.error.TimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.http.ParametersBuilder
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.http.isSuccess
import kotlinx.serialization.SerializationException
import kotlin.collections.set
import kotlin.coroutines.cancellation.CancellationException
import kotlin.reflect.typeOf

class HttpClientProvider(
    private val factories: Map<Environment, HttpClientFactory<*>>,
) {

    private val clients: MutableMap<Environment, io.ktor.client.HttpClient> = mutableMapOf()

    fun getClient(environment: Environment): io.ktor.client.HttpClient {
        var client = clients[environment]

        if (client == null) {
            clients[environment] = factories[environment]!!.createHttpClient()
            client = clients[environment]!!
        }

        return client
    }
}

abstract class HttpClient internal constructor(
    loggerFactory: LoggerFactory,
    val baseUrl: URLBuilder,
    val calls: HttpCalls,
) {

    val logger by lazy { loggerFactory.logger(this) }

    open val serverRequestBuilder: io.ktor.client.request.HttpRequestBuilder.() -> Unit = {}

    protected constructor(
        loggerFactory: LoggerFactory,
        httpClientProvider: HttpClientProvider,
        environment: Environment,
        endpoint: Endpoint,
        universalErrorTransformers: ErrorTransformerBuilder.() -> Unit = {},
        coreErrorTransformer: ErrorTransformer = ErrorTypeTransformer.create<String> { httpStatusCode, error ->
            ApiResponseException(
                httpStatusCode,
                error,
            )
        },
    ) : this(
        loggerFactory = loggerFactory,
        baseUrl = environment.url(endpoint),
        calls = InternalHttpCalls(
            httpClient = httpClientProvider.getClient(environment),
            universalErrorTransformers = universalErrorTransformers,
            coreErrorTransformer = coreErrorTransformer,
        ),
    )

    suspend inline fun <reified R : Any> get(
        path: String,
        query: Map<String, String> = mapOf(),
        jsonUtilities: JsonUtilities = JsonUtilities.get(),
        block: HttpRequestBuilderForResult.() -> Unit = {},
    ) = HttpRequestBuilderForResult(jsonUtilities) { callRequest ->
        calls.get(
            path = URLBuilder(baseUrl)
                .appendPathSegments(path)
                .apply {
                    encodedParameters = ParametersBuilder(query.size)
                        .apply { query.forEach { append(it.key, it.value) } }
                }
                .buildString(),
            callRequest = callRequest,
            serverRequest = serverRequestBuilder,
        )
    }
        .also(block)
        .call<R>()

    suspend inline fun <reified B : Any> put(
        path: String,
        query: Map<String, String> = mapOf(),  //TODO
        body: B,
        jsonUtilities: JsonUtilities = JsonUtilities.get(),
        block: HttpRequestBuilder.() -> Unit = {},
    ) = HttpRequestBuilder(jsonUtilities) { callRequest ->
        calls.put(
            URLBuilder(baseUrl).appendPathSegments(path).buildString(),
            body,
            callRequest,
            serverRequest = serverRequestBuilder,
        )
    }
        .also(block)
        .call()

    suspend inline fun <reified B : Any, reified R : Any> putForResult(
        path: String,
        query: Map<String, String> = mapOf(), //TODO
        body: B,
        jsonUtilities: JsonUtilities = JsonUtilities.get(),
        block: HttpRequestBuilderForResult.() -> Unit = {},
    ) = HttpRequestBuilderForResult(jsonUtilities) { callRequest ->
        calls.put(
            URLBuilder(baseUrl).appendPathSegments(path).buildString(),
            body,
            callRequest,
            serverRequest = serverRequestBuilder,
        )
    }
        .also(block)
        .call<R>()

    suspend inline fun <reified B : Any> post(
        path: String,
        query: Map<String, String> = mapOf(), //TODO
        body: B,
        jsonUtilities: JsonUtilities = JsonUtilities.get(),
        block: HttpRequestBuilder.() -> Unit = {},
    ) = HttpRequestBuilder(jsonUtilities) { callRequest ->
        calls.post(
            URLBuilder(baseUrl).appendPathSegments(path).buildString(),
            body,
            callRequest,
            serverRequest = serverRequestBuilder,
        )
    }
        .also(block)
        .call()

    suspend inline fun post(
        path: String,
        query: Map<String, String> = mapOf(), //TODO
        jsonUtilities: JsonUtilities = JsonUtilities.get(),
        block: HttpRequestBuilder.() -> Unit = {},
    ) = HttpRequestBuilder(jsonUtilities) { callRequest ->
        calls.post(
            URLBuilder(baseUrl).appendPathSegments(path).buildString(),
            callRequest,
            serverRequest = serverRequestBuilder,
        )
    }
        .also(block)
        .call()

    suspend inline fun <reified B : Any, reified R : Any> postForResult(
        path: String,
        query: Map<String, String> = mapOf(), //TODO
        body: B,
        jsonUtilities: JsonUtilities = JsonUtilities.get(),
        block: HttpRequestBuilderForResult.() -> Unit = {},
    ) = HttpRequestBuilderForResult(jsonUtilities) { callRequest ->
        calls.post(
            URLBuilder(baseUrl).appendPathSegments(path).buildString(),
            body,
            callRequest,
            serverRequest = serverRequestBuilder,
        )
    }
        .also(block)
        .call<R>()

    suspend inline fun <reified R : Any> postForResult(
        path: String,
        query: Map<String, String> = mapOf(), //TODO
        jsonUtilities: JsonUtilities = JsonUtilities.get(),
        block: HttpRequestBuilderForResult.() -> Unit = {},
    ) = HttpRequestBuilderForResult(jsonUtilities) { callRequest ->
        calls.post(
            URLBuilder(baseUrl).appendPathSegments(path).buildString(),
            callRequest,
            serverRequest = serverRequestBuilder,
        )
    }
        .also(block)
        .call<R>()

    suspend inline fun <reified B : Any> delete(
        path: String,
        query: Map<String, String> = mapOf(), //TODO
        body: B,
        jsonUtilities: JsonUtilities = JsonUtilities.get(),
        block: HttpRequestBuilder.() -> Unit = {},
    ) = HttpRequestBuilder(jsonUtilities) { callRequest ->
        calls.delete(
            URLBuilder(baseUrl).appendPathSegments(path).buildString(),
            body,
            callRequest,
            serverRequest = serverRequestBuilder,
        )
    }
        .also(block)
        .call()

    suspend inline fun <reified B : Any, reified R : Any> deleteForResult(
        path: String,
        query: Map<String, String> = mapOf(), //TODO
        body: B,
        jsonUtilities: JsonUtilities = JsonUtilities.get(),
        block: HttpRequestBuilderForResult.() -> Unit = {},
    ) = HttpRequestBuilderForResult(jsonUtilities) { callRequest ->
        calls.delete(
            URLBuilder(baseUrl).appendPathSegments(path).buildString(),
            body,
            callRequest,
            serverRequest = serverRequestBuilder,
        )
    }
        .also(block)
        .call<R>()

    suspend inline fun delete(
        path: String,
        query: Map<String, String> = mapOf(), //TODO
        jsonUtilities: JsonUtilities = JsonUtilities.get(),
        block: HttpRequestBuilder.() -> Unit = {},
    ) = HttpRequestBuilder(jsonUtilities) { callRequest ->
        calls.delete(
            URLBuilder(baseUrl).appendPathSegments(path).buildString(),
            callRequest,
            serverRequest = serverRequestBuilder,
        )
    }
        .also(block)
        .call()

    suspend inline fun <reified R : Any> deleteForResult(
        path: String,
        query: Map<String, String> = mapOf(), //TODO
        jsonUtilities: JsonUtilities = JsonUtilities.get(),
        block: HttpRequestBuilderForResult.() -> Unit = {},
    ) = HttpRequestBuilderForResult(jsonUtilities) { callRequest ->
        calls.delete(
            URLBuilder(baseUrl).appendPathSegments(path).buildString(),
            callRequest,
            serverRequest = serverRequestBuilder,
        )
    }
        .also(block)
        .call<R>()

    open inner class HttpRequestBuilder(
        val jsonUtilities: JsonUtilities, // for test mocking
        call: suspend (io.ktor.client.request.HttpRequestBuilder.() -> Unit) -> HttpResponse,
    ) : BaseRequestBuilder(
        call = call,
    ) {
        suspend inline fun call() = try {
            call(callRequest)
                .let { httpResponse ->
                    if (!httpResponse.status.isSuccess()) {
                        httpResponse.handleErrors(
                            errorTransformers = errorTransformers,
                            jsonUtilities = jsonUtilities,
                        ).let { error ->
                            logger.log(Logger.Level.Error, "Http response error", error)
                            throw error
                        }
                    }
                }
        } catch (e: SerializationException) {
            throw e
        } catch (e: Exception) {
            throw handleConnectionErrors(e)
        }
    }

    open inner class HttpRequestBuilderForResult(
        val jsonUtilities: JsonUtilities, // for test mocking
        call: suspend (io.ktor.client.request.HttpRequestBuilder.() -> Unit) -> HttpResponse,
    ) : BaseRequestBuilder(
        call = call,
    ) {

        suspend inline fun <reified R : Any> call(): R = try {
            call(callRequest)
                .let { httpResponse ->
                    if (httpResponse.status.isSuccess()) {
                        httpResponse.body()
                            .let {
                                if (R::class == String::class) {
                                    it as R
                                } else {
                                    jsonUtilities.jsonToObj(it, typeOf<R>()) as R
                                }
                            }
                    } else {
                        httpResponse.handleErrors(
                            errorTransformers = errorTransformers,
                            jsonUtilities = jsonUtilities,
                        ).let { error ->
                            logger.log(Logger.Level.Error, "Http response error", error)
                            throw error
                        }
                    }
                }
        } catch (e: SerializationException) {
            throw e
        } catch (e: Exception) {
            throw handleConnectionErrors(e)
        }
    }

    abstract inner class BaseRequestBuilder(
        val call: suspend (io.ktor.client.request.HttpRequestBuilder.() -> Unit) -> HttpResponse,
    ) {
        var errorTransformers: ErrorTransformerBuilder.() -> Unit = {}

        var callRequest: io.ktor.client.request.HttpRequestBuilder.() -> Unit = {}

        fun handleConnectionErrors(
            e: Exception,
        ) = when (e) {
            is CancellationException,
            is HandledException,
            -> e

            else -> (
                (
                    if (e.isTimeout()) {
                        TimeoutException(cause = e)
                    } else {
                        e.cause
                            ?.let {
                                if (it.isTimeout()) {
                                    TimeoutException(cause = e)
                                } else {
                                    ConnectionException(cause = e)
                                }
                            }
                            ?: ConnectionException(cause = e)
                    }
                    )
                )
                .also { logger.log(Logger.Level.Error, "Http connection error", it) }
        }

        private fun Throwable.isTimeout() = this is SocketTimeoutException ||
            message?.contains("timeout") == true
    }
}
