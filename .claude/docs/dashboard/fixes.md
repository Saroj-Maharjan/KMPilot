# Dashboard Feature - Required Fixes
**Date:** 2026-05-22
**Review status:** PASS WITH WARNINGS (0 Critical, 3 Warnings)

---

## Fix 1 (P2-1): Remove or register DashboardLocalDataSource dead pair

**File**: `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/data/datasource/DashboardLocalDataSource.kt`
**File**: `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/data/datasource/DashboardLocalDataSourceImpl.kt`
**File**: `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/di/DashboardModules.kt`

**Issue**: `DashboardLocalDataSource` and `DashboardLocalDataSourceImpl` exist but are neither referenced by `DashboardRepositoryImpl` nor registered in `DashboardModules`. The pair is dead code.

**Option A — Remove the dead pair (if local caching is not planned)**:
Delete `DashboardLocalDataSource.kt` and `DashboardLocalDataSourceImpl.kt`.

**Option B — Wire it up (if local caching is planned)**:

Current `DashboardModules.kt` (relevant section):
```kotlin
module {
    singleOf(::DashboardRemoteDataSourceImpl).bind<DashboardRemoteDataSource>()
    singleOf(::DashboardRepositoryImpl).bind<DashboardRepository>()
    viewModelOf(::DashboardViewModel)
}
```

Fixed (add the missing binding):
```kotlin
module {
    singleOf(::DashboardRemoteDataSourceImpl).bind<DashboardRemoteDataSource>()
    singleOf(::DashboardLocalDataSourceImpl).bind<DashboardLocalDataSource>()
    singleOf(::DashboardRepositoryImpl).bind<DashboardRepository>()
    viewModelOf(::DashboardViewModel)
}
```

Then inject `DashboardLocalDataSource` into `DashboardRepositoryImpl` and use it for caching/fallback.

---

## Fix 2 (P2-2): Rename ViewModel public flow from `uiModelState` to `uiModel`

**File**: `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/presentation/DashboardViewModel.kt`
**File**: `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/presentation/ui/DashboardScreen.kt`

**Issue**: Rule 11 convention specifies `val uiModel: StateFlow<{Feature}UiModel>`. The current name `uiModelState` also causes the Screen to use the parameter name `uiState` for what is actually a `DashboardUiModel` — creating misleading naming that conflicts with `UiState<T>`.

**Current code** in `DashboardViewModel.kt`:
```kotlin
private val _uiModelState = MutableStateFlow(DashboardUiModel())
val uiModelState = _uiModelState.asStateFlow()
```

**Fixed**:
```kotlin
private val _uiModel = MutableStateFlow(DashboardUiModel())
val uiModel = _uiModel.asStateFlow()
```

Also update all `_uiModelState.setState` calls to `_uiModel.setState`:
```kotlin
// line 24
_uiModel.setState { copy(dashboardState = UiState.Loading) }
// lines 28, 32
_uiModel.setState { copy(dashboardState = UiState.Success(result.data)) }
_uiModel.setState { copy(dashboardState = UiState.Failed(result.error)) }
```

**Current code** in `DashboardScreen.kt`:
```kotlin
// DashboardScreen (line 72)
val uiState by viewModel.uiModelState.collectAsStateWithLifecycle()
DashboardScreenRoot(
    uiState = uiState,
    ...
)

// DashboardScreenRoot signature (line 83-84)
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

Update all references to `uiState` inside `DashboardScreenRoot` body to `uiModel` (e.g. `uiState.dashboardState` → `uiModel.dashboardState`), and update preview calls (`uiState = ...` → `uiModel = ...`).

---

## Fix 3 (P2-3): Add `@Immutable` to DashboardData to satisfy Rule 6

**File**: `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/data/model/DashboardData.kt`

**Issue**: `DashboardData` contains 6 `List<T>` fields. Compose treats `List<T>` as unstable, causing full recomposition of all list-driven sections on every state emission. Rule 6 requires stable collections in state.

**Recommended fix — annotate with `@Immutable`** (preferred for a `@Serializable` DTO that is never mutated after deserialization):

**Current code** (`DashboardData.kt:5-16`):
```kotlin
@Serializable
data class DashboardData(
    val accountBalance: AccountBalance,
    val monthlySummary: MonthlySummary,
    val recentTransactions: List<Transaction>,
    val budgetCategories: List<BudgetCategory>,
    val savingsGoals: List<SavingsGoal>,
    val quickActions: List<QuickAction>,
    val upcomingBills: List<UpcomingBill>,
    val spendingInsight: SpendingInsight,
    val portfolioAssets: List<PortfolioAsset>,
)
```

**Fixed**:
```kotlin
import androidx.compose.runtime.Immutable

@Immutable
@Serializable
data class DashboardData(
    val accountBalance: AccountBalance,
    val monthlySummary: MonthlySummary,
    val recentTransactions: List<Transaction>,
    val budgetCategories: List<BudgetCategory>,
    val savingsGoals: List<SavingsGoal>,
    val quickActions: List<QuickAction>,
    val upcomingBills: List<UpcomingBill>,
    val spendingInsight: SpendingInsight,
    val portfolioAssets: List<PortfolioAsset>,
)
```

**Alternative fix — convert to ImmutableList at ViewModel** (if you want stronger runtime guarantees):

In `DashboardViewModel.kt` around line 28-30, after receiving `Either.Success`:
```kotlin
is Either.Success -> {
    val data = result.data
    val stableData = data.copy(
        recentTransactions = data.recentTransactions.toImmutableList(),
        budgetCategories = data.budgetCategories.toImmutableList(),
        savingsGoals = data.savingsGoals.toImmutableList(),
        quickActions = data.quickActions.toImmutableList(),
        upcomingBills = data.upcomingBills.toImmutableList(),
        portfolioAssets = data.portfolioAssets.toImmutableList(),
    )
    _uiModel.setState { copy(dashboardState = UiState.Success(stableData)) }
}
```

Note: For the ImmutableList alternative, you would also need to change the `List<T>` field types in `DashboardData` to `ImmutableList<T>` (from `kotlinx.collections.immutable`), which would require a custom `@Serializable` serializer or keeping the DTO with `List<T>` and using a separate presentation-layer stable model. The `@Immutable` annotation approach (option A) is simpler and idiomatic for read-only DTOs.

---

## Fix 4 (P2 / Pre-existing): Extract DashboardContent to components/

**File**: `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/presentation/ui/DashboardScreen.kt:117-141`
**Target**: `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/presentation/ui/components/DashboardContent.kt`

**Issue**: `DashboardContent` (the success-state content orchestrator) is inlined in `Screen.kt`. Under the updated strict Screen.kt allowlist, it belongs in `components/`.

**Action**: Move the private `DashboardContent` composable (lines 117-141 of `DashboardScreen.kt`) to a new file `components/DashboardContent.kt`, change its visibility from `private` to `internal` (or `public`), and update the call site in `DashboardScreenRoot` to reference it.

**New file** `components/DashboardContent.kt`:
```kotlin
package thisissadeghi.dashboard.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import thisissadeghi.dashboard.data.model.DashboardData

@Composable
internal fun DashboardContent(
    data: DashboardData,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        DashboardHeader()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item { BalanceCard(data.accountBalance) }
            item { QuickActionsSection(data.quickActions, onActionClick) }
            item { InsightBanner(data.spendingInsight) }
            item { MonthlySummaryCard(data.monthlySummary) }
            item { BudgetsSection(data.budgetCategories) }
            item { SavingsGoalsSection(data.savingsGoals) }
            item { UpcomingBillsCard(data.upcomingBills) }
            item { PortfolioSection(data.portfolioAssets) }
            item { RecentTransactionsSection(data.recentTransactions) }
        }
    }
}
```

Remove lines 115-141 from `DashboardScreen.kt` and delete the `// ─── Dashboard Content ───` section header.
