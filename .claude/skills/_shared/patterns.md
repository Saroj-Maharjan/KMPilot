# KMP Architecture Patterns (Single Source of Truth)

All skills and agents import this file. Do not duplicate these rules elsewhere.

## 11 Critical Rules

1. **Interface + Impl** - DataSource and Repository always have interface + implementation pair
2. **Either<T>** - Return `Either<T>` for fallible operations, never throw exceptions
3. **setState** - Use `_uiModel.setState { copy() }`, NEVER `_uiModel.value =`
4. **4 UI States** - Handle all: Uninitialized / Loading / Success / Failed
5. **X-components** - Use `:core:designsystem` components, NO Material3
6. **ImmutableList** - Use `.toImmutableList()` for state collections
7. **Lowercase packages** - `{PKG_PREFIX}.featurename` (no hyphens/camelCase/underscores)
8. **DI Pattern** - `singleOf(::Impl).bind<Interface>()` + extend `BaseFeature`
9. **No UseCases** - ViewModels invoke repositories directly
10. **Callback params** - Screens take callbacks (`onBackClick`), not `navController`
11. **Single UiModel + DTO-wrapped UiState** - `*UiModel` is the only presentation state container (no `*UiState.kt`). It holds plain UI fields + one `UiState<DTO>` slot per async operation, where DTO is the data-layer model (use `UiState<Unit>` for void ops). Repository returns `Either<DTO>`; data layer never imports from `presentation`. UI-derived display values live as sibling fields on `*UiModel`, never as mirror DTO types.

## Design-Aware Implementation

Implementation skills (`/modifying-kmp-feature`, `/creating-kmp-feature`) auto-detect Stitch design blueprints:

1. Check for `.claude/docs/{featurename}/designs/{featurename}_blueprint.md`
2. Check `blueprintConsumed == false` in `.claude/docs/_project/stitch-project.json` under `features[featurename]`
3. If both conditions met → **design-aware mode**: blueprint drives UI implementation (XTheme updates, component tree, post-implementation checklist)
4. After implementation → set `blueprintConsumed: true` in `stitch-project.json.features[featurename]`

`/using-design-system` auto-activates for UI work and does not need explicit invocation.

## 4 Integration Points

Every feature requires exactly these 4 integrations:

| # | Point | File | Pattern |
|---|-------|------|---------|
| 1 | Gradle Include | `settings.gradle.kts` | `include(":feature:{featurename}")` |
| 2 | Gradle Dependency | `composeApp/build.gradle.kts` | `implementation(project(":feature:{featurename}"))` |
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
_uiModel.setState { copy(isLoading = true) }

// WRONG - never do this
_uiModel.value = _uiModel.value.copy(isLoading = true)
```

### Either (Rule 2) — repository returns Either<DTO> directly (Rule 11)
```kotlin
when (val result = repository.getData()) {
    is Either.Success -> _uiModel.setState { copy(dataState = UiState.Success(result.data)) }
    is Either.Failure -> _uiModel.setState { copy(dataState = UiState.Failed(result.error)) }
}
// result.data is the data-layer DTO. Do NOT map to a presentation-layer mirror type.
```

### UiModel (Rule 11) — single state container, DTOs inside UiState
```kotlin
data class FeatureUiModel(
    val searchQuery: String = "",                                    // plain UI field
    val selectedTab: Int = 0,                                         // plain UI field
    val dataState: UiState<FeatureResponse> = UiState.Uninitialized,  // UiState<DTO>
    val submitState: UiState<Unit> = UiState.Uninitialized,           // UiState<Unit> for void ops
)
```

### ScreenRoot (Rule 10 + Rule 11) — takes the UiModel + callbacks only
```kotlin
// Screen: ViewModel wrapper (NOT tested directly)
@Composable
fun FeatureScreen(viewModel: FeatureViewModel, onBackClick: () -> Unit) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    FeatureScreenRoot(uiModel = uiModel, onBackClick = onBackClick, onRetry = viewModel::retry)
}

// ScreenRoot: ViewModel-independent (TESTABLE)
@Composable
fun FeatureScreenRoot(uiModel: FeatureUiModel, onBackClick: () -> Unit, onRetry: () -> Unit) {
    // All UI implementation here; route on uiModel.dataState for the async slot
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
│   ├── {Feature}UiModel.kt      # Single state container: plain fields + UiState<DTO> slots
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
- State routing (`when (uiModel.dataState) { Loading -> ... Success -> ... }`) for data-fetching screens
- Top-level layout scaffold (e.g. the `LazyColumn` or `Column` that sequences sections)
- **For data-fetching screens (default shape)**: state screens (`LoadingContent`, `FailedContent`) — they exist only to respond to a UI state, not as standalone units
- **For form screens (documented deviation)**: a single `<Feature>Content` that takes derived `isLoading`/`errorMessage` params and stays mounted across all states — preserves user input/focus

**Move to `components/{Name}.kt`** — composables that are self-contained UI units:
- A composable can be named and described as a "thing" independently of the screen
- It owns its own internal structure, visual identity, or domain logic
- It has its own private sub-composables or private helper functions

> The guiding question: *"Does this composable have meaning on its own, or does it only make sense as part of the screen?"* If it has meaning on its own → `components/`. If it only exists to wire things together → `{Feature}Screen.kt`.

**Picking the screen shape**: see [architecture/ui.md → "Screen Shapes: Data-Fetching vs Form"](../creating-kmp-feature/architecture/ui.md) for the deciding question and both example shapes. Deviation from the default (Shape A — separated composables) must be recorded in the feature's spec under Design Decisions.

## Build Commands

```bash
./gradlew :feature:{featurename}:assembleAndroidMain  # Incremental (fast)
./gradlew assembleDebug                         # Full build
./gradlew :feature:{featurename}:ktlintFormat          # Format
./gradlew :feature:{featurename}:desktopTest           # Tests
```

## Hook Marker Contract

A PreToolUse hook (`.claude/hooks/protect-feature-files.sh`, registered in `.claude/settings.json`) blocks direct `Edit`/`Write` on files under `feature/` unless a skill is active.

| Aspect | Value |
|--------|-------|
| Marker file | `/tmp/.claude-kmpilot-skill-active` |
| Activation | `touch /tmp/.claude-kmpilot-skill-active` before editing feature files |
| Cleanup | `rm -f /tmp/.claude-kmpilot-skill-active` after completion or early exit |
| Staleness | Marker auto-expires after 2 hours; hook removes stale markers |
| Bypassed paths | `*/commonTest/*`, `*/desktopTest/*`, `*/androidTest/*`, `*/test/*`, any `build.gradle.kts` |

**Skills that activate the marker:** `/creating-kmp-feature`, `/modifying-kmp-feature`. Both skills' allowlists include `Bash(touch:*)` and `Bash(rm -f /tmp/.claude-kmpilot-skill-active)`. Test agents write test files directly (bypassed by path rule), so they do NOT need the marker.

If the hook blocks an edit, message shown: *"Blocked: Cannot edit feature source files directly. Use /creating-kmp-feature or /modifying-kmp-feature skill first."*
