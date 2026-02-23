# Feature: Sample

## Metadata
| Field | Value |
|-------|-------|
| Version | 3.1.0 |
| Status | Approved |
| Author | System |
| Created | 2026-01-05 |
| Updated | 2026-02-19 |
| Design | `.claude/docs/sample/designs/sample_blueprint.md` |
| Reviewers | N/A |

---

## 1. Overview

### 1.1 Summary
A finance dashboard mockup that shows all key personal finance sections using hard-coded local data. Demonstrates the standard KMP Clean Architecture patterns while providing a realistic finance UI: total balance, income/expenses summary, recent transactions, budget progress, savings goals, quick actions, upcoming bills, spending insights, and portfolio snapshot.

### 1.2 Goals
- Demonstrate the 4-state UI pattern (Uninitialized, Loading, Success, Failed)
- Showcase Clean Architecture layer separation (data, presentation, DI)
- Provide reference implementation for state management with `setState {}`
- Serve as a realistic finance dashboard mockup for future feature development

### 1.3 Non-Goals
- Active API usage in repository (infrastructure ready, mock data used)
- Functional quick actions (stubs only — handlers to be added later)
- Production-ready feature (mockup only)

---

## 2. Context

### 2.1 Background
The sample feature was repurposed from a generic pattern demonstrator into a realistic finance dashboard mockup. It demonstrates the complete feature lifecycle from data layer to UI without requiring external dependencies, using a realistic finance domain to serve as a design reference and architecture example.

### 2.2 Dependencies
- `:core:common` - UiState, ErrorModel, setState extension
- `:core:designsystem` - X-components (XButton, XText, XCard, XIcon, etc.)
- Koin - Dependency injection framework
- Kotlinx Serialization - Route serialization + model serialization
- Compose Navigation - Screen navigation
- Compose Material Icons Extended - Dashboard icons

### 2.3 Constraints
- Must use mock data only (no real API)
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
│  │         SampleScreen                    │    │
│  │      (Collects StateFlow)               │    │
│  └──────────────────┬──────────────────────┘    │
│                     │                            │
│  ┌──────────────────▼──────────────────────┐    │
│  │       SampleScreenRoot                  │    │
│  │  (4-state rendering: U/L/S/F)           │    │
│  └──────────────────┬──────────────────────┘    │
│                     │                            │
│           ┌─────────▼─────────┐                  │
│           │   SampleViewModel │                  │
│           │ (MutableStateFlow)│                  │
│           └─────────┬─────────┘                  │
├─────────────────────┼───────────────────────────┤
│                     ▼      Domain Layer          │
│  ┌──────────────────────────────────────────┐   │
│  │         SampleRepository                 │   │
│  │   (Interface + Impl pair)                │   │
│  └──────────────────┬───────────────────────┘   │
├─────────────────────┼───────────────────────────┤
│                     ▼       Data Layer           │
│  ┌─────────────────────────────────────────┐    │
│  │    SampleLocalDataSource                │    │
│  │   (Interface + Impl with mock data)     │    │
│  └─────────────────────────────────────────┘    │
└─────────────────────────────────────────────────┘
```

### 4.2 Components

| Component | Responsibility |
|-----------|----------------|
| SampleLocalDataSource | Interface for local data operations |
| SampleLocalDataSourceImpl | Hard-coded finance dashboard data |
| SampleRemoteDataSource | Interface for remote API operations |
| SampleRemoteDataSourceImpl | API implementation using ApiClient |
| SampleResources | Ktor Resource definitions for /api/finance/dashboard endpoint |
| SampleRepository | Interface for business logic |
| SampleRepositoryImpl | Business logic implementation (uses local data) |
| SampleViewModel | State management using MutableStateFlow + setState |
| SampleUiModel | UI state container with 4-state pattern |
| SampleScreen | Main composable connecting ViewModel to UI |
| SampleScreenRoot | ViewModel-independent root for testing |
| BalanceSection | Total balance hero card with gold accent |
| MonthlySummarySection | Income vs. expenses bar comparison |
| QuickActionsSection | 4 stub action buttons in a row |
| SectionHeader | Reusable labelled divider for dashboard sections |
| TransactionRow | Single transaction row with category icon |
| BudgetRow | Budget category with progress bar (red if over budget) |
| SavingsGoalCard | Savings goal with progress bar and due date |
| BillRow | Upcoming bill row (red if overdue) |
| InsightBanner | Full-width spending insight card |
| PortfolioAssetRow | Asset with symbol circle, balance, and change % |
| SampleRoute | Navigation route (@Serializable data object) |
| SampleModules | DI configuration (BaseFeature object) |

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
feature/sample/src/commonMain/kotlin/thisissadeghi/sample/
├── data/
│   ├── model/
│   │   ├── DashboardData.kt         # All finance data models (@Serializable)
│   │   └── SampleItem.kt            # Empty — replaced by DashboardData.kt
│   ├── remote/
│   │   └── SampleResources.kt       # Ktor Resource for /api/finance/dashboard
│   ├── datasource/
│   │   ├── SampleLocalDataSource.kt        # Interface
│   │   ├── SampleLocalDataSourceImpl.kt    # Hard-coded finance data
│   │   ├── SampleRemoteDataSource.kt       # Interface (remote)
│   │   └── SampleRemoteDataSourceImpl.kt   # API implementation
│   └── repository/
│       ├── SampleRepository.kt             # Interface
│       └── SampleRepositoryImpl.kt         # Implementation (uses local)
├── presentation/
│   ├── SampleViewModel.kt           # MutableStateFlow + setState {}
│   ├── SampleUiModel.kt             # @Stable data class
│   ├── ui/
│   │   ├── SampleScreen.kt          # Full dashboard screen + all section composables
│   │   └── components/
│   │       └── SampleCard.kt        # Empty — components live in SampleScreen.kt
│   └── navigation/
│       └── SampleNavigation.kt      # @Serializable route + extension
└── di/
    └── SampleModules.kt             # BaseFeature object with Koin modules
```

### 4.5 UI Design

**Theme:** Dark (background `#0D0919`, surface `#181228`, primary purple `#9D70FF`)

**Color tokens (M3 roles):**
- Income/positive: `MaterialTheme.colorScheme.tertiary` (`#4ADE80`)
- Expense/negative: `XTheme.Colors.ExpenseRed` (`#FF6B6B`)
- All other colors via `MaterialTheme.colorScheme.*` — no hardcoded hex in feature code

**Dashboard Layout (LazyColumn) — section order:**
1. **BalanceCard** — "TOTAL NET WORTH" uppercase label; wallet watermark icon (alpha 0.10); large purple `$` amount (38sp); trend pill with tertiary green bg + icon; 3dp primary top strip; 24dp card corners
2. **QuickActionsSection** — centered Row with 24dp gaps; 56dp icon circles with 32dp corners + primary tint border
3. **InsightBanner** — tertiary-tinted card with lightbulb icon + smart insight message
4. **MonthlySummarySection** — section heading; single card with "Income Performance" (tertiary) and "Expenses Used" (ExpenseRed) bars; 6dp bar height
5. **BudgetsSection** — section heading + "View All"; cards with 4dp left-accent border (ExpenseRed if over, primary otherwise); icon circles; "OVER" badge or "On track" label
6. **SavingsGoalsSection** — section heading; cards with outlineVariant border; tertiary progress bars (8dp height); percentage label
7. **UpcomingBillsSection** — section heading; cards with 4dp left-accent; icon circles; "OVERDUE" in ExpenseRed (no day count)
8. **PortfolioSection** — section heading; single card wrapping all assets with XHorizontalDivider between rows; symbol circle with primary fill
9. **RecentTransactionsSection** — section heading + tune icon; single card with XHorizontalDivider; category • date sub-label

**Header:** Custom Column (not XTopAppBar) — "Good morning" subtitle in onSurfaceVariant + "Dashboard" bold title in onSurface. No navigation icons.

**All cards:** `RoundedCornerShape(24.dp)`. Progress bar track: `Color(0xFF1E293B)` (slate-800).

---

## 5. Interfaces

### 5.1 API Contracts

**Endpoint:** `GET /api/finance/dashboard`

**Description:** Fetches the full finance dashboard data.

**Authentication:** Not required (demonstration endpoint)

**Response:** `200 OK` → `DashboardData`

**Notes:**
- API infrastructure is implemented but not currently used by repository
- Repository uses hard-coded local data for the mockup

### 5.2 Internal Contracts

```kotlin
interface SampleLocalDataSource {
    suspend fun getDashboard(): DashboardData
}

interface SampleRemoteDataSource {
    suspend fun getDashboard(): Either<DashboardData>
}

interface SampleRepository {
    suspend fun getDashboard(): DashboardData
}
```

### 5.3 External Integrations

Navigation callbacks:
- `onActionClick: (String) -> Unit` — Called when user taps a quick action button, passes action ID

---

## 6. Behavior

### 6.1 User Flows

#### View Dashboard Flow
1. User navigates to Sample screen via `navController.navigate(SampleRoute)`
2. Screen enters Uninitialized → immediately triggers Loading
3. ViewModel calls `loadDashboard()` on init
4. State transitions to Loading (shows spinner)
5. Repository fetches mock data from local DataSource
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

### 6.2 State Management

```kotlin
data class SampleUiModel(
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

| Version | Date | Changes |
|---------|------|---------|
| 3.1.0 | 2026-02-19 | UI redesign via Stitch (ui-designer skill): purple brand palette (#9D70FF primary), M3 tertiary for income (#4ADE80), XTheme.Colors.ExpenseRed for expenses, 24dp card corners, custom header Column, reordered sections (Insight Banner after Quick Actions), Budget/Bills redesigned as cards with 4dp left-accent borders, Portfolio/Transactions wrapped in single cards with dividers, removed private color vals in favour of MaterialTheme.colorScheme roles. |
| 3.0.0 | 2026-02-19 | Major rewrite: replaced generic sample with finance dashboard mockup. New domain models (DashboardData + 9 sub-models), hard-coded local data, full dashboard UI with balance, summary, transactions, budget, savings goals, quick actions, bills, insights, portfolio. Navigation callback renamed to onActionClick. |
| 2.2.0 | 2026-02-10 | Premium UI redesign: SampleCard with crimson accent bar + ghost index number. |
| 2.1.0 | 2026-01-20 | Added API infrastructure (Ktor Resource, RemoteDataSource). |
| 2.0 | 2026-01-20 | Updated spec to match new SDD patterns. |
| 1.0 | 2026-01-05 | Initial spec. |

### D. Integration Points

| Point | File | Status |
|-------|------|--------|
| Module include | settings.gradle.kts | ✅ Completed |
| Dependency | composeApp/build.gradle.kts | ✅ Completed |
| DI initialization | initKoin.kt | ✅ Completed |
| Navigation wiring | BaseAppNavHost.kt | ✅ Updated (onActionClick) |
