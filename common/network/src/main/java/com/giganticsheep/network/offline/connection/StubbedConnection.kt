package com.giganticsheep.network.offline.connection

import com.giganticsheep.network.offline.response.EndpointStub
import com.giganticsheep.network.offline.response.MockRequest
import com.giganticsheep.network.offline.response.MockResponse
import com.giganticsheep.network.offline.stub.EndpointStubs

class StubProviders(vararg provider: StubProvider) {
    private val providers: List<StubProvider> = provider.asList()

    internal inline fun <reified Collector : StubCollector<*, *, *, *>> stubs() = providers
        .map { it.collectedStubs }
        .filterIsInstance<Collector>()
}

abstract class StubbedConnection<Request : MockRequest, Response : MockResponse> {

    fun respond(
        request: Request,
    ): Response = stubs(request)
        .let { responses ->
            responses.find { it.canRespond(request) }
                ?.respond(request)
                ?: throw RuntimeException("No mapping for ${request.url}")
        }

    abstract fun stubs(request: Request): List<EndpointStub<Request, Response>>
}

abstract class StubProvider internal constructor() {
    internal abstract val collectedStubs: StubCollector<*, *, *, *>
}

internal abstract class StubCollector<
    Request : MockRequest,
    Response : MockResponse,
    Stub : EndpointStub<Request, Response>,
    Stubs : EndpointStubs<Stub>,
    >(
    vararg stubs: Stubs,
) {

    val stubs by lazy {
        stubs
            .asList()
            .fold(mutableListOf()) { acc: MutableList<Stub>, stub: Stubs ->
                acc.apply { addAll(stub.stubs) }
            }
    }
}
