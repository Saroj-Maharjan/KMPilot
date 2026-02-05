---
name: test-repository
description: Generates Repository tests using Mokkery.
allowed-tools: ["Read", "Write", "Glob", "Bash(./gradlew:*)"]
model: sonnet
color: cyan
---

# Repository Test Agent

Test Repository implementations using Mokkery. **Do NOT re-read source files** - use provided context.

## Output Path
```
feature/{name}/src/commonTest/kotlin/{PKG_PATH}/{name}/data/{Feature}RepositoryImplTest.kt
```

## Template

```kotlin
package {PKG_PREFIX}.{name}.data

import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentiallyReturns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.resetAnswers
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import {CORE_COMMON_PKG}.Either
import {CORE_COMMON_PKG}.ErrorModel
import {PKG_PREFIX}.{name}.data.datasource.{Feature}RemoteDataSource
import {PKG_PREFIX}.{name}.fixtures.{Feature}Fixtures
import kotlin.test.*

class {Feature}RepositoryImplTest {

    private val remoteDataSource = mock<{Feature}RemoteDataSource>()
    private lateinit var repository: {Feature}RepositoryImpl

    @BeforeTest
    fun setup() {
        repository = {Feature}RepositoryImpl(remoteDataSource = remoteDataSource)
    }

    @AfterTest
    fun teardown() {
        resetAnswers(remoteDataSource)
    }

    // === SUCCESS CASES ===

    @Test
    fun `get{Entity}s returns mapped entity list on success`() = runTest {
        val response = {Feature}Fixtures.create{Feature}Response()
        everySuspend { remoteDataSource.get{Entity}s(any(), any(), any()) } returns
            Either.Success(response)

        val result = repository.get{Entity}s()

        assertTrue(result is Either.Success)
        assertEquals(3, result.value.size)
    }

    @Test
    fun `get{Entity}s returns empty list when response is empty`() = runTest {
        everySuspend { remoteDataSource.get{Entity}s(any(), any(), any()) } returns
            Either.Success({Feature}Fixtures.createEmpty{Feature}Response())

        val result = repository.get{Entity}s()

        assertTrue(result is Either.Success)
        assertTrue(result.value.isEmpty())
    }

    // === ERROR PROPAGATION ===

    @Test
    fun `get{Entity}s propagates network failure`() = runTest {
        everySuspend { remoteDataSource.get{Entity}s(any(), any(), any()) } returns
            Either.Failure({Feature}Fixtures.networkError)

        val result = repository.get{Entity}s()

        assertTrue(result is Either.Failure)
        assertEquals({Feature}Fixtures.networkError, (result as Either.Failure).error)
    }

    @Test
    fun `get{Entity}s propagates unauthorized error`() = runTest {
        everySuspend { remoteDataSource.get{Entity}s(any(), any(), any()) } returns
            Either.Failure({Feature}Fixtures.unauthorizedError)

        val result = repository.get{Entity}s()

        assertTrue(result is Either.Failure)
        assertEquals({Feature}Fixtures.unauthorizedError, (result as Either.Failure).error)
    }

    @Test
    fun `get{Entity}s propagates server error`() = runTest {
        everySuspend { remoteDataSource.get{Entity}s(any(), any(), any()) } returns
            Either.Failure({Feature}Fixtures.serverError)

        val result = repository.get{Entity}s()

        assertTrue(result is Either.Failure)
        assertEquals({Feature}Fixtures.serverError, (result as Either.Failure).error)
    }

    @Test
    fun `get{Entity}s propagates not found error`() = runTest {
        everySuspend { remoteDataSource.get{Entity}s(any(), any(), any()) } returns
            Either.Failure({Feature}Fixtures.notFoundError)

        val result = repository.get{Entity}s()

        assertTrue(result is Either.Failure)
        assertEquals({Feature}Fixtures.notFoundError, (result as Either.Failure).error)
    }

    // === PARAMETER VERIFICATION ===

    @Test
    fun `get{Entity}s passes correct parameters to dataSource`() = runTest {
        everySuspend { remoteDataSource.get{Entity}s(any(), any(), any()) } returns
            Either.Success({Feature}Fixtures.create{Feature}Response())

        repository.get{Entity}s(offset = 10, limit = 20, ordering = "-created_time")

        verifySuspend { remoteDataSource.get{Entity}s(10, 20, "-created_time") }
    }
}
```

## Checklist

**Success:** Mapped entity | Mapped list | Empty list | All fields mapped | Single item | Large list (100)

**Error Propagation (use ErrorConst):** NoNetwork | Unauthorized | ServerUnknownError | MessageCode (404, 400) | SerializationError

**Data Transformation:** Timestamps → Long | Nested objects | Null optionals | Filters/sorts

**Parameter Verification:** Correct ID | Offset/limit | Ordering | Filters

**Boundary:** Max page (100) | Min page (1) | Zero offset | Special chars in ID | Long ID

**Caching (if applicable):** Cache hit | Cache miss → network | Update cache | Cache failure

## Verify
```bash
./gradlew :feature:{name}:cleanDesktopTest :feature:{name}:desktopTest --tests "*RepositoryImplTest"
```
