package com.giganticsheep.network.offline.connection

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.giganticsheep.network.offline.response.EndpointStub
import com.giganticsheep.network.offline.response.MockRequest
import com.giganticsheep.network.offline.response.MockResponse
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class StubbedConnectionTest {

    class TestMockRequest : MockRequest {
        override val url = ""
        override val body = ""
    }

    data class TestMockResponse(
        val name: String,
    ) : MockResponse

    class TestMockRequestResponse(
        private val canRespond: Boolean,
        private val response: () -> TestMockResponse,
    ) : EndpointStub<TestMockRequest, TestMockResponse> {
        override fun canRespond(request: TestMockRequest) = canRespond

        override fun respond(request: TestMockRequest) = response()
    }

    class TestStubbedConnection(
        private val responses: List<EndpointStub<TestMockRequest, TestMockResponse>>,
    ) : StubbedConnection<TestMockRequest, TestMockResponse>() {

        override fun stubs(
            request: TestMockRequest,
        ): List<EndpointStub<TestMockRequest, TestMockResponse>> = responses
    }

    @Test
    fun `test correct response`() {
        val testMockResponse1 = TestMockResponse("ONE")
        val testMockResponse2 = TestMockResponse("TWO")

        val stubbedConnection = TestStubbedConnection(
            listOf(
                TestMockRequestResponse(false) { testMockResponse1 },
                TestMockRequestResponse(true) { testMockResponse2 },
            ),
        )

        assertThat(stubbedConnection.respond(TestMockRequest()))
            .isEqualTo(testMockResponse2)
    }

    @Test
    fun `test no response`() {
        val testMockResponse1 = TestMockResponse("ONE")
        val testMockResponse2 = TestMockResponse("TWO")

        val stubbedConnection = TestStubbedConnection(
            listOf(
                TestMockRequestResponse(false) { testMockResponse1 },
                TestMockRequestResponse(false) { testMockResponse2 },
            ),
        )

        assertFailsWith<RuntimeException> { (stubbedConnection.respond(TestMockRequest())) }
    }
}
