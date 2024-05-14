package com.giganticsheep.pokemon.data.generations

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.giganticsheep.pokemon.data.generations.model.GenerationItem
import com.giganticsheep.pokemon.data.generations.model.GenerationItemResponse
import com.giganticsheep.pokemon.data.network.client.PokemonHttpClient
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

//TODO
internal class GenerationsApiTest {

    private val mockHttpClient = mockk<PokemonHttpClient>()
    private val mockEndpointManager = mockk<GenerationEndpointManager>()

    private val generationItemResponse = GenerationItemResponse(
        count = 1,
        results = listOf(
            GenerationItem(GENERATION_NAME),
            GenerationItem(GENERATION_NAME + "1"),
            GenerationItem(GENERATION_NAME + "2"),
            GenerationItem(GENERATION_NAME + "3")
        )
    )

    private lateinit var generationsApi: InternalGenerationsApi

    @Before
    fun setup() {
        generationsApi = InternalGenerationsApi(
            mockHttpClient,
            mockEndpointManager
        )
    }

    @Test
    fun `when getGenerations then return results`() = runTest {
        every { mockEndpointManager.generation } returns GENERATION
        every { mockEndpointManager.offset } returns OFFSET

        coEvery {
            mockHttpClient.get<GenerationItemResponse>(
                GENERATION,
                mapOf(
                    OFFSET to "0",
                    LIMIT to "20"
                )
            )
        } returns generationItemResponse

        val result = generationsApi.getGenerations()

        assertThat(result).isEqualTo(generationItemResponse.results)
    }

    companion object {
        private const val GENERATION = "GENERATION"
        private const val OFFSET = "OFFSET"
        private const val LIMIT = "LIMIT"
        private const val GENERATION_NAME = "GENERATION_NAME"
    }
}

