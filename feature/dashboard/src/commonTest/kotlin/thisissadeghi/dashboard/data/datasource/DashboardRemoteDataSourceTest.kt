package thisissadeghi.dashboard.data.datasource

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
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import thisissadeghi.common.Either
import thisissadeghi.common.ErrorModel
import thisissadeghi.dashboard.fixtures.DashboardFixtures
import thisissadeghi.data.ErrorConst
import thisissadeghi.data.remote.network.ktor.ApiClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DashboardRemoteDataSourceTest {
    private lateinit var mockEngine: MockEngine

    private fun createDataSource(
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    ): DashboardRemoteDataSource {
        mockEngine = MockEngine { request -> handler(request) }
        val client =
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
        val apiClient = ApiClient(client)
        return DashboardRemoteDataSourceImpl(apiClient)
    }

    // === SUCCESS CASES ===

    @Test
    fun `getDashboard returns success when API returns 200 with valid JSON`() =
        runTest {
            val dataSource =
                createDataSource {
                    respond(
                        content = DashboardFixtures.validDashboardDataJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }

            val result = dataSource.getDashboard()

            assertTrue(result is Either.Success)
        }

    @Test
    fun `getDashboard returns success with correct accountBalance when API returns 200`() =
        runTest {
            val dataSource =
                createDataSource {
                    respond(
                        content = DashboardFixtures.validDashboardDataJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }

            val result = dataSource.getDashboard()

            assertTrue(result is Either.Success)
            assertEquals(12450.75, result.data.accountBalance.totalBalance)
            assertEquals("USD", result.data.accountBalance.currency)
        }

    @Test
    fun `getDashboard returns success with empty lists when API returns 200 with empty collections`() =
        runTest {
            val dataSource =
                createDataSource {
                    respond(
                        content = DashboardFixtures.emptyListsDashboardDataJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }

            val result = dataSource.getDashboard()

            assertTrue(result is Either.Success)
            val data = result.data
            assertTrue(data.recentTransactions.isEmpty())
            assertTrue(data.budgetCategories.isEmpty())
            assertTrue(data.savingsGoals.isEmpty())
            assertTrue(data.quickActions.isEmpty())
            assertTrue(data.upcomingBills.isEmpty())
            assertTrue(data.portfolioAssets.isEmpty())
        }

    // === HTTP ERROR CODES ===

    @Test
    fun `getDashboard returns failure on 400 Bad Request`() =
        runTest {
            val dataSource =
                createDataSource {
                    respond(
                        content = DashboardFixtures.error400Json,
                        status = HttpStatusCode.BadRequest,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }

            val result = dataSource.getDashboard()

            assertTrue(result is Either.Failure)
            val error = result.error as ErrorModel.MessageCode
            assertEquals("Invalid request parameters", error.message)
            assertEquals(4001, error.code)
        }

    @Test
    fun `getDashboard returns failure on 401 Unauthorized`() =
        runTest {
            val dataSource =
                createDataSource {
                    respond(
                        content = DashboardFixtures.error401Json,
                        status = HttpStatusCode.Unauthorized,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }

            val result = dataSource.getDashboard()

            assertTrue(result is Either.Failure)
            val error = result.error as ErrorModel.MessageCode
            assertEquals("You must login", error.message)
            assertEquals(1001, error.code)
        }

    @Test
    fun `getDashboard returns failure on 403 Forbidden`() =
        runTest {
            val dataSource =
                createDataSource {
                    respond(
                        content = DashboardFixtures.error403Json,
                        status = HttpStatusCode.Forbidden,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }

            val result = dataSource.getDashboard()

            assertTrue(result is Either.Failure)
            val error = result.error as ErrorModel.MessageCode
            assertEquals("Access denied", error.message)
            assertEquals(403, error.code)
        }

    @Test
    fun `getDashboard returns failure on 404 Not Found`() =
        runTest {
            val dataSource =
                createDataSource {
                    respond(
                        content = DashboardFixtures.error404Json,
                        status = HttpStatusCode.NotFound,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }

            val result = dataSource.getDashboard()

            assertTrue(result is Either.Failure)
            val error = result.error as ErrorModel.MessageCode
            assertEquals("Dashboard not found", error.message)
            assertEquals(404, error.code)
        }

    @Test
    fun `getDashboard returns failure on 500 Internal Server Error`() =
        runTest {
            val dataSource =
                createDataSource {
                    respond(
                        content = DashboardFixtures.error500Json,
                        status = HttpStatusCode.InternalServerError,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }

            val result = dataSource.getDashboard()

            assertTrue(result is Either.Failure)
            val error = result.error as ErrorModel.MessageCode
            assertEquals("Internal Server Error", error.message)
            assertEquals(5001, error.code)
        }

    @Test
    fun `getDashboard returns failure on 503 Service Unavailable with null body`() =
        runTest {
            val dataSource =
                createDataSource {
                    respond(
                        content = DashboardFixtures.error503Json,
                        status = HttpStatusCode.ServiceUnavailable,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }

            val result = dataSource.getDashboard()

            assertTrue(result is Either.Failure)
            val error = result.error as ErrorModel.MessageCode
            assertEquals("An unknown network error has occurred!", error.message)
            assertEquals(503, error.code)
        }

    // === PARSING ERRORS ===

    @Test
    fun `getDashboard returns SerializationError when response body is malformed JSON`() =
        runTest {
            val dataSource =
                createDataSource {
                    respond(
                        content = DashboardFixtures.malformedJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }

            val result = dataSource.getDashboard()

            assertTrue(result is Either.Failure)
            assertEquals(ErrorConst.SerializationError, result.error)
        }

    @Test
    fun `getDashboard returns SerializationError when response body is incomplete JSON`() =
        runTest {
            val dataSource =
                createDataSource {
                    respond(
                        content = DashboardFixtures.incompleteJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }

            val result = dataSource.getDashboard()

            assertTrue(result is Either.Failure)
            assertEquals(ErrorConst.SerializationError, result.error)
        }

    @Test
    fun `getDashboard returns SerializationError when response body is empty`() =
        runTest {
            val dataSource =
                createDataSource {
                    respond(
                        content = "",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }

            val result = dataSource.getDashboard()

            assertTrue(result is Either.Failure)
            assertEquals(ErrorConst.SerializationError, result.error)
        }

    @Test
    fun `getDashboard returns success when response contains extra unknown fields`() =
        runTest {
            val jsonWithExtraFields =
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
                  "unknownField": "ignored",
                  "anotherUnknown": 42
                }
                """.trimIndent()

            val dataSource =
                createDataSource {
                    respond(
                        content = jsonWithExtraFields,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }

            val result = dataSource.getDashboard()

            assertTrue(result is Either.Success)
        }

    // === NETWORK FAILURES ===

    @Test
    fun `getDashboard returns NoNetwork failure on connection error`() =
        runTest {
            val dataSource =
                createDataSource {
                    throw Exception("Connection refused")
                }

            val result = dataSource.getDashboard()

            assertTrue(result is Either.Failure)
            assertEquals(ErrorConst.NoNetwork, result.error)
        }

    @Test
    fun `getDashboard returns NoNetwork failure on timeout`() =
        runTest {
            val dataSource =
                createDataSource {
                    throw Exception("Connect timed out")
                }

            val result = dataSource.getDashboard()

            assertTrue(result is Either.Failure)
            assertEquals(ErrorConst.NoNetwork, result.error)
        }

    @Test
    fun `getDashboard returns NoNetwork failure on unknown host`() =
        runTest {
            val dataSource =
                createDataSource {
                    throw Exception("UnknownHostException: Unable to resolve host")
                }

            val result = dataSource.getDashboard()

            assertTrue(result is Either.Failure)
            assertEquals(ErrorConst.NoNetwork, result.error)
        }

    // === REQUEST VERIFICATION ===

    @Test
    fun `getDashboard sends GET request to correct path`() =
        runTest {
            var capturedUrl: String? = null
            val dataSource =
                createDataSource { request ->
                    capturedUrl = request.url.toString()
                    respond(
                        content = DashboardFixtures.validDashboardDataJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }

            dataSource.getDashboard()

            assertNotNull(capturedUrl)
            assertTrue(capturedUrl.contains("/finance/dashboard.json"), "URL was: $capturedUrl")
        }

    @Test
    fun `getDashboard sends GET HTTP method`() =
        runTest {
            var capturedMethod: String? = null
            val dataSource =
                createDataSource { request ->
                    capturedMethod = request.method.value
                    respond(
                        content = DashboardFixtures.validDashboardDataJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }

            dataSource.getDashboard()

            assertEquals("GET", capturedMethod)
        }
}
