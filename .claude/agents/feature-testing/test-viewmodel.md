---
name: test-viewmodel
description: Generates ViewModel tests using Turbine for Flow testing.
allowed-tools: ["Read", "Write", "Glob", "Bash(./gradlew:*)"]
model: sonnet
color: green
---

# Optimized ViewModel Test Agent

You test ViewModels using Turbine for StateFlow assertions. Context is pre-computed by orchestrator.

## Input

Orchestrator provides:
- Feature name and package
- ViewModel class name
- Dependencies to mock (Repository)
- UiState class with state variants
- Public action methods
- Fixtures location

**Do NOT re-read source files** - use provided context.

## Output Path

```
feature/{name}/src/commonTest/kotlin/{PKG_PATH}/{name}/presentation/{Feature}ViewModelTest.kt
```

Use `{PKG_PATH}` (package prefix as path, e.g., `acme` or `com/example`).

## Template (1 Complete Example per Pattern)

```kotlin
package {PKG_PREFIX}.{name}.presentation

import app.cash.turbine.test
import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentiallyReturns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.resetAnswers
import dev.mokkery.verifySuspend
import dev.mokkery.matcher.any
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import {CORE_COMMON_PKG}.Either
import {CORE_COMMON_PKG}.UiState
import {PKG_PREFIX}.{name}.domain.repository.{Feature}Repository
import {PKG_PREFIX}.{name}.fixtures.{Feature}Fixtures
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class {Feature}ViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository = mock<{Feature}Repository>()
    private lateinit var viewModel: {Feature}ViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
        resetAnswers(repository)
    }

    private fun createViewModel() {
        viewModel = {Feature}ViewModel(repository)
    }

    // ==========================================
    // PATTERN 1: State Transition (Loading → Success)
    // ==========================================

    @Test
    fun `load{Entity}s emits Loading then Success on success`() = runTest {
        everySuspend { repository.get{Entity}s(any<Int>(), any<Int>(), any<String>()) } returns
            {Feature}Fixtures.createSuccess{Entity}List()

        createViewModel() // Init runs automatically - no advanceUntilIdle here!

        viewModel.uiModelState.test {
            // Initial state is Loading (init triggers load)
            var current = awaitItem()
            if (current.{state}State is UiState.Uninitialized) {
                current = awaitItem()
            }
            assertTrue(current.{state}State is UiState.Loading)

            advanceUntilIdle() // Let init coroutine complete

            current = awaitItem()
            assertTrue(current.{state}State is UiState.Success)
            assertEquals(3, (current.{state}State as UiState.Success).value.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==========================================
    // PATTERN 2: State Transition (Loading → Failed)
    // ==========================================

    @Test
    fun `load{Entity}s emits Loading then Failed on error`() = runTest {
        everySuspend { repository.get{Entity}s(any<Int>(), any<Int>(), any<String>()) } returns
            {Feature}Fixtures.createFailure{Entity}()

        createViewModel()

        viewModel.uiModelState.test {
            var current = awaitItem()
            while (current.{state}State !is UiState.Failed) {
                advanceUntilIdle()
                current = awaitItem()
            }

            assertTrue(current.{state}State is UiState.Failed)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==========================================
    // PATTERN 3: Retry Flow
    // ==========================================

    @Test
    fun `retry after failure transitions to Loading then Success`() = runTest {
        everySuspend { repository.get{Entity}s(any<Int>(), any<Int>(), any<String>()) } sequentiallyReturns listOf(
            {Feature}Fixtures.createFailure{Entity}(),
            {Feature}Fixtures.createSuccess{Entity}List()
        )

        createViewModel() // Init runs automatically - no advanceUntilIdle here!

        viewModel.uiModelState.test {
            // Wait for initial failure
            var current = awaitItem()
            while (current.{state}State !is UiState.Failed) {
                advanceUntilIdle()
                current = awaitItem()
            }

            // Retry - IMPORTANT: advanceUntilIdle immediately after the call
            viewModel.retry()
            advanceUntilIdle() // Immediately after method call!

            // Should transition to success
            current = expectMostRecentItem()
            assertTrue(current.{state}State is UiState.Success)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==========================================
    // PATTERN 4: User Action Verification
    // ==========================================

    @Test
    fun `on{Action} calls repository with correct params`() = runTest {
        everySuspend { repository.perform{Action}(any()) } returns Either.Success(Unit)

        createViewModel() // Init runs automatically - no advanceUntilIdle here!

        viewModel.uiModelState.test {
            skipItems(1) // Skip initial state

            // IMPORTANT: advanceUntilIdle immediately after method call
            viewModel.on{Action}("test-param")
            advanceUntilIdle()

            verifySuspend { repository.perform{Action}("test-param") }

            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

## Test Checklist (ALL MANDATORY)

### INITIAL STATE
- [ ] Initial state is Uninitialized or Loading (depends on init behavior)
- [ ] Init triggers automatic data load (if applicable)

### STATE TRANSITIONS - HAPPY PATH
- [ ] Uninitialized → Loading → Success with data
- [ ] Success state contains correct mapped data
- [ ] Success state has correct item count
- [ ] All entity fields are correctly accessible

### STATE TRANSITIONS - ERROR PATH
- [ ] Uninitialized → Loading → Failed on NetworkFailure
- [ ] Uninitialized → Loading → Failed on ServerFailure
- [ ] Uninitialized → Loading → Failed on UnauthorizedFailure
- [ ] Failed state contains error message

### RETRY FLOW
- [ ] Failed → Loading → Success on retry
- [ ] Failed → Loading → Failed again (persistent error)
- [ ] Retry ignored when not in Failed state

### RAPID ACTIONS / DEBOUNCING
- [ ] Second load call ignored while loading
- [ ] Rapid button clicks only trigger action once
- [ ] Verify repository called exactly once

### EMPTY STATE
- [ ] Empty list shows empty state flag
- [ ] Empty state message is appropriate

### INPUT VALIDATION (if form inputs exist)
- [ ] Empty input shows validation error
- [ ] Blank input (whitespace only) shows error
- [ ] Max length exceeded shows error
- [ ] Valid input proceeds to repository call

### USER ACTIONS
- [ ] Action calls repository with correct parameters
- [ ] Action updates state on success
- [ ] Action shows error state on failure

### REFRESH / PULL-TO-REFRESH (if applicable)
- [ ] Refresh shows refreshing indicator
- [ ] Refresh updates data on success
- [ ] Refresh failure keeps existing data with error indicator

### DIALOG STATE (if dialogs exist)
- [ ] Show dialog sets dialog state to true
- [ ] Show dialog stores relevant entity
- [ ] Hide dialog resets dialog state
- [ ] Dialog action (confirm) calls repository

### CANCELLATION
- [ ] Scope cancellation doesn't corrupt state

## Turbine Best Practices

```kotlin
// CRITICAL RULE: advanceUntilIdle() must be called IMMEDIATELY AFTER calling a method that contains coroutines
// NEVER call advanceUntilIdle() immediately after ViewModel creation (init runs automatically)

// ❌ WRONG - advanceUntilIdle after ViewModel creation
viewModel = MyViewModel(repository)
advanceUntilIdle() // Don't do this!

// ❌ WRONG - advanceUntilIdle not immediately after method call
viewModel.loadData()
val loading = awaitItem()
advanceUntilIdle() // Too late!

// ✅ CORRECT - Init block testing
viewModel = MyViewModel(repository)

viewModel.uiModel.test {
    val initial = awaitItem() // Init runs automatically
    advanceUntilIdle() // Let init coroutine complete
    val success = awaitItem()
}

// ✅ CORRECT - Explicit method call
viewModel.loadData()
advanceUntilIdle() // Immediately after the call!
val result = expectMostRecentItem()

// ✅ CORRECT - Multiple method calls
viewModel.retry()
advanceUntilIdle() // After each call

viewModel.loadData()
advanceUntilIdle() // After each call

// Always clean up
cancelAndIgnoreRemainingEvents()

// Skip known states
skipItems(1)

// Wait for specific state
while (current.state !is UiState.Success) {
    current = awaitItem()
}

// Check no unexpected emissions
expectNoEvents()
```

## Verify

```bash
./gradlew :feature:{name}:cleanDesktopTest :feature:{name}:desktopTest --tests "*ViewModelTest"
```

Fix failures and re-run until green.

## Output

Report: "ViewModel tests created at {path}" with test count and state coverage summary.
