# Asset Detail Specification

## Metadata
| Field | Value |
|-------|-------|
| Version | 1.0.1 |
| Status | Active |
| Created | 2026-06-10 |
| Updated | 2026-06-10 |

## Purpose
Displays a full breakdown of a single crypto asset — live price, historical price chart with selectable periods, key market stats, user holdings, recent activity, and a top-holders community widget. A sticky Buy/Sell footer opens a trade bottom sheet for purchasing. Navigated to as a pushed screen from the Dashboard portfolio list.

## Goals
- Display live price, 24-hour change badge, and key market stats for a single crypto asset
- Render a scrollable price-history chart with selectable time periods (1D/1W/1M/1Y/All)
- Provide a Buy trade flow via a `ModalBottomSheet` (amount input, slider, quick-amount chips, pay-with dropdown, confirm)
- Show the user's current holdings and recent activity for the asset

## Non-Goals
- Sell flow implementation (UI placeholder only — Sell button shown but not actionable beyond UI)
- Real payment processing or wallet deduction
- Live WebSocket price streaming (static fetch on load + time-period change)
- Full transaction history pagination (shows most-recent 3 rows only)

## Background & Rationale
The Dashboard already surfaces a portfolio card per asset. Users tapping an asset need a detail view with enough context to make buy decisions. The Buy bottom sheet is the first monetisation touchpoint in the app.

## Platform Profile & Capabilities
| Field | Value |
|-------|-------|
| Platform Profile | `network` |
| Capabilities | none |
| Native view | No |
| Sourcing option | n/a |
| iOS-Swift bridge | No |

## Design Decisions
| Decision | Choice | Alternatives | Rationale |
|----------|--------|--------------|-----------|
| Chart rendering | Custom `Canvas`-based composable | Third-party chart lib | No external dep; design requires specific gradient-area fill and glow |
| Avatar stack | Manual `Box` with negative offset | `LazyRow` | Fixed count (max 5 visible + "+N"); simpler layout |
| Buy flow | `ModalBottomSheet` | Separate screen | Design specifies overlay sheet; keeps context visible |
| Price history granularity | Single API call per period selection | Paginated streaming | Simpler mock; fits the API contract established by dashboard |
| Font | Manrope variable TTF (global swap from Outfit) | Keep Outfit | Design specifies Manrope; global font swap in `XTheme.kt` — not per-feature |
| Local mock for GETs | `AssetDetailLocalDataSourceImpl` returns hardcoded data | Live API | API endpoints not yet available; mock keeps build green; routed via repository |
| Price counter animation | `animateFloatAsState` on float value | Static text | Design-confirmed Value-driven motion family |

## Last Updated
- 2026-06-10 — Generated from implementation
- 2026-06-10 — Fix iOS build: `kotlin.text.String.format` ("%.Nf" patterns, JVM-only, unresolved on Kotlin/Native) replaced with `:core:common` `Double.formatDecimals(decimals: Int)` / `Float.formatDecimals(decimals: Int)` extensions (`thisissadeghi.common.ext`) at all 14 call sites: PriceChart.kt, AssetDetailViewModel.kt, HeroSection.kt (x2, incl. new `Float` overload for `displayPrice`), ActivitySection.kt (x2), StatsGrid.kt (x6), BuyBottomSheet.kt. Same fix as swap (1.0.0→1.0.1).

## Requirements

### Requirement: Asset Detail Display
The system SHALL fetch and display asset detail (price, change, market cap, volume, circulating supply, holdings) when the screen is opened with a valid assetId.

#### Scenario: Asset detail loads successfully
- GIVEN the user navigates to `AssetDetailScreen` with `assetId = "bitcoin"`
- WHEN all data loads successfully
- THEN the hero section MUST show coin name, price, and % change badge
- AND the chart MUST render with data points
- AND stats cards MUST show Market Cap, 24h Volume, Circulating Supply, and Your Holdings
- AND entrance animation MUST stagger sections top-to-bottom (unless reduced motion is enabled)

#### Scenario: Asset detail handles errors
- GIVEN the user navigates to `AssetDetailScreen`
- WHEN a network error occurs
- THEN `AppErrorState` MUST be displayed with title, message, and retry button
- AND tapping retry MUST reload all data

### Requirement: Time Period Chart Selection
The system SHALL reload the price chart when the user selects a different time period.

#### Scenario: Time period selection reloads chart
- GIVEN the asset detail is in Success state
- WHEN the user taps a different time-period chip (e.g. "1M")
- THEN `selectedPeriod` MUST update to "1M"
- AND `priceHistoryState` MUST transition Loading → Success with new data points

### Requirement: Buy Trade Flow
The system SHALL allow the user to submit a buy order via a bottom sheet.

#### Scenario: Buy order submitted successfully
- GIVEN the buy sheet is open with a valid amount entered
- WHEN the user taps "Confirm Purchase"
- THEN a POST request MUST be sent with the entered amount
- AND on success `buyState = Success` and the sheet MUST close
- AND on failure `buyState = Failed` with an in-sheet error message

#### Scenario: Reduced motion respected
- GIVEN the OS accessibility setting "Reduce Motion" is enabled
- WHEN the screen loads
- THEN no entrance animations MUST play (immediate final state)
- AND the price counter MUST show the final value without counting up

## Architecture

### Package Structure
```
feature/assetdetail/src/commonMain/kotlin/thisissadeghi/assetdetail/
├── data/
│   ├── model/           AssetDetailData.kt (9 @Serializable DTOs)
│   ├── remote/          AssetDetailResources.kt (Ktor Resources)
│   ├── datasource/      AssetDetailLocalDataSource / Impl (mock GET responses)
│   │                    AssetDetailRemoteDataSource / Impl (POST buy)
│   └── repository/      AssetDetailRepository / Impl
├── presentation/
│   ├── AssetDetailViewModel.kt
│   ├── AssetDetailUiModel.kt
│   ├── navigation/      AssetDetailNavigation.kt (AssetDetailRoute, assetdetail ext)
│   └── ui/
│       ├── AssetDetailScreen.kt
│       ├── components/  (12 component files — see Component Tree)
│       └── motion/      AssetDetailMotion.kt
└── di/
    └── AssetDetailModules.kt
```

### Data Flow
```
API → Ktor Resources → DataSource → Repository → ViewModel → UI
        Either<Error,DTO>  Either<DTO>  UiState<DTO> (inside AssetDetailUiModel)
```
GET endpoints are routed through `AssetDetailLocalDataSourceImpl` (mock data) until the API is available. POST buy is routed through `AssetDetailRemoteDataSourceImpl` (live Ktor). Repository decides routing; ViewModel never sees platform types. DTOs flow unchanged — no presentation-layer mirror types (Rule 11).

### Data Models (9 DTOs in `data/model/AssetDetailData.kt`)
```kotlin
@Serializable data class AssetDetailResponse(
    val id: String, val name: String, val symbol: String,
    val price: Double, val changePercent24h: Double,
    val marketCap: Double, val volume24h: Double,
    val circulatingSupply: Double,
    val holdingAmount: Double, val holdingFiatValue: Double,
    val currency: String,
)

@Serializable data class PricePoint(val timestamp: String, val price: Double)

@Serializable data class PriceHistoryResponse(
    val assetId: String, val period: String, val dataPoints: List<PricePoint>,
)

@Serializable data class AssetTransaction(
    val id: String, val type: String, val title: String,
    val timestamp: String, val amount: Double,
    val fiatValue: Double, val currency: String,
)

@Serializable data class ActivityResponse(
    val assetId: String, val transactions: List<AssetTransaction>,
)

@Serializable data class HolderAvatar(
    val id: String, val initials: String, val colorHex: String,
)

@Serializable data class TopHoldersResponse(
    val assetId: String, val holders: List<HolderAvatar>, val additionalCount: Int,
)

@Serializable data class BuyOrderRequest(
    val assetId: String, val amount: Double, val currency: String,
)

@Serializable data class BuyOrderResponse(val orderId: String, val status: String)
```

### Key Classes
| Class | Purpose | Location |
|-------|---------|----------|
| `AssetDetailLocalDataSource` / Impl | Mock GET responses for detail, history, activity, holders | `data/datasource/` |
| `AssetDetailRemoteDataSource` / Impl | Live Ktor POST buy order | `data/datasource/` |
| `AssetDetailRepository` / Impl | Routes GET → local, POST → remote; returns `Either<DTO>` | `data/repository/` |
| `AssetDetailViewModel` | Loads all data on init; manages period selection, buy sheet, buy submission | `presentation/` |
| `AssetDetailScreen` / `ScreenRoot` | ViewModel wrapper + state router; `XScreen` with topBar + sticky bottomBar | `presentation/ui/` |
| `AssetDetailContent` | Full scrollable success content | `presentation/ui/components/` |
| `AssetDetailMotion` | `animateFloatAsState` price counter; `animateColorAsState` badge color | `presentation/ui/motion/` |

### API Endpoints
| Method | Path | Handled by |
|--------|------|-----------|
| GET | `/crypto/assets/{id}` | `AssetDetailLocalDataSourceImpl` (mock) |
| GET | `/crypto/assets/{id}/price-history?period={period}` | `AssetDetailLocalDataSourceImpl` (mock) |
| GET | `/crypto/assets/{id}/activity` | `AssetDetailLocalDataSourceImpl` (mock) |
| GET | `/crypto/assets/{id}/holders` | `AssetDetailLocalDataSourceImpl` (mock) |
| POST | `/crypto/assets/{id}/buy` | `AssetDetailRemoteDataSourceImpl` (live Ktor) |

## Integration Points

| # | Point | File | Status |
|---|-------|------|--------|
| 1 | Module include | `settings.gradle.kts` | ✅ (added by data-layer) |
| 2 | Gradle dependency | `composeApp/build.gradle.kts` | ✅ |
| 3 | DI initialization | `composeApp/.../initKoin.kt` | ✅ |
| 4 | Navigation | `composeApp/.../BaseAppNavHost.kt` | ✅ |
| 5 | Bottom-bar tab | N/A (pushed screen) | N/A |

## State Management

### UiModel Structure
```kotlin
data class AssetDetailUiModel(
    val assetId: String = "",
    val selectedPeriod: String = "1D",
    val buyAmountInput: String = "",
    val buySliderValue: Float = 0f,
    val isBuySheetVisible: Boolean = false,
    val detailState: UiState<AssetDetailResponse> = UiState.Uninitialized,
    val priceHistoryState: UiState<PriceHistoryResponse> = UiState.Uninitialized,
    val activityState: UiState<ActivityResponse> = UiState.Uninitialized,
    val holdersState: UiState<TopHoldersResponse> = UiState.Uninitialized,
    val buyState: UiState<BuyOrderResponse> = UiState.Uninitialized,
)
```

### State Transitions
- On init: all 4 load operations launch concurrently in `viewModelScope`
- `detailState`, `priceHistoryState`, `activityState`, `holdersState`: Uninitialized → Loading → Success | Failed
- `buyState`: Uninitialized → Loading → Success (sheet closes) | Failed (in-sheet error)
- `selectedPeriod` change triggers `priceHistoryState` reload
- `isBuySheetVisible` toggled by `showBuySheet()` / `hideBuySheet()`
- All state mutations via `_uiModel.setState { copy(...) }` (Rule 3)

### ViewModel Actions
| Action | Description |
|--------|-------------|
| `loadAll()` | Launches all 4 load coroutines concurrently (called on init + retry) |
| `selectPeriod(period)` | Updates `selectedPeriod` and reloads price history |
| `updateBuyAmount(input)` | Syncs amount text field → slider value |
| `updateBuySlider(value)` | Syncs slider → formatted amount text field |
| `selectQuickAmount(percent)` | Delegates to `updateBuySlider` |
| `showBuySheet()` / `hideBuySheet()` | Toggles `isBuySheetVisible` |
| `confirmBuy()` | Submits POST buy order; closes sheet on success |
| `retry()` | Resets all UiStates to Uninitialized and calls `loadAll()` |

## UI Layer

### Screen Structure
`AssetDetailScreen` (ViewModel wrapper) → `AssetDetailScreenRoot` (state router using `XScreen` — no nested Scaffold per Rule 13)

- `topBar` = `XTopAppBar` with back arrow
- `bottomBar` = sticky Buy + Sell buttons (uses `Modifier.windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime))` per Rule 13 Case A)
- Content: routes on `detailState` — Uninitialized/Loading → `AppLoadingState()`, Failed → `AppErrorState(...)`, Success → `AssetDetailContent`

### Component Tree
```
AssetDetailContent
├── HeroSection         — coin circle, name+ticker, animated price, % change badge
├── TimePeriodSelector  — row of 1D/1W/1M/1Y/All chips
├── PriceChart          — Canvas chart with gradient fill, Y/X labels
├── StatsGrid           — 2×2 LazyVerticalGrid
│   └── StatsCard       — label, value, optional primary border (holdings card)
├── ActivitySection
│   └── ActivityRow     — icon circle, title, timestamp, signed amount
└── TopHoldersSection
    ├── AvatarStack     — overlapping circles with "+N" overflow
    └── Join Group button

BuyBottomSheet (shown when isBuySheetVisible = true)
├── Amount input field
├── Slider (XSlider)
├── QuickAmountChips    — 25%/50%/75%/Max row
├── Pay-with dropdown (XExposedDropdownMenuBox)
└── Confirm Purchase button
```

## Motion

### Motion Families
| Family | Implementation | Location |
|--------|---------------|----------|
| Loading shimmer | `Modifier.shimmer()` from `:core:designsystem` | `AssetDetailContent` (applied during priceHistoryState.Loading) |
| Staggered entrance | `RevealOnAppear` from `:core:designsystem` | Each section in `AssetDetailContent` |
| Price counter | `animateFloatAsState` on price value | `AssetDetailMotion.kt` |
| Badge color | `animateColorAsState` on change-percent sign | `AssetDetailMotion.kt` |

All motion families are gated by `rememberReducedMotion()` — when the OS "Reduce Motion" accessibility setting is enabled, animations are skipped and the final state renders immediately. Interaction press/hover effects are dropped (primary targets are Android + iOS). `prefers-reduced-motion` is an `expect/actual` reading the OS setting.

## Navigation

- **Route:** `AssetDetailRoute(assetId: String)` — `@Serializable data class`
- **Entry:** `navController.navigate(AssetDetailRoute(assetId = "bitcoin"))`
- **Exit:** `onBackClick` callback → `navController.popBackStack()`
- **ViewModel injection:** `koinViewModel<AssetDetailViewModel>(parameters = { parametersOf(route.assetId) })` — `assetId` passed via Koin `parametersOf` at the call site in `AssetDetailNavigation.kt`
- **Top-level tab:** No — pushed screen only

## Dashboard Wiring Chain

The `onAssetClick` callback flows through the dashboard feature chain without any cross-feature import:

```
Portfolio.kt
  PortfolioItem(onClick = { onAssetClick(asset.id) })
  ↑ onAssetClick: (String) -> Unit

DashboardContent.kt
  Portfolio(assets, onAssetClick = onAssetClick)
  ↑ onAssetClick: (String) -> Unit

DashboardScreen.kt (DashboardScreen + DashboardScreenRoot)
  DashboardContent(data, onActionClick, onAssetClick = onAssetClick)
  ↑ onAssetClick: (String) -> Unit

DashboardNavigation.kt (NavGraphBuilder.dashboard)
  DashboardScreen(viewModel, onActionClick, onBackToDashboard, onAssetClick = onAssetClick)
  ↑ onAssetClick: (String) -> Unit

BaseAppNavHost.kt
  dashboard(onActionClick = ..., onBackToDashboard = ...,
            onAssetClick = { assetId -> navController.navigate(AssetDetailRoute(assetId)) })
```

`feature/dashboard` never imports anything from `feature/assetdetail` — only a `String` ID crosses the feature boundary (Rule: features NEVER depend on other features).
