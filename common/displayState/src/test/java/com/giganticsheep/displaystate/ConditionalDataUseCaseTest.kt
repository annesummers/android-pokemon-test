package com.giganticsheep.displaystate

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.giganticsheep.error.HandledException
import com.giganticsheep.response.CompletableResponseState
import com.giganticsheep.response.DataResponseState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class ConditionalDataUseCaseTest {

    private class TestException(message: String) : HandledException(internalMessage = message)

    private class TestClass
    private class TestClassDisplay

    private inner class TestScreenStateProvider(
        dispatcher: CoroutineDispatcher,
    ) : DisplayStateProvider<DisplayScreenState>,
        DisplayScreenStateSetter by DisplayScreenStateSet(dispatcher) {

        fun provides(response: CompletableResponseState) {
            showResult(response)
        }
    }

    private inner class TestDataStateProvider(
        dispatcher: CoroutineDispatcher,
    ) : DisplayStateProvider<DisplayDataState<TestClassDisplay>>,
        DisplayDataStateSetter<TestClass, TestClassDisplay> by DisplayDataStateSet(dispatcher) {

        fun provides(response: DataResponseState<TestClass>) {
            showResult(response) { testClassDisplay }
        }
    }

    private class TestConditionalDataUseCase(
        dispatcher: CoroutineDispatcher,
        initialProvider: DisplayStateProvider<DisplayScreenState>,
        provider: DisplayStateProvider<DisplayDataState<TestClassDisplay>>,
    ) : ConditionalDataUseCase<TestClassDisplay>(
        dispatcher = dispatcher,
        initialProvider = initialProvider,
        provider = provider
    ) {
        fun testInitialProviderDefault(
            action: suspend () -> Unit,
        ) = onInitialProviderDefault(action)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())


    private val testProvider = TestDataStateProvider(
        dispatcher = testDispatcher
    )

    private val testInitialProvider = TestScreenStateProvider(
        dispatcher = testDispatcher
    )

    private val testClassDisplay = TestClassDisplay()

    private lateinit var useCase: TestConditionalDataUseCase

    @Test
    fun `when initial provider is uninitialised and provider is uninitialised then displayState is uninitialised`() =
        runTest {

            useCase = TestConditionalDataUseCase(
                dispatcher = testDispatcher,
                initialProvider = testInitialProvider,
                provider = testProvider
            )

            useCase.testInitialProviderDefault {
                testProvider.provides(DataResponseState.Empty())
            }

            testInitialProvider.provides(CompletableResponseState.Empty)

            useCase.displayState.test {

                assertThat(awaitItem())
                    .isInstanceOf<DisplayDataState.Uninitialised<TestClassDisplay>>()
            }
        }

    @Test
    fun `when initial provider is uninitialised and provider is loading then displayState is uninitialised`() =
        runTest {

            useCase = TestConditionalDataUseCase(
                dispatcher = testDispatcher,
                initialProvider = testInitialProvider,
                provider = testProvider
            )

            useCase.testInitialProviderDefault {
                testProvider.provides(DataResponseState.Loading())
            }

            testInitialProvider.provides(CompletableResponseState.Empty)

            useCase.displayState.test {

                assertThat(awaitItem())
                    .isInstanceOf<DisplayDataState.Uninitialised<TestClassDisplay>>()
            }
        }

    @Test
    fun `when initial provider is uninitialised and provider is error then displayState is uninitialised`() =
        runTest {
            val testException = TestException("Test")

            useCase = TestConditionalDataUseCase(
                dispatcher = testDispatcher,
                initialProvider = testInitialProvider,
                provider = testProvider
            )

            useCase.testInitialProviderDefault {
                testProvider.provides(DataResponseState.Error(testException))
            }

            testInitialProvider.provides(CompletableResponseState.Empty)

            useCase.displayState.test {

                assertThat(awaitItem())
                    .isInstanceOf<DisplayDataState.Uninitialised<TestClassDisplay>>()
            }
        }

    @Test
    fun `when initial provider is uninitialised and provider is data then displayState is uninitialised`() =
        runTest {
            useCase = TestConditionalDataUseCase(
                dispatcher = testDispatcher,
                initialProvider = testInitialProvider,
                provider = testProvider
            )

            useCase.testInitialProviderDefault {
                testProvider.provides(DataResponseState.Data(TestClass()))
            }

            testInitialProvider.provides(CompletableResponseState.Empty)

            useCase.displayState.test {

                assertThat(awaitItem())
                    .isInstanceOf<DisplayDataState.Uninitialised<TestClassDisplay>>()
            }
        }

    @Test
    fun `when initial provider is loading and provider is uninitialised then displayState is loading`() =
        runTest {
            useCase = TestConditionalDataUseCase(
                dispatcher = testDispatcher,
                initialProvider = testInitialProvider,
                provider = testProvider
            )

            useCase.testInitialProviderDefault {
                testProvider.provides(DataResponseState.Empty())
            }

            testInitialProvider.provides(CompletableResponseState.Loading)

            useCase.displayState.test {

                assertThat(awaitItem())
                    .isInstanceOf<DisplayDataState.Loading<TestClassDisplay>>()
            }
        }

    @Test
    fun `when initial provider is loading and provider is loading then displayState is loading`() =
        runTest {
            useCase = TestConditionalDataUseCase(
                dispatcher = testDispatcher,
                initialProvider = testInitialProvider,
                provider = testProvider
            )

            useCase.testInitialProviderDefault {
                testProvider.provides(DataResponseState.Loading())
            }

            testInitialProvider.provides(CompletableResponseState.Loading)

            useCase.displayState.test {

                assertThat(awaitItem())
                    .isInstanceOf<DisplayDataState.Loading<TestClassDisplay>>()
            }
        }

    @Test
    fun `when initial provider is loading and provider is error then displayState is loading`() =
        runTest {
            val testException = TestException("Test")

            useCase = TestConditionalDataUseCase(
                dispatcher = testDispatcher,
                initialProvider = testInitialProvider,
                provider = testProvider
            )

            useCase.testInitialProviderDefault {
                testProvider.provides(DataResponseState.Error(testException))
            }

            testInitialProvider.provides(CompletableResponseState.Loading)

            useCase.displayState.test {

                assertThat(awaitItem())
                    .isInstanceOf<DisplayDataState.Loading<TestClassDisplay>>()
            }
        }

    @Test
    fun `when initial provider is loading and provider is data then displayState is loading`() =
        runTest {
            useCase = TestConditionalDataUseCase(
                dispatcher = testDispatcher,
                initialProvider = testInitialProvider,
                provider = testProvider
            )
            useCase.testInitialProviderDefault {
                testProvider.provides(DataResponseState.Data(TestClass()))
            }

            testInitialProvider.provides(CompletableResponseState.Loading)

            useCase.displayState.test {

                assertThat(awaitItem())
                    .isInstanceOf<DisplayDataState.Loading<TestClassDisplay>>()
            }
        }

    @Test
    fun `when initial provider is error and provider is uninitialised then displayState is error`() =
        runTest {
            val testException = TestException("Test")

            useCase = TestConditionalDataUseCase(
                dispatcher = testDispatcher,
                initialProvider = testInitialProvider,
                provider = testProvider
            )

            useCase.testInitialProviderDefault {
                testProvider.provides(DataResponseState.Empty())
            }

            testInitialProvider.provides(CompletableResponseState.Error(testException))

            useCase.displayState.test {

                val item = awaitItem()

                assertThat(item)
                    .isInstanceOf<DisplayDataState.Error<TestClassDisplay>>()

                assertThat((item as DisplayDataState.Error).error)
                    .isEqualTo(testException.internalMessage)
            }
        }

    @Test
    fun `when initial provider is error and provider is loading then displayState is error`() =
        runTest {
            val testException = TestException("Test")

            useCase = TestConditionalDataUseCase(
                dispatcher = testDispatcher,
                initialProvider = testInitialProvider,
                provider = testProvider
            )

            useCase.testInitialProviderDefault {
                testProvider.provides(DataResponseState.Loading())
            }

            testInitialProvider.provides(CompletableResponseState.Error(testException))

            useCase.displayState.test {

                val item = awaitItem()

                assertThat(item)
                    .isInstanceOf<DisplayDataState.Error<TestClassDisplay>>()

                assertThat((item as DisplayDataState.Error).error)
                    .isEqualTo(testException.internalMessage)
            }
        }

    @Test
    fun `when initial provider is error and provider is error then displayState is error`() =
        runTest {
            val testException = TestException("Test")
            val testException2 = TestException("Test2")

            useCase = TestConditionalDataUseCase(
                dispatcher = testDispatcher,
                initialProvider = testInitialProvider,
                provider = testProvider
            )

            useCase.testInitialProviderDefault {
                testProvider.provides(DataResponseState.Error(testException2))
            }

            testInitialProvider.provides(CompletableResponseState.Error(testException))

            useCase.displayState.test {

                val item = awaitItem()

                assertThat(item)
                    .isInstanceOf<DisplayDataState.Error<TestClassDisplay>>()

                assertThat((item as DisplayDataState.Error).error)
                    .isEqualTo(testException.internalMessage)
            }
        }

    @Test
    fun `when initial provider is error and provider is data then displayState is error`() =
        runTest {
            val testException = TestException("Test")

            useCase = TestConditionalDataUseCase(
                dispatcher = testDispatcher,
                initialProvider = testInitialProvider,
                provider = testProvider
            )

            useCase.testInitialProviderDefault {
                testProvider.provides(DataResponseState.Data(TestClass()))
            }

            testInitialProvider.provides(CompletableResponseState.Error(testException))

            useCase.displayState.test {

                val item = awaitItem()

                assertThat(item)
                    .isInstanceOf<DisplayDataState.Error<TestClassDisplay>>()

                assertThat((item as DisplayDataState.Error).error)
                    .isEqualTo(testException.internalMessage)
            }
        }

    @Test
    fun `when initial provider is default and provider is uninitialised then displayState is uninitialised`() =
        runTest {

            useCase = TestConditionalDataUseCase(
                dispatcher = testDispatcher,
                initialProvider = testInitialProvider,
                provider = testProvider
            )

            useCase.testInitialProviderDefault {
                testProvider.provides(DataResponseState.Empty())
            }

            testInitialProvider.provides(CompletableResponseState.Loading)

            useCase.displayState.test {

                assertThat(awaitItem())
                    .isInstanceOf<DisplayDataState.Loading<TestClassDisplay>>()
            }
        }

    @Test
    fun `when initial provider is default and provider is loading then displayState is loading`() =
        runTest {

            useCase = TestConditionalDataUseCase(
                dispatcher = testDispatcher,
                initialProvider = testInitialProvider,
                provider = testProvider
            )

            useCase.testInitialProviderDefault {
                testProvider.provides(DataResponseState.Loading())
            }

            testInitialProvider.provides(CompletableResponseState.Success)

            useCase.displayState.test {

                assertThat(awaitItem())
                    .isInstanceOf<DisplayDataState.Loading<TestClassDisplay>>()
            }
        }

    @Test
    fun `when initial provider is default and provider is error then displayState is error`() =
        runTest {
            val testException = TestException("Test")

            useCase = TestConditionalDataUseCase(
                dispatcher = testDispatcher,
                initialProvider = testInitialProvider,
                provider = testProvider
            )

            useCase.testInitialProviderDefault {
                testProvider.provides(DataResponseState.Error(testException))
            }

            testInitialProvider.provides(CompletableResponseState.Success)

            useCase.displayState.test {

                val item = awaitItem()

                assertThat(item)
                    .isInstanceOf<DisplayDataState.Error<TestClassDisplay>>()

                assertThat((item as DisplayDataState.Error).error)
                    .isEqualTo(testException.internalMessage)
            }
        }

    @Test
    fun `when initial provider is default and provider is data then displayState is data`() =
        runTest {
            useCase = TestConditionalDataUseCase(
                dispatcher = testDispatcher,
                initialProvider = testInitialProvider,
                provider = testProvider
            )

            useCase.testInitialProviderDefault {
                testProvider.provides(DataResponseState.Data(TestClass()))
            }

            testInitialProvider.provides(CompletableResponseState.Success)

            useCase.displayState.test {

                assertThat(awaitItem())
                    .isEqualTo(DisplayDataState.Data(testClassDisplay))
            }
        }
}

