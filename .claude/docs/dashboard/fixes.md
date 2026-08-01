# Dashboard Feature - Required Fixes
**Date:** 2026-08-01
**Review status:** PASS WITH WARNINGS (0 Critical, 3 Warnings)

Note: prior fixes.md (2026-05-22) items for the dead `DashboardLocalDataSource` binding, missing `@Immutable`/ImmutableList, and `DashboardContent` extraction are **already resolved** in current code — confirmed by fresh read during this review. Superseded by the 3 items below.

---

## Fix 1 (P2-1): Rename ViewModel public flow from `uiModelState` to `uiModel`

**File**: `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/presentation/DashboardViewModel.kt`
**File**: `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/presentation/ui/DashboardScreen.kt`

**Issue**: Rule 11 convention specifies `val uiModel: StateFlow<{Feature}UiModel>`. Current name `uiModelState` propagates as param/local name `uiState` in the Screen, which reads as `UiState<T>` (a different type) rather than the UiModel it actually is. Every other reviewed feature (`assetdetail`, `profile`, `swap`) uses `uiModel`. Since CLAUDE.md names `feature/dashboard/` the reference implementation, this drift risks propagating into features copied from it.

**Current code** in `DashboardViewModel.kt:16-17`:
```kotlin
private val _uiModelState = MutableStateFlow(DashboardUiModel())
val uiModelState = _uiModelState.asStateFlow()
```

**Fixed**:
```kotlin
private val _uiModel = MutableStateFlow(DashboardUiModel())
val uiModel = _uiModel.asStateFlow()
```

Also rename all `_uiModelState.setState` call sites to `_uiModel.setState`.

**Current code** in `DashboardScreen.kt:46,60`:
```kotlin
// DashboardScreen
val uiState by viewModel.uiModelState.collectAsStateWithLifecycle()
DashboardScreenRoot(
    uiState = uiState,
    ...
)

// DashboardScreenRoot signature
fun DashboardScreenRoot(
    uiState: DashboardUiModel,
    ...
```

**Fixed**:
```kotlin
// DashboardScreen
val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
DashboardScreenRoot(
    uiModel = uiModel,
    ...
)

// DashboardScreenRoot signature
fun DashboardScreenRoot(
    uiModel: DashboardUiModel,
    ...
```

Update all body references (`uiState.dashboardState` → `uiModel.dashboardState`, etc.) and preview call sites (`uiState = ...` → `uiModel = ...`).

---

## Fix 2 (P2-2): Update spec §5.3 Navigation — add `onAssetClick`/`onProfileClick`

**File**: `.claude/docs/dashboard/spec.md:313-315` (§5.3 Navigation)

**Issue**: Implementation wires two real callbacks the spec doesn't document: `onAssetClick: (String) -> Unit` (portfolio asset card click) and `onProfileClick: () -> Unit` (profile avatar click). Spec currently lists only `onActionClick`/`onBackToDashboard`.

**Action**: Add both callbacks to §5.3's callback table/signature list, matching `DashboardScreen.kt:42-43` and `DashboardNavigation.kt:15-16`.

---

## Fix 3 (P2-3): Update spec §3.1 FR-6 — 5 quick actions, not 4

**File**: `.claude/docs/dashboard/spec.md:67` (§3.1 FR-6)

**Issue**: Spec says "4 quick action buttons: Send, Receive, Pay, Top Up (stubs)." Implementation ships a 5th — Swap (`QuickActions.kt:52-56`, `onSwapClick`, `quick_action_swap` string, `swap_horiz` icon), wired end-to-end including sample data in `DashboardLocalDataSourceImpl`. This is a deliberate, complete addition — spec text is just stale.

**Action**: Update FR-6 to read "5 quick action buttons: Send, Receive, Pay, Top Up, Swap."

---
