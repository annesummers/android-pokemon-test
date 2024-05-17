package com.giganticsheep.network.offline.connection

import com.giganticsheep.network.offline.response.HttpEndpointStub
import com.giganticsheep.network.offline.response.MockHttpRequest
import com.giganticsheep.network.offline.response.MockHttpResponse
import com.giganticsheep.network.offline.stub.HttpStubs

class HttpStubProvider(
    vararg stubs: HttpStubs,
) : StubProvider() {

    override val collectedStubs: HttpStubCollector = HttpStubCollector(*stubs)
}

internal class HttpStubCollector(
    vararg stubs: HttpStubs,
) : StubCollector<MockHttpRequest, MockHttpResponse, HttpEndpointStub, HttpStubs>(
    *stubs,
)

internal class HttpStubbedConnection(
    stubs: List<HttpStubCollector>,
) : StubbedConnection<MockHttpRequest, MockHttpResponse>() {

    private val stubs by lazy {
        stubs
            .fold(mutableListOf<HttpEndpointStub>()) { acc, collector ->
                acc.apply { addAll(collector.stubs) }
            }
            .groupBy { it.call.type }
    }

    override fun stubs(
        request: MockHttpRequest,
    ): List<HttpEndpointStub> = stubs[request.method]
        ?: throw RuntimeException("No mapping for ${request.url}, request type ${request.method}")
}
