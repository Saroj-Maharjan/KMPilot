# Feature Spec: Send

**Status:** Complete
**Version:** 1.0.3
**Module:** `:feature:send`
**Package:** `thisissadeghi.send`
**Generated:** 2026-02-25

---

## Goals

- Render the Send screen pixel-accurately against the approved Stitch design
- Display all 4 UI states (Uninitialized, Loading, Success, Failed) with correct layout per state
- Provide dummy data via a local DataSource so the feature is self-contained

## Non-Goals

- Real API calls or network requests
- Functional paste, QR scan, percent, MAX, Send, or Retry buttons (all no-ops)
- Coin/network selector dialogs or sheets
- Form validation or error messaging for invalid input

## Background & Rationale

The send flow is a core action in the crypto wallet app. This first iteration ships the UI shell with dummy data so design can be validated before the backend is wired.

---

## Design Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Data source | LocalDataSource with hardcoded dummy data | No backend ready; avoids blocking UI validation |
| Button actions | No-ops (empty lambdas) | Actions not in scope for this iteration |
| UI states | All 4 states represented in UiState | Architecture rule; loading/failed driven by ViewModel init path |

---

## Architecture

### Layers

| Layer | Description |
|-------|-------------|
| Data | Local-only. `SendLocalDataSource` interface + `SendLocalDataSourceImpl` returns hardcoded dummy `SendData`. No network calls. |
| Repository | `SendRepository` interface + `SendRepositoryImpl` wraps the data source. Returns `Either<SendUiModel>`. |
| Presentation | `SendViewModel` calls repository directly (no UseCases). Drives `SendUiState` through `Uninitialized → Loading → Success/Failed`. |
| UI | `SendScreen` (ViewModel wrapper) + `SendScreenRoot` (testable composable). State-routed via `when(uiState.state)`. |
| DI | `SendModules` object extends `BaseFeature`. Registered via `FeatureRegistry`. |
| Navigation | `SendRoute` (`@Serializable object`). `NavGraphBuilder.send(onBackClick)` extension function. |

### Data Model

```kotlin
data class SendData(
    val recipientAddress: String,
    val amount: String,
    val selectedCoin: CoinInfo,
    val availableBalance: String,
    val selectedNetwork: NetworkInfo,
    val networkFee: String,
    val totalDeduct: String,
    val estimatedArrival: String
)

data class CoinInfo(val name: String, val symbol: String)
data class NetworkInfo(val name: String, val description: String)
```

**Dummy values:**
- `recipientAddress`: `""` (empty — user sees placeholder)
- `amount`: `"0.00"`
- `selectedCoin`: `CoinInfo("Bitcoin", "BTC")`
- `availableBalance`: `"Balance: 1.24 BTC (~$78,420.00)"`
- `selectedNetwork`: `NetworkInfo("Bitcoin Network", "BTC • ERC-20")`
- `networkFee`: `"0.00012 BTC (~$7.54)"`
- `totalDeduct`: `"0.00012 BTC"`
- `estimatedArrival`: `"Fast (10 min)"`

---

## Files Created

### Data Layer
| File | Description |
|------|-------------|
| `data/model/SendData.kt` | Local domain model |
| `data/model/CoinInfo.kt` | Coin identifier model |
| `data/model/NetworkInfo.kt` | Network identifier model |
| `data/datasource/SendLocalDataSource.kt` | Interface |
| `data/datasource/SendLocalDataSourceImpl.kt` | Hardcoded dummy data implementation |
| `data/repository/SendRepository.kt` | Interface — `getSendData(): Either<SendUiModel>` |
| `data/repository/SendRepositoryImpl.kt` | Implementation — delegates to data source |

### Presentation Layer
| File | Description |
|------|-------------|
| `presentation/SendUiState.kt` | State holder — wraps `UiState<SendUiModel>` |
| `presentation/SendUiModel.kt` | UI-facing model derived from `SendData` |
| `presentation/SendViewModel.kt` | ViewModel — invokes repository, manages 4 UI states |
| `presentation/ui/SendScreen.kt` | `SendScreen` (ViewModel wrapper) + `SendScreenRoot` (testable) |
| `presentation/ui/components/RecipientAddressInput.kt` | Label + text field + paste icon button |
| `presentation/ui/components/AmountInput.kt` | Amount display + balance + 25%/50%/MAX buttons |
| `presentation/ui/components/AssetSelectorRow.kt` | Reusable asset/network selector row |
| `presentation/ui/components/TransactionSummaryCard.kt` | Fee rows + divider + arrival row |
| `presentation/navigation/SendRoute.kt` | `@Serializable object SendRoute` |
| `presentation/navigation/SendNavigation.kt` | `NavGraphBuilder.send(onBackClick)` |

### DI
| File | Description |
|------|-------------|
| `di/SendModules.kt` | `object SendModules : BaseFeature(...)` — registers DataSource, Repository, ViewModel |

---

## UI States

| State | Layout |
|-------|--------|
| Uninitialized | Empty/idle before init — no visible content |
| Loading | Centered `XCircularProgressIndicator` 48dp |
| Success | Full form: RecipientAddressInput + AmountInput + AssetSelectorRow ×2 + TransactionSummaryCard + "Send Bitcoin" CTA |
| Failed | Centered error icon (80dp) + "Transaction Failed" heading + subtitle + Retry CTA |

---

## Design Tokens

All from Stitch design — approved 2026-02-25. Blueprint: `.claude/docs/send/designs/send_blueprint.md`.

### Color Roles

| Hex | M3 Role | Usage |
|-----|---------|-------|
| #0D0919 | `background` | Screen bg, app bar bg, bottom gradient |
| #181228 | `surface` | Transaction summary card bg |
| #9D70FF | `primary` | CTA buttons, paste icon, coin ticker, quick-% tint |
| #1A0054 | `onPrimary` | CTA button text |
| #E9E0FF | `onSurface` | Amount value, coin/network names, fee values |
| #C5BCE0 | `onSurfaceVariant` | Section labels, muted subtitles, expand icons |
| #231A38 | `surfaceVariant` | Input field fill, asset selector fill |
| #4A3F6B | `outline` | Input + selector 1dp borders |
| #FFB4AB | `error` | Failed state error icon |
| #4ADE80 | `XTheme.Colors.Success` | "Fast" arrival indicator |
| #EAB308 | `Color(0xFFEAB308)` inline | Bitcoin coin icon tint (brand color only) |

### Typography

| Usage | Size | Weight |
|-------|------|--------|
| App bar title | 20sp | Bold |
| Amount value | 40sp | Bold |
| Coin ticker beside amount | 20sp | SemiBold |
| Error heading | 24sp | Bold |
| Section labels | 14sp | Medium |
| Coin/network primary name | 16sp | Bold |
| Button labels | 16sp | Bold |
| Fee row labels/values | 14sp | Normal |

### Spacing & Shapes

- Screen horizontal padding: 16dp (success), 24dp (failed)
- `rounded-xl` = `RoundedCornerShape(24.dp)` — all inputs, selectors, CTA buttons
- Bottom gradient: `Brush.verticalGradient(transparent → background.copy(0.95f) → background)`
- CTA button elevation: 8dp
- Loading spinner: 48dp
- Failed error icon: 80dp

---

## 4 Integration Points

| # | Point | File | Code Added |
|---|-------|------|------------|
| 1 | Gradle Include | `/Users/ali/KMPProjects/KMPilot/settings.gradle.kts` | `include(":feature:send")` |
| 2 | Gradle Dependency | `/Users/ali/KMPProjects/KMPilot/composeApp/build.gradle.kts` | `implementation(project(":feature:send"))` |
| 3 | DI Initialization | `/Users/ali/KMPProjects/KMPilot/composeApp/src/commonMain/kotlin/thisissadeghi/kmpilot/initKoin.kt` | `SendModules.initialize()` |
| 4 | Navigation Wiring | `/Users/ali/KMPProjects/KMPilot/composeApp/src/commonMain/kotlin/thisissadeghi/kmpilot/BaseAppNavHost.kt` | `send(onBackClick = { navController.popBackStack() })` |

---

## Acceptance Criteria

### Test Scenarios

| Scenario | Given | When | Then |
|----------|-------|------|------|
| Loading state renders | User navigates to Send | Screen initialises | Centered spinner shown |
| Success state renders | Data loads | State transitions to Success | Full form rendered with dummy data |
| Failed state renders | Error occurs | State is Failed | Error icon, heading, subtitle, Retry button shown |
| Back navigation | User on Send | Taps back | `onBackClick` callback invoked |

### Functional Scenarios

**Send screen loads with dummy data**
- GIVEN the user navigates to Send
- WHEN the ViewModel initialises
- THEN a brief Loading state MUST appear
- AND THEN the screen MUST transition to Success with dummy data displayed

**Send screen failure path**
- GIVEN the ViewModel enters Failed state
- THEN the error layout MUST show with "Transaction Failed" heading and Retry button

**All buttons are visible but inert**
- GIVEN the screen is in Success state
- WHEN the user taps Send Bitcoin, Paste, 25%, 50%, MAX, or any selector
- THEN nothing happens (no-op callbacks)

### Technical Verification

- [x] Build passes: `./gradlew assembleDebug`
- [x] X-components used exclusively (no direct Material3)
- [x] Blueprint post-implementation checklist satisfied
- [x] Code formatted: `./gradlew :feature:send:ktlintFormat`
- [x] All 4 integration points wired

---

## Navigation

To navigate to Send from another screen:

```kotlin
navController.navigate(SendRoute)
```

`SendRoute` is exposed from `thisissadeghi.send.presentation.navigation.SendRoute`.

---

## Known Limitations (Component-Level)

These mismatches cannot be fixed at the feature level without modifying shared design system components:

| Issue | Root Cause | Component to fix |
|-------|-----------|-----------------|
| App bar title is centered instead of left-aligned | `XTopAppBar` wraps `CenterAlignedTopAppBar` unconditionally | `:core:designsystem` — `XTopAppBar` |

---

## Dependencies

| Module | When |
|--------|------|
| `:core:common` | Always (Either, UiState, setState, ErrorModel, BaseFeature) |
| `:core:designsystem` | Always (X-components, XTheme) |

---

## Last Updated

- 2026-02-25 — Initial implementation (v1.0.0)
- 2026-02-25 — Audit fixes: paste button overlap (critical), QR icon hidden in Loading/Failed states (v1.0.1)
- 2026-04-30 — UI audit fixes: transparent XIconButton bg on app bar, XTextField contentPadding exposed (16dp default), removed unused M3 import (v1.0.2)
- 2026-05-01 — UI audit fix: paste button XIconButton missing transparent colors override — added `containerColor = Color.Transparent, contentColor = primary` (v1.0.3)
