package com.giganticsheep.network.offline.stub

import com.giganticsheep.network.FileUtilities
import com.giganticsheep.network.JsonUtilities
import com.giganticsheep.network.offline.response.EndpointStub
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlin.reflect.KType
import kotlin.reflect.typeOf

abstract class EndpointStubs<Stub : EndpointStub<*, *>> internal constructor(
    val name: String,
    val fileUtilities: FileUtilities,
    val jsonUtilities: JsonUtilities,
) {

    interface CallData {
        val key: String
        val type: HttpMethod
    }

    abstract val calls: List<CallData>

    abstract fun getStub(callData: CallData): Stub

    val stubs: List<Stub> by lazy { calls.map { getStub(it) } }

    abstract class StubCreator<Stub : EndpointStub<*, *>>(
        val stub: Stub,
    ) {

        abstract class Builder<Stub : EndpointStub<*, *>>(
            private val name: String,
            private val call: CallData,
            protected val fileUtilities: FileUtilities,
        ) {
            val stub: Stub
                get() = createStub(
                    name = name,
                    call = call,
                    status = status,
                    filename = filename,
                    bodyMatch = bodyMatch,
                    regexSubstitution = regexSubstitution,
                    trailingSubstitution = trailingSubstitution,
                    mapper = mapper,
                )

            var filename: String? = null
            var bodyMatch: String? = null
            var regexSubstitution: String? = null
            var trailingSubstitution: String? = null
            var mapper: CallMap? = null
            var status: HttpStatusCode = HttpStatusCode.OK

            abstract fun createStub(
                name: String,
                call: CallData,
                status: HttpStatusCode,
                filename: String?,
                bodyMatch: String?,
                regexSubstitution: String?,
                trailingSubstitution: String?,
                mapper: CallMap?,
            ): Stub

            abstract fun build(): StubCreator<Stub>
        }
    }

    inline fun <reified Request : Any, reified Response : Any> stub(
        call: CallData,
        stubBuilder: StubCreator.Builder<Stub>.() -> Unit = {},
        crossinline mapper: (Request) -> Response,
    ) = createBuilder(
        name = name,
        call = call,
        fileUtilities = fileUtilities,
    )
        .apply(stubBuilder)
        .apply {
            this.mapper = RequestResponseCallMap<Request, Response>(
                requestType = typeOf<Request>(),
                responseType = if (Response::class == Unit::class) {
                    null
                } else {
                    typeOf<Response>()
                },
                jsonUtilities = jsonUtilities,
            ) { mapper(it) }
        }
        .build()
        .stub

    inline fun <reified Response : Any> stub(
        call: CallData,
        stubBuilder: StubCreator.Builder<Stub>.() -> Unit = {},
        crossinline mapper: () -> Response,
    ) = createBuilder(
        name = name,
        call = call,
        fileUtilities = fileUtilities,
    )
        .apply(stubBuilder)
        .apply {
            this.mapper = ResponseCallMap(
                responseType = typeOf<Response>(),
                jsonUtilities = jsonUtilities,
            ) { mapper() }
        }
        .build()
        .stub

    fun stub(
        call: CallData,
        responseBuilder: StubCreator.Builder<Stub>.() -> Unit,
    ) = createBuilder(
        name = name,
        call = call,
        fileUtilities = fileUtilities,
    )
        .apply(responseBuilder)
        .build()
        .stub

    abstract fun createBuilder(
        name: String,
        call: CallData,
        fileUtilities: FileUtilities,
    ): StubCreator.Builder<Stub>
/*
    abstract class StubsBuilder<Stub : EndpointStub<*, *>>(
        val name: String,
        val fileUtilities: FileUtilities,
        val jsonUtilities: JsonUtilities,
    ) {
        val internalStubs: MutableList<Stub> = mutableListOf()

        val stubs: List<Stub>
            get() = internalStubs.toList()

        inline fun <reified Request : Any, reified Response : Any> stub(
            call: CallData,
            stubBuilder: StubCreator.Builder<Stub>.() -> Unit = {},
            crossinline mapper: (Request) -> Response,
        ) {
            internalStubs.add(
                createBuilder(
                    name = name,
                    call = call,
                    fileUtilities = fileUtilities,
                )
                    .apply(stubBuilder)
                    .apply {
                        this.mapper = RequestResponseCallMap<Request, Response>(
                            requestType = typeOf<Request>(),
                            responseType = if (Response::class == Unit::class) {
                                null
                            } else {
                                typeOf<Response>()
                            },
                            jsonUtilities = jsonUtilities,
                        ) { mapper(it) }
                    }
                    .build()
                    .stub,
            )
        }

        inline fun <reified Response : Any> stub(
            call: CallData,
            stubBuilder: StubCreator.Builder<Stub>.() -> Unit = {},
            crossinline mapper: () -> Response,
        ) {
            internalStubs.add(
                createBuilder(
                    name = name,
                    call = call,
                    fileUtilities = fileUtilities,
                )
                    .apply(stubBuilder)
                    .apply {
                        this.mapper = ResponseCallMap(
                            responseType = typeOf<Response>(),
                            jsonUtilities = jsonUtilities,
                        ) { mapper() }
                    }
                    .build()
                    .stub,
            )
        }

        fun stub(
            call: CallData,
            responseBuilder: StubCreator.Builder<Stub>.() -> Unit,
        ) {
            internalStubs.add(
                createBuilder(
                    name = name,
                    call = call,
                    fileUtilities = fileUtilities,
                )
                    .apply(responseBuilder)
                    .build()
                    .stub,
            )
        }

        abstract fun createBuilder(
            name: String,
            call: CallData,
            fileUtilities: FileUtilities,
        ): StubCreator.Builder<Stub>
    }*/
}

interface CallMap {
    fun respond(body: String): String
}

class ResponseCallMap<Response : Any>(
    private val responseType: KType,
    private val jsonUtilities: JsonUtilities,
    private val stub: () -> Response,
) : CallMap {

    override fun respond(
        body: String,
    ) = jsonUtilities.objToJson(
        obj = stub(),
        type = responseType,
    )
}

@Suppress("UNCHECKED_CAST")
class RequestResponseCallMap<Request : Any, Response : Any>(
    private val requestType: KType,
    private val responseType: KType? = null,
    private val jsonUtilities: JsonUtilities,
    private val stub: (Request) -> Response,
) : CallMap {

    override fun respond(
        body: String,
    ) = responseType
        ?.let {
            jsonUtilities.objToJson(
                obj = stub(jsonUtilities.jsonToObj(body, requestType) as Request),
                type = responseType,
            )
        }
        ?: stub(jsonUtilities.jsonToObj(body, requestType) as Request) as String
}
