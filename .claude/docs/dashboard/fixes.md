# Dashboard Feature - Required Fixes
**Date:** 2026-05-19

---

## Critical (must fix)

None.

---

## Warnings (should fix)

### W-1: ImmutableList missing on DashboardData collection fields

**Rule:** Rule 6 — Use `.toImmutableList()` for state collections

**File:** `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/data/model/DashboardData.kt:9-15`

**Problem:** `DashboardData` is the `Success` payload of `UiState<DashboardData>` flowing directly into the Compose UI. It holds 6 mutable `List<T>` fields. Compose cannot skip recomposition for these because `List<T>` is not `@Stable` — the compiler treats it as unstable and will recompose unnecessarily on each state emission.

**Current code (DashboardData.kt:6-16):**
```kotlin
data class DashboardData(
    val accountBalance: AccountBalance,
    val monthlySummary: MonthlySummary,
    val recentTransactions: List<Transaction>,       // line 9
    val budgetCategories: List<BudgetCategory>,      // line 10
    val savingsGoals: List<SavingsGoal>,             // line 11
    val quickActions: List<QuickAction>,             // line 12
    val upcomingBills: List<UpcomingBill>,           // line 13
    val spendingInsight: SpendingInsight,
    val portfolioAssets: List<PortfolioAsset>,       // line 15
)
```

**Fix:** Change to `ImmutableList<T>` and add `toImmutableList()` at construction sites.

```kotlin
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Serializable
data class DashboardData(
    val accountBalance: AccountBalance,
    val monthlySummary: MonthlySummary,
    val recentTransactions: ImmutableList<Transaction>,
    val budgetCategories: ImmutableList<BudgetCategory>,
    val savingsGoals: ImmutableList<SavingsGoal>,
    val quickActions: ImmutableList<QuickAction>,
    val upcomingBills: ImmutableList<UpcomingBill>,
    val spendingInsight: SpendingInsight,
    val portfolioAssets: ImmutableList<PortfolioAsset>,
)
```

**Note:** `ImmutableList` is serializable-compatible when using `kotlinx-collections-immutable`. Also update `DashboardLocalDataSourceImpl.kt` to call `.toImmutableList()` on each `listOf(...)` call, and the deserialized response from `DashboardRemoteDataSourceImpl` will need a mapping step (or a custom serializer) since JSON arrays deserialize to `List<T>`. The simplest approach is to add a `.toImmutableList()` call in `DashboardRepositoryImpl` after receiving the `Either.Success` value.

---

## Passed (no action needed)

- **Rule 1 — Interface + Impl:** All three pairs present: `DashboardLocalDataSource`/`Impl`, `DashboardRemoteDataSource`/`Impl`, `DashboardRepository`/`Impl`.
- **Rule 2 — Either<T>:** Consistent use of `Either<DashboardData>` on all fallible operations. ViewModel correctly handles both branches.
- **Rule 3 — setState:** All 3 state updates use `_uiModelState.setState { copy(...) }`. No direct `.value =` assignments found.
- **Rule 4 — 4 UI States:** `DashboardScreenRoot` routes all four states: `Uninitialized` and `Loading` → `LoadingContent`; `Failed` → `ErrorContent`; `Success` → `DashboardContent`.
- **Rule 5 — X-components:** `XScaffold`, `XButton`, `XText`, `XIcon`, `XTextButton`, `XCircularProgressIndicator` used. Only `MaterialTheme.colorScheme.*` accessed from M3 (allowed — XTheme wraps MaterialTheme). No forbidden M3 component types imported.
- **Rule 7 — Lowercase packages:** All packages follow `thisissadeghi.dashboard.*` — fully lowercase.
- **Rule 8 — DI Pattern:** `DashboardModules : BaseFeature(...)` with `singleOf(::Impl).bind<Interface>()` for both data source and repository, `viewModelOf(::DashboardViewModel)`.
- **Rule 9 — No UseCases:** No UseCase class exists anywhere in the feature.
- **Rule 10 — Callback params:** `DashboardScreen` accepts `onActionClick: (String) -> Unit` and `onBackToDashboard: () -> Unit`. No `navController` parameter.
- **Integration 1 — settings.gradle.kts:** `include(":feature:dashboard")` at line 37.
- **Integration 2 — composeApp/build.gradle.kts:** `implementation(project(":feature:dashboard"))` at line 48.
- **Integration 3 — initKoin.kt:** `DashboardModules.initialize()` at line 27.
- **Integration 4 — BaseAppNavHost.kt:** `dashboard(onActionClick = {...}, onBackToDashboard = {...})` wired with correct `popBackStack` behavior.
- **UI File Organization:** `DashboardScreen.kt` is lean (Screen + ScreenRoot + state routing + state screens only). All 10 self-contained sections correctly live under `components/`.
- **Spec Compliance:** Data models, interfaces, state machine, navigation callbacks, and DI all match spec v3.3.0 exactly.
- **Design-Aware:** Blueprint consumed, implementation verified (2026-05-15), `xComponentsCompliant: true`.
