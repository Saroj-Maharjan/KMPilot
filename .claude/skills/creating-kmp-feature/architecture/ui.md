# UI Layer Architecture Principles

Principles for implementing the UI/presentation layer in KMP features using Compose Multiplatform and MVVM.

**Note**: Uses `{PKG_PREFIX}` placeholder for package prefix (resolved via Context Discovery).

## UI Layer Structure

```
{PKG_PREFIX}.{featurename}/presentation/
├── {Feature}ViewModel.kt      # State management and business logic
├── {Feature}UiState.kt        # @Stable data class wrapping UiState fields
├── {Feature}UiModel.kt        # @Stable UI data models
├── {Entity}UiModel.kt         # Additional UI models if needed
├── ui/
│   ├── {Feature}Screen.kt     # Main screen composable
│   ├── {Secondary}Screen.kt   # Additional screens
│   └── components/            # Feature-specific reusable components
└── navigation/
    └── {Feature}Navigation.kt # @Serializable routes + NavGraphBuilder extension
```

## Critical Rules (UI Layer)

1. **setState Extension**: Always use `setState { copy(...) }`, NEVER `_state.value =` or direct assignment
2. **4 UI States**: Handle all 4 states (Uninitialized/Loading/Success/Failed) for every async data field
3. **X-components Only**: Use X-components from `:core:designsystem` (XScaffold, XButton, XText), NO Material3
4. **ImmutableList**: Use `.toImmutableList()` for collections in state
5. **Callback Parameters**: Screens take callbacks (e.g., `onBackClick: () -> Unit`), not `navController`
6. **ScreenRoot Pattern**: ALWAYS create `{Feature}ScreenRoot` for ViewModel-independent, testable UI

## Layer Responsibilities

### 1. ViewModel (presentation/)

**Purpose**: Manage UI state, coordinate business logic, handle user interactions

**Pattern**:
- Extend `ViewModel` from AndroidX Lifecycle
- Use `MutableStateFlow<{Feature}UiState>` for state, expose as `StateFlow` via `.asStateFlow()`
- Inject Repository interface(s) via constructor
- Use `viewModelScope.launch` for coroutines
- Load initial data in `init` block (if needed)
- Provide functions for user actions (e.g., `submitForm()`, `retryLoad()`)

**Key Rules**:
- **Always** use `_uiState.setState { copy(field = newValue) }` for state updates
- Handle `Either<T>` results from repository with pattern matching
- Convert domain models to UI models (e.g., `toUiModel()` extension functions)
- Validation logic goes here (e.g., email validation, form validation)
- No direct UI references (no Context, no Composables)

**State Update Pattern**:
```kotlin
// CORRECT
_uiState.setState { copy(isLoading = true) }

// WRONG - NEVER DO THIS
_uiState.value = _uiState.value.copy(isLoading = true)
```

**Error Handling Pattern**:
```kotlin
when (val result = repository.getData()) {
    is Either.Success -> {
        val uiModel = result.data.toUiModel()
        _uiState.setState { copy(dataState = UiState.Success(uiModel)) }
    }
    is Either.Failure -> {
        _uiState.setState { copy(dataState = UiState.Failed(result.error)) }
    }
}
```

### 2. UiState (presentation/)

**Purpose**: Container for all UI state for a feature

**Pattern**:
- Annotated with `@Stable` (from Compose runtime)
- Data class with default values for all fields
- Uses `UiState<T>` for async data fields
- Naming: `{Feature}UiState` (e.g., `ProductDetailUiState`)

**Key Points**:
- One UiState per feature (not per screen, even if multiple screens)
- Async fields use `UiState<T>` (e.g., `val productState: UiState<ProductData> = UiState.Uninitialized`)
- Sync fields use regular types (e.g., `val selectedTab: Int = 0`, `val searchQuery: String = ""`)
- Collections use `ImmutableList` (e.g., `val items: ImmutableList<Item> = persistentListOf()`)
- Use nested data classes for complex state groups

### 3. UiModel (presentation/)

**Purpose**: UI-specific data models (transformed from domain/API models)

**Pattern**:
- Annotated with `@Stable`
- Simple data classes with UI-friendly fields
- Naming: `{Entity}UiModel`, `{Feature}Data`, etc.

**Key Points**:
- Transform domain models to UI models in ViewModel (e.g., format dates, compute display strings)
- Keep UI concerns here (e.g., "3 days ago" strings, color values, visibility flags)
- Domain models stay in data layer, don't leak to UI
- Use extension functions for conversion (e.g., `fun Product.toUiModel(): ProductUiModel`)

### 4. Screen Composables (presentation/ui/)

**Purpose**: Main entry point for feature UI

**CRITICAL: Always implement TWO composables:**

1. **`{Feature}Screen`** - ViewModel wrapper
   - Takes ViewModel as parameter (with `koinViewModel()` default in Navigation, not in Screen)
   - Collects state with `.collectAsStateWithLifecycle()`
   - Delegates to ScreenRoot with state and callbacks

2. **`{Feature}ScreenRoot`** - ViewModel-independent (TESTABLE)
   - Takes UiState/UiModel as parameter
   - Takes all callbacks as lambda parameters
   - Contains all actual UI implementation
   - **This is what UI tests target**

**Key Rules**:
- Only X-components allowed (XScaffold, XTopAppBar, XButton, XText, etc.) - NO Material3 directly
- Handle all 4 UiState cases for async data in ScreenRoot
- Use `Modifier` parameters for customization
- ScreenRoot has NO ViewModel dependency

**Screen Pattern** (ViewModel wrapper):
```kotlin
@Composable
fun FeatureScreen(
    onBackClick: () -> Unit,
    viewModel: FeatureViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FeatureScreenRoot(
        uiState = uiState,
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
    uiState: FeatureUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    XScaffold(
        topBar = {
            XTopAppBar(
                title = { XText("Title") },
                navigationIcon = { XIconButton(onClick = onBackClick) { /* icon */ } }
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState.dataState) {
                UiState.Uninitialized -> { /* Empty */ }
                UiState.Loading -> { XCircularProgressIndicator() }
                is UiState.Success -> { FeatureContent(state.value) }
                is UiState.Failed -> { ErrorView(state.error, onRetry) }
            }
        }
    }
}
```

### 5. Component Composables (presentation/ui/components/)

**Purpose**: Reusable UI components specific to the feature

**Pattern**:
- Simple composable functions
- Naming: Descriptive (e.g., `ProductHeader`, `PriceSelector`, `OrderConfirmDialog`)
- Accept data and callbacks as parameters
- Use X-components from design system

**Key Points**:
- Keep components small and focused (single responsibility)
- Use preview annotations for development (`@Preview`)
- No ViewModels in components (data/callbacks passed down)
- Prefer stateless composables (state hoisted to parent)

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
when (val state = uiState.dataState) {
    UiState.Uninitialized -> {
        // Empty or placeholder
    }
    UiState.Loading -> {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            XCircularProgressIndicator()
        }
    }
    is UiState.Success -> {
        DataContent(data = state.value)
    }
    is UiState.Failed -> {
        ErrorView(
            error = state.error,
            onRetry = viewModel::retryLoad
        )
    }
}
```

## X-Components (Design System)

**Required**: All UI must use X-components from `:core:designsystem`

**Common X-components**:
- Layout: `XScaffold`, `XColumn`, `XRow`, `XBox`, `XSpacer`
- Text: `XText`, `XTextField`, `XOutlinedTextField`
- Buttons: `XButton`, `XTextButton`, `XIconButton`, `XFilledButton`
- App bars: `XTopAppBar`, `XBottomAppBar`
- Navigation: `XNavHost` (app-level only, not in features)
- Progress: `XCircularProgressIndicator`, `XLinearProgressIndicator`
- Dialogs: `XAlertDialog`, `XDialog`
- Icons: `XIcon`

**Note**: XTheme is app-level only (in `composeApp`), features don't wrap in XTheme

## Model Conversion

**Pattern**: Convert domain/API models to UI models in ViewModel using extension functions

**Location**: Same file as ViewModel, or separate `{Feature}Mappers.kt` if many conversions

**Example**:
```kotlin
// In ViewModel file
private fun ProductResponse.toUiModel() = ProductUiModel(
    id = id,
    name = name,
    price = "$${price}",  // Format for display
    isAvailable = stock > 0,  // Compute UI flag
)
```

## Common Patterns

### Initial Data Load Pattern
```kotlin
class FeatureViewModel(
    private val repository: FeatureRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FeatureUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        _uiState.setState { copy(dataState = UiState.Loading) }

        viewModelScope.launch {
            when (val result = repository.getData()) {
                is Either.Success -> {
                    val uiModel = result.data.toUiModel()
                    _uiState.setState { copy(dataState = UiState.Success(uiModel)) }
                }
                is Either.Failure -> {
                    _uiState.setState { copy(dataState = UiState.Failed(result.error)) }
                }
            }
        }
    }
}
```

### Form Validation Pattern
```kotlin
fun updateEmail(email: String) {
    _uiState.setState { copy(email = email, emailError = null) }
}

fun validateEmail(): Boolean {
    val email = _uiState.value.email
    val error = when {
        email.isBlank() -> "Email is required"
        !email.contains("@") -> "Invalid email format"
        else -> null
    }
    _uiState.setState { copy(emailError = error) }
    return error == null
}

fun submit() {
    if (!validateEmail()) return

    _uiState.setState { copy(submitState = UiState.Loading) }
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
