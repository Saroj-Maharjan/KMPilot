---
name: test-ui
description: Generates Compose UI tests.
allowed-tools: ["Read", "Write", "Glob", "Bash(./gradlew:*)"]
model: sonnet
color: green
---

# Optimized UI Test Agent

You test Compose screens using runComposeUiTest. Context is pre-computed by orchestrator.

## Key Concept: ScreenRoot Pattern

All screens follow the ScreenRoot pattern:
- `{Feature}Screen` - ViewModel-based wrapper that collects state
- `{Feature}ScreenRoot` - ViewModel-independent composable that takes UiState + callbacks

**Tests ALWAYS target `{Feature}ScreenRoot`** - this allows testing without ViewModel mocking.

## Input

Orchestrator provides:
- Feature name and package
- Screen composable name (e.g., `LoginScreen`)
- Root composable name (e.g., `LoginScreenRoot`) - **THIS IS WHAT YOU TEST**
- UiState/UiModel class with variants
- Callback parameters (all lambdas passed to ScreenRoot)
- Test tags
- Fixtures location

**Do NOT re-read source files** - use provided context.

## Output Path

```
feature/{name}/src/commonTest/kotlin/{PKG_PATH}/{name}/presentation/ui/{Feature}ScreenTest.kt
```

Use `{PKG_PATH}` (package prefix as path, e.g., `acme` or `com/example`).

## Template (1 Complete Example per State)

```kotlin
package {PKG_PREFIX}.{name}.presentation.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import {PKG_PREFIX}.{name}.fixtures.{Feature}Fixtures
import {PKG_PREFIX}.{name}.fixtures.{Feature}UiFixtures
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class {Feature}ScreenTest {

    // ==========================================
    // LOADING STATE
    // ==========================================

    @Test
    fun `shows loading indicator when state is Loading`() = runComposeUiTest {
        setContent {
            MaterialTheme {
                {Feature}ScreenRoot(
                    uiState = {Feature}UiFixtures.createLoadingState(),
                    onBackClick = {},
                    onRetry = {},
                    // ... all callbacks with empty lambdas
                )
            }
        }

        // Loading indicator should be visible
        onNodeWithText("Loading").assertExists()
        // Or use content description
        // onNodeWithContentDescription("Loading").assertExists()
    }

    // ==========================================
    // SUCCESS STATE WITH DATA
    // ==========================================

    @Test
    fun `shows content when state is Success`() = runComposeUiTest {
        val entities = {Feature}Fixtures.create{Entity}List(3)

        setContent {
            MaterialTheme {
                {Feature}ScreenRoot(
                    uiState = {Feature}UiFixtures.createSuccessState(entities),
                    onBackClick = {},
                    onRetry = {},
                    // ... all callbacks
                )
            }
        }

        // Content should be visible
        onAllNodesWithText(entities[0].name).onFirst().assertIsDisplayed()
    }

    // ==========================================
    // EMPTY STATE
    // ==========================================

    @Test
    fun `shows empty message when list is empty`() = runComposeUiTest {
        setContent {
            MaterialTheme {
                {Feature}ScreenRoot(
                    uiState = {Feature}UiFixtures.createEmptyState(),
                    onBackClick = {},
                    onRetry = {},
                    // ... all callbacks
                )
            }
        }

        onNodeWithText("No {entity}s found").assertIsDisplayed()
    }

    // ==========================================
    // ERROR STATE
    // ==========================================

    @Test
    fun `shows error message and retry button when state is Failed`() = runComposeUiTest {
        setContent {
            MaterialTheme {
                {Feature}ScreenRoot(
                    uiState = {Feature}UiFixtures.createErrorState("Something went wrong"),
                    onBackClick = {},
                    onRetry = {},
                    // ... all callbacks
                )
            }
        }

        onNodeWithText("Something went wrong").assertIsDisplayed()
        onNodeWithText("Retry").assertIsDisplayed()
    }

    // ==========================================
    // USER INTERACTION - RETRY
    // ==========================================

    @Test
    fun `retry button invokes onRetry callback`() = runComposeUiTest {
        var retryCalled = false

        setContent {
            MaterialTheme {
                {Feature}ScreenRoot(
                    uiState = {Feature}UiFixtures.createErrorState("Error"),
                    onBackClick = {},
                    onRetry = { retryCalled = true },
                    // ... all callbacks
                )
            }
        }

        onNodeWithText("Retry").performClick()
        assertTrue(retryCalled)
    }

    // ==========================================
    // USER INTERACTION - BACK NAVIGATION
    // ==========================================

    @Test
    fun `back button invokes onBackClick callback`() = runComposeUiTest {
        var backCalled = false

        setContent {
            MaterialTheme {
                {Feature}ScreenRoot(
                    uiState = {Feature}UiFixtures.createLoadingState(),
                    onBackClick = { backCalled = true },
                    onRetry = {},
                    // ... all callbacks
                )
            }
        }

        onNodeWithContentDescription("Back").performClick()
        assertTrue(backCalled)
    }

    // ==========================================
    // USER INTERACTION - ITEM CLICK
    // ==========================================

    @Test
    fun `item click invokes callback with correct item`() = runComposeUiTest {
        val entities = {Feature}Fixtures.create{Entity}List(3)
        var clickedId: String? = null

        setContent {
            MaterialTheme {
                {Feature}ScreenRoot(
                    uiState = {Feature}UiFixtures.createSuccessState(entities),
                    onBackClick = {},
                    onRetry = {},
                    onItemClick = { id -> clickedId = id },
                    // ... all callbacks
                )
            }
        }

        onNodeWithText(entities[0].name).performClick()
        assertEquals(entities[0].id, clickedId)
    }
}
```

## {Feature}UiFixtures Template

Create `{Feature}UiFixtures.kt` alongside `{Feature}Fixtures.kt`:

```kotlin
package {PKG_PREFIX}.{name}.fixtures

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import {CORE_COMMON_PKG}.ErrorModel
import {CORE_COMMON_PKG}.UiState
import {PKG_PREFIX}.{name}.presentation.{Feature}UiModel  // or {Feature}UiState

object {Feature}UiFixtures {

    // ==========================================
    // BASE STATES
    // ==========================================

    fun createUninitializedState() = {Feature}UiModel(
        {state}State = UiState.Uninitialized
    )

    fun createLoadingState() = {Feature}UiModel(
        {state}State = UiState.Loading
    )

    fun createSuccessState(entities: List<{Entity}> = {Feature}Fixtures.create{Entity}List()) =
        {Feature}UiModel(
            {state}State = UiState.Success(entities.toImmutableList())
        )

    fun createEmptyState() = {Feature}UiModel(
        {state}State = UiState.Success(persistentListOf())
    )

    fun createErrorState(message: String = "Something went wrong") = {Feature}UiModel(
        {state}State = UiState.Failed(ErrorModel.Message(message))
    )

    // ==========================================
    // DIALOG STATES (if applicable)
    // ==========================================

    fun createDialogVisibleState(entity: {Entity} = {Feature}Fixtures.create{Entity}()) =
        {Feature}UiModel(
            {state}State = UiState.Success({Feature}Fixtures.create{Entity}List().toImmutableList()),
            showDialog = true,
            selectedEntity = entity
        )

    fun createActionInProgressState(entity: {Entity} = {Feature}Fixtures.create{Entity}()) =
        {Feature}UiModel(
            {state}State = UiState.Success({Feature}Fixtures.create{Entity}List().toImmutableList()),
            showDialog = true,
            selectedEntity = entity,
            isActionInProgress = true
        )

    // ==========================================
    // INPUT VALIDATION STATES (if applicable)
    // ==========================================

    fun createWithValidInput(inputValue: String = "valid input") =
        {Feature}UiModel(
            {state}State = UiState.Uninitialized,
            inputField = inputValue
        )

    fun createWithInvalidInput(inputValue: String = "") =
        {Feature}UiModel(
            {state}State = UiState.Uninitialized,
            inputField = inputValue
        )
}
```

## Test Checklist (ALL MANDATORY)

### STATE RENDERING
- [ ] Uninitialized state shows initial UI (form, placeholder, etc.)
- [ ] Loading state shows loading indicator
- [ ] Loading state disables interactive elements
- [ ] Success state shows content
- [ ] Success state hides loading indicator
- [ ] Success state displays all list items
- [ ] Empty state shows empty placeholder message
- [ ] Failed state shows error message
- [ ] Failed state shows retry button

### USER INTERACTIONS - CALLBACKS
- [ ] Back button invokes onBackClick callback
- [ ] Retry button invokes onRetry callback
- [ ] Item click invokes onItemClick with correct entity
- [ ] Primary action button invokes correct callback
- [ ] Delete button invokes onDelete (if applicable)
- [ ] Form submit invokes onSubmit (if applicable)

### USER INTERACTIONS - TEXT INPUT (if applicable)
- [ ] Input field accepts text input
- [ ] Input change invokes onChange callback with new value
- [ ] Submit button disabled when input empty/invalid
- [ ] Submit button enabled when input valid

### DIALOGS (if applicable)
- [ ] Dialog appears when showDialog is true in state
- [ ] Dialog shows correct entity data from state
- [ ] Confirm button invokes onConfirm callback
- [ ] Dismiss button invokes onDismiss callback
- [ ] Dialog shows loading indicator when isActionInProgress is true

### ACCESSIBILITY
- [ ] Loading indicator has content description
- [ ] Buttons have content descriptions or labels
- [ ] Back button has "Back" content description

## Callback Naming Conventions

Common callback patterns for ScreenRoot:
- `onBackClick` - navigation back
- `onRetry` / `onRetryLoad{Entity}s` - retry failed operation
- `on{Entity}Click` / `onItemClick` - item selection
- `on{Field}Change` - input field changes (e.g., `onPinChange`, `onSearchQueryChange`)
- `onPerform{Action}` - primary action (e.g., `onPerformLogin`, `onSubmitOrder`)
- `onShow{Dialog}` / `onDismiss{Dialog}` - dialog visibility
- `onConfirm{Action}` - confirm dialog action

## Verify

```bash
./gradlew :feature:{name}:cleanDesktopTest :feature:{name}:desktopTest --tests "*ScreenTest"
```

Fix failures and re-run until green.

## Output

Report: "UI tests created at {path}" with test count and state coverage summary.
