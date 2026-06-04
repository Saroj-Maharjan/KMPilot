# Code Review: Dashboard
**Date**: 2026-05-22 | **Spec**: v3.3.0

---

## Summary
Passed: 15/18 | Warnings: 3 | Critical: 0
**Status**: PASS WITH WARNINGS

---

## Preview Rule Verification

This section specifically addresses the Compose Multiplatform preview additions.

| Check | Result | Detail |
|-------|--------|--------|
| Deprecated `org.jetbrains.compose.ui.tooling.preview.Preview` import | NOT PRESENT | Zero matches across all 11 files that import @Preview |
| Canonical `androidx.compose.ui.tooling.preview.Preview` import | CORRECT | Found in `DashboardScreen.kt` and all 10 component files |
| Preview composables false-flagged by allowlist | NOT FALSE-FLAGGED | All 13 preview functions are `private` and `@Preview`-annotated — correctly exempt |
| `sampleDashboardData` false-flagged as an allowlist violation | NOT FALSE-FLAGGED | Private `val` (non-`@Composable`) that supports previews — exempt |
| Gradle: `compose.ui.tooling.preview` in commonMain | CORRECT | `implementation(libs.compose.ui.tooling.preview)` in `commonMain` dependencies |
| Gradle: `compose.ui.tooling` as androidRuntimeClasspath | CORRECT | `androidRuntimeClasspath(libs.compose.ui.tooling)` at module level |

**Conclusion**: The new preview encoding works correctly with zero false positives. The canonical CMP 1.11.0+ import is used everywhere; the deprecated JetBrains import is absent.

---

## Spec Compliance

| Section | Status | Details |
|---------|--------|---------|
| Data Models | PASS | All 9 `@Serializable` DTOs present matching spec |
| Interfaces | PASS | `DashboardRemoteDataSource` and `DashboardRepository` present; `DashboardLocalDataSource` present (unbound — see P2-1) |
| State | PASS | `UiState<DashboardData>` in `DashboardUiModel` matches spec |
| Navigation | PASS | `onActionClick: (String) -> Unit`, `onBackToDashboard: () -> Unit` callbacks; no navController |

---

## Rules (1-11)

### PASS  Rule 1: Interface + Impl
**Files**: `data/datasource/` (4 files), `data/repository/` (2 files)
**Findings**: `DashboardRemoteDataSource`/`DashboardRemoteDataSourceImpl` and `DashboardRepository`/`DashboardRepositoryImpl` are correctly paired. `DashboardLocalDataSource`/`DashboardLocalDataSourceImpl` also exist as a valid pair, but `DashboardLocalDataSourceImpl` is not bound in DI (see P2-1).

### PASS  Rule 2: Either<T>
**Files**: `data/datasource/DashboardRemoteDataSource.kt:7`, `data/repository/DashboardRepository.kt:7`, `data/repository/DashboardRepositoryImpl.kt:10`
**Findings**: All fallible operations return `Either<DashboardData>`. Repository delegates directly to remote data source.

### PASS  Rule 3: setState
**Files**: `presentation/DashboardViewModel.kt:24,28,32`
**Findings**: Three `_uiModelState.setState { copy(...) }` calls. Zero `_uiModel.value =` or `_uiState.value =` direct assignments found.

### PASS  Rule 4: 4 UI States
**Files**: `presentation/ui/DashboardScreen.kt:94-111`
**Findings**: `when` branch in `DashboardScreenRoot` handles `Uninitialized`+`Loading` → `LoadingContent`, `Failed` → `ErrorContent`, `Success` → `DashboardContent`. All four states covered.

### PASS  Rule 5: X-Components (No Material3)
**Files**: All presentation files
**Findings**: Zero forbidden Material3 component imports. `XScaffold`, `XText`, `XButton`, `XTextButton`, `XIcon`, `XCircularProgressIndicator` used throughout. `MaterialTheme.colorScheme.*` accesses are permitted (theme accessor via `XTheme`).

### WARNING  Rule 6: ImmutableList
**Files**: `data/model/DashboardData.kt:9-15`, `presentation/DashboardViewModel.kt:28-30`
**Findings**: `DashboardData` DTO has 6 mutable `List<T>` fields. No `.toImmutableList()` conversion exists anywhere in the feature. Since `DashboardData` is stored inside `UiState.Success`, Compose cannot guarantee referential stability for the list fields, preventing safe recomposition skipping of list-driven sections.

### PASS  Rule 7: Lowercase Packages
**Files**: All 24 `.kt` files
**Findings**: All packages follow `thisissadeghi.dashboard.*` lowercase pattern. No violations.

### PASS  Rule 8: DI Binding
**Files**: `di/DashboardModules.kt`
**Findings**: `singleOf(::DashboardRemoteDataSourceImpl).bind<DashboardRemoteDataSource>()` and `singleOf(::DashboardRepositoryImpl).bind<DashboardRepository>()` present in top-level `val dashboardModule`. `viewModelOf(::DashboardViewModel)` registered.

### PASS  Rule 9: No UseCases
**Files**: All files scanned
**Findings**: Zero `UseCase` references. ViewModel calls `repository.getDashboard()` directly.

### PASS  Rule 10: Callback Parameters
**Files**: `presentation/ui/DashboardScreen.kt:66-79`
**Findings**: `DashboardScreen` and `DashboardScreenRoot` take `onActionClick: (String) -> Unit` and `onBackToDashboard: () -> Unit`. Zero `navController` references.

### PASS  Rule 11: Single UiModel + DTO-wrapped UiState
**Files**: `presentation/DashboardUiModel.kt`, `data/` layer
**Findings**:
- (a) Zero `*UiState.kt` files in `presentation/` — PASS
- (b) Exactly 1 `*UiModel.kt` — PASS
- (c) Zero `import .*\.presentation\.` in `data/` layer — PASS
- (d) `UiState<DashboardData>` wraps a `data/model/` DTO — PASS
- (e) Repository returns `Either<DashboardData>` — PASS
- (f) ViewModel exposes `val uiModelState` (not `val uiModel`) — minor naming deviation, see P2-2

### WARNING  UI File Organization: DashboardContent in Screen.kt
**Files**: `presentation/ui/DashboardScreen.kt:117-141`
**Findings**: `DashboardContent` (private, 24 lines) is inlined in `Screen.kt` rather than extracted to `components/DashboardContent.kt`. Under the updated strict allowlist, `Screen.kt` is limited to `DashboardScreen`, `DashboardScreenRoot`, and the three optional state shells (`LoadingContent`, `FailedContent`, `EmptyContent`). `DashboardContent` is the success content orchestrator — it owns the `LazyColumn` layout across 9 sections and qualifies as a self-contained unit that should live in `components/`. Flagged as Warning (predates the rule update).

### PASS  UI File Organization: @Preview Composables (Exempt)
**Files**: `DashboardScreen.kt:316-356`, all 10 `components/*.kt` files
**Findings**: All 13 preview functions are `private @Preview @Composable`, co-located with the composable they preview. Exempt from allowlist per preview rule. Three screen-level previews in `DashboardScreen.kt`; ten component-level previews each in their respective component file. `sampleDashboardData` is a private `val` supporting those previews — also exempt.

---

## Integration Points

### PASS  Point 1: Gradle Include
**Found**: YES — `settings.gradle.kts:37`

### PASS  Point 2: Gradle Dependency
**Found**: YES — `composeApp/build.gradle.kts:48`

### PASS  Point 3: DI Init
**Found**: YES — `initKoin.kt` — `dashboardModule` listed in `modules(...)`

### PASS  Point 4: Navigation
**Found**: YES — `BaseAppNavHost.kt:26` — `dashboard(...)` extension

---

## Design-Aware Compliance

| Check | Result | Detail |
|-------|--------|--------|
| Blueprint present | YES | `.claude/docs/dashboard/designs/dashboard_blueprint.md` |
| `blueprintConsumed` flag | TRUE | `stitch-project.json → features.dashboard.blueprintConsumed: true` |
| Component coverage | PASS | All 10 blueprint-defined sections mapped to component files |
| Theme alignment | PASS | Blueprint tokens match `stitch-project.json` dark theme snapshot |

---

## Recommendations

### Critical (P1)
None.

### Warnings (P2)

**P2-1: DashboardLocalDataSource pair exists but is dead (not bound in DI, not used)**
`DashboardLocalDataSource` + `DashboardLocalDataSourceImpl` are not referenced by `DashboardRepositoryImpl` or registered in `DashboardModules`. Either remove the dead pair or register and wire it.
Fix: Remove both files, or add `singleOf(::DashboardLocalDataSourceImpl).bind<DashboardLocalDataSource>()` to `DashboardModules` and inject into `DashboardRepositoryImpl` for local caching.
Files: `di/DashboardModules.kt`, `data/datasource/DashboardLocalDataSource.kt`, `data/datasource/DashboardLocalDataSourceImpl.kt`

**P2-2: ViewModel public flow named `uiModelState` instead of `uiModel` (Rule 11 convention)**
`val uiModelState` in `DashboardViewModel` propagates as `uiState` naming in `DashboardScreen` and `DashboardScreenRoot`. Rule 11 convention specifies `val uiModel: StateFlow<DashboardUiModel>`.
Fix: Rename `_uiModelState` → `_uiModel`, `uiModelState` → `uiModel` in ViewModel, and update `DashboardScreen` collector and `DashboardScreenRoot` parameter accordingly.
Files: `presentation/DashboardViewModel.kt:16-17`, `presentation/ui/DashboardScreen.kt:72,84`

**P2-3: ImmutableList not used for DTO list fields (Rule 6)**
`DashboardData` holds 6 `List<T>` fields with no `.toImmutableList()` conversion before they reach `UiState.Success`. Compose treats mutable `List` as unstable, forcing recomposition of all list-driven sections on every state update.
Fix option A (preferred): Annotate `DashboardData` with `@Immutable` (from `androidx.compose.runtime`) since it is a `@Serializable` data class whose contents won't be mutated after construction.
Fix option B: In `DashboardRepositoryImpl`/ViewModel, convert each list field via `.toImmutableList()` before wrapping in `UiState.Success`.
Files: `data/model/DashboardData.kt:9-15`, `presentation/DashboardViewModel.kt:28-30`
