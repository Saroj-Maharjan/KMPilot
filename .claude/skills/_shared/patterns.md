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

## UI Implementation Workflow (MANDATORY)

**STOP — When creating or modifying ANY Screens, Composables, or UI components:**

1. **Check** the available skills list (in the system-reminder) for `frontend-design`
2. **If `frontend-design` IS in the available skills** → invoke `/frontend-design` FIRST — MANDATORY, do NOT skip

`/using-design-system` auto-activates for UI work and does not need explicit invocation. `/frontend-design` is required whenever it appears in the available skills list — when present, NEVER skip it.

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
│   ├── ui/              # Screens + components
│   └── navigation/      # Routes + NavGraphBuilder
└── di/
    └── {Feature}Modules.kt
```

## Build Commands

```bash
./gradlew :feature:{name}:assembleAndroidMain  # Incremental (fast)
./gradlew assembleDebug                         # Full build
./gradlew :feature:{name}:ktlintFormat          # Format
./gradlew :feature:{name}:desktopTest           # Tests
```
