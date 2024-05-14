package com.giganticsheep.pokemon.domain.generations

import app.cash.turbine.turbineScope
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.giganticsheep.error.HandledException
import com.giganticsheep.pokemon.data.generations.GenerationsApi
import com.giganticsheep.pokemon.data.generations.model.Generation
import com.giganticsheep.pokemon.data.generations.model.GenerationItem
import com.giganticsheep.pokemon.data.generations.model.RegionItem
import com.giganticsheep.response.DataResponseState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

//TODO
class GenerationsRepositoryTest {

    private class TestException : HandledException(internalMessage = "Test")

    private val api = mockk<GenerationsApi>()

    private val testGeneration = Generation(
        name = GENERATION_NAME,
        moves = listOf(),
        species = listOf(),
        id = GENERATION_ID,
        mainRegion = RegionItem(GENERATION_REGION)
    )

    private val testGenerations = listOf(
        GenerationItem(GENERATION_NAME),
        GenerationItem(GENERATION_NAME + "1"),
        GenerationItem(GENERATION_NAME + "2"),
        GenerationItem(GENERATION_NAME + "3")
    )

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private lateinit var repository: InternalGenerationsRepository

    @Before
    fun setup() {
        repository = InternalGenerationsRepository(
            api,
            testDispatcher
        )
    }

    @Test
    fun `when getGeneration by name then return generation`() = runTest(testDispatcher) {

        coEvery {
            api.getGeneration(GENERATION_NAME)
        } returns testGeneration

        assertThat(repository.getGeneration(GENERATION_NAME))
            .isEqualTo(testGeneration)
    }

    @Test
    fun `when fetchGenerations success then generations Flow returns generations`() = runTest(testDispatcher) {

        coEvery {
            api.getGenerations()
        } returns testGenerations

        turbineScope {
            repository.generations.testIn(this)
                .apply {

                    repository.fetchGenerations()

                    awaitItem() // Empty
                    awaitItem() // Loading

                    val item = awaitItem() // Data

                    assertThat(item)
                        .isInstanceOf(DataResponseState.Success::class.java)

                    assertThat((item as DataResponseState.Success).result)
                        .isEqualTo(testGenerations)
                }
        }
    }


    companion object {
        private const val GENERATION_ID = 1
        private const val GENERATION_NAME = "GENERATION_NAME"
        private const val GENERATION_REGION = "GENERATION_REGION"
    }

}