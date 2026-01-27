---
name: test-fixtures
description: Generates test fixtures from a provided model path.
allowed-tools: ["Read", "Write", "Glob", "Bash(./gradlew:*)"]
model: sonnet
color: green
---

# Optimized Fixtures Agent

You create comprehensive test fixtures including domain fixtures AND UI test fixtures. Context is pre-computed by orchestrator.

## Input

Orchestrator provides:
- Feature name and package
- Entity names with field definitions
- DTO names with field definitions
- UiState/UiModel class with field definitions

**Do NOT re-read source files** - use provided context.

## Output Paths

```
feature/{name}/src/commonTest/kotlin/{PKG_PATH}/{name}/fixtures/{Feature}Fixtures.kt
feature/{name}/src/commonTest/kotlin/{PKG_PATH}/{name}/fixtures/{Feature}UiFixtures.kt
```

Use `{PKG_PATH}` (package prefix as path, e.g., `acme` or `com/example`).

## Template: {Feature}Fixtures.kt (Domain/Data Fixtures)

```kotlin
package {PKG_PREFIX}.{name}.fixtures

import {PKG_PREFIX}.{name}.domain.model.*
import {PKG_PREFIX}.{name}.data.model.*
import {CORE_COMMON_PKG}.ErrorModel
import {CORE_COMMON_PKG}.Either

object {Feature}Fixtures {

    // ==========================================
    // DOMAIN MODELS - CORE FACTORIES
    // ==========================================

    fun create{Entity}(
        // Include ALL fields with sensible defaults
        id: String = "test-id",
        name: String = "Test Name",
        description: String? = "Test Description",
        // ... all other fields
    ) = {Entity}(
        id = id,
        name = name,
        description = description,
        // ... all other fields
    )

    fun create{Entity}List(count: Int = 3) =
        (1..count).map { index ->
            create{Entity}(
                id = "id-$index",
                name = "Entity $index"
            )
        }

    fun createEmpty{Entity}List() = emptyList<{Entity}>()
    fun createSingle{Entity}List() = listOf(create{Entity}())
    fun createLarge{Entity}List(count: Int = 100) = create{Entity}List(count)

    // ==========================================
    // DOMAIN MODELS - EDGE CASES (ALL MANDATORY)
    // ==========================================

    fun create{Entity}WithNullOptionals() = create{Entity}(
        description = null,
        // ... all nullable fields set to null
    )

    fun create{Entity}WithEmptyStrings() = create{Entity}(
        name = "",
        description = ""
    )

    fun create{Entity}WithBlankStrings() = create{Entity}(
        name = "   ",
        description = "   "
    )

    fun create{Entity}WithSpecialCharacters() = create{Entity}(
        name = "Test's \"Entity\" with <special> & chars",
        description = "Line1\nLine2\tTabbed"
    )

    fun create{Entity}WithUnicode() = create{Entity}(
        name = "Test 日本語 émoji 🎉 中文",
        description = "Ümlauts and açcénts"
    )

    fun create{Entity}WithLongStrings() = create{Entity}(
        name = "a".repeat(500),
        description = "b".repeat(5000)
    )

    fun create{Entity}WithMaxValues() = create{Entity}(
        // Set numeric fields to MAX_VALUE
    )

    fun create{Entity}WithMinValues() = create{Entity}(
        // Set numeric fields to MIN_VALUE or 0
    )

    // ==========================================
    // DTOs - FACTORIES
    // ==========================================

    fun create{Entity}Dto(
        // Include ALL DTO fields
    ) = {Entity}Dto(
        // ... field assignments
    )

    fun create{Entity}DtoList(count: Int = 3) =
        (1..count).map { index ->
            create{Entity}Dto(id = "id-$index")
        }

    // ==========================================
    // RESPONSE WRAPPERS (if applicable)
    // ==========================================

    fun create{Feature}Response(
        results: List<{Entity}Dto> = create{Entity}DtoList(),
        count: Int = results.size,
        next: String? = null,
        previous: String? = null
    ) = {Feature}Response(
        results = results,
        count = count,
        next = next,
        previous = previous
    )

    fun createEmpty{Feature}Response() = create{Feature}Response(
        results = emptyList(),
        count = 0
    )

    // ==========================================
    // ERROR HELPERS (Use Project ErrorConst)
    // ==========================================

    // Import at top: import {CORE_DATA_PKG}.ErrorConst

    // Network/Connection errors (no HTTP response)
    val networkError = ErrorConst.NoNetwork

    // HTTP 401 - Always maps to ErrorConst.Unauthorized
    val unauthorizedError = ErrorConst.Unauthorized

    // HTTP 404 - Custom message with code
    val notFoundError = ErrorModel.MessageCode(
        message = "{Resource} not found",
        code = 404
    )

    // HTTP 400 - Bad request
    val badRequestError = ErrorModel.MessageCode(
        message = "Invalid request parameters",
        code = 4001
    )

    // HTTP 408 - Timeout (triggers ServerUnknownError)
    val timeoutError = ErrorConst.ServerUnknownError(408)

    // HTTP 500 - Server error (triggers ServerUnknownError)
    val serverError = ErrorConst.ServerUnknownError(500)

    // HTTP 503 - Service unavailable (triggers ServerUnknownError)
    val serviceUnavailableError = ErrorConst.ServerUnknownError(503)

    // Serialization errors
    val serializationError = ErrorConst.SerializationError

    // ==========================================
    // EITHER HELPERS
    // ==========================================

    fun createSuccess{Entity}(entity: {Entity} = create{Entity}()) =
        Either.Success(entity)

    fun createSuccess{Entity}List(entities: List<{Entity}> = create{Entity}List()) =
        Either.Success(entities)

    fun createFailure{Entity}(error: ErrorModel = networkError) =
        Either.Failure(error)

    // Specific Either responses for common error scenarios
    val successResponse: Either<{Entity}> = Either.Success(create{Entity}())
    val networkErrorResponse: Either<{Entity}> = Either.Failure(networkError)
    val unauthorizedErrorResponse: Either<{Entity}> = Either.Failure(unauthorizedError)
    val notFoundErrorResponse: Either<{Entity}> = Either.Failure(notFoundError)
    val serverErrorResponse: Either<{Entity}> = Either.Failure(serverError)

    // ==========================================
    // JSON RESPONSES (ALL MANDATORY for MockEngine)
    // ==========================================

    val valid{Entity}Json = """
        {
            "id": "test-id",
            "name": "Test Name"
            // ... all required fields
        }
    """.trimIndent()

    val valid{Entity}ListJson = """
        [
            {"id": "id-1", "name": "Entity 1"},
            {"id": "id-2", "name": "Entity 2"},
            {"id": "id-3", "name": "Entity 3"}
        ]
    """.trimIndent()

    // Paginated response format
    val valid{Feature}ResponseJson = """
        {
            "count": 3,
            "next": null,
            "previous": null,
            "results": $valid{Entity}ListJson
        }
    """.trimIndent()

    val empty{Entity}ListJson = "[]"
    val empty{Feature}ResponseJson = """{"count": 0, "next": null, "previous": null, "results": []}"""

    // ==========================================
    // ERROR JSON RESPONSES (NetworkErrorModel format)
    // ==========================================
    // CRITICAL: All error responses must use {"detail": "...", "code": ...} format
    // This matches {CORE_DATA_PKG}.model.NetworkErrorModel

    // HTTP 400 - Bad Request
    val error400Json = """{"detail": "Invalid request parameters", "code": 4001}"""

    // HTTP 401 - Unauthorized (ANY response triggers ErrorConst.Unauthorized)
    val error401Json = """{"detail": "Unauthorized", "code": null}"""

    // HTTP 404 - Not Found
    val error404Json = """{"detail": "{Resource} not found", "code": 404}"""

    // HTTP 408 - Timeout (blank detail triggers ServerUnknownError)
    val error408Json = """{"detail": "", "code": null}"""

    // HTTP 500 - Internal Server Error
    val error500Json = """{"detail": "Internal Server Error", "code": 5001}"""

    // HTTP 503 - Service Unavailable (null detail triggers ServerUnknownError)
    val error503Json = """{"detail": null, "code": null}"""

    // Malformed/Edge case JSONs
    val malformedJson = "{ invalid json"
    val incompleteJson = """{"id": "test-id"}"""
    val nullFieldsJson = """{"id": "test-id", "name": "Test", "description": null}"""
    val extraFieldsJson = """{"id": "test-id", "name": "Test", "unknownField": "ignored"}"""
    val emptyStringFieldsJson = """{"id": "", "name": ""}"""
    val specialCharsJson = """{"id": "test-id", "name": "Test's \"Name\" & <more>"}"""
    val unicodeJson = """{"id": "test-id", "name": "日本語 🎉"}"""

    // ==========================================
    // ERROR RESPONSE JSON (ALL MANDATORY)
    // ==========================================

    val error400Json = """{"error": "Bad Request", "message": "Invalid parameters"}"""
    val error401Json = """{"error": "Unauthorized", "message": "Token expired"}"""
    val error403Json = """{"error": "Forbidden", "message": "Access denied"}"""
    val error404Json = """{"error": "Not Found", "message": "Resource not found"}"""
    val error500Json = """{"error": "Internal Server Error", "message": "Something went wrong"}"""
    val error503Json = """{"error": "Service Unavailable", "message": "Try again later"}"""
}
```

## Template: {Feature}UiFixtures.kt (UI State Fixtures)

**CRITICAL: This file provides fixtures for testing {Feature}ScreenRoot composable.**

```kotlin
package {PKG_PREFIX}.{name}.fixtures

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import {CORE_COMMON_PKG}.ErrorModel
import {CORE_COMMON_PKG}.UiState
import {PKG_PREFIX}.{name}.presentation.{Feature}UiModel  // or {Feature}UiState

/**
 * UI Test Fixtures for {Feature}ScreenRoot testing.
 *
 * These fixtures create various UI states to test all rendering paths
 * and user interaction scenarios in the ScreenRoot composable.
 */
object {Feature}UiFixtures {

    // ==========================================
    // BASE UI STATES (4 MANDATORY STATES)
    // ==========================================

    /**
     * Initial state before any data is loaded.
     * Used to test initial UI rendering (forms, placeholders).
     */
    fun createUninitializedState() = {Feature}UiModel(
        {state}State = UiState.Uninitialized
    )

    /**
     * Loading state while fetching data.
     * Used to test loading indicator visibility and disabled interactions.
     */
    fun createLoadingState() = {Feature}UiModel(
        {state}State = UiState.Loading
    )

    /**
     * Success state with data.
     * Used to test content rendering with various data scenarios.
     */
    fun createSuccessState(
        entities: List<{Entity}> = {Feature}Fixtures.create{Entity}List()
    ) = {Feature}UiModel(
        {state}State = UiState.Success(entities.toImmutableList())
    )

    /**
     * Success state with empty list.
     * Used to test empty state placeholder UI.
     */
    fun createEmptyState() = {Feature}UiModel(
        {state}State = UiState.Success(persistentListOf())
    )

    /**
     * Failed state with error message.
     * Used to test error message and retry button rendering.
     */
    fun createErrorState(message: String = "Something went wrong") = {Feature}UiModel(
        {state}State = UiState.Failed(ErrorModel.Message(message))
    )

    // ==========================================
    // LOADING SUBSTATES (if applicable)
    // ==========================================

    // Example for features with multiple loading phases:
    // fun createLoadingWithPhase(phase: LoadingPhase) = {Feature}UiModel(
    //     {state}State = UiState.Loading,
    //     loadingPhase = phase
    // )

    // ==========================================
    // SUCCESS STATE VARIATIONS
    // ==========================================

    /**
     * Success state with a single item.
     */
    fun createSingleItemState() = createSuccessState(
        entities = {Feature}Fixtures.createSingle{Entity}List()
    )

    /**
     * Success state with many items (for scroll testing).
     */
    fun createLargeListState(count: Int = 50) = createSuccessState(
        entities = {Feature}Fixtures.create{Entity}List(count)
    )

    // ==========================================
    // INPUT FIELD STATES (if applicable)
    // ==========================================

    /**
     * State with valid input value.
     */
    fun createWithValidInput(inputValue: String = "valid input") = {Feature}UiModel(
        {state}State = UiState.Uninitialized,
        // inputField = inputValue  // Uncomment and adapt to actual field name
    )

    /**
     * State with empty input (for testing disabled submit button).
     */
    fun createWithEmptyInput() = {Feature}UiModel(
        {state}State = UiState.Uninitialized,
        // inputField = ""  // Uncomment and adapt to actual field name
    )

    /**
     * State with invalid input (for testing validation errors).
     */
    fun createWithInvalidInput(inputValue: String = "invalid") = {Feature}UiModel(
        {state}State = UiState.Uninitialized,
        // inputField = inputValue,
        // inputValidation = InputValidation.Invalid("Error message")
    )

    // ==========================================
    // DIALOG STATES (if applicable)
    // ==========================================

    /**
     * State with confirmation dialog visible.
     */
    fun createShowingConfirmDialog(
        entity: {Entity} = {Feature}Fixtures.create{Entity}()
    ) = {Feature}UiModel(
        {state}State = UiState.Success({Feature}Fixtures.create{Entity}List().toImmutableList()),
        showConfirmDialog = true,
        selectedEntity = entity
    )

    /**
     * State with dialog showing loading indicator (action in progress).
     */
    fun createDialogActionInProgress(
        entity: {Entity} = {Feature}Fixtures.create{Entity}()
    ) = {Feature}UiModel(
        {state}State = UiState.Success({Feature}Fixtures.create{Entity}List().toImmutableList()),
        showConfirmDialog = true,
        selectedEntity = entity,
        isActionInProgress = true
    )

    /**
     * State with success dialog visible.
     */
    fun createShowingSuccessDialog() = {Feature}UiModel(
        {state}State = UiState.Success({Feature}Fixtures.create{Entity}List().toImmutableList()),
        showSuccessDialog = true
    )

    // ==========================================
    // ERROR STATE VARIATIONS
    // ==========================================

    /**
     * Network error state.
     */
    fun createNetworkErrorState() = createErrorState("No internet connection")

    /**
     * Server error state.
     */
    fun createServerErrorState() = createErrorState("Server error. Please try again.")

    /**
     * Not found error state.
     */
    fun createNotFoundErrorState() = createErrorState("Resource not found")

    // ==========================================
    // SELECTION STATES (if applicable)
    // ==========================================

    /**
     * State with a specific item selected.
     */
    fun createWithSelectedItem(
        selectedId: String = "id-1",
        entities: List<{Entity}> = {Feature}Fixtures.create{Entity}List()
    ) = {Feature}UiModel(
        {state}State = UiState.Success(entities.toImmutableList()),
        selectedItemId = selectedId
    )

    // ==========================================
    // FILTER/SEARCH STATES (if applicable)
    // ==========================================

    /**
     * State with search query.
     */
    fun createWithSearchQuery(
        query: String = "search term",
        results: List<{Entity}> = {Feature}Fixtures.create{Entity}List()
    ) = {Feature}UiModel(
        {state}State = UiState.Success(results.toImmutableList()),
        searchQuery = query
    )

    /**
     * State with selected filter/category.
     */
    fun createWithSelectedFilter(
        filter: {Filter}? = {Feature}Fixtures.createFilter()
    ) = {Feature}UiModel(
        {state}State = UiState.Success({Feature}Fixtures.create{Entity}List().toImmutableList()),
        selectedFilter = filter
    )
}
```

## Generation Checklist (ALL MANDATORY)

### {Feature}Fixtures.kt - Core Factories
- [ ] `create{Entity}()` with ALL fields as parameters
- [ ] `create{Entity}List(count)` with unique IDs
- [ ] `createEmpty{Entity}List()`
- [ ] `createSingle{Entity}List()`
- [ ] `createLarge{Entity}List(count = 100)`

### {Feature}Fixtures.kt - Edge Case Factories (MANDATORY)
- [ ] `create{Entity}WithNullOptionals()` - all nullable fields = null
- [ ] `create{Entity}WithEmptyStrings()` - string fields = ""
- [ ] `create{Entity}WithBlankStrings()` - string fields = "   "
- [ ] `create{Entity}WithSpecialCharacters()` - quotes, ampersands, angle brackets, newlines
- [ ] `create{Entity}WithUnicode()` - Japanese, Chinese, emojis, accents
- [ ] `create{Entity}WithLongStrings()` - 500+ char strings
- [ ] `create{Entity}WithMaxValues()` - Long.MAX_VALUE, Int.MAX_VALUE
- [ ] `create{Entity}WithMinValues()` - 0, Long.MIN_VALUE

### {Feature}Fixtures.kt - DTO Factories
- [ ] `create{Entity}Dto()` with ALL DTO fields
- [ ] `create{Entity}DtoList(count)`
- [ ] Response wrapper factory if paginated

### {Feature}Fixtures.kt - Error Helpers (ALL 8 TYPES)
- [ ] `createNetworkError()`
- [ ] `createServerError(code)`
- [ ] `createUnauthorizedError()`
- [ ] `createNotFoundError()`
- [ ] `createBadRequestError()`
- [ ] `createForbiddenError()`
- [ ] `createParsingError()`
- [ ] `createServiceUnavailableError()`

### {Feature}Fixtures.kt - Either Helpers
- [ ] `createSuccess{Entity}()`
- [ ] `createSuccess{Entity}List()`
- [ ] `createFailure{Entity}(error)`

### {Feature}Fixtures.kt - JSON Responses (ALL for MockEngine tests)
- [ ] `valid{Entity}Json` - single valid entity
- [ ] `valid{Entity}ListJson` - array of entities
- [ ] `valid{Feature}ResponseJson` - paginated wrapper
- [ ] `empty{Entity}ListJson` - []
- [ ] `empty{Feature}ResponseJson` - {count: 0, results: []}
- [ ] `malformedJson` - broken JSON
- [ ] `incompleteJson` - missing required fields
- [ ] `nullFieldsJson` - optional fields null
- [ ] `extraFieldsJson` - unknown fields (should be ignored)
- [ ] `emptyStringFieldsJson` - empty string values
- [ ] `specialCharsJson` - escaped characters
- [ ] `unicodeJson` - non-ASCII characters

### {Feature}Fixtures.kt - Error Response JSON (ALL 6)
- [ ] `error400Json`, `error401Json`, `error403Json`
- [ ] `error404Json`, `error500Json`, `error503Json`

### {Feature}UiFixtures.kt - Base States (ALL 4 MANDATORY)
- [ ] `createUninitializedState()` - initial state
- [ ] `createLoadingState()` - loading indicator visible
- [ ] `createSuccessState(entities)` - content visible
- [ ] `createEmptyState()` - empty placeholder visible
- [ ] `createErrorState(message)` - error + retry visible

### {Feature}UiFixtures.kt - Success Variations
- [ ] `createSingleItemState()` - single item in list
- [ ] `createLargeListState(count)` - many items for scroll testing

### {Feature}UiFixtures.kt - Dialog States (if feature has dialogs)
- [ ] `createShowingConfirmDialog(entity)` - dialog visible with entity data
- [ ] `createDialogActionInProgress(entity)` - dialog with loading
- [ ] `createShowingSuccessDialog()` - success dialog visible

### {Feature}UiFixtures.kt - Input States (if feature has input fields)
- [ ] `createWithValidInput(value)` - valid input, submit enabled
- [ ] `createWithEmptyInput()` - empty input, submit disabled
- [ ] `createWithInvalidInput(value)` - invalid input with error

### {Feature}UiFixtures.kt - Error Variations
- [ ] `createNetworkErrorState()` - network error message
- [ ] `createServerErrorState()` - server error message
- [ ] `createNotFoundErrorState()` - not found error message

## Verify

```bash
./gradlew :feature:{name}:compileTestKotlinDesktop
```

Fix any compilation errors before reporting success.

## Output

Report: "Fixtures created at {path}" with:
- Count of domain fixture factory functions
- Count of UI state fixture factory functions
- Files created:
  - `{Feature}Fixtures.kt`
  - `{Feature}UiFixtures.kt`
