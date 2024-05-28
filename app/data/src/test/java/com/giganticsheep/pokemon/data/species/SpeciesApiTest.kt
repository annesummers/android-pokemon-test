package com.giganticsheep.pokemon.data.species

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.giganticsheep.pokemon.data.EndpointManager.Companion.LIMIT
import com.giganticsheep.pokemon.data.EndpointManager.Companion.OFFSET
import com.giganticsheep.pokemon.data.network.client.PokemonHttpClient
import com.giganticsheep.pokemon.data.network.client.PokemonImageHttpClient
import com.giganticsheep.pokemon.data.species.model.Species
import com.giganticsheep.pokemon.data.species.model.SpeciesItem
import com.giganticsheep.pokemon.data.species.model.SpeciesItemsResponse
import io.ktor.http.URLBuilder
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.reflect.typeOf

// TODO
internal class SpeciesApiTest {

    private val mockHttpClient = mockk<PokemonHttpClient>()
    private val mockHttpImageClient = mockk<PokemonImageHttpClient>()
    private val mockEndpointManager = mockk<SpeciesEndpointManager> {
        every { species } returns SPECIES
    }

    private val testItemsResponse = SpeciesItemsResponse(
        count = 1,
        results = listOf(
            SpeciesItem(SPECIES_NAME),
            SpeciesItem(SPECIES_NAME + "1"),
            SpeciesItem(SPECIES_NAME + "2"),
            SpeciesItem(SPECIES_NAME + "3"),
        ),
    )

    private val testSpecies = Species(
        internalName = SPECIES_NAME,
        id = SPECIES_ID,
        names = listOf(),
        descriptions = listOf(),
    )

    private lateinit var api: InternalSpeciesApi

    @Before
    fun setup() {
        api = InternalSpeciesApi(
            mockHttpClient,
            mockHttpImageClient,
            mockEndpointManager,
        )
    }

    @Test
    fun `when getSpecies then return results`() = runTest {
        every { mockEndpointManager.species } returns SPECIES

        coEvery {
            mockHttpClient.get<SpeciesItemsResponse>(
                path = SPECIES,
                query = mapOf(
                    OFFSET to "0",
                    LIMIT to "20",
                ),
                jsonUtilities = any(),
                block = any(),
                type = typeOf<SpeciesItemsResponse>(),
            )
        } returns testItemsResponse

        val result = api.getSpecies()

        assertThat(result).isEqualTo(testItemsResponse)
    }

    @Test
    fun `when getSpecies by name or id then return species`() = runTest {
        val url = "url"

        every {
            mockEndpointManager.species(SPECIES_ID.toString())
        } returns url

        coEvery {
            mockHttpClient.get<Species>(
                path = url,
                query = any(),
                jsonUtilities = any(),
                block = any(),
                type = typeOf<Species>(),
            )
        } returns testSpecies

        val result = api.getSpecies(SPECIES_ID.toString())

        assertThat(result).isEqualTo(testSpecies)
    }

    @Test
    fun `when getSpecies image url then return url string`() = runTest {
        val testHost = "host"
        val testPath1 = "testPath1"
        val testPath2 = "testPath2"

        val testBaseImageUrl = URLBuilder().apply {
            protocol = io.ktor.http.URLProtocol.HTTPS
            host = testHost
            pathSegments = listOf(testPath1, testPath2)
        }

        every {
            mockHttpImageClient.baseUrl
        } returns testBaseImageUrl

        val result = api.getSpeciesImageUrl(SPECIES_ID)

        assertThat(result)
            .isEqualTo("https://$testHost/$testPath1/$testPath2/1.svg")
    }

    companion object {
        private const val SPECIES = "SPECIES"
        private const val SPECIES_ID = 1
        private const val SPECIES_NAME = "SPECIES_NAME"
    }
}
