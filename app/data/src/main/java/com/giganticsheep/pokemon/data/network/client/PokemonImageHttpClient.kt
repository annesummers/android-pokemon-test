package com.giganticsheep.pokemon.data.network.client

import com.giganticsheep.logging.LoggerFactory
import com.giganticsheep.network.client.HttpClient
import com.giganticsheep.network.client.HttpClientProvider
import com.giganticsheep.network.environment.Environment
import com.giganticsheep.pokemon.data.network.environment.PokemonEndpoint
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonImageHttpClient @Inject constructor(
    httpClientProvider: HttpClientProvider,
    loggerFactory: LoggerFactory,
    environment: Environment,
) : HttpClient(
    loggerFactory = loggerFactory,
    environment = environment,
    endpoint = PokemonEndpoint.Images,
    httpClientProvider = httpClientProvider,
)
