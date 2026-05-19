# Feature: Dashboard

## Metadata
| Field | Value                                                   |
|-------|---------------------------------------------------------|
| Version | 3.3.0                                                   |
| Status | Approved                                                |
| Author | System                                                  |
| Created | 2026-01-05                                              |
| Updated | 2026-05-19                                              |
| Design | `.claude/docs/dashboard/designs/dashboard_blueprint.md` |
| Reviewers | N/A                                                     |

---

## 1. Overview

### 1.1 Summary
A finance dashboard that shows all key personal finance sections, fetching data from a GitHub Pages-hosted JSON mock via the remote data source. Demonstrates the standard KMP Clean Architecture patterns while providing a realistic finance UI: total balance, income/expenses summary, recent transactions, budget progress, savings goals, quick actions, upcoming bills, spending insights, and portfolio snapshot.

### 1.2 Goals
- Demonstrate the 4-state UI pattern (Uninitialized, Loading, Success, Failed)
- Showcase Clean Architecture layer separation (data, presentation, DI)
- Provide reference implementation for state management with `setState {}`
- Serve as a realistic finance dashboard mockup for future feature development

### 1.3 Non-Goals
- Live/production backend (GitHub Pages JSON mock is the data source)
- Functional quick actions (stubs only — handlers to be added later)
- Production-ready feature (mockup only)

---

## 2. Context

### 2.1 Background
The dashboard feature was repurposed from a generic pattern demonstrator into a realistic finance dashboard mockup. It demonstrates the complete feature lifecycle from data layer to UI without requiring external dependencies, using a realistic finance domain to serve as a design reference and architecture example.

### 2.2 Dependencies
- `:core:common` - UiState, ErrorModel, setState extension
- `:core:designsystem` - X-components (XButton, XText, XCard, XIcon, etc.)
- Koin - Dependency injection framework
- Kotlinx Serialization - Route serialization + model serialization
- Compose Navigation - Screen navigation
- Compose Material Icons Extended - Dashboard icons

### 2.3 Constraints
- Data is served from GitHub Pages mock; local data source is retained but unused in production DI
- Must demonstrate all critical patterns
- Must build successfully on both Android and iOS
- Must use X-components exclusively (no Material3 components directly)
- Quick actions are stubs (render but take no real action)

---

## 3. Requirements

### 3.1 Functional Requirements

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-1 | Display total balance card with currency, amount, and change percentage | Must |
| FR-2 | Show monthly income vs. expenses summary with visual bars | Must |
| FR-3 | Display last 5 recent transactions with category icon and amount | Must |
| FR-4 | Show budget progress for 4 categories with percentage-filled bar | Must |
| FR-5 | Display 2 savings goals with progress bars and target dates | Must |
| FR-6 | Show 4 quick action buttons: Send, Receive, Pay, Top Up (stubs) | Must |
| FR-7 | Display upcoming bills list (3 items) | Must |
| FR-8 | Show spending insight banner | Must |
| FR-9 | Display portfolio/currency snapshot (3 assets) | Must |
| FR-10 | Show loading state while data is fetching | Must |
| FR-11 | Handle error state with retry | Must |

### 3.2 Non-Functional Requirements

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-1 | State transitions | Explicit setState {} usage only |
| NFR-2 | Architecture compliance | 100% adherence to 10 critical rules |
| NFR-3 | Design system usage | X-components only, zero direct Material3 |
| NFR-4 | Code quality | ktlint passing, no warnings |

---

## 4. Design

### 4.1 Architecture

```
┌─────────────────────────────────────────────────┐
│                  UI Layer                        │
│  ┌─────────────────────────────────────────┐    │
│  │         DashboardScreen                    │    │
│  │      (Collects StateFlow)               │    │
│  └──────────────────┬──────────────────────┘    │
│                     │                            │
│  ┌──────────────────▼──────────────────────┐    │
│  │       DashboardScreenRoot                  │    │
│  │  (4-state rendering: U/L/S/F)           │    │
│  └──────────────────┬──────────────────────┘    │
│                     │                            │
│           ┌─────────▼─────────┐                  │
│           │   DashboardViewModel │                  │
│           │ (MutableStateFlow)│                  │
│           └─────────┬─────────┘                  │
├─────────────────────┼───────────────────────────┤
│                     ▼      Domain Layer          │
│  ┌──────────────────────────────────────────┐   │
│  │         DashboardRepository                 │   │
│  │   (Interface + Impl pair)                │   │
│  └──────────────────┬───────────────────────┘   │
├─────────────────────┼───────────────────────────┤
│                     ▼       Data Layer           │
│  ┌─────────────────────────────────────────┐    │
│  │    DashboardLocalDataSource                │    │
│  │   (Interface + Impl with mock data)     │    │
│  └─────────────────────────────────────────┘    │
└─────────────────────────────────────────────────┘
```

### 4.2 Components

| Component | Responsibility |
|-----------|----------------|
| DashboardLocalDataSource | Interface for local data operations |
| DashboardLocalDataSourceImpl | Hard-coded finance dashboard data |
| DashboardRemoteDataSource | Interface for remote API operations |
| DashboardRemoteDataSourceImpl | API implementation using ApiClient |
| DashboardResources | Ktor Resource definitions for /api/finance/dashboard.json endpoint |
| DashboardRepository | Interface for business logic |
| DashboardRepositoryImpl | Business logic implementation (delegates to remote data source; returns Either<DashboardData>) |
| DashboardViewModel | State management using MutableStateFlow + setState |
| DashboardUiModel | UI state container with 4-state pattern |
| DashboardScreen | Main composable connecting ViewModel to UI |
| DashboardScreenRoot | ViewModel-independent root for testing |
| BalanceCard | Total balance hero card — Box+border, no XCard, full AccountBalance |
| QuickActionsSection | 4 stub action buttons — CircleShape circles, primaryContainer bg |
| InsightBanner | Raw Row — surface bg, outlineVariant border, primary icon tint |
| MonthlySummaryCard | Single combined split-bar, INCOME/EXPENSES side-by-side |
| BudgetsSection | 2-column grid; plain cards with 6dp progress bar, no icon circles |
| SavingsGoalsSection | Cards with 20dp padding; icon derived from goal name; success progress bars |
| UpcomingBillsCard | Single card + XHorizontalDivider; OVERDUE inline badge |
| PortfolioSection | 3-column grid; each asset its own card (symbol + change %) |
| RecentTransactionsSection | Each transaction its OWN card (no dividers); CircleShape icon circles |
| DashboardHeader | Sticky Box — "Good morning," + "Dashboard" 20sp Bold |
| DashboardRoute | Navigation route (@Serializable data object) |
| DashboardModules | DI configuration (BaseFeature object) |

### 4.3 Data Models

```kotlin
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

data class AccountBalance(val totalBalance: Double, val currency: String,
    val changePercent: Double, val changeAmount: Double)

data class MonthlySummary(val monthName: String, val income: Double,
    val expenses: Double, val currency: String)

data class Transaction(val id: String, val title: String, val category: String,
    val amount: Double, val isIncome: Boolean, val date: String, val currency: String)

data class BudgetCategory(val name: String, val spent: Double, val total: Double,
    val currency: String)  // computed: progress, isOverBudget

data class SavingsGoal(val name: String, val current: Double, val target: Double,
    val currency: String, val dueDate: String)  // computed: progress

data class QuickAction(val id: String, val label: String, val iconName: String)

data class UpcomingBill(val id: String, val name: String, val amount: Double,
    val dueDate: String, val currency: String, val isOverdue: Boolean)

data class SpendingInsight(val message: String, val percentageChange: Double,
    val isPositive: Boolean)

data class PortfolioAsset(val id: String, val name: String, val symbol: String,
    val balance: Double, val value: Double, val changePercent: Double, val currency: String)
```

### 4.4 Package Structure

```
feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/
├── data/
│   ├── model/
│   │   ├── DashboardData.kt         # All finance data models (@Serializable)
│   │   └── DashboardItem.kt            # Empty — replaced by DashboardData.kt
│   ├── remote/
│   │   └── DashboardResources.kt       # Ktor Resource for /api/finance/dashboard.json
│   ├── datasource/
│   │   ├── DashboardLocalDataSource.kt        # Interface
│   │   ├── DashboardLocalDataSourceImpl.kt    # Hard-coded finance data
│   │   ├── DashboardRemoteDataSource.kt       # Interface (remote)
│   │   └── DashboardRemoteDataSourceImpl.kt   # API implementation
│   └── repository/
│       ├── DashboardRepository.kt             # Interface
│       └── DashboardRepositoryImpl.kt         # Implementation (uses local)
├── presentation/
│   ├── DashboardViewModel.kt           # MutableStateFlow + setState {}
│   ├── DashboardUiModel.kt             # @Stable data class
│   ├── ui/
│   │   ├── DashboardScreen.kt          # Screen + ScreenRoot + state routing
│   │   └── components/
│   │       ├── BalanceCard.kt
│   │       ├── QuickActionsSection.kt
│   │       ├── InsightBanner.kt
│   │       ├── MonthlySummaryCard.kt
│   │       ├── BudgetsSection.kt
│   │       ├── SavingsGoalsSection.kt
│   │       ├── UpcomingBillsCard.kt
│   │       ├── PortfolioSection.kt
│   │       ├── RecentTransactionsSection.kt
│   │       └── DashboardHeader.kt
│   └── navigation/
│       └── DashboardNavigation.kt      # @Serializable route + extension
└── di/
    └── DashboardModules.kt             # BaseFeature object with Koin modules
```

### 4.5 UI Design

**Theme:** Dark gold/champagne on warm obsidian — background `#0F0D09`, surface `#1C1910`, primary gold `#F5D76E`

**Color tokens (M3 roles):**
- All M3 roles via `MaterialTheme.colorScheme.*` — no hardcoded `Color()` hex in feature code
- Income/positive: `XTheme.Colors.Success` (`#4ADE80`)
- Expense/negative/overdue: `XTheme.Colors.Danger` (`#FF6B6B`)

**Dashboard Layout (`XScaffold` + Column, LazyColumn `contentPadding = start/end 24dp, bottom 48dp`) — section order:**
1. **BalanceCard** — Box+border+clip (not XCard); gold 3dp top strip; 36sp ExtraBold amount; trend pill CircleShape (no border); "vs last month" text
2. **QuickActionsSection** — `spacedBy(16dp)` Row, `weight(1f)` columns; `CircleShape` circles; `primaryContainer.copy(0.3f)` bg + `outlineVariant` border
3. **InsightBanner** — `surface` bg + `outlineVariant` border; `primary` icon bg `primary.copy(0.1f)` + `RoundedCornerShape(20dp)`; 20dp padding
4. **MonthlySummaryCard** — section heading "Monthly Summary"; INCOME/EXPENSES side-by-side 24sp; single combined split bar 12dp
5. **BudgetsSection** — section heading "Monthly Budgets"; 2-column chunked grid; plain cards with 6dp progress bar; no icon circles, no accent strips
6. **SavingsGoalsSection** — section heading; cards 20dp padding; icon derived from goal name; `XTheme.Colors.Success` progress bars (8dp)
7. **UpcomingBillsCard** — section heading; single card + `XHorizontalDivider(outlineVariant.copy(0.3f))`; icon circles `surfaceVariant + RoundedCornerShape(20dp)`; inline OVERDUE badge
8. **PortfolioSection** — section heading "Portfolio"; 3-column chunked grid; each asset its own card (32dp circle + symbol + change%)
9. **RecentTransactionsSection** — section heading; each transaction its OWN card (no dividers); `CircleShape` icon circles; income bg `Success.copy(0.1f)`, expense bg `surfaceVariant`

**Header:** Sticky `Box` OUTSIDE `LazyColumn` — "Good morning," subtitle 14sp Medium `onSurfaceVariant`; "Dashboard" title 20sp Bold `onSurface` letterSpacing (-0.5).sp; `background(background)`

**All cards:** `RoundedCornerShape(24.dp)` + `border(1.dp, outline/outlineVariant)` via `Modifier.background(surface, ...)` — **no XCard**

**Progress bar tracks:** `MaterialTheme.colorScheme.surfaceVariant` — no hardcoded Color()

**Loading state:** Layered loader (centered) — 96dp decorative outline ring (1dp `outlineVariant.copy(alpha = 0.3f)`, CircleShape) wrapping a 64dp track (4dp `surfaceVariant` border, CircleShape), a 4dp `primary.copy(alpha = 0.4f)` indeterminate arc, and a central 8dp `primary` dot.

**Failed state:** Warning icon backed by an 80dp `error.copy(alpha = 0.1f)` circular glow; "Something went wrong" title 20sp SemiBold with `letterSpacing = (-0.5).sp` and `lineHeight = 32.5.sp`; primary `Retry` `XButton` followed by a secondary `XTextButton("Return to Dashboard", color = onSurfaceVariant, 14sp Medium)`.

---

## 5. Interfaces

### 5.1 API Contracts

**Endpoint:** `GET /api/finance/dashboard.json`

**Description:** Fetches the full finance dashboard data.

**Authentication:** Not required

**Response:** `200 OK` → `DashboardData`

**Notes:**
- Served from `https://thisissadeghi.github.io/KMPilot/mock-api/`
- File lives at `mock-api/api/finance/dashboard.json` in this repo

### 5.2 Internal Contracts

```kotlin
interface DashboardLocalDataSource {
    suspend fun getDashboard(): DashboardData
}

interface DashboardRemoteDataSource {
    suspend fun getDashboard(): Either<DashboardData>
}

interface DashboardRepository {
    suspend fun getDashboard(): Either<DashboardData>
}
```

### 5.3 External Integrations

Navigation callbacks:
- `onActionClick: (String) -> Unit` — Called when user taps a quick action button, passes action ID
- `onBackToDashboard: () -> Unit` — Called from the Failed state "Return to Dashboard" secondary action; host pops navigation back to the dashboard start destination

---

## 6. Behavior

### 6.1 User Flows

#### View Dashboard Flow
1. User navigates to Dashboard screen via `navController.navigate(DashboardRoute)`
2. Screen enters Uninitialized → immediately triggers Loading
3. ViewModel calls `loadDashboard()` on init
4. State transitions to Loading (shows spinner)
5. Repository calls RemoteDataSource → ApiClient fetches from GitHub Pages JSON
6. State transitions to Success — full dashboard renders
7. On error: State transitions to Failed with retry button

#### Quick Action Flow
1. User taps any quick action button (Send, Receive, Pay, Top Up)
2. `onActionClick(actionId)` callback is invoked with the action's ID
3. No further navigation or behaviour (stub — to be implemented later)

#### Retry Flow
1. User is in Failed state
2. User taps "Try Again"
3. ViewModel calls `retry()` → `loadDashboard()`
4. State transitions back to Loading

#### Return to Dashboard Flow
1. User is in Failed state with Retry + "Return to Dashboard" buttons visible
2. User taps "Return to Dashboard"
3. `onBackToDashboard()` callback is invoked
4. Host pops navigation back to the dashboard start destination

### 6.2 State Management

```kotlin
data class DashboardUiModel(
    val dashboardState: UiState<DashboardData> = UiState.Uninitialized,
)
```

**State Transitions:**
```
Uninitialized ──[loadDashboard()]──► Loading
                                         │
                         ┌───────────────┴───────────────┐
                         │                               │
                  [success]                          [failure]
                         │                               │
                         ▼                               ▼
                      Success                         Failed
                                                         │
                                                  [retry]─┘
```

### 6.3 Error Handling

| Error Scenario | User Message | Action Available |
|----------------|--------------|------------------|
| Generic exception | Error message in premium card overlay | "Try Again" XButton |

---

## 7. Testing

### 7.1 Test Scenarios

| Scenario | Given | When | Then |
|----------|-------|------|------|
| Dashboard loads | User navigates to screen | Data loads from mock source | Full dashboard visible, state = Success |
| Dashboard fails | Mock source throws exception | Data load attempted | Error overlay with retry button shown |
| Retry after error | Error state displayed | User taps retry | Loading state shown, data reloads |
| Quick action tap | Dashboard displayed | User taps any action button | onActionClick called with action ID |

### 7.2 Acceptance Criteria

**Display & States:**
- [ ] Loading indicator shown during data fetch
- [ ] Success state renders all 9 dashboard sections
- [ ] Failed state shows error message and retry button

**Dashboard Sections:**
- [ ] Balance card shows total, currency chip, and trend indicator
- [ ] Monthly summary shows income/expense bars for current month
- [ ] 5 recent transactions visible with category icons and amounts
- [ ] 4 budget categories with progress bars (Shopping shows OVER badge)
- [ ] 2 savings goals with green progress bars
- [ ] 4 quick action buttons rendered as surface cards
- [ ] 3 upcoming bills (Internet shows OVERDUE in red)
- [ ] Spending insight banner renders with lightbulb icon
- [ ] 3 portfolio assets with symbol circles and change percentages

**Architecture:**
- [ ] All state updates use `setState {}`
- [ ] X-components used exclusively
- [ ] ktlint passes without errors

---

## Appendix

### A. Glossary

| Term | Definition |
|------|------------|
| 4-state pattern | UI state management with Uninitialized, Loading, Success, Failed states |
| setState {} | Extension function for safe state updates in ViewModel |
| DashboardData | Top-level model containing all finance dashboard sections |
| Quick action stub | UI element that renders but performs no real operation (placeholder) |
| X-components | Custom design system components (`:core:designsystem`) |

### B. References

- Core common library: `core/common/`
- Design system: `core/designsystem/`
- Architecture patterns: `.claude/skills/creating-kmp-feature/references/patterns.md`

### C. Changelog

| Version | Date | Changes                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|---------|------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 3.3.0 | 2026-05-19 | Switched to remote data source: `DashboardRepositoryImpl` now delegates to `DashboardRemoteDataSource` (returns `Either<DashboardData>`); `DashboardRepository` interface return type updated to `Either<DashboardData>`; `DashboardViewModel` replaced try/catch with `Either` pattern; `LocalDataSource` removed from DI (files retained); `DashboardResources` path updated to `/api/finance/dashboard.json`; `BASE_URL` pointed at GitHub Pages mock (`https://thisissadeghi.github.io/KMPilot/mock-api/`). |
| 3.2.1 | 2026-05-15 | UI fidelity fixes from `dashboard_audit.md`: removed extra "Monthly Summary" heading + outer Column wrapper in `MonthlySummaryCard`; fixed Portfolio icon→symbol spacing (0dp → 4dp); dropped "• {date}" suffix from `RecentTransactionsSection` category line; rebuilt layered Loading state (96dp outline ring + 64dp surface-variant track + 4dp primary arc + 8dp central dot); added "Return to Dashboard" secondary `XTextButton` + new `onBackToDashboard: () -> Unit` callback threaded through `DashboardScreen`, `DashboardScreenRoot`, the `dashboard()` nav extension, and `BaseAppNavHost` (pops to dashboard); added `letterSpacing = (-0.5).sp` + `lineHeight = 32.5.sp` to "Something went wrong" title; removed 32dp spacer between subtitle and Retry button; added 80dp `error.copy(alpha = 0.1f)` decorative glow behind the warning icon. |
| 3.2.0 | 2026-05-11 | Blueprint implementation (ui-designer skill): gold/champagne theme (#F5D76E primary), component renames (BalanceCard, QuickActionsSection, InsightBanner, MonthlySummaryCard, UpcomingBillsCard), XScaffold replacing manual Column, 2-col budget grid, 3-col portfolio grid, single split-bar monthly summary (12dp), individual transaction cards, single bills card with dividers, XTheme.Colors.Success/Danger replacing obsolete ExpenseRed/tertiary usage, DashboardHeader sticky Box with background.                                                                                                                                                                                                                                                                                                                                                   |
| 3.1.0 | 2026-02-19 | UI redesign via Stitch (ui-designer skill): purple brand palette (#9D70FF primary), M3 tertiary for income (#4ADE80), XTheme.Colors.ExpenseRed for expenses, 24dp card corners, custom header Column, reordered sections (Insight Banner after Quick Actions), Budget/Bills redesigned as cards with 4dp left-accent borders, Portfolio/Transactions wrapped in single cards with dividers, removed private color vals in favour of MaterialTheme.colorScheme roles.                                                                                                                                                                                                                                                                                                                                                                                           |
| 3.0.0 | 2026-02-19 | Major rewrite: replaced generic dashboard with finance dashboard mockup. New domain models (DashboardData + 9 sub-models), hard-coded local data, full dashboard UI with balance, summary, transactions, budget, savings goals, quick actions, bills, insights, portfolio. Navigation callback renamed to onActionClick.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| 2.2.0 | 2026-02-10 | Premium UI redesign: DashboardCard with crimson accent bar + ghost index number.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| 2.1.0 | 2026-01-20 | Added API infrastructure (Ktor Resource, RemoteDataSource).                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| 2.0 | 2026-01-20 | Updated spec to match new SDD patterns.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| 1.0 | 2026-01-05 | Initial spec.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |

### D. Integration Points

| Point | File | Status |
|-------|------|--------|
| Module include | settings.gradle.kts | ✅ Completed |
| Dependency | composeApp/build.gradle.kts | ✅ Completed |
| DI initialization | initKoin.kt | ✅ Completed |
| Navigation wiring | BaseAppNavHost.kt | ✅ Updated (onActionClick) |
