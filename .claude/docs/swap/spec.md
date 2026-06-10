# Swap Specification

## Metadata
| Field | Value |
|-------|-------|
| Version | 1.0.0 |
| Status | Active |
| Created | 2026-06-10 |
| Updated | 2026-06-10 |

## Purpose
A crypto swap form screen (Bitcoin → Ethereum) that lets the user enter an amount of one asset, see a live-computed amount of the other asset they'll receive, review fee/slippage/total details, and submit the swap. Reached as a pushed screen from a new "Swap" Dashboard Quick Action.

## Goals
- Let users convert one held crypto asset into another from a single focused screen
- Show a live-computed "you receive" amount as the user types the "from" amount
- Surface swap economics (network fee, slippage tolerance, estimated total) before submission

## Non-Goals
- Asset selection — From/To assets are fixed to BTC/ETH per the Stitch design; choosing other coins is out of scope
- Slippage tolerance adjustment — the chevron next to "Slippage Tolerance" is rendered but tapping it is a no-op this iteration (no adjustment bottom sheet was designed)
- A dedicated "Swap Review/Confirmation" screen — "Review Swap" submits directly via the repository
- Real pricing/exchange-rate API — quote data (rate, fee, slippage, balances) is mocked via local datasource

## Background & Rationale
The Stitch design blueprint (`.claude/docs/swap/designs/swap_blueprint.md`) was approved and drove implementation. The feature follows the same patterns as `assetdetail` (Rule-11-compliant: single `*UiModel`, local GET + remote POST split, dedicated motion file).

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
| Amount input component | `BasicTextField` (transparent, 36sp ExtraBold, no border/bg) | `XTextField` | `XTextField` forces 280dp min-width, CircleShape/RoundedCornerShape(20dp), visible surface bg, 48dp min-height — too divergent from design. Same call as send's `RecipientCard` |
| Asset card shell | Custom `Column` (`AssetCard` in `components/AssetCard.kt`) — surface bg, 20dp corners, 1dp outlineVariant border | `XCard` | `XCard` defaults to surfaceVariant bg, 12dp corners, no border — diverges from design on all three axes |
| Swap-direction toggle re-fetch | Tapping the toggle swaps `fromAsset`/`toAsset` by calling `repository.getSwapQuote(fromAssetId = toAsset.id, toAssetId = fromAsset.id)` again | Pure client-side field swap (no repo call) | Keeps data flow consistent with Either/repository pattern; quote (rate/fee/slippage/balances) is asset-pair-dependent so it must be recomputed, not just relabeled |
| Slippage chevron | No-op `onSlippageClick` callback wired but unimplemented | Build a slippage-adjustment bottom sheet | No such screen exists in the design; adding one would be scope creep — left as a Non-Goal for a future iteration |
| Review Swap CTA | Calls `repository.executeSwap(SwapExecuteRequest)` (Ktor POST, mocked remote datasource); on `Either.Success` pops back to Dashboard via `onSwapComplete` callback | Navigate to a new confirmation screen | No confirmation screen was designed; mirrors `assetdetail`'s `confirmBuy` (local GET + remote POST) submit-and-return pattern |
| Avatar images | `AsyncImage(url = avatarUrl, loadingResId = DesignSystemResources.drawable.ds_image_placeholder, ...)` for both From (BTC) and To (ETH) icons | Bundled per-feature drawables | `images.json` marks both avatars `delivery: remote, delivery_locked: true` — `avatarUrl` is repository-supplied data on `SwapAsset` |
| Entry point | New "Swap" Dashboard Quick Action (5th button, alongside Send/Receive/Pay/Top Up) → `navController.navigate(SwapRoute)` | AssetDetail trade-bar button / route-only (no UI entry) | Matches existing Send/Receive quick-action wiring pattern in `BaseAppNavHost`; `QuickActions` is hardcoded (4 fixed buttons) so a 5th was added rather than replacing an existing one |

## Last Updated
- 2026-06-10 - Generated from existing implementation

## Requirements

### Requirement: Swap Quote Display
The system SHALL fetch and display a swap quote (From/To assets, balances, exchange rate, network fee, slippage tolerance, estimated total) when the screen is opened.

#### Scenario: Swap quote loads successfully
- GIVEN the user navigates to the Swap screen
- WHEN `SwapRepository.getSwapQuote()` completes successfully
- THEN the From/To asset cards, rate row, and details card MUST be displayed with the returned data
- AND `quoteState` MUST transition to `UiState.Success`

#### Scenario: Swap quote handles errors
- GIVEN the user is on the Swap screen
- WHEN `getSwapQuote()` returns `Either.Failure`
- THEN `AppErrorState` MUST be displayed with a retry action
- AND tapping retry MUST re-trigger `getSwapQuote()`

### Requirement: Live Amount Conversion
The system SHALL recompute the "to" amount whenever the "from" amount changes, using the quote's exchange rate.

#### Scenario: Editing the "from" amount recomputes "to" amount
- GIVEN the quote has loaded successfully
- WHEN the user types a numeric value into the "From Asset" amount field
- THEN `fromAmount` MUST update to the entered value
- AND `toAmount` MUST update to `fromAmount * exchangeRate` (formatted to 4 decimals)

#### Scenario: MAX fills balance
- GIVEN the quote has loaded successfully
- WHEN the user taps "MAX"
- THEN `fromAmount` MUST be set to `fromAsset.balance` (formatted to 4 decimals)
- AND `toAmount` MUST recompute accordingly

### Requirement: Swap Direction Toggle
The system SHALL swap the From/To asset pair and re-fetch the quote when the user taps the direction toggle.

#### Scenario: Swap direction toggle swaps the asset pair
- GIVEN the quote has loaded successfully
- WHEN the user taps the swap-direction toggle
- THEN `fromAsset` and `toAsset` MUST swap (a new `getSwapQuote(fromAssetId = toAsset.id, toAssetId = fromAsset.id)` call is made)
- AND the screen MUST show `AppLoadingState()` while the new quote loads, then render with the swapped pair

### Requirement: Review Swap Submission
The system SHALL submit the swap via `executeSwap` and return to the Dashboard on success.

#### Scenario: Review Swap submits and returns to Dashboard
- GIVEN the quote has loaded and the user has entered an amount
- WHEN the user taps "Review Swap" and `executeSwap()` returns `Either.Success`
- THEN `executeState` MUST transition to `UiState.Success`
- AND the app MUST navigate back to the Dashboard (`popBackStack(DashboardRoute, inclusive = false)`)

### Requirement: Navigation
The system SHALL provide entry from the Dashboard and a back action from the Swap screen.

#### Scenario: Swap navigation works correctly
- GIVEN the user is on the Dashboard
- WHEN the user taps the "Swap" quick action
- THEN the app MUST navigate to `SwapRoute`
- AND tapping the back arrow on the Swap screen MUST return to the Dashboard

## Architecture

### Package Structure
```
feature/swap/src/commonMain/kotlin/thisissadeghi/swap/
├── data/
│   ├── model/           SwapData.kt (SwapAsset, SwapQuoteResponse, SwapExecuteRequest, SwapExecuteResponse)
│   ├── remote/           SwapResources.kt (Ktor Resources — POST /swap/execute)
│   ├── datasource/       SwapLocalDataSource/Impl (mocked quote), SwapRemoteDataSource/Impl (live Ktor execute)
│   └── repository/       SwapRepository/Impl
├── presentation/
│   ├── SwapViewModel.kt
│   ├── SwapUiModel.kt
│   ├── ui/
│   │   ├── SwapScreen.kt        (SwapScreen + SwapScreenRoot + previews)
│   │   ├── components/          AssetCard, FromAssetSection, ToAssetSection, SwapDirectionToggle, RateRow, SwapDetailsCard, SwapContent
│   │   └── motion/SwapMotion.kt (shimmerBrush + syncRotation)
│   └── navigation/       SwapNavigation.kt (SwapRoute, NavGraphBuilder.swap)
└── di/
    └── SwapModules.kt    (swapModule)
```

### Data Flow
```
SwapLocalDataSource.getSwapQuote()  → Either<SwapQuoteResponse>  → quoteState   → UI
SwapRemoteDataSource.executeSwap()  → Either<SwapExecuteResponse> → executeState → UI (then pop back to Dashboard)
```
`SwapRepositoryImpl` routes `getSwapQuote` to the local (mocked) datasource and `executeSwap` to the remote (Ktor) datasource. Both return `Either<DTO>` unchanged — no presentation-layer mirror types (Rule 11).

### Key Classes
| Class | Purpose | Location |
|-------|---------|----------|
| `SwapLocalDataSource` / Impl | Mocked swap quote (assets, balances, rate, fee, slippage, estimated total) | `data/datasource/` |
| `SwapRemoteDataSource` / Impl | Live Ktor `POST /swap/execute` | `data/datasource/` |
| `SwapRepository` / Impl | Routes GET → local, POST → remote; returns `Either<DTO>` | `data/repository/` |
| `SwapViewModel` | Loads quote on init; manages amount/MAX/direction-swap/execute/retry | `presentation/` |
| `SwapScreen` / `SwapScreenRoot` | ViewModel wrapper + state router; `XScreen` with topBar + sticky bottomBar (Review Swap CTA) | `presentation/ui/` |
| `SwapContent` | Full scrollable success content | `presentation/ui/components/` |
| `SwapMotion` | `shimmerBrush()` (loading shimmer brush) + `syncRotation()` (4s infinite rotation for swap-direction icon) | `presentation/ui/motion/` |

### Data Models
```kotlin
@Serializable
data class SwapAsset(
    val id: String,
    val name: String,
    val symbol: String,
    val avatarUrl: String,
    val balance: Double,
)

@Serializable
data class SwapQuoteResponse(
    val fromAsset: SwapAsset,
    val toAsset: SwapAsset,
    val exchangeRate: Double,
    val rateDisplay: String,
    val networkFee: String,
    val slippageTolerance: String,
    val estimatedTotal: String,
)

@Serializable
data class SwapExecuteRequest(
    val fromAssetId: String,
    val toAssetId: String,
    val fromAmount: Double,
)

@Serializable
data class SwapExecuteResponse(
    val transactionId: String,
    val status: String,
)
```

### API Endpoints
| Method | Path | Handled by |
|--------|------|-----------|
| (local) | `SwapLocalDataSource.getSwapQuote(fromAssetId?, toAssetId?)` | `SwapLocalDataSourceImpl` (mock) |
| POST | `/swap/execute` | `SwapRemoteDataSourceImpl` (live Ktor) |

## Integration Points

| # | Point | File | Status |
|---|-------|------|--------|
| 1 | Module include | `settings.gradle.kts` | ✅ |
| 2 | Gradle dependency | `composeApp/build.gradle.kts` | ✅ |
| 3 | DI initialization | `composeApp/src/commonMain/kotlin/thisissadeghi/kmpilot/initKoin.kt` | ✅ |
| 4 | Navigation | `composeApp/src/commonMain/kotlin/thisissadeghi/kmpilot/BaseAppNavHost.kt` | ✅ |
| 5 | Bottom-bar tab | N/A (pushed screen) | N/A |

## State Management

### UiModel Structure
```kotlin
data class SwapUiModel(
    val fromAmount: String = "",
    val toAmount: String = "",
    val quoteState: UiState<SwapQuoteResponse> = UiState.Uninitialized,
    val executeState: UiState<SwapExecuteResponse> = UiState.Uninitialized,
)
```

### State Transitions
- On init: `loadQuote()` runs, `quoteState`: Uninitialized → Loading → Success | Failed
- On `Success`, `toAmount` is initialized to `"%.4f".format(toAsset.balance)`
- `onFromAmountChange(value)`: updates `fromAmount`; recomputes `toAmount = fromAmount * exchangeRate` (formatted), or falls back to `toAsset.balance` if the input is non-numeric
- `onMaxClick()`: delegates to `onFromAmountChange("%.4f".format(fromAsset.balance))`
- `onSwapDirectionClick()`: re-invokes `loadQuote(fromAssetId = toAsset.id, toAssetId = fromAsset.id)` — `quoteState` cycles Loading → Success | Failed again with the swapped pair
- `onReviewSwapClick()`: `executeState` Uninitialized → Loading → Success | Failed; on `Success`, `SwapScreenRoot`'s `LaunchedEffect(uiModel.executeState)` calls `onSwapComplete()`
- `retry()`: resets `quoteState` to `Uninitialized` and calls `loadQuote()`
- All state mutations via `_uiModel.setState { copy(...) }` (Rule 3)

### ViewModel Actions
| Action | Description |
|--------|-------------|
| `loadQuote(fromAssetId?, toAssetId?)` | Loads/reloads the swap quote; resets `fromAmount`/`toAmount` to defaults |
| `onFromAmountChange(value)` | Updates `fromAmount`, recomputes `toAmount` |
| `onMaxClick()` | Fills `fromAmount` with `fromAsset.balance` |
| `onSwapDirectionClick()` | Swaps `fromAsset`/`toAsset` and reloads the quote |
| `onReviewSwapClick()` | Submits `executeSwap`; on success triggers Dashboard return |
| `retry()` | Resets `quoteState` and reloads |

## UI Layer

### Screen Structure
`SwapScreen` (ViewModel wrapper) → `SwapScreenRoot` (state router using `XScreen` — no nested Scaffold per Rule 13)

- `topBar` = `XTopAppBar` with `arrow_back` icon button (transparent background, primary tint) and title "Swap"
- `bottomBar` = sticky "Review Swap" `XButton` shown only when `quoteState is UiState.Success`, padded with `Modifier.windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime))` per Rule 13 Case A
- Content: routes on `quoteState` — Uninitialized/Loading → `AppLoadingState()`, Failed → `AppErrorState(swap_error_title, swap_error_message, onRetry, secondaryAction = null)`, Success → `SwapContent`

### Component Tree
```
SwapContent
├── FromAssetSection     — AssetCard (BTC), balance label, BasicTextField amount input, MAX button
├── SwapDirectionToggle   — circular icon button, syncRotation() applied to icon
├── ToAssetSection        — AssetCard (ETH), "You receive" computed amount (shimmer while loading)
├── RateRow               — "1 BTC ≈ 17.84 ETH" rate display
└── SwapDetailsCard       — Network Fee / Slippage Tolerance (chevron, no-op) / Estimated Total
```

## Motion

### Motion Families
| Family | Implementation | Location |
|--------|---------------|----------|
| Loading shimmer | `shimmerBrush()` — animated `Brush.linearGradient` (XMotion.SHIMMER duration, Linear easing) used as text/placeholder brush | `SwapMotion.kt`, applied in `RateRow` / `ToAssetSection` |
| Sync rotation | `syncRotation()` — infinite 360° rotation over 4000ms (design-specified, overrides default `XMotion.SHIMMER`), applied to the swap-direction icon | `SwapMotion.kt`, applied in `SwapDirectionToggle` |

Both motion functions are gated by `rememberReducedMotion()` — when the OS "Reduce Motion" accessibility setting is enabled, `shimmerBrush()` returns a static (non-animated) gradient and `syncRotation()` returns `0f`. Interaction press/hover effects are dropped (primary targets are Android + iOS).

## Design System Additions

The following generic `:core:designsystem` resources were added/promoted (shared with `assetdetail`) and are consumed by `SwapScreen`:
- `DesignSystemResources.drawable.arrow_back` — back-arrow icon used in `XTopAppBar`'s `navigationIcon`
- `DesignSystemResources.drawable.chevron_right` — chevron icon (used by `SwapDetailsCard`'s slippage row)
- `DesignSystemResources.string.cd_back` ("Back") — content description for the back-arrow icon button

## Dashboard Quick Action

A 5th Dashboard Quick Action ("Swap") was added alongside the existing Send/Receive/Pay/Top Up buttons (kept unchanged):
- `feature/dashboard/.../composeResources/drawable/swap_horiz.xml` — Material Symbols Outlined "swap_horiz", 960×960 viewport, single `#000000` pathData (matches `send.xml`/`download.xml`/`payments.xml`/`add_circle.xml` format)
- `quick_action_swap` ("Swap") string added to `feature/dashboard/.../composeResources/values/strings.xml`
- `QuickActions.kt`: new `onSwapClick: () -> Unit` param + 5th `QuickActionButton(Res.drawable.swap_horiz, ...)`
- `DashboardContent.kt`: `onSwapClick = { onActionClick("swap") }`
- `DashboardLocalDataSourceImpl.kt`: `QuickAction("swap", "Swap", "swap")` appended to `quickActions`
- `DashboardScreen.kt` `@Preview`: matching `QuickAction("swap", "Swap", "swap")` appended for consistency

`feature/dashboard` does not import anything from `feature/swap` — only the `"swap"` action-id `String` crosses the boundary via `onActionClick`, resolved in `BaseAppNavHost`'s `dashboard(onActionClick = { actionId -> ... })`.

## Navigation

- **Route:** `SwapRoute` — `@Serializable object`
- **Entry:** Dashboard "Swap" Quick Action → `navController.navigate(SwapRoute)` (wired in `BaseAppNavHost`'s `dashboard(onActionClick = { actionId -> if (actionId == "swap") navController.navigate(SwapRoute) })`)
- **Exit:** `onBackClick` callback → `navController.popBackStack()`; on successful `executeSwap` → `onSwapComplete` → `navController.popBackStack(DashboardRoute, inclusive = false)`
- **Top-level tab:** No — pushed screen only
