# Receive Specification

## Metadata

| Field | Value |
|-------|-------|
| Version | 1.2.1 |
| Status | Active |
| Created | 2026-05-03 |
| Updated | 2026-06-02 |

---

## Purpose

Display the user's wallet address for a selected crypto asset and network so they can share or copy it to receive funds.

---

## Goals

- Allow users to quickly copy or share their wallet address for a specific coin/network.
- Surface a persistent network-only warning to prevent funds being sent on the wrong network.

## Non-Goals

- QR code generation.
- Asset/network switching — the selector is a no-op (future flow).
- Push notification for received funds.
- Remote/API wallet address fetching — feature is offline-only with static data.
- Functional Share or Copy callbacks (wired as no-ops).

---

## Background & Rationale

Receiving crypto requires sharing an exact wallet address. A dedicated Receive screen reduces friction and copy errors by presenting the address in a scannable pill with a one-tap copy action and a safety warning banner.

---

## Design Decisions

| Decision | Choice | Alternatives | Rationale |
|----------|--------|--------------|-----------|
| Address source | Hardcoded static data in ViewModel | Remote API | Feature is offline-only; no network dependency required at this stage |
| Address truncation | `TextOverflow.Ellipsis` | Show full address | Pills overflow on short screens; tap-to-copy exposes full value |
| Warning style | Error-tinted banner (`errorContainer` bg @20% + `error` border @40%) | Toast / snackbar | Persistent visibility — user must see it before acting |
| Bottom bar | Sticky `surface`-bg bar with rounded top corners in `XScreen`'s `bottomBar` slot | Floating buttons | `XScreen` (Rule 13) lays the bar below content; the app-shell `Scaffold` pads the NavHost top + horizontal + ime only (not the bottom), so the bar owns its nav-bar inset via `windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime))` (collapses when the keyboard lifts the screen) |
| No loading state | ViewModel jumps Uninitialized → Success synchronously | Show loading spinner | No async work; a visible loading flash would be a lie |
| Top bar | Custom `Row` layout | `XTopAppBar` | `XTopAppBar` center-aligns titles; design requires left-aligned title next to back arrow |
| Address card | `AddressCard` wraps private `AddressPill` | Standalone `AddressPill` | Design requires gold border + "Your Bitcoin address" label around the pill |
| Warning banner | Hardcoded strings (no parameters) | Parameterised heading/body | Banner is Bitcoin-specific; generalisation adds complexity without value |

---

## Last Updated

- 2026-06-02 — Rule 13 (single app-shell Scaffold) + IME-inset fix (v1.2.1): `ReceiveScreenRoot` migrated `XScaffold` → `XScreen` (sticky `ReceiveBottomBar` in `bottomBar` slot, Success-state only); removed `paddingValues` threading from `ReceiveLoadingContent`/`ReceiveSuccessContent`/`ReceiveFailedContent`. The app shell pads the NavHost top + horizontal + ime; `ReceiveBottomBar` owns its nav-bar inset, padding content with `windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime))` (`max(0, navBar − ime)`) so it clears the nav bar when the keyboard is closed and drops to 0 when the shell's `imePadding()` lifts the screen. Background still bleeds to the screen edge.
- 2026-05-31 — i18n (Rule 12): extracted all hardcoded UI strings (top bar, address label, network warning, bottom-bar actions, failed state) to `composeResources/values/strings.xml`, replaced with `stringResource`. Coin/network names left as data (VM-supplied). No behavior change.
- 2026-05-17 — UI audit fixes: corrected failed-state typography (20sp/SemiBold/onSurfaceVariant title; 14sp/outline body), added Retry button to failed state, fixed loading spinner (64dp), fixed success content top padding (24dp), fixed bottom bar padding, fixed address label center alignment
- 2026-05-11 — Design-aware update: aligned to Stitch gold/warm theme; AddressCard replaces AddressPill; custom Row top bar; redesigned bottom bar
- 2026-05-03 — Initial spec generated from implementation

---

## Requirements

### Requirement: Address Screen Loads Immediately

The system SHALL display the wallet address in Success state as soon as the screen is composed.

#### Scenario: Screen loads with static data
- GIVEN the user navigates to the Receive screen
- WHEN the ViewModel initialises
- THEN the screen MUST immediately show Success state
- AND the `AssetSelectorCard`, `AddressCard`, and `NetworkWarningBanner` MUST be visible
- AND the sticky bottom bar with Share and Copy Address buttons MUST be visible

#### Scenario: Back navigation
- GIVEN the user is on the Receive screen
- WHEN they tap the back arrow
- THEN the `onBackClick` callback MUST be invoked

#### Scenario: Copy address
- GIVEN the screen is in Success state
- WHEN the user taps the Copy Address button or the copy icon inside the pill
- THEN the `onCopyClick` callback MUST be invoked

#### Scenario: Share
- GIVEN the screen is in Success state
- WHEN the user taps the Share button
- THEN the `onShareClick` callback MUST be invoked

#### Scenario: Retry on failure
- GIVEN the screen is in Failed state
- WHEN the user taps the Retry button
- THEN the `onRetry` callback MUST be invoked

#### Scenario: Asset selector tap
- GIVEN the screen is in Success state
- WHEN the user taps the `AssetSelectorCard`
- THEN `onAssetSelectorClick` is invoked (no-op — future flow)

---

## Architecture

### Package Structure

```
feature/receive/src/commonMain/kotlin/thisissadeghi/receive/
├── presentation/
│   ├── ReceiveViewModel.kt
│   ├── ReceiveUiModel.kt
│   ├── ReceiveUiState.kt
│   ├── ui/
│   │   ├── ReceiveScreen.kt
│   │   └── components/
│   │       ├── AddressCard.kt
│   │       ├── AssetSelectorCard.kt
│   │       └── NetworkWarningBanner.kt
│   └── navigation/
│       ├── ReceiveRoute.kt
│       └── ReceiveNavigation.kt
└── di/
    └── ReceiveModules.kt
```

### Data Flow

```
ViewModel (static hardcoded data)
    └─▶ UiState<ReceiveUiModel>
            └─▶ [UI]
```

No DataSource or Repository — data never leaves the ViewModel.

### Key Classes

| Class | Purpose | Location |
|-------|---------|----------|
| `ReceiveViewModel` | Emits static `UiState.Success` on init | `presentation/` |
| `ReceiveUiState` | State holder wrapping `UiState<ReceiveUiModel>` | `presentation/` |
| `ReceiveUiModel` | UI-facing data model | `presentation/` |
| `ReceiveScreen` | ViewModel wrapper — collects state, delegates to ScreenRoot | `presentation/ui/` |
| `ReceiveScreenRoot` | Testable composable — all UI implemented here | `presentation/ui/` |
| `AssetSelectorCard` | Tappable coin + network selector row card | `presentation/ui/components/` |
| `AddressCard` | Gold-bordered card with "Your Bitcoin address" label + private `AddressPill` | `presentation/ui/components/` |
| `NetworkWarningBanner` | Error-tinted network safety warning banner | `presentation/ui/components/` |
| `ReceiveRoute` | `@Serializable object` for type-safe navigation | `presentation/navigation/` |
| `ReceiveModules` | Koin DI — registers `ReceiveViewModel` | `di/` |

### Data Models

```kotlin
data class ReceiveUiModel(
    val coinName: String,
    val networkName: String,
    val walletAddress: String,
)

data class ReceiveUiState(
    val state: UiState<ReceiveUiModel> = UiState.Uninitialized,
)
```

**Static values (hardcoded in ViewModel):**

| Field | Value |
|-------|-------|
| `coinName` | `"Bitcoin"` |
| `networkName` | `"Bitcoin Network"` |
| `walletAddress` | `"bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh"` |

---

## Integration Points

| Point | File | Status |
|-------|------|--------|
| Module include | `settings.gradle.kts` | ✅ |
| Dependency | `composeApp/build.gradle.kts` | ✅ |
| DI init | `composeApp/.../initKoin.kt` | ✅ |
| Navigation | `composeApp/.../BaseAppNavHost.kt` | ✅ |

Navigation wiring: `receive(onBackClick = { navController.popBackStack() })` + `navController.navigate(ReceiveRoute)` inside the `dashboard` screen's `onActionClick("receive")`.

---

## State Management

### UiState Structure

```kotlin
data class ReceiveUiState(
    val state: UiState<ReceiveUiModel> = UiState.Uninitialized,
)
```

### State Transitions

```
Uninitialized ──(init)──▶ Success
```

The transition is synchronous inside `ReceiveViewModel.init` via `_uiState.setState { copy(state = UiState.Success(...)) }`. There is no Loading intermediate — `Uninitialized` and `Loading` both render the loading spinner but the screen never visibly pauses there.

The `Failed` state is defined in `UiState` and handled in the UI (error layout) but is unreachable in the current offline implementation.

---

## Navigation

- **Route:** `ReceiveRoute` (`thisissadeghi.receive.presentation.navigation.ReceiveRoute`)
- **Entry:** `navController.navigate(ReceiveRoute)`
- **Exit:** `onBackClick` → `navController.popBackStack()`
- **Source:** Dashboard screen `onActionClick("receive")`

---

## Design Tokens

From Stitch design — approved 2026-05-03. Blueprint: `.claude/docs/receive/designs/receive_blueprint.md`.

### Color Roles

| Hex | M3 Role | Usage |
|-----|---------|-------|
| #0F0D09 | `background` | Screen canvas, app bar bg |
| #1C1910 | `surface` | Asset selector fill, address card fill, bottom bar bg |
| #302B1C | `surfaceVariant` | Address pill bg, Share button bg |
| #726A48 | `outline` | All borders, chevron |
| #EDE8D5 | `onSurface` | App bar title, coin name, address text, warning heading, Share label |
| #C4BA94 | `onSurfaceVariant` | Network subtitle, address label, warning body |
| #F5D76E | `primary` | Back arrow, address card border, copy icon, Copy Address button fill |
| #2C1900 | `onPrimary` | Copy Address button label |
| #FFB4AB | `error` | Warning icon, warning border (40% alpha), failed icon + container bg (@10%) |
| #93000A | `errorContainer` | Warning banner bg (20% alpha) |
| #F7931A | `XTheme.Colors.Bitcoin` | Bitcoin coin icon container bg (already in XTheme.kt) |
| #FFFFFF | `Color.White` | "₿" symbol text on Bitcoin orange circle |

### Typography

| Usage | Size (sp) | Weight |
|-------|-----------|--------|
| App bar title | 20 | Bold, −0.5sp tracking |
| Coin name | 14 | Bold |
| Network subtitle | 12 | Normal |
| Address label | 14 | Medium |
| Address text | 12 | Normal (Monospace), −0.025em tracking |
| Warning heading | 14 | Bold |
| Warning body | 12 | Normal, 1.625× line height |
| Failed heading | 20 | SemiBold |
| Failed subtitle | 14 | Normal, 1.625× line height |

### Spacing & Shapes

- Screen horizontal padding: 16dp
- Section gap: 24dp
- Asset selector: 64dp height, `CircleShape`
- Asset selector coin icon: 40dp circle (`XTheme.Colors.Bitcoin`)
- Address card border: 1dp `primary`, `RoundedCornerShape(20.dp)`, 32dp internal padding
- Address pill: `RoundedCornerShape(24.dp)`, 16dp horizontal / 12dp vertical padding
- Warning banner corner radius: `RoundedCornerShape(24.dp)`
- Bottom bar: `surface` bg, `RoundedCornerShape(topStart=20, topEnd=20)`, 16dp horizontal, 16dp top, 32dp bottom
- Button height: 56dp, `RoundedCornerShape(24.dp)`
- Loading spinner: 64dp, 4dp stroke
- Failed icon container: 96dp, `RoundedCornerShape(24.dp)`
- Failed icon: 80dp
- Failed content max width: 280dp
