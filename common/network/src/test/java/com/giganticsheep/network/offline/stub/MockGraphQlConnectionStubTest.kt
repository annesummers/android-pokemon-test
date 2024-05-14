package com.giganticsheep.network.offline.stub

internal class MockGraphQlConnectionStubTest {
/*
    private val fakeFileUtilities = mockk<FileUtilities>()
    private val fakeJsonUtilities = FakeJsonUtilities()
    private val mockBufferUtils = mockk<BufferUtils>()

    private val requestBody = "getJson"

    private val mockRequest = mockk<MockGraphQlRequest> {
        every { body } returns requestBody
    }
    private val mockResponse = mockk<MockGraphQlResponse>()

    private class TestMockConnectionStub(
        fileUtilities: FileUtilities,
        jsonUtilities: JsonUtilities,
        bufferUtils: BufferUtils
    ) : GraphQlStubs(
        "TEST",
        fileUtilities,
        jsonUtilities,
        bufferUtils
    ) {
        override fun requestResponses() = requestResponses {
            requestResponse(Call.TEST_GET) {
                filename = "get_filename"
            }
            requestResponse(Call.TEST_GET2) {
                filename = "get_filename2"
                status = HttpStatusCode.OK
            }
        }

        enum class Call(
            override val type: HttpMethod,
            override val queryName: String
        ) : CallData {
            TEST_GET(HttpMethod.Get, "Query"),
            TEST_GET2(HttpMethod.Get, "Query2");

            override val key: String = name
        }
    }

    private lateinit var testMockConnectionStub: TestMockConnectionStub

    private val responseBody = "responseBody"

    @BeforeTest
    fun setup() {
        testMockConnectionStub = TestMockConnectionStub(
            fakeFileUtilities,
            fakeJsonUtilities,
            mockBufferUtils
        )
    }

    @Test
    fun `test get 1`() {
        every { mockRequest.body } returns requestBody
        every {
            fakeJsonUtilities.jsonToObj<ApolloQueryBody>(requestBody)
        } returns ApolloQueryBody("Query")
        every { fakeFileUtilities.stringFromFile("get_filename") } returns responseBody

        testMockConnectionStub.requestResponses().find {
            it.call.key == TestMockConnectionStub.Call.TEST_GET.name
        }?.let {
            assertThat(it.canRespond(mockRequest))
                .isTrue()

            it.respond(mockRequest)
        }
    }

    @Test
    fun `test get 2`() {
        every { mockRequest.body } returns requestBody
        every {
            fakeJsonUtilities.jsonToObj<ApolloQueryBody>(requestBody)
        } returns ApolloQueryBody("Query2")
        every { fakeFileUtilities.stringFromFile("get_filename2") } returns responseBody

        testMockConnectionStub.requestResponses().find {
            it.call.key == TestMockConnectionStub.Call.TEST_GET2.name
        }?.let {
            assertThat(it.canRespond(mockRequest))
                .isTrue()

            it.respond(mockRequest)
        }
    }*/
}
