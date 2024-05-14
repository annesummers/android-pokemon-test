package com.giganticsheep.network.offline.responder.http

import com.giganticsheep.network.FileUtilities
import com.giganticsheep.network.offline.responder.MatchBodyResponded
import com.giganticsheep.network.offline.responder.MatchBodyResponder
import com.giganticsheep.network.offline.response.MockHttpRequest
import com.giganticsheep.network.offline.response.MockHttpResponse
import com.giganticsheep.network.offline.stub.escapeForRegex
import io.ktor.http.HttpStatusCode

internal open class FileHttpResponder(
    status: HttpStatusCode,
    protected val filename: String,
    protected val fileUtilities: FileUtilities,
) : HttpResponder(
    status,
) {
    override fun respond(
        request: MockHttpRequest,
    ) = MockHttpResponse(
        request.respond(
            fileUtilities.stringFromFile(filename),
            status,
        ),
    )
}

internal class SubstitutionFileHttpResponder(
    status: HttpStatusCode,
    filename: String,
    fileUtilities: FileUtilities,
    private val substitution: String,
) : FileHttpResponder(
    status = status,
    filename = filename,
    fileUtilities = fileUtilities,
) {
    override fun respond(
        request: MockHttpRequest,
    ) = MockHttpResponse(
        request.respond(
            fileUtilities.stringFromFile(filename.replace(substitution, match(request.path))),
            status,
        ),
    )

    private fun match(
        path: String,
    ) = substitution
        .escapeForRegex()
        .toRegex()
        .find(path)
        ?.value
        ?: error("No match, check your substitution")
}

internal class MatchBodyFileHttpResponder(
    status: HttpStatusCode,
    filename: String,
    fileUtilities: FileUtilities,
    match: String,
) : FileHttpResponder(
    status = status,
    filename = filename,
    fileUtilities = fileUtilities,
),
    MatchBodyResponder by MatchBodyResponded(match)
