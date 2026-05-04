# Receive Specification

## Metadata

| Field | Value |
|-------|-------|
| Version | 1.0.0 |
| Status | Active |
| Created | 2026-05-03 |
| Updated | 2026-05-03 |

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
| Warning style | Error-tinted banner (`surface` bg + `error` border @40%) | Toast / snackbar | Persistent visibility — user must see it before acting |
| Bottom bar | Sticky gradient + button row in `bottomBar` slot | Floating buttons | `XScaffold` handles insets correctly via `bottomBar` |
| No loading state | ViewModel jumps Uninitialized → Success synchronously | Show loading spinner | No async work; a visible loading flash would be a lie |

---

## Last Updated

- 2026-05-03 — Initial spec generated from implementation

---

## Requirements

### Requirement: Address Screen Loads Immediately

The system SHALL display the wallet address in Success state as soon as the screen is composed.

#### Scenario: Screen loads with static data
- GIVEN the user navigates to the Receive screen
- WHEN the ViewModel initialises
- THEN the screen MUST immediately show Success state
- AND the `AssetSelectorCard`, `AddressPill`, and `NetworkWarningBanner` MUST be visible
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
│   │       ├── AddressPill.kt
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
| `AddressPill` | Monospace address with inline copy icon button | `presentation/ui/components/` |
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

Navigation wiring: `receive(onBackClick = { navController.popBackStack() })` + `navController.navigate(ReceiveRoute)` inside the `sample` screen's `onActionClick("receive")`.

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
- **Source:** Sample screen `onActionClick("receive")`

---

## Design Tokens

From Stitch design — approved 2026-05-03. Blueprint: `.claude/docs/receive/designs/receive_blueprint.md`.

### Color Roles

| Hex | M3 Role | Usage |
|-----|---------|-------|
| #0D0919 | `background` | Screen bg, app bar bg, bottom bar backdrop |
| #181228 | `surface` | Warning banner bg |
| #231A38 | `surfaceVariant` | Asset selector fill, address pill fill, Share button fill |
| #4A3F6B | `outline` | Asset selector border, address pill border, Share button border |
| #E9E0FF | `onSurface` | App bar title, coin name, address text, warning heading, CTA label |
| #C5BCE0 | `onSurfaceVariant` | Network subtitle, expand icon, warning body, failed subtitle |
| #9D70FF | `primary` | Copy icon bg (@10%), Copy Address button fill, loading spinner |
| #1A0054 | `onPrimary` | Copy Address button label |
| #FFB4AB | `error` | Warning icon, warning border (@40%), failed icon + container bg (@10%) |
| #EAB308 | inline `Color(0xFFEAB308)` | Bitcoin coin circle bg (brand color — not an M3 role) |

### Typography

| Usage | Size (sp) | Weight |
|-------|-----------|--------|
| App bar title | 20 | Bold, −0.5sp tracking |
| Coin name | 16 | Bold |
| Network subtitle | 12 | Medium, +0.5sp tracking |
| Address text | 14 | Normal (JetBrains Mono) |
| Warning heading | 14 | Bold |
| Warning body | 12 | Medium, +0.5sp tracking, 1.625× line height |
| Failed heading | 24 | Bold |
| Failed subtitle | 16 | Normal, 1.625× line height |

### Spacing & Shapes

- Screen padding: 16dp horizontal, 24dp vertical
- Section gap: 24dp
- Asset selector corner radius: `RoundedCornerShape(24.dp)`
- Address pill: `CircleShape`
- Warning banner corner radius: `RoundedCornerShape(12.dp)`
- Bottom bar: 24dp horizontal, 40dp bottom, 16dp top
- Button height: 56dp
- Loading spinner: 48dp, 4dp stroke
- Failed icon container: 96dp, `RoundedCornerShape(24.dp)`
- Failed icon: 80dp
- Failed content max width: 280dp
