package thisissadeghi.dashboard.integration

import app.cash.turbine.test
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import thisissadeghi.common.ErrorModel
import thisissadeghi.common.UiState
import thisissadeghi.dashboard.data.datasource.DashboardRemoteDataSourceImpl
import thisissadeghi.dashboard.data.repository.DashboardRepositoryImpl
import thisissadeghi.dashboard.fixtures.DashboardFixtures
import thisissadeghi.dashboard.presentation.DashboardViewModel
import thisissadeghi.data.ErrorConst
import thisissadeghi.data.remote.network.ktor.ApiClient
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardIntegrationTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockEngine: MockEngine
    private lateinit var viewModel: DashboardViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun setupWithMockEngine(handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData) {
        mockEngine = MockEngine { request -> handler(request) }

        val httpClient =
            HttpClient(mockEngine) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                        },
                    )
                }
                install(Resources)
            }

        val apiClient = ApiClient(httpClient)
        val dataSource = DashboardRemoteDataSourceImpl(apiClient)
        val repository = DashboardRepositoryImpl(dataSource)
        viewModel = DashboardViewModel(repository)
    }

    // === HAPPY PATH ===

    @Test
    fun `full flow - load dashboard succeeds with valid response`() =
        runTest(testDispatcher) {
            setupWithMockEngine {
                respond(
                    content = DashboardFixtures.validDashboardDataJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

            viewModel.uiModelState.test {
                var current = awaitItem()

                if (current.dashboardState is UiState.Uninitialized) {
                    current = awaitItem()
                }

                if (current.dashboardState is UiState.Loading) {
                    advanceUntilIdle()
                    current = awaitItem()
                }

                assertTrue(current.dashboardState is UiState.Success)
                val data = current.dashboardState.value
                assertEquals(12450.75, data.accountBalance.totalBalance)
                assertEquals("USD", data.accountBalance.currency)
                assertEquals(2.35, data.accountBalance.changePercent)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `full flow - dashboard data contains all collections with correct sizes`() =
        runTest(testDispatcher) {
            setupWithMockEngine {
                respond(
                    content = DashboardFixtures.validDashboardDataJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

            viewModel.uiModelState.test {
                var current = awaitItem()

                while (current.dashboardState !is UiState.Success) {
                    advanceUntilIdle()
                    current = awaitItem()
                }

                val data = current.dashboardState.value
                assertEquals(3, data.recentTransactions.size)
                assertEquals(3, data.budgetCategories.size)
                assertEquals(3, data.savingsGoals.size)
                assertEquals(4, data.quickActions.size)
                assertEquals(3, data.upcomingBills.size)
                assertEquals(3, data.portfolioAssets.size)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `full flow - all field mappings work end to end`() =
        runTest(testDispatcher) {
            setupWithMockEngine {
                respond(
                    content = DashboardFixtures.validDashboardDataJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

            viewModel.uiModelState.test {
                var current = awaitItem()

                while (current.dashboardState !is UiState.Success) {
                    advanceUntilIdle()
                    current = awaitItem()
                }

                val data = current.dashboardState.value

                // Monthly summary
                assertEquals("May 2026", data.monthlySummary.monthName)
                assertEquals(5200.0, data.monthlySummary.income)
                assertEquals(3740.5, data.monthlySummary.expenses)

                // Spending insight
                assertEquals("You spent 12% less on dining this month.", data.spendingInsight.message)
                assertEquals(-12.0, data.spendingInsight.percentageChange)
                assertTrue(data.spendingInsight.isPositive)

                // First transaction
                val firstTxn = data.recentTransactions.first()
                assertEquals("txn-1", firstTxn.id)
                assertEquals("Transaction 1", firstTxn.title)
                assertEquals(10.0, firstTxn.amount)

                // First budget category
                val firstBudget = data.budgetCategories.first()
                assertEquals("Category 1", firstBudget.name)
                assertEquals(50.0, firstBudget.spent)
                assertEquals(100.0, firstBudget.total)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `full flow - empty collections load successfully`() =
        runTest(testDispatcher) {
            setupWithMockEngine {
                respond(
                    content = DashboardFixtures.emptyListsDashboardDataJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

            viewModel.uiModelState.test {
                var current = awaitItem()

                while (current.dashboardState !is UiState.Success) {
                    advanceUntilIdle()
                    current = awaitItem()
                }

                val data = current.dashboardState.value
                assertTrue(data.recentTransactions.isEmpty())
                assertTrue(data.budgetCategories.isEmpty())
                assertTrue(data.savingsGoals.isEmpty())
                assertTrue(data.quickActions.isEmpty())
                assertTrue(data.upcomingBills.isEmpty())
                assertTrue(data.portfolioAssets.isEmpty())

                cancelAndIgnoreRemainingEvents()
            }
        }

    // === STATE TRANSITIONS ===

    @Test
    fun `full flow - state transitions from Uninitialized through Loading to Success`() =
        runTest(testDispatcher) {
            setupWithMockEngine {
                respond(
                    content = DashboardFixtures.validDashboardDataJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

            viewModel.uiModelState.test {
                // First emission must be Uninitialized or Loading (init triggers loadDashboard immediately)
                val first = awaitItem()
                assertTrue(
                    first.dashboardState is UiState.Uninitialized || first.dashboardState is UiState.Loading,
                    "Expected Uninitialized or Loading, got ${first.dashboardState}",
                )

                // After advancing, we get Success
                advanceUntilIdle()

                val final = awaitItem()
                assertTrue(final.dashboardState is UiState.Success)

                cancelAndIgnoreRemainingEvents()
            }
        }

    // === ERROR RECOVERY ===

    @Test
    fun `full flow - retry after 503 failure succeeds`() =
        runTest(testDispatcher) {
            var requestCount = 0
            setupWithMockEngine {
                requestCount++
                if (requestCount == 1) {
                    respond(
                        content = DashboardFixtures.error503Json,
                        status = HttpStatusCode.ServiceUnavailable,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                } else {
                    respond(
                        content = DashboardFixtures.validDashboardDataJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
            }

            viewModel.uiModelState.test {
                var current = awaitItem()

                while (current.dashboardState !is UiState.Failed) {
                    advanceUntilIdle()
                    current = awaitItem()
                }

                viewModel.retry()
                advanceUntilIdle()

                while (current.dashboardState !is UiState.Success) {
                    current = awaitItem()
                }

                assertEquals(2, requestCount)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `full flow - retry after network failure succeeds`() =
        runTest(testDispatcher) {
            var requestCount = 0
            setupWithMockEngine { request ->
                requestCount++
                if (requestCount == 1) {
                    throw Exception("Connection refused")
                } else {
                    respond(
                        content = DashboardFixtures.validDashboardDataJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
            }

            viewModel.uiModelState.test {
                var current = awaitItem()

                while (current.dashboardState !is UiState.Failed) {
                    advanceUntilIdle()
                    current = awaitItem()
                }

                viewModel.retry()
                advanceUntilIdle()

                while (current.dashboardState !is UiState.Success) {
                    current = awaitItem()
                }

                assertEquals(2, requestCount)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `full flow - multiple retries eventually succeed`() =
        runTest(testDispatcher) {
            var requestCount = 0
            setupWithMockEngine { request ->
                requestCount++
                if (requestCount < 3) {
                    respond(
                        content = DashboardFixtures.error503Json,
                        status = HttpStatusCode.ServiceUnavailable,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                } else {
                    respond(
                        content = DashboardFixtures.validDashboardDataJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
            }

            viewModel.uiModelState.test {
                var current = awaitItem()

                // First failure
                while (current.dashboardState !is UiState.Failed) {
                    advanceUntilIdle()
                    current = awaitItem()
                }

                // Second attempt → still fails
                viewModel.retry()
                advanceUntilIdle()
                while (current.dashboardState !is UiState.Failed) {
                    current = awaitItem()
                }

                // Third attempt → succeeds
                viewModel.retry()
                advanceUntilIdle()
                while (current.dashboardState !is UiState.Success) {
                    current = awaitItem()
                }

                assertEquals(3, requestCount)

                cancelAndIgnoreRemainingEvents()
            }
        }

    // === HTTP ERROR HANDLING ===

    @Test
    fun `full flow - handles 401 unauthorized error`() =
        runTest(testDispatcher) {
            setupWithMockEngine {
                respond(
                    content = DashboardFixtures.error401Json,
                    status = HttpStatusCode.Unauthorized,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

            viewModel.uiModelState.test {
                skipItems(1)
                advanceUntilIdle()

                val failed = awaitItem()
                assertTrue(failed.dashboardState is UiState.Failed)

                val error = failed.dashboardState.error as ErrorModel.MessageCode
                assertEquals("You must login", error.message)
                assertEquals(1001, error.code)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `full flow - handles 403 forbidden error`() =
        runTest(testDispatcher) {
            setupWithMockEngine {
                respond(
                    content = DashboardFixtures.error403Json,
                    status = HttpStatusCode.Forbidden,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

            viewModel.uiModelState.test {
                skipItems(1)
                advanceUntilIdle()

                val failed = awaitItem()
                assertTrue(failed.dashboardState is UiState.Failed)

                val error = failed.dashboardState.error as ErrorModel.MessageCode
                assertEquals("Access denied", error.message)
                assertEquals(403, error.code)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `full flow - handles 404 not found error`() =
        runTest(testDispatcher) {
            setupWithMockEngine {
                respond(
                    content = DashboardFixtures.error404Json,
                    status = HttpStatusCode.NotFound,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

            viewModel.uiModelState.test {
                skipItems(1)
                advanceUntilIdle()

                val failed = awaitItem()
                assertTrue(failed.dashboardState is UiState.Failed)

                val error = failed.dashboardState.error as ErrorModel.MessageCode
                assertEquals("Dashboard not found", error.message)
                assertEquals(404, error.code)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `full flow - handles 400 bad request error`() =
        runTest(testDispatcher) {
            setupWithMockEngine {
                respond(
                    content = DashboardFixtures.error400Json,
                    status = HttpStatusCode.BadRequest,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

            viewModel.uiModelState.test {
                skipItems(1)
                advanceUntilIdle()

                val failed = awaitItem()
                assertTrue(failed.dashboardState is UiState.Failed)

                val error = failed.dashboardState.error as ErrorModel.MessageCode
                assertEquals("Invalid request parameters", error.message)
                assertEquals(4001, error.code)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `full flow - handles 500 internal server error`() =
        runTest(testDispatcher) {
            setupWithMockEngine {
                respond(
                    content = DashboardFixtures.error500Json,
                    status = HttpStatusCode.InternalServerError,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

            viewModel.uiModelState.test {
                skipItems(1)
                advanceUntilIdle()

                val failed = awaitItem()
                assertTrue(failed.dashboardState is UiState.Failed)

                val error = failed.dashboardState.error as ErrorModel.MessageCode
                assertEquals("Internal Server Error", error.message)
                assertEquals(5001, error.code)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `full flow - handles 503 service unavailable error`() =
        runTest(testDispatcher) {
            setupWithMockEngine {
                respond(
                    content = DashboardFixtures.error503Json,
                    status = HttpStatusCode.ServiceUnavailable,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

            viewModel.uiModelState.test {
                skipItems(1)
                advanceUntilIdle()

                val failed = awaitItem()
                assertTrue(failed.dashboardState is UiState.Failed)

                val error = failed.dashboardState.error as ErrorModel.MessageCode
                assertEquals("An unknown network error has occurred!", error.message)
                assertEquals(503, error.code)

                cancelAndIgnoreRemainingEvents()
            }
        }

    // === NETWORK ERRORS ===

    @Test
    fun `full flow - handles connection refused`() =
        runTest(testDispatcher) {
            setupWithMockEngine {
                throw Exception("Connection refused")
            }

            viewModel.uiModelState.test {
                skipItems(1)
                advanceUntilIdle()

                val failed = awaitItem()
                assertTrue(failed.dashboardState is UiState.Failed)

                val error = failed.dashboardState.error
                assertEquals(ErrorConst.NoNetwork, error)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `full flow - handles connection timeout`() =
        runTest(testDispatcher) {
            setupWithMockEngine {
                throw Exception("Connect timed out")
            }

            viewModel.uiModelState.test {
                skipItems(1)
                advanceUntilIdle()

                val failed = awaitItem()
                assertTrue(failed.dashboardState is UiState.Failed)

                val error = failed.dashboardState.error
                assertEquals(ErrorConst.NoNetwork, error)

                cancelAndIgnoreRemainingEvents()
            }
        }

    // === PARSING ERRORS ===

    @Test
    fun `full flow - handles malformed JSON`() =
        runTest(testDispatcher) {
            setupWithMockEngine {
                respond(
                    content = DashboardFixtures.malformedJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

            viewModel.uiModelState.test {
                skipItems(1)
                advanceUntilIdle()

                val failed = awaitItem()
                assertTrue(failed.dashboardState is UiState.Failed)

                val error = failed.dashboardState.error
                assertEquals(ErrorConst.SerializationError, error)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `full flow - handles incomplete JSON`() =
        runTest(testDispatcher) {
            setupWithMockEngine {
                respond(
                    content = DashboardFixtures.incompleteJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

            viewModel.uiModelState.test {
                skipItems(1)
                advanceUntilIdle()

                val failed = awaitItem()
                assertTrue(failed.dashboardState is UiState.Failed)

                val error = failed.dashboardState.error
                assertEquals(ErrorConst.SerializationError, error)

                cancelAndIgnoreRemainingEvents()
            }
        }

    // === REQUEST VERIFICATION ===

    @Test
    fun `full flow - sends GET request to correct URL path`() =
        runTest(testDispatcher) {
            var capturedUrl: String? = null
            setupWithMockEngine { request ->
                capturedUrl = request.url.toString()
                respond(
                    content = DashboardFixtures.validDashboardDataJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

            viewModel.uiModelState.test {
                advanceUntilIdle()

                while (awaitItem().dashboardState is UiState.Loading) {
                    advanceUntilIdle()
                }

                assertNotNull(capturedUrl)
                assertTrue(
                    capturedUrl.contains("/finance/dashboard.json"),
                    "Expected URL to contain /finance/dashboard.json but was: $capturedUrl",
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `full flow - sends GET HTTP method`() =
        runTest(testDispatcher) {
            var capturedMethod: String? = null
            setupWithMockEngine { request ->
                capturedMethod = request.method.value
                respond(
                    content = DashboardFixtures.validDashboardDataJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

            viewModel.uiModelState.test {
                advanceUntilIdle()

                while (awaitItem().dashboardState is UiState.Loading) {
                    advanceUntilIdle()
                }

                assertEquals("GET", capturedMethod)

                cancelAndIgnoreRemainingEvents()
            }
        }

    // === CONCURRENCY ===

    @Test
    fun `full flow - rapid retry calls use correct final request count`() =
        runTest(testDispatcher) {
            var requestCount = 0
            setupWithMockEngine {
                requestCount++
                respond(
                    content = DashboardFixtures.error503Json,
                    status = HttpStatusCode.ServiceUnavailable,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

            viewModel.uiModelState.test {
                var current = awaitItem()

                while (current.dashboardState !is UiState.Failed) {
                    advanceUntilIdle()
                    current = awaitItem()
                }

                // Rapid successive retries
                viewModel.retry()
                viewModel.retry()
                advanceUntilIdle()

                // At least the original + retries happened
                assertTrue(requestCount >= 2, "Expected at least 2 requests, got $requestCount")

                cancelAndIgnoreRemainingEvents()
            }
        }

    // === EDGE CASES ===

    @Test
    fun `full flow - handles response with extra unknown fields`() =
        runTest(testDispatcher) {
            val jsonWithExtras =
                """
                {
                  "accountBalance": {"totalBalance": 100.0, "currency": "USD", "changePercent": 0.0, "changeAmount": 0.0},
                  "monthlySummary": {"monthName": "May 2026", "income": 0.0, "expenses": 0.0, "currency": "USD"},
                  "recentTransactions": [],
                  "budgetCategories": [],
                  "savingsGoals": [],
                  "quickActions": [],
                  "upcomingBills": [],
                  "spendingInsight": {"message": "ok", "percentageChange": 0.0, "isPositive": true},
                  "portfolioAssets": [],
                  "unknownField": "should be ignored",
                  "anotherUnknown": 42
                }
                """.trimIndent()

            setupWithMockEngine {
                respond(
                    content = jsonWithExtras,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

            viewModel.uiModelState.test {
                var current = awaitItem()

                while (current.dashboardState !is UiState.Success) {
                    advanceUntilIdle()
                    current = awaitItem()
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `full flow - loadDashboard on init triggers data fetch automatically`() =
        runTest(testDispatcher) {
            var requestCount = 0
            setupWithMockEngine {
                requestCount++
                respond(
                    content = DashboardFixtures.validDashboardDataJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

            viewModel.uiModelState.test {
                advanceUntilIdle()

                while (awaitItem().dashboardState !is UiState.Success) {
                    advanceUntilIdle()
                }

                assertEquals(1, requestCount, "ViewModel init should trigger exactly one automatic data fetch")

                cancelAndIgnoreRemainingEvents()
            }
        }
}
