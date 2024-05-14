package com.giganticsheep.network.offline.stub

import com.giganticsheep.network.FileUtilities
import com.giganticsheep.network.JsonUtilities
import com.giganticsheep.network.offline.responder.http.FileHttpResponder
import com.giganticsheep.network.offline.responder.http.MatchBodyFileHttpResponder
import com.giganticsheep.network.offline.responder.http.RequestToResponseHttpResponder
import com.giganticsheep.network.offline.responder.http.ResponseCodeHttpResponder
import com.giganticsheep.network.offline.responder.http.SubstitutionFileHttpResponder
import com.giganticsheep.network.offline.response.HttpEndpointStub
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

fun String.escapeForRegex() = replace("?", "\\?")
    .replace("$", "\\$")
    .replace(":", "\\:")
    .replace("+", "\\+")

abstract class HttpStubs(
    name: String,
    fileUtilities: FileUtilities,
    jsonUtilities: JsonUtilities = JsonUtilities.get(),
) : EndpointStubs<HttpEndpointStub>(name, fileUtilities, jsonUtilities) {

    interface CallData : EndpointStubs.CallData {
        val url: String

        val urlRegex: Regex
            get() = url
                .escapeForRegex()
                .let { "$it$".toRegex() }
    }

    class CallDataWrapper(
        private val callData: CallData,
        override val key: String,
    ) : CallData {
        override val url: String
            get() = callData.url
        override val type: HttpMethod
            get() = callData.type
    }

    final override fun createBuilder(
        name: String,
        call: EndpointStubs.CallData,
        fileUtilities: FileUtilities,
    ) = HttpStubCreator.HttpBuilder(
        name = name,
        call = call as CallData,
        fileUtilities = fileUtilities,
    )

    class HttpStubCreator private constructor(
        stub: HttpEndpointStub,
    ) : StubCreator<HttpEndpointStub>(stub) {

        private constructor(builder: HttpBuilder) : this(builder.stub)

        class HttpBuilder(
            name: String,
            call: CallData,
            fileUtilities: FileUtilities,
        ) : Builder<HttpEndpointStub>(
            name = name,
            call = call,
            fileUtilities = fileUtilities,
        ) {

            override fun createStub(
                name: String,
                call: EndpointStubs.CallData,
                status: HttpStatusCode,
                filename: String?,
                bodyMatch: String?,
                substitution: String?,
                mapper: CallMap?,
            ) = HttpEndpointStub.create(
                call = CallDataWrapper(call as CallData, "$name|${call.key}"),
                responder = createResponder(
                    status = status,
                    filename = filename,
                    bodyMatch = bodyMatch,
                    substitution = substitution,
                    mapper = mapper,
                ),
            )

            override fun build() = HttpStubCreator(this)

            private fun createResponder(
                status: HttpStatusCode,
                filename: String?,
                bodyMatch: String?,
                substitution: String?,
                mapper: CallMap?,
            ) = filename
                ?.let {
                    bodyMatch
                        ?.let {
                            MatchBodyFileHttpResponder(
                                status = status,
                                filename = filename,
                                fileUtilities = fileUtilities,
                                match = bodyMatch,
                            )
                        }
                        ?: substitution
                            ?.let {
                                if (filename.contains(it)) {
                                    SubstitutionFileHttpResponder(
                                        status = status,
                                        filename = filename,
                                        fileUtilities = fileUtilities,
                                        substitution = substitution,
                                    )
                                } else {
                                    null
                                }
                            }
                        ?: FileHttpResponder(
                            status = status,
                            filename = filename,
                            fileUtilities = fileUtilities,
                        )
                }
                ?: mapper
                    ?.let {
                        RequestToResponseHttpResponder(
                            status = status,
                            mapper = mapper,
                        )
                    }
                ?: ResponseCodeHttpResponder(
                    status = status,
                )
        }
    }
}
