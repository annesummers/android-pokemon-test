package com.giganticsheep.pokemon.data.network.client

import com.giganticsheep.logging.LoggerFactory
import com.giganticsheep.network.client.HttpClient
import com.giganticsheep.network.client.HttpClientProvider
import com.giganticsheep.network.environment.Environment
import com.giganticsheep.pokemon.data.network.environment.PokemonEndpoint
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonHttpClient @Inject constructor(
    httpClientProvider: HttpClientProvider,
    loggerFactory: LoggerFactory,
    environment: Environment,
) : HttpClient(
    loggerFactory = loggerFactory,
    environment = environment,
    endpoint = PokemonEndpoint.Data,
    httpClientProvider = httpClientProvider,
) {

    override val serverRequestBuilder: io.ktor.client.request.HttpRequestBuilder.() -> Unit = {
        accept(ContentType.Application.Json)
    }
}