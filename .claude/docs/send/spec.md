# Feature Spec: Send

**Status:** Complete
**Version:** 2.3.1
**Module:** `:feature:send`
**Package:** `thisissadeghi.send`
**Generated:** 2026-02-25

---

## Goals

- Render the Send screen pixel-accurately against the approved Stitch design (KMPilot Gold theme)
- Display all 4 UI states (Uninitialized, Loading, Success, Failed) with correct layout per state
- Provide dummy data via a local DataSource so the feature is self-contained

## Non-Goals

- Real API calls or network requests
- Functional paste, QR scan, percent, MAX, Send, or Retry buttons (all no-ops)
- Coin/network selector dialogs or sheets
- Form validation or error messaging for invalid input

## Background & Rationale

The send flow is a core action in the crypto wallet app. This first iteration ships the UI shell with dummy data so design can be validated before the backend is wired. The v2.0 redesign updates from a purple scheme to the KMPilot Gold theme with a restructured layout: hero-size amount, card-based recipient input, 2-column asset/network selector grid, and a gold accent bar.

---

## Design Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Data source | LocalDataSource with hardcoded dummy data | No backend ready; avoids blocking UI validation |
| Button actions | No-ops (empty lambdas) | Actions not in scope for this iteration |
| UI states | All 4 states represented in UiState | Architecture rule; loading/failed driven by ViewModel init path |
| Quick chips | Custom `Box` composable | `XFilterChip` defaults (CircleShape, surface bg) diverge too far from design |
| RecipientCard accent bar | `Box` overlay at `TopStart` within clipped outer `Box` | Cleanest way to draw a full-height left bar on a rounded card |
| Screen container | `XScreen` (Rule 13), not `XScaffold` | The app-shell `Scaffold` (`App.kt`) pads the NavHost top + horizontal + ime only — **not** the bottom; the sticky "Send" CTA lives in `XScreen`'s `bottomBar` slot. `SendBottomBar` owns the bottom nav-bar inset via `windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime))` (collapses to 0 when the shell's `imePadding()` lifts the screen) |

---

## Architecture

### Layers

| Layer | Description |
|-------|-------------|
| Data | Local-only. `SendLocalDataSource` interface + `SendLocalDataSourceImpl` returns hardcoded dummy `SendData`. No network calls. |
| Repository | `SendRepository` interface + `SendRepositoryImpl` wraps the data source. Returns `Either<SendData>`. |
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
    val balanceBtc: String,
    val balanceUsd: String,
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
- `balanceBtc`: `"1.24 BTC"`
- `balanceUsd`: `"78,420"`
- `selectedNetwork`: `NetworkInfo("Bitcoin Network", "BTC • ERC-20")`
- `networkFee`: `"~$7.54"`
- `totalDeduct`: `"0.00012 BTC"`
- `estimatedArrival`: `"Fast · ~10 min"`

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
| `data/repository/SendRepository.kt` | Interface — `getSendData(): Either<SendData>` |
| `data/repository/SendRepositoryImpl.kt` | Implementation — delegates to data source |

### Presentation Layer
| File | Description |
|------|-------------|
| `presentation/SendUiState.kt` | State holder — wraps `UiState<SendUiModel>` |
| `presentation/SendUiModel.kt` | UI-facing model derived from `SendData` |
| `presentation/SendViewModel.kt` | ViewModel — invokes repository, manages 4 UI states |
| `presentation/ui/SendScreen.kt` | `SendScreen` (ViewModel wrapper) + `SendScreenRoot` (testable) + state composables |
| `presentation/ui/components/HeroAmountSection.kt` | 64sp ExtraBold amount + BTC pill + gold cursor underline + balance row + quick chips |
| `presentation/ui/components/RecipientCard.kt` | Card with gold left accent bar + `BasicTextField` + paste/QR icon buttons |
| `presentation/ui/components/AssetNetworkGrid.kt` | 2-column `Row` with private `AssetSelectorCard` helper |
| `presentation/ui/components/TransactionSummaryCard.kt` | Fee rows + `drawBehind` top-border divider + Estimated Arrival row |
| `presentation/navigation/SendRoute.kt` | `@Serializable object SendRoute` |
| `presentation/navigation/SendNavigation.kt` | `NavGraphBuilder.send(onBackClick)` |

### DI
| File | Description |
|------|-------------|
| `di/SendModules.kt` | `object SendModules : BaseFeature(...)` — registers DataSource, Repository, ViewModel |

### Design System Extension
| File | Change |
|------|--------|
| `core/designsystem/XTheme.kt` | Added `val Bitcoin = Color(0xFFF7931A)` to `XTheme.Colors` |

---

## UI States

| State | Layout |
|-------|--------|
| Uninitialized | Empty/idle before init — no visible content |
| Loading | Centered `XCircularProgressIndicator` 64dp |
| Success | Full form: `HeroAmountSection` + `RecipientCard` + `AssetNetworkGrid` + `TransactionSummaryCard` + `SecurityBadge` + sticky gold CTA footer |
| Failed | Centered warning icon (80dp) + "Something went wrong" heading + subtitle + inline Retry button (max-width 200dp, 12dp corners) + "Return to Dashboard" text action |

---

## UI Design

Design approved 2026-05-11. Blueprint: `.claude/docs/send/designs/send_blueprint.md`.
Screenshots: `.claude/docs/send/designs/send.png`.

---

## Design Tokens

All from Stitch design (KMPilot Gold theme) — approved 2026-05-11.

### Color Roles

| Hex | M3 Role | Usage |
|-----|---------|-------|
| #0F0D09 | `background` | Screen canvas, footer gradient |
| #1C1910 | `surface` | Recipient, Asset, Network cards |
| #302B1C | `surfaceVariant` | Quick chip fill, network icon bg, summary card bg |
| #EDE8D5 | `onSurface` | Hero amount, coin names, summary values |
| #C4BA94 | `onSurfaceVariant` | Labels, balance text, placeholder, subtitles |
| #3F3822 | `outlineVariant` | Card borders, chip borders, summary divider |
| #F5D76E | `primary` | Gold accents, CTA fill, cursor line, left accent bar |
| #2C1900 | `onPrimary` | CTA button text |
| #4ADE80 | `XTheme.Colors.Success` | Estimated Arrival value + bolt icon |
| #F7931A | `XTheme.Colors.Bitcoin` | Bitcoin coin icon background |

### Typography

| Usage | Size | Weight |
|-------|------|--------|
| App bar title | headlineSmall (forced by XTopAppBar) | SemiBold (forced) |
| Hero amount | 64sp | ExtraBold (800) |
| BTC pill | 12sp | Bold, tracking-widest, uppercase |
| Balance text | 14sp | Medium |
| Quick chips | 12sp | Bold |
| Card section labels | 12sp | Bold, tracking-widest, uppercase |
| Input placeholder | 16sp | Normal, italic |
| Coin name | 14sp | Bold |
| Coin subtitle | 10sp | Normal |
| Summary label | 14sp | Normal |
| Summary fee value | 14sp | Medium |
| Summary total value | 14sp | Bold |
| Estimated Arrival value | 14sp | Bold |
| Security badge | 10sp | Bold, tracking-widest |
| CTA button | 16sp | Bold |

### Spacing & Shapes

- Screen horizontal padding: 24dp (success) / 32dp (failed)
- Screen bottom padding: 144dp (footer clearance)
- `rounded-xl` = `RoundedCornerShape(24.dp)` — all cards, CTA button
- Failed retry button: `RoundedCornerShape(12.dp)`, max-width 200dp
- Bottom gradient: `Brush.verticalGradient(transparent → background.copy(0.8f))`
- Gold cursor underline: 128dp wide, 1dp tall, `primary` color
- Loading spinner: 64dp
- Failed error icon: 80dp
- Gold left accent bar: 4dp wide

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
| Failed state renders | Error occurs | State is Failed | Warning icon, heading, subtitle shown |
| Back navigation | User on Send | Taps back | `onBackClick` callback invoked |

### Functional Scenarios

**Send screen loads with dummy data**
- GIVEN the user navigates to Send
- WHEN the ViewModel initialises
- THEN a brief Loading state MUST appear
- AND THEN the screen MUST transition to Success with dummy data displayed

**Send screen failure path**
- GIVEN the ViewModel enters Failed state
- THEN the error layout MUST show with "Something went wrong" heading and Retry button in bottom bar

**All buttons are visible but inert**
- GIVEN the screen is in Success state
- WHEN the user taps Send Bitcoin, Paste, QR, 25%, 50%, MAX, or any selector
- THEN nothing happens (no-op callbacks)

### Technical Verification

- [x] Build passes: `./gradlew :feature:send:assembleAndroidMain`
- [x] X-components used exclusively (no direct Material3 widgets)
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

| Issue | Root Cause | Component to fix |
|-------|-----------|-----------------|
| App bar title is center-aligned instead of left-aligned | `XTopAppBar` wraps `CenterAlignedTopAppBar` unconditionally | `:core:designsystem` — `XTopAppBar` |

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
- 2026-05-11 — KMPilot Gold redesign: purple → gold color scheme, 64sp hero amount, card-based layout, 2-column asset/network grid, gold accent bar, SecurityBadge (v2.0.0)
- 2026-05-17 — UI audit fixes (11 critical, 2 minor): top padding 32dp, bottom padding 144dp, cursor spacer 8dp, label font 12sp/1.2sp, input font 16sp, spinner 64dp, CTA icon right, failed padding 32dp, subtitle max 240dp, retry inline with 12dp corners, "Return to Dashboard" action, title letter-spacing + line-height, inner icons 14dp (v2.1.0)
- 2026-05-31 — i18n (Rule 12): extracted all hardcoded UI strings to `composeResources/values/strings.xml`, replaced with `stringResource`. Balance/USD use format-arg templates. No behavior change. Quick-amount chips (`25%`/`50%`/`MAX`) intentionally left as raw control sentinels (parsed in logic); ₿/$ glyphs left as symbols (v2.2.0)
- 2026-06-02 — Rule 13 (single app-shell Scaffold) + IME-inset fix: `SendScreenRoot` migrated `XScaffold` → `XScreen` (sticky `SendBottomBar` in `bottomBar` slot, Success-state only); removed `paddingValues` threading from `SuccessContent`/`LoadingContent`/`FailedContent`. The app shell pads the NavHost top + horizontal + ime; `SendBottomBar` owns its nav-bar inset, padding content with `windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime))` (`max(0, navBar − ime)`) so it clears the nav bar when the keyboard is closed and drops to 0 when the shell's `imePadding()` lifts the screen — avoiding a double gap above the keyboard. Background still bleeds to the screen edge (padding after `.background(...)`) (v2.3.1)
