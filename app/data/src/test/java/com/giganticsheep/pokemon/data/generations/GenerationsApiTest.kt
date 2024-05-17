package com.giganticsheep.pokemon.data.generations

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.giganticsheep.pokemon.data.EndpointManager
import com.giganticsheep.pokemon.data.generations.model.Generation
import com.giganticsheep.pokemon.data.generations.model.GenerationItem
import com.giganticsheep.pokemon.data.generations.model.GenerationItemsResponse
import com.giganticsheep.pokemon.data.generations.model.RegionItem
import com.giganticsheep.pokemon.data.network.client.PokemonHttpClient
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.reflect.typeOf

// TODO
internal class GenerationsApiTest {

    private val mockHttpClient = mockk<PokemonHttpClient>()
    private val mockEndpointManager = mockk<GenerationEndpointManager> {
        every { generation } returns GENERATION
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private val generationItemResponse = GenerationItemsResponse(
        count = 1,
        results = listOf(
            GenerationItem(GENERATION_NAME),
            GenerationItem(GENERATION_NAME + "1"),
            GenerationItem(GENERATION_NAME + "2"),
            GenerationItem(GENERATION_NAME + "3"),
        ),
    )

    private val generation = Generation(
        name = GENERATION_NAME,
        id = GENERATION_ID,
        mainRegion = RegionItem(MAIN_REGION),
        moves = listOf(),
        species = listOf(),
    )

    private lateinit var generationsApi: InternalGenerationsApi

    @Before
    fun setup() {
        generationsApi = InternalGenerationsApi(
            mockHttpClient,
            mockEndpointManager,
        )
    }

    @Test
    fun `when getGenerations then return results`() = runTest {
        coEvery {
            mockHttpClient.get<GenerationItemsResponse>(
                path = GENERATION,
                query = mapOf(
                    EndpointManager.OFFSET to "0",
                    EndpointManager.LIMIT to "20",
                ),
                jsonUtilities = any(),
                block = any(),
                type = typeOf<GenerationItemsResponse>(),
            )
        } returns generationItemResponse

        val result = generationsApi.getGenerations()

        assertThat(result)
            .isEqualTo(generationItemResponse)
    }

    @Test
    fun `when getGeneration then return results`() = runTest {
        val url = "url"

        every {
            mockEndpointManager.generation(GENERATION_NAME)
        } returns url

        coEvery {
            mockHttpClient.get<Generation>(
                path = url,
                query = any(),
                jsonUtilities = any(),
                block = any(),
                type = typeOf<Generation>(),
            )
        } returns generation

        val result = generationsApi.getGeneration(GENERATION_NAME)

        assertThat(result)
            .isEqualTo(generation)
    }

    companion object {
        private const val GENERATION = "GENERATION"
        private const val GENERATION_NAME = "GENERATION_NAME"
        private const val GENERATION_ID = 1
        private const val MAIN_REGION = "MAIN_REGION"
    }
}
