package com.giganticsheep.pokemon.data.generations

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.giganticsheep.error.HandledException
import com.giganticsheep.pokemon.data.generations.model.Generation
import com.giganticsheep.pokemon.data.generations.model.GenerationItem
import com.giganticsheep.pokemon.data.generations.model.GenerationItemsResponse
import com.giganticsheep.pokemon.data.generations.model.RegionItem
import com.giganticsheep.response.DataResponse
import com.giganticsheep.response.DataResponseState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

// TODO
class GenerationsRepositoryTest {

    private class TestException : HandledException(internalMessage = "Test")

    private val api = mockk<GenerationsApi>()

    private val testGeneration = Generation(
        name = GENERATION_NAME,
        moves = listOf(),
        species = listOf(),
        id = GENERATION_ID,
        mainRegion = RegionItem(GENERATION_REGION),
    )

    private val testGenerationResponse =
        GenerationItemsResponse(
            count = 4,
            results = listOf(
                GenerationItem(GENERATION_NAME),
                GenerationItem(GENERATION_NAME + "1"),
                GenerationItem(GENERATION_NAME + "2"),
                GenerationItem(GENERATION_NAME + "3"),
            ),
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private lateinit var repository: InternalGenerationsRepository

    @Before
    fun setup() {
        repository = InternalGenerationsRepository(
            api,
            testDispatcher,
        )
    }

    @Test
    fun `when clearData then generations Flow emits Empty`() = runTest {
        repository.generations.test {
            assertThat(awaitItem()) // Empty is emitted when the class is instantiated
                .isInstanceOf(DataResponseState.Empty::class.java)

            repository.clearData()

            assertThat(awaitItem())
                .isInstanceOf(DataResponseState.Empty::class.java)
        }
    }

    @Test
    fun `when getGeneration by name then return generation`() = runTest {
        coEvery {
            api.getGeneration(GENERATION_NAME)
        } returns testGeneration

        val result = repository.getGeneration(GENERATION_NAME)

        assertThat(result)
            .isInstanceOf(DataResponse.Success::class)

        assertThat((result as DataResponse.Success).result)
            .isEqualTo(testGeneration)
    }

    @Test
    fun `when fetchGenerations success then generations Flow emits Loading then Success with GenerationItemsResponse`() =
        runTest {
            coEvery {
                api.getGenerations()
            } returns testGenerationResponse

            repository.generations.test {
                assertThat(awaitItem()) // Empty is emitted when the class is instantiated
                    .isInstanceOf(DataResponseState.Empty::class.java)

                repository.fetchGenerations()

                assertThat(awaitItem())
                    .isInstanceOf(DataResponseState.Loading::class.java)

                val item = awaitItem() // Data

                assertThat(item)
                    .isInstanceOf(DataResponseState.Data::class.java)

                assertThat((item as DataResponseState.Data).result)
                    .isEqualTo(testGenerationResponse.results)
            }
        }

    companion object {
        private const val GENERATION_ID = 1
        private const val GENERATION_NAME = "GENERATION_NAME"
        private const val GENERATION_REGION = "GENERATION_REGION"
    }
}
