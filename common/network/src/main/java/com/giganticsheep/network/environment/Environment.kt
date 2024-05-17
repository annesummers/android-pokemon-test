package com.giganticsheep.network.environment

import io.ktor.http.URLBuilder
import io.ktor.http.set

open class Environment(
    private val hosts: Map<Endpoint, Host>,
) {

    fun url(
        endpoint: Endpoint,
    ) = hosts[endpoint]
        ?.url
        ?: error("Host does not exist")
}

interface Endpoint

data class Host(
    private val host: String,
    private val path: String,
) {
    val url = URLBuilder()
        .apply {
            host = this@Host.host
            protocol = io.ktor.http.URLProtocol.HTTPS
            pathSegments = path.split('/')
        }
}
