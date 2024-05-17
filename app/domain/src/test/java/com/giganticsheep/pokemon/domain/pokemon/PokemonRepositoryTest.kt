package com.giganticsheep.pokemon.domain.pokemon

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.giganticsheep.error.HandledException
import com.giganticsheep.pokemon.data.species.SpeciesApi
import com.giganticsheep.pokemon.data.species.model.Language
import com.giganticsheep.pokemon.data.species.model.Species
import com.giganticsheep.pokemon.data.species.model.SpeciesItem
import com.giganticsheep.pokemon.data.species.model.SpeciesItemsResponse
import com.giganticsheep.response.CompletableResponse
import com.giganticsheep.response.DataResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

// TODO
class PokemonRepositoryTest {

    private class TestException : HandledException(internalMessage = "Test")

    private val api = mockk<SpeciesApi>()

    private val testSpecies = Species(
        internalName = SPECIES_NAME,
        id = SPECIES_ID,
        names = listOf(Species.Name(Language("en"), SPECIES_DISPLAY_NAME)),
        descriptions = listOf(Species.FlavorText(Language("en"), SPECIES_DESCRIPTION)),
    )

    private val testPokemon = Pokemon(
        id = SPECIES_ID,
        name = SPECIES_DISPLAY_NAME,
        descriptions = listOf(SPECIES_DESCRIPTION),
        internalName = SPECIES_NAME,
        imageUrl = SPECIES_IMAGE_URL,
    )

    private val testSpeciesResponse = SpeciesItemsResponse(
        count = 1,
        results = listOf(
            SpeciesItem(SPECIES_NAME),
        ),
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private lateinit var repository: InternalPokemonRepository

    @Before
    fun setup() {
        repository = InternalPokemonRepository(
            api,
        )
    }

    @Test
    fun `when getPokemon by name success then return pokemon`() = runTest {
        coEvery {
            api.getSpecies(SPECIES_NAME)
        } returns testSpecies

        coEvery {
            api.getSpeciesImageUrl(SPECIES_ID)
        } returns SPECIES_IMAGE_URL

        val result = repository.getPokemon(SPECIES_NAME)

        assertThat(result)
            .isInstanceOf<DataResponse.Success<Pokemon>>()

        assertThat((result as DataResponse.Success).result)
            .isEqualTo(testPokemon)
    }

    @Test
    fun `when getPokemon by name getSpecies error then return error`() = runTest {
        val testException = TestException()

        coEvery {
            api.getSpecies(SPECIES_NAME)
        } throws testException

        coEvery {
            api.getSpeciesImageUrl(SPECIES_ID)
        } returns SPECIES_IMAGE_URL

        val result = repository.getPokemon(SPECIES_NAME)

        assertThat(result)
            .isInstanceOf<DataResponse.Error<Pokemon>>()

        assertThat((result as DataResponse.Error).error)
            .isEqualTo(testException)

        coVerify(exactly = 0) { api.getSpeciesImageUrl(SPECIES_ID) }
    }

    @Test
    fun `when getPokemon by name getSpeciesImageUrl error then return error`() = runTest {
        val testException = TestException()

        coEvery {
            api.getSpecies(SPECIES_NAME)
        } returns testSpecies

        coEvery {
            api.getSpeciesImageUrl(SPECIES_ID)
        } throws testException

        val result = repository.getPokemon(SPECIES_NAME)

        assertThat(result)
            .isInstanceOf<DataResponse.Error<Pokemon>>()

        assertThat((result as DataResponse.Error).error)
            .isEqualTo(testException)
    }

    @Test
    fun `when setup success then return success`() = runTest {
        coEvery {
            api.getSpecies(0, 0)
        } returns testSpeciesResponse

        val result = repository.setup()

        assertThat(result)
            .isInstanceOf<CompletableResponse.Success>()

        coVerify { api.getSpecies(0, 0) }
    }

    @Test
    fun `when setup success then setup return success and getSpecies called only once`() = runTest {
        coEvery {
            api.getSpecies(0, 0)
        } returns testSpeciesResponse

        repository.setup()
        val result = repository.setup()

        assertThat(result)
            .isInstanceOf<CompletableResponse.Success>()

        coVerify(exactly = 1) { api.getSpecies(0, 0) }
    }

    @Test
    fun `when setup getSpecies error then return error`() = runTest {
        val testException = TestException()

        coEvery {
            api.getSpecies(0, 0)
        } throws testException

        val result = repository.setup()

        assertThat(result)
            .isInstanceOf<CompletableResponse.Error>()

        assertThat((result as CompletableResponse.Error).error)
            .isEqualTo(testException)
    }

    @Test
    fun `when setup and getRandomPokemon success then return pokemon`() = runTest {
        coEvery {
            api.getSpecies(0, 0)
        } returns testSpeciesResponse

        coEvery {
            api.getSpecies("1")
        } returns testSpecies

        coEvery {
            api.getSpeciesImageUrl(SPECIES_ID)
        } returns SPECIES_IMAGE_URL

        repository.setup()

        val result = repository.getRandomPokemon()

        assertThat(result)
            .isInstanceOf<DataResponse.Success<Pokemon>>()

        assertThat((result as DataResponse.Success).result)
            .isEqualTo(testPokemon)
    }

    @Test
    fun `when getRandomPokemon and setup not called then return error`() = runTest {
        coEvery {
            api.getSpecies(0, 0)
        } returns testSpeciesResponse

        coEvery {
            api.getSpecies("1")
        } returns testSpecies

        coEvery {
            api.getSpeciesImageUrl(SPECIES_ID)
        } returns SPECIES_IMAGE_URL

        val result = repository.getRandomPokemon()

        assertThat(result)
            .isInstanceOf<DataResponse.Error<Pokemon>>()

        assertThat((result as DataResponse.Error).error.internalMessage)
            .isEqualTo("Not ready")
    }

    companion object {
        private const val SPECIES_ID = 1
        private const val SPECIES_NAME = "SPECIES_NAME"
        private const val SPECIES_DESCRIPTION = "SPECIES_DESCRIPTION"
        private const val SPECIES_DISPLAY_NAME = "SPECIES_DISPLAY_NAME"
        private const val SPECIES_IMAGE_URL = "SPECIES_IMAGE_URL"
    }
}
