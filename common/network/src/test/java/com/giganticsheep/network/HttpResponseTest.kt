package com.giganticsheep.network

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.giganticsheep.error.HandledException
import com.giganticsheep.network.error.ApiResponseException
import com.giganticsheep.network.error.TimeoutException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class HttpResponseTest {

    private val fakeJsonUtilities = FakeJsonUtilities()

    @Test
    fun `test handleResponseError handled code`() = runTest {
        val testException = TestException()

        val httpResponse = HttpResponse(
            universalErrorTransformers = {},
            coreErrorTransformer = errorTransform<String> { httpStatusCode, body ->
                ApiResponseException(httpStatusCode, body)
            },
            status = HttpStatusCode.Conflict,
            body = { "" },
        )

        assertThat(
            httpResponse.handleErrors(fakeJsonUtilities) {
                transform(HttpStatusCode.Conflict) { testException }
            },
        )
            .isEqualTo(testException)
    }

    @Test
    fun `test handleResponseError string handled type`() = runTest {
        val body = "body"

        val httpResponse = HttpResponse(
            universalErrorTransformers = {},
            coreErrorTransformer = errorTransform<String> { httpStatusCode, body ->
                ApiResponseException(httpStatusCode, body)
            },
            status = HttpStatusCode.Conflict,
            body = { body },
        )

        assertThat(
            httpResponse.handleErrors(fakeJsonUtilities) {
                transform<String> { TestException(it) }
            }.message,
        )
            .isEqualTo(body)
    }

    @Test
    fun `test handleResponseError object handled type`() = runTest {
        data class ErrorException(val error: FakeJsonUtilities.TestClass) :
            HandledException(internalMessage = "")

        val httpResponse = HttpResponse(
            universalErrorTransformers = {},
            coreErrorTransformer = errorTransform<String> { httpStatusCode, body ->
                ApiResponseException(httpStatusCode, body)
            },
            status = HttpStatusCode.Conflict,
            body = { fakeJsonUtilities.jsonString },
        )

        val exception = httpResponse.handleErrors(fakeJsonUtilities) {
            transform<FakeJsonUtilities.TestClass> { ErrorException(it) }
        }

        assertThat(exception)
            .isInstanceOf(ErrorException::class)

        assertThat((exception as ErrorException).error)
            .isEqualTo(fakeJsonUtilities.jsonObject)
    }

    @Test
    fun `test handleResponseError no type handler`() = runTest {
        val body = "body"

        val httpResponse = HttpResponse(
            universalErrorTransformers = {},
            coreErrorTransformer = errorTransform<String> { httpStatusCode, body ->
                ApiResponseException(httpStatusCode, body)
            },
            status = HttpStatusCode.Conflict,
            body = { body },
        )

        val exception = httpResponse.handleErrors(fakeJsonUtilities) {}

        assertThat(exception)
            .isInstanceOf(ApiResponseException::class)

        assertThat(exception.message)
            .isEqualTo(body)

        assertThat((exception as ApiResponseException).statusCode)
            .isEqualTo(HttpStatusCode.Conflict.value)
    }

    @Test
    fun `test handleResponseError universal error`() = runTest {
        val body = "body"

        val httpResponse = HttpResponse(
            universalErrorTransformers = {
                transform<String>(HttpStatusCode.RequestTimeout) { TimeoutException(it) }
            },
            coreErrorTransformer = errorTransform<String> { httpStatusCode, body ->
                ApiResponseException(httpStatusCode, body)
            },
            status = HttpStatusCode.RequestTimeout,
            body = { body },
        )

        val exception = httpResponse.handleErrors(fakeJsonUtilities) {}

        assertThat(exception)
            .isInstanceOf(TimeoutException::class)

        assertThat(exception.message)
            .isEqualTo(body)
    }

    @Test
    fun `test handleResponseError call error`() = runTest {
        val body = "body"

        val httpResponse = HttpResponse(
            universalErrorTransformers = {},
            coreErrorTransformer = errorTransform<String> { httpStatusCode, body ->
                ApiResponseException(httpStatusCode, body)
            },
            status = HttpStatusCode.RequestTimeout,
            body = { body },
        )

        val exception = httpResponse.handleErrors(fakeJsonUtilities) {
            transform<String>(HttpStatusCode.RequestTimeout) { TimeoutException(it) }
        }

        assertThat(exception)
            .isInstanceOf(TimeoutException::class)

        assertThat(exception.message)
            .isEqualTo(body)
    }
}
