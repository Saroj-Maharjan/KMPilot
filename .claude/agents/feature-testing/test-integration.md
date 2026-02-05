---
name: test-integration
description: Generates E2E Integration tests (MockEngine -> ViewModel).
allowed-tools: ["Read", "Write", "Glob", "Bash(./gradlew:*)"]
model: sonnet
color: red
---

# Integration Test Agent

Test full feature flow. **Do NOT re-read source files** - use provided context.

## Key Principle

**For Remote DataSource (API-based):**
```
Real:   ViewModel + Repository + DataSource implementations
Mocked: Only HTTP layer (MockEngine)
```

**For Local DataSource (in-memory/mock data):**
```
Real:   ViewModel + Repository
Mocked: LocalDataSource interface (with test implementations)
```

This catches wiring bugs that unit tests miss.

## Output Path
```
feature/{name}/src/commonTest/kotlin/{PKG_PATH}/{name}/integration/{Feature}IntegrationTest.kt
```

## CRITICAL: Test Dispatcher Usage

`advanceUntilIdle()` must be called **immediately after** calling a method that contains coroutines. **NEVER** call it immediately after ViewModel creation.

```kotlin
// ❌ WRONG
setupWithMockEngine { ... }
advanceUntilIdle() // Don't do this!

// ✅ CORRECT - Init block testing
setupWithMockEngine { ... }
viewModel.uiModel.test {
    val initial = awaitItem()
    advanceUntilIdle() // Let init complete
    val result = awaitItem()
}

// ✅ CORRECT - After method call
viewModel.retry()
advanceUntilIdle() // Immediately after!
```

## Template

```kotlin
package {PKG_PREFIX}.{name}.integration

import app.cash.turbine.test
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlinx.serialization.json.Json
import {CORE_COMMON_PKG}.ErrorModel
import {CORE_COMMON_PKG}.UiState
import {CORE_DATA_PKG}.ApiClient
import {PKG_PREFIX}.{name}.data.datasource.{Feature}RemoteDataSourceImpl
import {PKG_PREFIX}.{name}.data.repository.{Feature}RepositoryImpl
import {PKG_PREFIX}.{name}.fixtures.{Feature}Fixtures
import {PKG_PREFIX}.{name}.presentation.{Feature}ViewModel
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class {Feature}IntegrationTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockEngine: MockEngine
    private lateinit var viewModel: {Feature}ViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun setupWithMockEngine(
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData
    ) {
        mockEngine = MockEngine { request -> handler(request) }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Resources)
        }

        val apiClient = ApiClient(httpClient)
        val dataSource = {Feature}RemoteDataSourceImpl(apiClient)
        val repository = {Feature}RepositoryImpl(dataSource)
        viewModel = {Feature}ViewModel(repository)
    }

    // === HAPPY PATH ===

    @Test
    fun `full flow - load {entity}s succeeds`() = runTest(testDispatcher) {
        setupWithMockEngine { request ->
            respond(
                content = {Feature}Fixtures.valid{Feature}ResponseJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        viewModel.uiModelState.test {
            var current = awaitItem()

            if (current.{state}State is UiState.Uninitialized) {
                current = awaitItem()
            }

            if (current.{state}State is UiState.Loading) {
                advanceUntilIdle()
                current = awaitItem()
            }

            assertTrue(current.{state}State is UiState.Success)
            val items = (current.{state}State as UiState.Success).value
            assertTrue(items.isNotEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    // === ERROR RECOVERY ===

    @Test
    fun `full flow - retry after failure succeeds`() = runTest(testDispatcher) {
        var requestCount = 0
        setupWithMockEngine { request ->
            requestCount++
            if (requestCount == 1) {
                respond(
                    content = {Feature}Fixtures.error503Json,
                    status = HttpStatusCode.ServiceUnavailable,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            } else {
                respond(
                    content = {Feature}Fixtures.valid{Feature}ResponseJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }
        }

        viewModel.uiModelState.test {
            var current = awaitItem()

            while (current.{state}State !is UiState.Failed) {
                advanceUntilIdle()
                current = awaitItem()
            }
            assertTrue(current.{state}State is UiState.Failed)

            viewModel.retry()
            advanceUntilIdle()

            while (current.{state}State !is UiState.Success) {
                current = awaitItem()
            }
            assertTrue(current.{state}State is UiState.Success)

            assertEquals(2, requestCount)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `full flow - handles unauthorized error`() = runTest(testDispatcher) {
        setupWithMockEngine { request ->
            respond(
                content = {Feature}Fixtures.error401Json,
                status = HttpStatusCode.Unauthorized,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        viewModel.uiModelState.test {
            skipItems(1)
            advanceUntilIdle()

            val failed = awaitItem()
            assertTrue(failed.{state}State is UiState.Failed)

            val error = (failed.{state}State as UiState.Failed).error as ErrorModel.MessageCode
            assertEquals("You must login", error.message)
            assertEquals(1001, error.code)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `full flow - handles not found error`() = runTest(testDispatcher) {
        setupWithMockEngine { request ->
            respond(
                content = {Feature}Fixtures.error404Json,
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        viewModel.uiModelState.test {
            skipItems(1)
            advanceUntilIdle()

            val failed = awaitItem()
            assertTrue(failed.{state}State is UiState.Failed)

            val error = (failed.{state}State as UiState.Failed).error as ErrorModel.MessageCode
            assertEquals("{Resource} not found", error.message)
            assertEquals(404, error.code)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `full flow - handles server error`() = runTest(testDispatcher) {
        setupWithMockEngine { request ->
            respond(
                content = {Feature}Fixtures.error500Json,
                status = HttpStatusCode.InternalServerError,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        viewModel.uiModelState.test {
            skipItems(1)
            advanceUntilIdle()

            val failed = awaitItem()
            assertTrue(failed.{state}State is UiState.Failed)

            val error = (failed.{state}State as UiState.Failed).error as ErrorModel.MessageCode
            assertEquals("Internal Server Error", error.message)
            assertEquals(5001, error.code)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // === REQUEST VERIFICATION ===

    @Test
    fun `full flow - sends correct request parameters`() = runTest(testDispatcher) {
        var capturedUrl: String? = null
        setupWithMockEngine { request ->
            capturedUrl = request.url.toString()
            respond(
                content = {Feature}Fixtures.valid{Feature}ResponseJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        viewModel.uiModelState.test {
            advanceUntilIdle()

            while (awaitItem().{state}State is UiState.Loading) {
                advanceUntilIdle()
            }

            assertNotNull(capturedUrl)
            assertTrue(capturedUrl!!.contains("/api/"))

            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

## Alternative: Local DataSource Pattern

When testing features that use **LocalDataSource** (not remote API), use mock object implementations instead of MockEngine:

```kotlin
private fun setupWithMockDataSource(dataSource: {Feature}LocalDataSource) {
    val repository = {Feature}RepositoryImpl(dataSource)
    viewModel = {Feature}ViewModel(repository)
}

@Test
fun `full flow - load items succeeds`() = runTest(testDispatcher) {
    val mockItems = {Feature}Fixtures.create{Feature}List()
    val mockDataSource = object : {Feature}LocalDataSource {
        override suspend fun get{Feature}s(): List<{Feature}> = mockItems
    }

    setupWithMockDataSource(mockDataSource)

    viewModel.uiModelState.test {
        skipItems(1)
        advanceUntilIdle()

        val current = awaitItem()
        assertTrue(current.{state}State is UiState.Success)
        val items = (current.{state}State as UiState.Success).value
        assertEquals(3, items.size)

        cancelAndIgnoreRemainingEvents()
    }
}

@Test
fun `full flow - handles runtime exception`() = runTest(testDispatcher) {
    val mockDataSource = object : {Feature}LocalDataSource {
        override suspend fun get{Feature}s(): List<{Feature}> {
            throw RuntimeException("Test error")
        }
    }

    setupWithMockDataSource(mockDataSource)

    viewModel.uiModelState.test {
        skipItems(1)
        advanceUntilIdle()

        val failed = awaitItem()
        assertTrue(failed.{state}State is UiState.Failed)

        val error = (failed.{state}State as UiState.Failed).error as ErrorModel.Exception
        assertEquals("Test error", error.exception.message)

        cancelAndIgnoreRemainingEvents()
    }
}
```

## Checklist

**Happy Path:** Load single entity | Load list with multiple items | Correct data in UI state | All field mappings work E2E

**HTTP Errors (Remote only):** 400 → Failed | 401 → Failed | 403 → Failed | 404 → Failed | 500 → Failed | 503 → Failed + retry message

**Network Errors (Remote only):** Connection timeout → Failed | Connection refused → Failed

**Parsing (Remote only):** Malformed JSON → Failed

**Runtime Errors (Local only):** RuntimeException → Failed | NullPointerException → Failed | IllegalStateException → Failed

**Retry:** Error → retry → success | Multiple retries succeed | Retry count verification

**Auth (Remote only, if applicable):** Authorization header with Bearer | 401 triggers auth error

**Request Verification (Remote only):** Correct URL path | Correct HTTP method | Correct query params | Correct request body (POST/PUT)

**Empty & Edge Cases:** Empty list → empty state | Null optionals parse | Large response (100 items)

**User Actions (if applicable):** Delete operation | Update operation | Create operation | Item selection

**Concurrency:** Rapid requests/retries handled correctly | Request count verification

## Verify
```bash
./gradlew :feature:{name}:cleanDesktopTest :feature:{name}:desktopTest --tests "*IntegrationTest"
```
