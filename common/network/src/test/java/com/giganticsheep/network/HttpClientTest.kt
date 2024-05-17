package com.giganticsheep.network

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.giganticsheep.network.client.HttpCalls
import com.giganticsheep.network.client.HttpClient
import com.giganticsheep.network.client.deleteForResult
import com.giganticsheep.network.client.get
import com.giganticsheep.network.client.postForResult
import com.giganticsheep.network.client.putForResult
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class HttpClientTest {

    private val fakeLoggerFactory = FakeLoggerFactory()
    private val fakeJsonUtilities = FakeJsonUtilities()

    private val baseUrl = URLBuilder("https://baseUrl")
    private val testUrl = "test/url"
    private val testQuery = mapOf("field" to "value", "field2" to "value2")

    @Test
    fun `test get success`() = runTest {
        assertThat(
            createHttpClient(
                FakeGetHttpCalls {
                    assertThat(it).isEqualTo("https://baseUrl/test/url?field=value&field2=value2")

                    HttpResponse(
                        universalErrorTransformers = {},
                        coreErrorTransformer = errorTransform<String> { _, _ -> TestException() },
                        status = HttpStatusCode.OK,
                        body = { fakeJsonUtilities.jsonString },
                    )
                },
            ).get<FakeJsonUtilities.TestClass>(
                path = testUrl,
                query = testQuery,
                jsonUtilities = fakeJsonUtilities,
            ),
        )
            .isEqualTo(fakeJsonUtilities.jsonObject)
    }

    @Test
    fun `test get connection error`() = runTest {
        assertFailsWith<TestException> {
            createHttpClient(
                FakeGetHttpCalls { throw TestException() },
            )
                .get(path = testUrl)
        }
    }

    @Test
    fun `test put`() = runTest {
        createHttpClient(
            FakePutHttpCalls {
                assertThat(it).isEqualTo("https://baseUrl/test/url?field=value&field2=value2")

                HttpResponse(
                    universalErrorTransformers = {},
                    coreErrorTransformer = errorTransform<String> { _, _ -> TestException() },
                    status = HttpStatusCode.OK,
                    body = { "" },
                )
            },
        ).put(
            path = testUrl,
            query = testQuery,
            body = fakeJsonUtilities.jsonObject,
        )
    }

    @Test
    fun `test put connection error`() = runTest {
        assertFailsWith<TestException> {
            createHttpClient(FakePutHttpCalls { throw TestException() })
                .put(path = testUrl, body = fakeJsonUtilities.jsonObject)
        }
    }

    @Test
    fun `test put for result`() = runTest {
        assertThat(
            createHttpClient(
                FakePutHttpCalls {
                    assertThat(it).isEqualTo("https://baseUrl/test/url?field=value&field2=value2")

                    HttpResponse(
                        universalErrorTransformers = {},
                        coreErrorTransformer = errorTransform<String> { _, _ -> TestException() },
                        status = HttpStatusCode.OK,
                        body = { fakeJsonUtilities.jsonString },
                    )
                },
            ).putForResult<FakeJsonUtilities.TestClass, FakeJsonUtilities.TestClass>(
                path = testUrl,
                query = testQuery,
                body = fakeJsonUtilities.jsonObject,
                jsonUtilities = fakeJsonUtilities,
            ),
        ).isEqualTo(fakeJsonUtilities.jsonObject)
    }

    @Test
    fun `test put for result connection error`() = runTest {
        assertFailsWith<TestException> {
            createHttpClient(FakePutHttpCalls { throw TestException() })
                .putForResult(path = testUrl, body = fakeJsonUtilities.jsonObject)
        }
    }

    @Test
    fun `test post`() = runTest {
        createHttpClient(
            FakePostHttpCalls {
                assertThat(it).isEqualTo("https://baseUrl/test/url?field=value&field2=value2")

                HttpResponse(
                    universalErrorTransformers = {},
                    coreErrorTransformer = errorTransform<String> { _, _ -> TestException() },
                    status = HttpStatusCode.OK,
                    body = { "" },
                )
            },
        ).post(
            path = testUrl,
            query = testQuery,
            body = fakeJsonUtilities.jsonObject,
        )
    }

    @Test
    fun `test post no body`() = runTest {
        createHttpClient(
            FakePostNoBodyHttpCalls {
                assertThat(it).isEqualTo("https://baseUrl/test/url?field=value&field2=value2")

                HttpResponse(
                    universalErrorTransformers = {},
                    coreErrorTransformer = errorTransform<String> { _, _ -> TestException() },
                    status = HttpStatusCode.OK,
                    body = { "" },
                )
            },
        ).post(
            path = testUrl,
            query = testQuery,
        )
    }

    @Test
    fun `test post for result no body`() = runTest {
        assertThat(
            createHttpClient(
                FakePostNoBodyHttpCalls {
                    assertThat(it).isEqualTo("https://baseUrl/test/url?field=value&field2=value2")
                    HttpResponse(
                        universalErrorTransformers = {},
                        coreErrorTransformer = errorTransform<String> { _, _ -> TestException() },
                        status = HttpStatusCode.OK,
                        body = { fakeJsonUtilities.jsonString },
                    )
                },
            ).postForResult<FakeJsonUtilities.TestClass>(
                path = testUrl,
                query = testQuery,
                jsonUtilities = fakeJsonUtilities,
            ),
        ).isEqualTo(fakeJsonUtilities.jsonObject)
    }

    @Test
    fun `test post connection error`() = runTest {
        assertFailsWith<TestException> {
            createHttpClient(FakePostHttpCalls { throw TestException() })
                .post(path = testUrl, body = fakeJsonUtilities.jsonObject)
        }
    }

    @Test
    fun `test post for result`() = runTest {
        assertThat(
            createHttpClient(
                FakePostHttpCalls {
                    assertThat(it).isEqualTo("https://baseUrl/test/url?field=value&field2=value2")
                    HttpResponse(
                        universalErrorTransformers = {},
                        coreErrorTransformer = errorTransform<String> { _, _ -> TestException() },
                        status = HttpStatusCode.OK,
                        body = { fakeJsonUtilities.jsonString },
                    )
                },
            ).postForResult<FakeJsonUtilities.TestClass, FakeJsonUtilities.TestClass>(
                path = testUrl,
                query = testQuery,
                body = fakeJsonUtilities.jsonObject,
                jsonUtilities = fakeJsonUtilities,
            ),
        ).isEqualTo(fakeJsonUtilities.jsonObject)
    }

    @Test
    fun `test post for result connection error`() = runTest {
        assertFailsWith<TestException> {
            createHttpClient(FakePostHttpCalls { throw TestException() })
                .postForResult(path = testUrl, body = fakeJsonUtilities.jsonObject)
        }
    }

    @Test
    fun `test delete`() = runTest {
        createHttpClient(
            FakeDeleteHttpCalls {
                assertThat(it).isEqualTo("https://baseUrl/test/url?field=value&field2=value2")
                HttpResponse(
                    universalErrorTransformers = {},
                    coreErrorTransformer = errorTransform<String> { _, _ -> TestException() },
                    status = HttpStatusCode.OK,
                    body = { "" },
                )
            },
        )
            .delete(
                path = testUrl,
                query = testQuery,
                body = fakeJsonUtilities.jsonObject,
            )
    }

    @Test
    fun `test delete connection error`() = runTest {
        assertFailsWith<TestException> {
            createHttpClient(FakeDeleteHttpCalls { throw TestException() })
                .delete(path = testUrl, body = fakeJsonUtilities.jsonObject)
        }
    }

    @Test
    fun `test delete for result`() = runTest {
        assertThat(
            createHttpClient(
                FakeDeleteHttpCalls {
                    assertThat(it).isEqualTo("https://baseUrl/test/url?field=value&field2=value2")
                    HttpResponse(
                        universalErrorTransformers = {},
                        coreErrorTransformer = errorTransform<String> { _, _ -> TestException() },
                        status = HttpStatusCode.OK,
                        body = { fakeJsonUtilities.jsonString },
                    )
                },
            )
                .deleteForResult<FakeJsonUtilities.TestClass, FakeJsonUtilities.TestClass>(
                    path = testUrl,
                    query = testQuery,
                    body = fakeJsonUtilities.jsonObject,
                    jsonUtilities = fakeJsonUtilities,
                ),
        ).isEqualTo(fakeJsonUtilities.jsonObject)
    }

    @Test
    fun `test delete for result connection error`() = runTest {
        assertFailsWith<TestException> {
            createHttpClient(FakeDeleteHttpCalls { throw TestException() })
                .deleteForResult(path = testUrl, body = fakeJsonUtilities.jsonObject)
        }
    }

    @Test
    fun `test delete no body`() = runTest {
        createHttpClient(
            FakeDeleteNoBodyHttpCalls {
                assertThat(it).isEqualTo("https://baseUrl/test/url?field=value&field2=value2")
                HttpResponse(
                    universalErrorTransformers = {},
                    coreErrorTransformer = errorTransform<String> { _, _ -> TestException() },
                    status = HttpStatusCode.OK,
                    body = { "" },
                )
            },
        )
            .delete(
                path = testUrl,
                query = testQuery,
            )
    }

    @Test
    fun `test delete with body connection error`() = runTest {
        assertFailsWith<TestException> {
            createHttpClient(FakeDeleteHttpCalls { throw TestException() })
                .delete(
                    path = testUrl,
                    query = testQuery,
                    body = fakeJsonUtilities.jsonObject,
                    jsonUtilities = fakeJsonUtilities,
                )
        }
    }

    @Test
    fun `test delete no body for result`() = runTest {
        assertThat(
            createHttpClient(
                FakeDeleteNoBodyHttpCalls {
                    assertThat(it).isEqualTo("https://baseUrl/test/url?field=value&field2=value2")
                    HttpResponse(
                        universalErrorTransformers = {},
                        coreErrorTransformer = errorTransform<String> { _, _ -> TestException() },
                        status = HttpStatusCode.OK,
                        body = { fakeJsonUtilities.jsonString },
                    )
                },
            )
                .deleteForResult<FakeJsonUtilities.TestClass>(
                    path = testUrl,
                    query = testQuery,
                    jsonUtilities = fakeJsonUtilities,
                ),
        ).isEqualTo(fakeJsonUtilities.jsonObject)
    }

    @Test
    fun `test delete no body for result connection error`() = runTest {
        assertFailsWith<TestException> {
            createHttpClient(FakeDeleteNoBodyHttpCalls { throw TestException() })
                .deleteForResult(path = testUrl)
        }
    }

    @Test
    fun `test get success with incorrect body`() = runTest {
        val body = "testBody"

        val client = createHttpClient(
            FakeGetHttpCalls {
                assertThat(it).isEqualTo("https://baseUrl/test/url?field=value&field2=value2")
                HttpResponse(
                    universalErrorTransformers = {},
                    coreErrorTransformer = errorTransform<String> { _, _ -> TestException() },
                    status = HttpStatusCode.OK,
                    body = { body },
                )
            },
        )

        assertFailsWith<SerializationException> {
            client.get<FakeJsonUtilities.TestClass>(
                path = testUrl,
                query = testQuery,
                jsonUtilities = fakeJsonUtilities,
            )
        }
    }

    private fun createHttpClient(
        httpCalls: HttpCalls,
    ) = object : HttpClient(fakeLoggerFactory, baseUrl, httpCalls) {}
}
