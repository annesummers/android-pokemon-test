package com.giganticsheep.pokemon.data.species

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.giganticsheep.pokemon.data.network.client.PokemonHttpClient
import com.giganticsheep.pokemon.data.network.client.PokemonImageHttpClient
import com.giganticsheep.pokemon.data.species.model.SpeciesItem
import com.giganticsheep.pokemon.data.species.model.SpeciesItemResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

//TODO
internal class SpeciesApiTest {

    private val mockHttpClient = mockk<PokemonHttpClient>()
    private val mockHttpImageClient = mockk<PokemonImageHttpClient>()
    private val mockEndpointManager = mockk<SpeciesEndpointManager>()

    private val itemResponse = SpeciesItemResponse(
        count = 1,
        results = listOf(
            SpeciesItem(SPECIES_NAME),
            SpeciesItem(SPECIES_NAME + "1"),
            SpeciesItem(SPECIES_NAME + "2"),
            SpeciesItem(SPECIES_NAME + "3")
        )
    )

    private lateinit var api: InternalSpeciesApi

    @Before
    fun setup() {
        api = InternalSpeciesApi(
            mockHttpClient,
            mockHttpImageClient,
            mockEndpointManager
        )
    }

    @Test
    fun `when getGenerations then return results`() = runTest {
        every { mockEndpointManager.species } returns SPECIES
        every { mockEndpointManager.offset } returns OFFSET

        coEvery {
            mockHttpClient.get<SpeciesItemResponse>(
                SPECIES,
                mapOf(
                    OFFSET to "0",
                    LIMIT to "20"
                )
            )
        } returns itemResponse

        val result = api.getSpecies()

        assertThat(result).isEqualTo(itemResponse.results)
    }

    companion object {
        private const val SPECIES = "SPECIES"
        private const val OFFSET = "OFFSET"
        private const val LIMIT = "LIMIT"
        private const val SPECIES_NAME = "SPECIES_NAME"
    }
}

