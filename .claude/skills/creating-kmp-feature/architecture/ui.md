# UI Layer Architecture Principles

Principles for implementing the UI/presentation layer in KMP features using Compose Multiplatform and MVVM.

**Note**: Uses `{PKG_PREFIX}` placeholder for package prefix (resolved via Context Discovery).

## UI Layer Structure

```
{PKG_PREFIX}.{featurename}/presentation/
├── {Feature}ViewModel.kt      # State management and business logic
├── {Feature}UiModel.kt        # Single state container: plain UI fields + UiState<DTO> slots
├── ui/
│   ├── {Feature}Screen.kt     # Main screen composable
│   ├── {Secondary}Screen.kt   # Additional screens
│   └── components/            # Feature-specific reusable components
└── navigation/
    └── {Feature}Navigation.kt # @Serializable routes + NavGraphBuilder extension
```

## Critical Rules (UI Layer)

1. **setState Extension**: Always use `_uiModel.setState { copy(...) }`, NEVER `_uiModel.value =` or direct assignment
2. **4 UI States**: Handle all 4 states (Uninitialized/Loading/Success/Failed) for every async data field
3. **X-components Only**: Use X-components from `:core:designsystem` (XScreen, XButton, XText), NO Material3
4. **ImmutableList**: Use `.toImmutableList()` for collections in state
5. **Callback Parameters**: Screens take callbacks (e.g., `onBackClick: () -> Unit`), not `navController`
6. **ScreenRoot Pattern**: ALWAYS create `{Feature}ScreenRoot` for ViewModel-independent, testable UI
7. **Single UiModel (Rule 11)**: One `*UiModel.kt` per feature — plain fields + `UiState<DTO>` slots (DTOs from `data/model/`, NOT presentation-layer mirror types). No separate `*UiState.kt`.

## Layer Responsibilities

### 1. ViewModel (presentation/)

**Purpose**: Manage UI state, coordinate business logic, handle user interactions

**Pattern**:
- Extend `ViewModel` from AndroidX Lifecycle
- Use `MutableStateFlow<{Feature}UiModel>` for state, expose as `StateFlow` via `.asStateFlow()`
- Public flow name: `uiModel` (e.g., `val uiModel: StateFlow<{Feature}UiModel>`)
- Inject Repository interface(s) via constructor
- Use `viewModelScope.launch` for coroutines
- Load initial data in `init` block (if needed)
- Provide functions for user actions (e.g., `submitForm()`, `retryLoad()`)

**Key Rules**:
- **Always** use `_uiModel.setState { copy(field = newValue) }` for state updates
- Handle `Either<DTO>` results from repository with pattern matching — store `result.data` directly in `UiState.Success`, no mapping
- Validation logic goes here (e.g., email validation, form validation)
- No direct UI references (no Context, no Composables)
- For UI-derived display values (formatted price, "3 days ago"), add a **sibling field** on `*UiModel` and populate it when the relevant `UiState<DTO>` becomes Success. Do NOT define a mirror UI-layer copy of the DTO.

**State Update Pattern**:
```kotlin
// CORRECT
_uiModel.setState { copy(isLoading = true) }

// WRONG - NEVER DO THIS
_uiModel.value = _uiModel.value.copy(isLoading = true)
```

**Error Handling Pattern** (Rule 11 — store DTO directly):
```kotlin
when (val result = repository.getData()) {
    is Either.Success -> {
        _uiModel.setState { copy(dataState = UiState.Success(result.data)) }
    }
    is Either.Failure -> {
        _uiModel.setState { copy(dataState = UiState.Failed(result.error)) }
    }
}
// result.data is the DTO from data/model/. No .toUiModel() — that's a Rule 11 violation.
```

### 2. UiModel (presentation/) — Single State Container

**Purpose**: The one and only state container for a feature. Holds plain UI fields + one `UiState<T>` slot per independent async operation, where T is the **data-layer DTO** (from `data/model/`).

**Pattern**:
- Annotated with `@Stable` (from Compose runtime)
- Data class with default values for all fields
- Naming: `{Feature}UiModel` (e.g., `ProductDetailUiModel`, `LoginUiModel`)
- One file per feature: `{Feature}UiModel.kt`. **No `*UiState.kt` file.**

**Shape**:
```kotlin
data class FeatureUiModel(
    // Plain UI fields — form inputs, selected tab, search query, etc.
    val searchQuery: String = "",
    val selectedTab: Int = 0,

    // UiState<DTO> slots — one per independent async operation.
    // DTOs come from data/model/. Use UiState<Unit> for void ops.
    val dataState: UiState<FeatureResponse> = UiState.Uninitialized,
    val submitState: UiState<Unit> = UiState.Uninitialized,

    // Optional UI-derived display values — sibling fields, populated by ViewModel
    // when the related UiState<DTO> becomes Success. NEVER a mirror DTO type.
    val priceLabel: String = "",
)
```

**Key Points** (Rule 11):
- One `*UiModel` per feature, even if the feature has multiple screens
- Async fields use `UiState<DTO>` where DTO is the **data-layer model** — `UiState<LoginResponse>`, `UiState<Product>`, `UiState<Unit>` for void ops
- **Never** define a presentation-layer mirror of a DTO (no `LoginResult` shadowing `LoginResponse`). The data layer's DTO is what the UI reads.
- Plain sync fields use regular types: `val searchQuery: String = ""`, `val selectedTab: Int = 0`
- Collections use `ImmutableList` (e.g., `val items: ImmutableList<Item> = persistentListOf()`)
- UI-derived display values (formatted strings, computed flags) are **sibling fields** on `*UiModel`, populated by the ViewModel — not parallel data classes

### 4. Screen Composables (presentation/ui/)

**Purpose**: Main entry point for feature UI

**`{Feature}Screen.kt` allowlist** — these are the **only** top-level `@Composable fun` declarations permitted in `Screen.kt`:

| # | Name | Visibility | Required? |
|---|------|------------|-----------|
| 1 | `{Feature}Screen` | public | Always |
| 2 | `{Feature}ScreenRoot` | public | Always |
| 3 | `LoadingContent` | private | Optional — only if the design specifies a dedicated loading screen |
| 4 | `FailedContent` | private | Optional — only if the design specifies a dedicated failure screen |
| 5 | `EmptyContent` | private | Optional — only if the design specifies a dedicated empty/uninitialized screen |

Anything else — including `{Feature}Content` and every sub-component — lives under `presentation/ui/components/`, one file per component. See Section 5.

**CRITICAL: Always implement TWO composables:**

1. **`{Feature}Screen`** - ViewModel wrapper
   - Takes ViewModel as parameter (with `koinViewModel()` default in Navigation, not in Screen)
   - Collects state with `.collectAsStateWithLifecycle()`
   - Delegates to ScreenRoot with the UiModel snapshot and callbacks

2. **`{Feature}ScreenRoot`** - ViewModel-independent (TESTABLE)
   - Takes `uiModel: {Feature}UiModel` as parameter (Rule 11)
   - Takes all callbacks as lambda parameters
   - Contains all actual UI implementation
   - **This is what UI tests target**

**Key Rules**:
- Only X-components allowed (XScreen, XTopAppBar, XButton, XText, etc.) - NO Material3 directly. Feature screens use `XScreen`, never `XScaffold` (Rule 13)
- Handle all 4 UiState cases for each async data slot in `*UiModel`
- Use `Modifier` parameters for customization
- ScreenRoot has NO ViewModel dependency

**Screen Pattern** (ViewModel wrapper):
```kotlin
@Composable
fun FeatureScreen(
    onBackClick: () -> Unit,
    viewModel: FeatureViewModel,
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()

    FeatureScreenRoot(
        uiModel = uiModel,
        onBackClick = onBackClick,
        onRetry = viewModel::loadData,
        onAction = viewModel::performAction,
    )
}
```

**ScreenRoot Pattern** (testable, ViewModel-independent):
```kotlin
@Composable
fun FeatureScreenRoot(
    uiModel: FeatureUiModel,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Rule 13 — feature screens use XScreen, NEVER a Scaffold/XScaffold.
    // The single Scaffold lives in App.kt and owns all window insets; XScreen adds none.
    XScreen(
        topBar = {
            XTopAppBar(
                // Rule 12 — no hardcoded strings; resolve from the module's generated Res
                title = { XText(stringResource(Res.string.feature_title)) },
                navigationIcon = {
                    XIconButton(onClick = onBackClick) {
                        XIcon(/* ... */, contentDescription = stringResource(Res.string.cd_back))
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize(),
    ) {
        // Content fills XScreen's weight(1f) box — no paddingValues to thread.
        // Route on the relevant async slot inside the UiModel.
        // state.value is the DTO from data/model/ (Rule 11).
        when (val state = uiModel.dataState) {
            UiState.Uninitialized -> EmptyContent()                  // optional shell, design-driven
            UiState.Loading      -> LoadingContent()                 // optional shell, design-driven
            is UiState.Success   -> FeatureContent(data = state.value) // always from components/
            is UiState.Failed    -> FailedContent(state.error, onRetry) // optional shell, design-driven
        }
    }
}
```

### 5. Component Composables (presentation/ui/components/)

**Purpose**: Every UI piece that isn't a state-shell composable lives here. **One file per component, always.**

**Rule (no judgment calls)**:

- `{Feature}Content.kt` — the success-state composable (Shape A) or the always-mounted form composable (Shape B). It is **always** its own file.
- Every sub-component reachable from `{Feature}Content` lives in its own file under `components/`, no matter how small.
- A component's private helpers and private sub-composables stay in the **same file** as that component — they are not promoted to new files.
- The only composables that may live in `{Feature}Screen.kt` are the 5 allowlist entries (see "Screen Composables" above): `{Feature}Screen`, `{Feature}ScreenRoot`, and the three optional state shells `LoadingContent` / `FailedContent` / `EmptyContent` (present only if the design requires them).

**Pattern**:
- Simple composable functions
- Naming: Descriptive (e.g., `ProductHeader`, `PriceSelector`, `OrderConfirmDialog`)
- Accept data and callbacks as parameters
- Use X-components from design system

**Key Points**:
- Keep components small and focused (single responsibility)
- Use preview annotations for development (see "Previews" below)
- No ViewModels in components (data/callbacks passed down)
- Prefer stateless composables (state hoisted to parent)

### 5a. Utility Functions (non-`@Composable`)

Pure helpers (formatters, validators, mappers) are **not composables** and **do not go under `components/`**. Place them in `presentation/ui/{Feature}Utils.kt` at the same level as `Screen.kt`. `components/` contains only `@Composable` declarations.

```
presentation/ui/
├── {Feature}Screen.kt
├── {Feature}Utils.kt   ← fun formatBalance(...), fun validateEmail(...), etc.
└── components/         ← @Composable units only
```

### 5b. Previews (`@Preview` composables)

**Import (CMP 1.11.0+)**: `androidx.compose.ui.tooling.preview.Preview`. The older `org.jetbrains.compose.ui.tooling.preview.Preview` is deprecated.

**Placement**: a `@Preview` composable lives in the **same file** as the composable it previews, marked `private`. It is exempt from the `Screen.kt` allowlist and the "one file per `@Composable`" rule — its purpose is to render the sibling composable, so co-location is required.

```kotlin
// components/BalanceCard.kt
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BalanceCard(balance: String, currency: String) { /* ... */ }

@Preview
@Composable
private fun BalanceCardPreview() {
    XTheme { BalanceCard(balance = "1,250.00", currency = "USD") }
}
```

**Naming**: `{ComponentName}Preview`. For multi-variant previews use suffixes: `{ComponentName}PreviewDark`, `{ComponentName}PreviewLoading`, `{ComponentName}PreviewLongString`.

**Wrap in `XTheme`**: previews don't get the app-level theme automatically. Wrap the previewed composable in `XTheme { ... }` so colors, typography, and shapes resolve correctly.

**`@PreviewParameter`** (CMP 1.11.0+): use a `PreviewParameterProvider<T>` to render multiple variants from one declaration.

```kotlin
private class BalancePreviewParams : PreviewParameterProvider<String> {
    override val values = sequenceOf("0.00", "1,250.00", "1,234,567.89")
}

@Preview
@Composable
private fun BalanceCardPreviews(
    @PreviewParameter(BalancePreviewParams::class) balance: String,
) {
    XTheme { BalanceCard(balance = balance, currency = "USD") }
}
```

**Dependencies** — each feature module needs both:

```kotlin
// feature/{featurename}/build.gradle.kts
sourceSets {
    commonMain {
        dependencies {
            implementation(libs.compose.ui.tooling.preview)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.ui.tooling)  // AS preview renderer
}
```

Both aliases exist in `libs.versions.toml` (`compose-ui-tooling-preview`, `compose-ui-tooling`).

### 6. Navigation (presentation/navigation/)

**Purpose**: Define routes and integrate feature into navigation graph

**Pattern**:
- Create `@Serializable` route object(s) for type-safe navigation
- Create `NavGraphBuilder` extension function (named after feature, lowercase)
- Use `composable<RouteType>` builder
- Extract route parameters with `.toRoute<RouteType>()`

**Key Rules**:
- Route naming: `{Feature}Route` (e.g., `ProductDetailRoute`)
- Extension function naming: `{featurename}` lowercase (e.g., `fun NavGraphBuilder.productdetail(...)`)
- Use callback parameters for navigation (e.g., `onBackClick: () -> Unit`, `onNavigateToDetail: (Int) -> Unit`)
- All navigation wiring happens in `BaseAppNavHost.kt`, not in the feature
- Route parameters as constructor properties (e.g., `data class ProductDetailRoute(val productId: Int)`)

**Navigation Pattern**:
```kotlin
@Serializable
data class FeatureRoute(val param: String)

fun NavGraphBuilder.feature(
    onBackClick: () -> Unit,
    onNavigate: (String) -> Unit,
) {
    composable<FeatureRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<FeatureRoute>()
        FeatureScreen(
            param = route.param,
            onBackClick = onBackClick,
            onNavigate = onNavigate,
            viewModel = koinViewModel(viewModelStoreOwner = backStackEntry)
        )
    }
}
```

## 4-State UI Pattern (MANDATORY)

Every async data field (wrapped in `UiState<T>`) MUST handle all 4 states:

**1. Uninitialized**: Initial state, no action taken yet
- Behavior: Show empty state or placeholder, or do nothing

**2. Loading**: Data is being fetched
- Behavior: Show loading indicator (e.g., `XCircularProgressIndicator()`)

**3. Success<T>**: Data loaded successfully
- Behavior: Render content with `state.value`

**4. Failed**: Operation failed with error
- Behavior: Show error message with retry button

**Pattern**:
```kotlin
when (val state = uiModel.dataState) {
    UiState.Uninitialized -> {
        // Empty or placeholder
    }
    UiState.Loading -> {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            XCircularProgressIndicator()
        }
    }
    is UiState.Success -> {
        // state.value is the DTO from data/model/
        DataContent(data = state.value)
    }
    is UiState.Failed -> {
        ErrorView(
            error = state.error,
            onRetry = onRetry
        )
    }
}
```

## Screen Shapes: Data-Fetching vs Form

Rule 4 mandates handling all four UI states. **How** to render them varies by what the screen is *for*. Two shapes are sanctioned; pick by the deciding question below.

### Deciding question

> *"Does the screen have content that must persist across state transitions — user input, focus, IME state?"*

- **No** → **Data-fetching screen** (default). Visible content is entirely a function of the async result. Use the separated shape — one composable per UI state.
- **Yes** → **Form screen** (exception). The form is the persistent UI; loading/error are decorations on it. Collapse states into a single Content composable with derived `isLoading`/`errorMessage` parameters.

### Shape A — Data-fetching screen (default)

Use for: product detail, order list, profile, dashboard, anything where the screen has nothing to show until data arrives.

```kotlin
// In ProductDetailScreen.kt
@Composable
fun ProductDetailScreenRoot(
    uiModel: ProductDetailUiModel,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
) {
    XScreen(topBar = { /* XTopAppBar(...) */ }) {   // Rule 13 — XScreen, not XScaffold
        when (val state = uiModel.productState) {
            UiState.Uninitialized -> EmptyContent()                              // optional shell
            UiState.Loading      -> LoadingContent()                             // optional shell
            is UiState.Success   -> ProductDetailContent(product = state.value)  // from components/
            is UiState.Failed    -> FailedContent(error = state.error, onRetry = onRetry) // optional shell
        }
    }
}

@Composable private fun LoadingContent() { /* XCircularProgressIndicator — only if design specifies a loading screen */ }
@Composable private fun FailedContent(error: ErrorModel, onRetry: () -> Unit) { /* only if design specifies a failure screen */ }
@Composable private fun EmptyContent() { /* only if design specifies an empty/uninitialized screen */ }
// ProductDetailContent lives in components/ProductDetailContent.kt — always its own file.
```

**State-shell composables (`LoadingContent`/`FailedContent`/`EmptyContent`) are optional** and present only when the design specifies a dedicated screen for that state. A screen that shows a skeleton inside the content composable, or that renders errors inline, does not introduce these shells.

`{Feature}Content` **always** lives in `components/{Feature}Content.kt` — it is never inlined into `Screen.kt`. The same rule applies to every sub-component of `{Feature}Content`.

### Shape B — Form screen (documented exception)

Use for: login, signup, search-as-you-type input, payment form, any screen whose primary content is user input that must survive state transitions.

```kotlin
@Composable
fun LoginScreenRoot(
    uiModel: LoginUiModel,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onBackClick: () -> Unit,
) {
    val isLoading = uiModel.submitState is UiState.Loading
    val errorMessage = (uiModel.submitState as? UiState.Failed)?.error?.asString()

    if (uiModel.submitState is UiState.Success) {
        LaunchedEffect(Unit) { onLoginSuccess() }
    }

    XScreen {   // Rule 13 — XScreen, not XScaffold; no paddingValues to thread
        LoginContent(
            username = uiModel.username,
            password = uiModel.password,
            isLoading = isLoading,
            errorMessage = errorMessage,
            // ... callbacks
        )
    }
}
// LoginContent lives in components/LoginContent.kt — always its own file.
```

Loading is shown as a button-progress indicator inside `LoginContent`; error is shown as inline text below the form. The form stays mounted across all four states, so input/focus is preserved.

Under the file-organization rule, Shape B's `Screen.kt` contains **only** `LoginScreen` + `LoginScreenRoot` — no state-shell composables, because the form itself absorbs all four states. `LoginContent` is in `components/`, not in `Screen.kt`.

### Why form screens deviate

Replacing the form with a `LoadingContent` on submit would:
1. Tear `TextField` out of composition → lose user input across recompose
2. Lose focus and IME state
3. Hide the action the user just took

That's a UX cost only justified for forms. Data-fetching screens don't pay it because there's no input to preserve.

### Mixed screens

A screen with both a persistent input section and a separate data-fetched section gets **multiple `UiState<DTO>` slots** on its `*UiModel`. Apply Shape A to the data section and Shape B to the form section — they're independent state machines.

### Documenting the choice

The chosen shape — especially deviation to Shape B — must be recorded in the feature's `.claude/docs/{featurename}/spec.md` under **Design Decisions**, with the rationale. Example from `.claude/docs/login/spec.md`:

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Error display | Inline text below form | Less disruptive; keeps user in context |
| Loading state | XButtonProgress in-button indicator | Keeps form visible; shows which action is in progress |

The reviewer uses the spec's Design Decisions to know which shape is expected. Without that record, the reviewer assumes Shape A (the default).

### Decision matrix

`Screen.kt` allowlist is fixed: `Screen` + `ScreenRoot` always; `LoadingContent` / `FailedContent` / `EmptyContent` only when the **design** specifies a dedicated screen for that state. `{Feature}Content` and every other composable always live in `components/`.

| Screen kind | Shape | Composables in `Screen.kt` | Where the content lives |
|-------------|-------|----------------------------|--------------------------|
| Data-fetching (no persistent input) | A — separated | `Screen`, `ScreenRoot`, and any of `LoadingContent`/`FailedContent`/`EmptyContent` that the design requires | `components/{Feature}Content.kt` |
| Form (persistent input) | B — single Content | `Screen`, `ScreenRoot` only (loading/error render inline inside `Content`) | `components/{Feature}Content.kt` |
| Mixed | A + B combined | `Screen`, `ScreenRoot`, plus the optional state shells that apply to the data section | `components/{Feature}Content.kt` (+ section components) |

## X-Components (Design System)

**Required**: All UI must use X-components from `:core:designsystem`

**Common X-components**:
- Layout: `XScreen` (feature screen container — Rule 13), `XColumn`, `XRow`, `XBox`, `XSpacer` — `XScaffold` is app-shell only, never in a feature
- Text: `XText`, `XTextField`, `XOutlinedTextField`
- Buttons: `XButton`, `XTextButton`, `XIconButton`, `XFilledButton`
- App bars: `XTopAppBar`, `XBottomAppBar`
- Navigation: `XNavHost` (app-level only, not in features)
- Progress: `XCircularProgressIndicator`, `XLinearProgressIndicator`
- Dialogs: `XAlertDialog`, `XDialog`
- Icons: `XIcon`

**Note**: XTheme is app-level only (in `composeApp`), features don't wrap in XTheme

## Strings & Localization (Rule 12)

**No hardcoded user-facing strings.** Every `text`, `label`, `placeholder`, and `contentDescription` resolves from a string resource. Full rules and the `UiText`/`DesignSystemResources` patterns live in `@../../_shared/patterns.md` → "Strings & Localization (Rule 12)". UI-layer essentials:

1. **Create the catalog first**: `feature/{featurename}/src/commonMain/composeResources/values/strings.xml`. Add a key per display string before/while writing the composable. Key naming: `{area}_{purpose}` (e.g. `send_title`, `recipient_placeholder`, `cd_back`, `section_portfolio`).

2. **Resolve in the composable** via the module's generated `Res`:
   ```kotlin
   import {PROJECT_NAMESPACE}.feature.{featurename}.generated.resources.Res
   import {PROJECT_NAMESPACE}.feature.{featurename}.generated.resources.send_title
   import org.jetbrains.compose.resources.stringResource

   XText(text = stringResource(Res.string.send_title))
   XText(text = stringResource(Res.string.balance_amount_template, balanceBtc))  // format args
   ```

3. **ViewModel-origin text** (validation/computed messages) → carry as `UiText` on `*UiModel`, resolve with `.asString()` in the composable. ViewModels never call `stringResource`.

4. **Shared strings** (Retry/Yes/No/common errors) come from `DesignSystemResources`, not a per-feature key.

5. **Leave as literals**: control sentinels parsed in logic, single-glyph symbols (`$`, `₿`, `%`), and repository-supplied data (names, dates, tickers).

No Gradle change needed — feature modules already include `libs.compose.components.resources` and a `composeResources/` dir.

## Computed Display Values (Rule 11)

**Pattern**: If the UI needs a formatted/derived value from a DTO (e.g. `"3 days ago"` from a `Long` timestamp, `"$12.99"` from a `Double`), add a **sibling field** to `*UiModel` and populate it in the ViewModel when the source `UiState<DTO>` becomes Success.

**Do NOT define a mirror UI-layer copy of the DTO.** That's a Rule 11 violation and inverts the dependency rule (presentation should depend on data, not the reverse).

**Example**:
```kotlin
// In *UiModel:
data class ProductDetailUiModel(
    val dataState: UiState<Product> = UiState.Uninitialized,  // Product is the DTO from data/model/
    val priceLabel: String = "",                              // computed display value
    val joinedAgoLabel: String = "",                          // computed display value
)

// In ViewModel:
when (val result = repository.getProduct()) {
    is Either.Success -> {
        val product = result.data
        _uiModel.setState {
            copy(
                dataState = UiState.Success(product),
                priceLabel = "$${product.price}",
                joinedAgoLabel = formatRelative(product.createdAt),
            )
        }
    }
    is Either.Failure -> _uiModel.setState { copy(dataState = UiState.Failed(result.error)) }
}
```

## Common Patterns

### Initial Data Load Pattern
```kotlin
class FeatureViewModel(
    private val repository: FeatureRepository
) : ViewModel() {
    private val _uiModel = MutableStateFlow(FeatureUiModel())
    val uiModel = _uiModel.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        _uiModel.setState { copy(dataState = UiState.Loading) }

        viewModelScope.launch {
            when (val result = repository.getData()) {
                is Either.Success -> {
                    // result.data is the DTO — store directly. No mapping (Rule 11).
                    _uiModel.setState { copy(dataState = UiState.Success(result.data)) }
                }
                is Either.Failure -> {
                    _uiModel.setState { copy(dataState = UiState.Failed(result.error)) }
                }
            }
        }
    }
}
```

### Form Validation Pattern (form fields live on *UiModel)
```kotlin
// FeatureUiModel: error messages are UiText, not String literals (Rule 12)
// data class FeatureUiModel(val email: String = "", val emailError: UiText? = null, val submitState: UiState<Unit> = UiState.Uninitialized)

fun updateEmail(email: String) {
    _uiModel.setState { copy(email = email, emailError = null) }
}

fun validateEmail(): Boolean {
    val email = _uiModel.value.email
    // ViewModel can't call stringResource — build UiText, resolve in the composable with .asString()
    val error: UiText? = when {
        email.isBlank() -> UiText.Resource(Res.string.email_required)
        !email.contains("@") -> UiText.Resource(Res.string.email_invalid)
        else -> null
    }
    _uiModel.setState { copy(emailError = error) }
    return error == null
}

fun submit() {
    if (!validateEmail()) return

    _uiModel.setState { copy(submitState = UiState.Loading) }
    // ... submit logic
}
```

## Module Dependencies

**UI layer requires**:
- `:core:common` - For Either, UiState, setState, ErrorModel
- `:core:designsystem` - For X-components
- Feature's data layer (same module) - Repository interfaces

**Standard libraries**:
- Compose Multiplatform (foundation, ui, material3, resources)
- AndroidX Lifecycle (ViewModel, collectAsStateWithLifecycle)
- Koin (koinViewModel)
- Kotlinx Collections Immutable
- Navigation Compose

> AndroidX Lifecycle's `ViewModel` and `lifecycle.runtime.compose` (source of `collectAsStateWithLifecycle`) are `api`-exposed by `:core:common`. Feature modules **do not** need to declare these dependencies directly.

## Validation Strategy

**Incremental build validation** (fast, per-feature):
```bash
./gradlew :feature:{featurename}:assembleAndroidMain
```

Run this after UI layer implementation to verify:
- Compilation succeeds
- Composables compile correctly
- State management correct
- X-components used properly

**Note**: Full `./gradlew assembleDebug` + `./gradlew ktlintFormat` runs during integration phase.
