# KMP Architecture Patterns (Single Source of Truth)

All skills and agents import this file. Do not duplicate these rules elsewhere.

## 10 Critical Rules

1. **Interface + Impl** - DataSource and Repository always have interface + implementation pair
2. **Either<T>** - Return `Either<T>` for fallible operations, never throw exceptions
3. **setState** - Use `_uiState.setState { copy() }`, NEVER `_state.value =`
4. **4 UI States** - Handle all: Uninitialized / Loading / Success / Failed
5. **X-components** - Use `:core:designsystem` components, NO Material3
6. **ImmutableList** - Use `.toImmutableList()` for state collections
7. **Lowercase packages** - `{PKG_PREFIX}.featurename` (no hyphens/camelCase/underscores)
8. **DI Pattern** - `singleOf(::Impl).bind<Interface>()` + extend `BaseFeature`
9. **No UseCases** - ViewModels invoke repositories directly
10. **Callback params** - Screens take callbacks (`onBackClick`), not `navController`

## Design-Aware Implementation

Implementation skills (`/modifying-kmp-feature`, `/creating-kmp-feature`) auto-detect Stitch design blueprints:

1. Check for `.claude/docs/{featurename}/designs/{featurename}_blueprint.md`
2. Check `blueprintConsumed == false` in `.claude/docs/{featurename}/stitch.json`
3. If both conditions met → **design-aware mode**: blueprint drives UI implementation (XTheme updates, component tree, post-implementation checklist)
4. After implementation → set `blueprintConsumed: true` in stitch.json

`/using-design-system` auto-activates for UI work and does not need explicit invocation.

## 4 Integration Points

Every feature requires exactly these 4 integrations:

| # | Point | File | Pattern |
|---|-------|------|---------|
| 1 | Gradle Include | `settings.gradle.kts` | `include(":feature:{name}")` |
| 2 | Gradle Dependency | `composeApp/build.gradle.kts` | `implementation(project(":feature:{name}"))` |
| 3 | DI Init | `{INIT_KOIN_PATH}` | `{Feature}Modules.initialize()` |
| 4 | Navigation | `{NAV_HOST_PATH}` | `{featurename}(onBackClick = {...})` |

## Naming Conventions

| Type | Pattern | Example |
|------|---------|---------|
| Package | `{PKG_PREFIX}.{featurename}` | `com.example.productdetail` |
| ViewModel | `{Feature}ViewModel` | `ProductDetailViewModel` |
| Repository | `{Entity}Repository` / `{Entity}RepositoryImpl` | `ProductRepository` |
| DataSource | `{Entity}RemoteDataSource` / `...Impl` | `ProductRemoteDataSource` |
| Screen | `{Feature}Screen` + `{Feature}ScreenRoot` | `ProductDetailScreen` |
| Route | `{Feature}Route` | `ProductDetailRoute` |
| Nav Extension | `{featurename}` (lowercase) | `fun NavGraphBuilder.productdetail()` |
| DI Module | `{Feature}Modules` | `ProductDetailModules` |

## Key Patterns

### setState (Rule 3)
```kotlin
// CORRECT
_uiState.setState { copy(isLoading = true) }

// WRONG - never do this
_uiState.value = _uiState.value.copy(isLoading = true)
```

### Either (Rule 2)
```kotlin
when (val result = repository.getData()) {
    is Either.Success -> _uiState.setState { copy(state = UiState.Success(result.data)) }
    is Either.Failure -> _uiState.setState { copy(state = UiState.Failed(result.error)) }
}
```

### ScreenRoot (Rule 10)
```kotlin
// Screen: ViewModel wrapper (NOT tested directly)
@Composable
fun FeatureScreen(viewModel: ViewModel, onBackClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FeatureScreenRoot(uiState = uiState, onBackClick = onBackClick, onRetry = viewModel::retry)
}

// ScreenRoot: ViewModel-independent (TESTABLE)
@Composable
fun FeatureScreenRoot(uiState: UiState, onBackClick: () -> Unit, onRetry: () -> Unit) {
    // All UI implementation here
}
```

### DI Module (Rule 8)
```kotlin
object FeatureModules : BaseFeature(FeatureModules::class.simpleName.toString()) {
    override fun getKoinModules(): List<Module> = listOf(
        module {
            singleOf(::RemoteDataSourceImpl).bind<RemoteDataSource>()
            singleOf(::RepositoryImpl).bind<Repository>()
            viewModelOf(::FeatureViewModel)
        }
    )
    override fun initialize() { FeatureModules }
}
```

## Module Dependencies

| Feature depends on | When |
|--------------------|------|
| `:core:common` | Always (Either, UiState, setState, ErrorModel) |
| `:core:designsystem` | Always (X-components) |
| `:core:data` | Only if using ApiClient |

**Features NEVER depend on other features.**

## Feature Module Structure

```
{PKG_PREFIX}.{featurename}/
├── data/
│   ├── model/           # @Serializable DTOs
│   ├── remote/          # Ktor Resources
│   ├── datasource/      # Interface + Impl
│   └── repository/      # Interface + Impl
├── presentation/
│   ├── {Feature}ViewModel.kt
│   ├── {Feature}UiState.kt
│   ├── {Feature}UiModel.kt
│   ├── ui/
│   │   ├── {Feature}Screen.kt   # Screen + ScreenRoot + state routing only
│   │   └── components/          # Self-contained UI units
│   └── navigation/      # Routes + NavGraphBuilder
└── di/
    └── {Feature}Modules.kt
```

### UI File Organization

`{Feature}Screen.kt` is the orchestrator — it must stay lean. Use this rule to decide where each composable lives:

**Keep in `{Feature}Screen.kt`** — composables that are structural glue:
- `{Feature}Screen` and `{Feature}ScreenRoot`
- State routing (`when (uiState) { Loading -> ... Success -> ... }`)
- Top-level layout scaffold (e.g. the `LazyColumn` or `Column` that sequences sections)
- State screens (`LoadingContent`, `ErrorContent`) — they exist only to respond to a UI state, not as standalone units

**Move to `components/{Name}.kt`** — composables that are self-contained UI units:
- A composable can be named and described as a "thing" independently of the screen
- It owns its own internal structure, visual identity, or domain logic
- It has its own private sub-composables or private helper functions

> The guiding question: *"Does this composable have meaning on its own, or does it only make sense as part of the screen?"* If it has meaning on its own → `components/`. If it only exists to wire things together → `{Feature}Screen.kt`.

## Build Commands

```bash
./gradlew :feature:{name}:assembleAndroidMain  # Incremental (fast)
./gradlew assembleDebug                         # Full build
./gradlew :feature:{name}:ktlintFormat          # Format
./gradlew :feature:{name}:desktopTest           # Tests
```
