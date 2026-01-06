# Core Patterns Reference

Core architectural patterns for KMP features. All agents internalize these principles.

---

## 10 Critical Rules (NON-NEGOTIABLE)

| # | Rule | Details |
|---|------|---------|
| 1 | **Interface + Impl** | Always create interface + implementation pairs (DataSource, Repository) |
| 2 | **Either<T>** | Return `Either<T>` for fallible operations (never throw exceptions) |
| 3 | **setState** | Use `setState { copy() }` extension, NEVER `_state.value =` |
| 4 | **4 UI States** | Handle: Uninitialized / Loading / Success / Failed |
| 5 | **X-components** | Use X-components from `:core:designsystem`, NO Material3 |
| 6 | **ImmutableList** | Use `.toImmutableList()` for collections in state |
| 7 | **Lowercase packages** | `{PKG_PREFIX}.featurename` (never hyphens, camelCase, underscores) |
| 8 | **DI Pattern** | `singleOf(::Impl).bind<Interface>()` + extend `BaseFeature` |
| 9 | **No Use-Cases** | ViewModels invoke repositories directly |
| 10 | **Callback params** | Screens take callbacks (`onBackClick`), not `navController` |

---

## 4 Integration Points (REQUIRED)

Every feature must be integrated at exactly 4 locations:

| # | Point | File | Pattern |
|---|-------|------|---------|
| 1 | Gradle Include | `settings.gradle.kts` | `include(":feature:{name}")` |
| 2 | Gradle Dependency | `composeApp/build.gradle.kts` | `implementation(project(":feature:{name}"))` |
| 3 | DI Init | `{INIT_KOIN_PATH}` | `{Feature}Modules.initialize()` |
| 4 | Navigation | `{NAV_HOST_PATH}` | `{featurename}(onBackClick = {...})` |

---

## Project Structure

```
{ProjectName}/
├── composeApp/              # Main app (navigation, DI init)
├── core/
│   ├── common/             # Either, UiState, ErrorModel, setState
│   ├── data/               # ApiClient, RequestConfig
│   └── designsystem/       # X-components (XScaffold, XButton, etc.)
└── feature/
    └── {featurename}/      # Isolated feature module
```

---

## Feature Module Structure

```
{PKG_PREFIX}.{featurename}/
├── data/
│   ├── model/              # @Serializable DTOs
│   ├── remote/             # Ktor Resources
│   ├── datasource/         # Interface + Impl
│   └── repository/         # Interface + Impl
├── presentation/
│   ├── {Feature}ViewModel.kt
│   ├── {Feature}UiState.kt
│   ├── {Feature}UiModel.kt
│   ├── ui/                 # Screens + components
│   └── navigation/         # Routes + NavGraphBuilder
└── di/
    └── {Feature}Modules.kt # BaseFeature object
```

---

## Naming Conventions

| Type | Pattern | Example |
|------|---------|---------|
| ViewModel | `{Feature}ViewModel` | `ProductDetailViewModel` |
| Repository | `{Entity}Repository` + `Impl` | `ProductRepository`, `ProductRepositoryImpl` |
| DataSource | `{Entity}RemoteDataSource` + `Impl` | `ProductRemoteDataSource`, `ProductRemoteDataSourceImpl` |
| Screen | `{Feature}Screen` | `ProductDetailScreen` |
| ScreenRoot | `{Feature}ScreenRoot` | `ProductDetailScreenRoot` |
| Route | `{Feature}Route` | `ProductDetailRoute` |
| DI Module | `{Feature}Modules` | `ProductDetailModules` |
| Nav Extension | `{featurename}` (lowercase) | `productdetail` |

---

## Key Patterns

### setState Pattern
```kotlin
// CORRECT
_uiState.setState { copy(isLoading = true) }

// WRONG - NEVER DO THIS
_uiState.value = _uiState.value.copy(isLoading = true)
```

### Either Pattern
```kotlin
when (val result = repository.getData()) {
    is Either.Success -> _uiState.setState { copy(state = UiState.Success(result.data)) }
    is Either.Failure -> _uiState.setState { copy(state = UiState.Failed(result.error)) }
}
```

### ScreenRoot Pattern
```kotlin
// Screen: ViewModel wrapper
@Composable
fun FeatureScreen(viewModel: ViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FeatureScreenRoot(uiState = uiState, onRetry = viewModel::retry)
}

// ScreenRoot: ViewModel-independent (testable)
@Composable
fun FeatureScreenRoot(uiState: UiState, onRetry: () -> Unit) {
    // All UI implementation here
}
```

### DI Module Pattern
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

---

## Module Dependencies

| Feature depends on | When |
|--------------------|------|
| `:core:common` | Always |
| `:core:designsystem` | Always |
| `:core:data` | Only if using ApiClient |

**Features NEVER depend on other features.**