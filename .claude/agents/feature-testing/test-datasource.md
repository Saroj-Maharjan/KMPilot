---
name: test-datasource
description: Generates DataSource tests using MockEngine.
allowed-tools: ["Read", "Write", "Glob", "Bash(./gradlew:*)"]
model: sonnet
color: green
---

# Optimized DataSource Test Agent

You test DataSource implementations using Ktor MockEngine. Context is pre-computed by orchestrator.

## Input

Orchestrator provides:
- Feature name and package
- DataSource interface and implementation names
- Method signatures with HTTP verbs and paths
- Fixtures location

**Do NOT re-read source files** - use provided context.

## Output Path

```
feature/{name}/src/commonTest/kotlin/{PKG_PATH}/{name}/data/datasource/{Feature}RemoteDataSourceTest.kt
```

Use `{PKG_PATH}` (package prefix as path, e.g., `acme` or `com/example`).

## Template (1 Complete Example)

```kotlin
package {PKG_PREFIX}.{name}.data.datasource

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import {CORE_COMMON_PKG}.Either
import {CORE_COMMON_PKG}.ErrorConst
import {PKG_PREFIX}.{name}.fixtures.{Feature}Fixtures
import {CORE_DATA_PKG}.ApiClient
import kotlin.test.*

class {Feature}RemoteDataSourceTest {

    private lateinit var mockEngine: MockEngine

    private fun createDataSource(
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    ): {Feature}RemoteDataSource {
        mockEngine = MockEngine { request -> handler(request) }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Resources)
        }
        val apiClient = ApiClient(client)
        return {Feature}RemoteDataSourceImpl(apiClient)
    }

    // ==========================================
    // SUCCESS CASES
    // ==========================================

    @Test
    fun `get{Entity} returns success when API returns 200`() = runTest {
        val dataSource = createDataSource { request ->
            respond(
                content = {Feature}Fixtures.valid{Entity}Json,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val result = dataSource.get{Entity}()

        assertTrue(result is Either.Success)
    }

    // ==========================================
    // HTTP ERROR CODES - Follow NetworkErrorModel format
    // ==========================================
    // IMPORTANT: All error JSONs use {"detail": "...", "code": ...} format
    // This is processed by EitherCallAdapter to create proper ErrorModel

    @Test
    fun `get{Entity} returns failure on 401 Unauthorized`() = runTest {
        val dataSource = createDataSource { request ->
            respond(
                content = {Feature}Fixtures.error401Json, // {"detail": "...", "code": null}
                status = HttpStatusCode.Unauthorized,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val result = dataSource.get{Entity}()

        // HTTP 401 always maps to ErrorConst.Unauthorized
        assertTrue(result is Either.Failure)
        val error = (result as Either.Failure).error as ErrorModel.MessageCode
        assertEquals("You must login", error.message)
        assertEquals(1001, error.code)
    }

    @Test
    fun `get{Entity} returns failure on 404 Not Found`() = runTest {
        val dataSource = createDataSource { request ->
            respond(
                content = {Feature}Fixtures.error404Json, // {"detail": "Resource not found", "code": 404}
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val result = dataSource.get{Entity}()

        assertTrue(result is Either.Failure)
        val error = (result as Either.Failure).error as ErrorModel.MessageCode
        assertEquals("{Resource} not found", error.message)
        assertEquals(404, error.code)
    }

    @Test
    fun `get{Entity} returns failure on 500 Internal Server Error`() = runTest {
        val dataSource = createDataSource { request ->
            respond(
                content = {Feature}Fixtures.error500Json, // {"detail": "Internal Server Error", "code": 5001}
                status = HttpStatusCode.InternalServerError,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val result = dataSource.get{Entity}()

        assertTrue(result is Either.Failure)
        val error = (result as Either.Failure).error as ErrorModel.MessageCode
        assertEquals("Internal Server Error", error.message)
        assertEquals(5001, error.code)
    }

    @Test
    fun `get{Entity} returns failure on 503 Service Unavailable`() = runTest {
        val dataSource = createDataSource { request ->
            respond(
                content = {Feature}Fixtures.error503Json, // {"detail": null, "code": null}
                status = HttpStatusCode.ServiceUnavailable,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val result = dataSource.get{Entity}()

        // Blank/null detail triggers ServerUnknownError
        assertTrue(result is Either.Failure)
        val error = (result as Either.Failure).error as ErrorModel.MessageCode
        assertEquals("An unknown network error has occurred!", error.message)
        assertEquals(503, error.code)
    }

    // ==========================================
    // NETWORK FAILURES - Copy pattern for each
    // ==========================================

    @Test
    fun `get{Entity} returns failure on connection error`() = runTest {
        val dataSource = createDataSource { _ ->
            throw Exception("Connection refused")
        }

        val result = dataSource.get{Entity}()

        assertTrue(result is Either.Failure)
        assertEquals(ErrorConst.NoNetwork, (result as Either.Failure).error)
    }

    // ==========================================
    // REQUEST VERIFICATION - Copy pattern for each
    // ==========================================

    @Test
    fun `get{Entity} sends correct request path`() = runTest {
        var capturedUrl: String? = null
        val dataSource = createDataSource { request ->
            capturedUrl = request.url.toString()
            respond(
                content = {Feature}Fixtures.valid{Feature}ResponseJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        dataSource.get{Entity}()

        assertNotNull(capturedUrl)
        assertTrue(capturedUrl!!.contains("/api/v1/{endpoint}/"))
    }
}
```

## Test Checklist (ALL MANDATORY)

### SUCCESS CASES
- [ ] 200 OK with valid single entity response
- [ ] 200 OK with valid list response
- [ ] 200 OK with empty list response
- [ ] 200 OK with paginated response (if applicable)

### HTTP ERROR CODES (Copy template, change status code)
- [ ] 400 BadRequest → returns failure
- [ ] 401 Unauthorized → returns failure
- [ ] 403 Forbidden → returns failure
- [ ] 404 NotFound → returns failure
- [ ] 500 InternalServerError → returns failure
- [ ] 503 ServiceUnavailable → returns failure

### PARSING EDGE CASES
- [ ] Malformed JSON → returns ErrorConst.SerializationError
- [ ] Empty response body → returns ErrorConst.SerializationError
- [ ] Missing required fields → returns ErrorConst.SerializationError
- [ ] Null optional fields → parses successfully
- [ ] Extra unknown fields → ignores and parses (ignoreUnknownKeys = true)

### NETWORK FAILURES
- [ ] Connection refused → returns ErrorConst.NoNetwork
- [ ] Timeout → returns ErrorConst.NoNetwork
- [ ] Unknown host → returns ErrorConst.NoNetwork

### REQUEST VERIFICATION (for each API method)
- [ ] Sends correct URL path
- [ ] Sends correct HTTP method (GET/POST/PUT/DELETE)
- [ ] Sends Authorization header with Bearer token
- [ ] Sends correct Content-Type for POST/PUT
- [ ] Serializes request body correctly (for POST/PUT)
- [ ] Sends correct query parameters (for GET with params)

## Verify

```bash
./gradlew :feature:{name}:cleanDesktopTest :feature:{name}:desktopTest --tests "*RemoteDataSourceTest"
```

Fix failures and re-run until green.

## Output

Report: "DataSource tests created at {path}" with test count.
