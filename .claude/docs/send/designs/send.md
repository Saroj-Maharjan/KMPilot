# Send Screen

**Approved**: 2026-02-25

## Design Description

A dark-themed crypto wallet Send screen. Users enter a recipient wallet address, specify an amount with quick percentage shortcuts, select an asset (coin) and network from dropdowns, review a transaction summary card, and confirm with a prominent Send button. Error and loading states reuse the same app bar structure with state-specific content areas.

## Visual Specifications

### Colors (M3 roles → hex from XDarkColors)

| Role | Hex | Usage |
|------|-----|-------|
| background | #0D0919 | Screen bg, app bar, bottom gradient overlay |
| surface | #181228 | Transaction summary card bg |
| primary | #9D70FF | CTA buttons, paste icon, amount coin label, quick-% text and tint |
| onPrimary | #1A0054 | CTA button text |
| onSurface | #E9E0FF | Amount value, coin/network names, fee values |
| onSurfaceVariant | #C5BCE0 | Section labels, balance text, muted subtitles, expand icons |
| surfaceVariant | #231A38 | Input field fill, asset selector fill |
| outline | #4A3F6B | Input borders, selector borders |
| error | #FFB4AB | Error icon (failed state) |
| XTheme.Colors.Success | #4ADE80 | "Fast" arrival time indicator |

### Custom Colors

| Name | Hex | Usage | Justification |
|------|-----|-------|---------------|
| Bitcoin yellow | #EAB308 (yellow-500) | BTC icon tint + container | Coin-specific brand color |

### Typography

| Usage | Size | Weight | Letter Spacing |
|-------|------|--------|----------------|
| App bar title | 20sp | Bold | -0.5sp |
| Section labels | 14sp | Medium | 0 |
| Summary header | 12sp | Bold | 0.6sp (uppercase) |
| Amount value | 40sp | Bold | 0 |
| Coin ticker (next to amount) | 20sp | SemiBold | 0 |
| Balance / muted text | 14sp | Normal | 0 |
| Coin/network name | 16sp | Bold | 0 |
| Coin/network subtitle | 12sp | Normal | 0 |
| Quick % labels | 12sp | Bold | 0 |
| Fee row labels/values | 14sp | Normal | 0 |
| Total row value | 14sp | SemiBold | 0 |
| Arrival label/value | 12sp | Normal | 0 |
| Error heading | 24sp | Bold | 0 |
| Error subtitle | 16sp | Normal | 0 |
| Button label | 16sp | Bold | 0 |

### Layout

- Screen padding: 16dp horizontal (main column)
- App bar: 16dp horizontal + vertical padding
- Section spacing: 16dp (recipient), 32dp (amount), 40dp (asset selectors), 32dp (summary card)
- Asset selectors gap: 16dp
- Summary card rows gap: 12dp
- Bottom button area: 24dp padding, gradient overlay, 16dp home indicator spacer
- Corner radius: 24dp (inputs, selectors, buttons, summary card); CircleShape (quick % buttons, icon containers)

### Components

- `XScaffold` + `XTopAppBar` (back arrow + QR icon)
- `XTextField` for wallet address (with `XIconButton` paste action)
- `XButton` / `XOutlinedButton` for quick percentage buttons + Send/Retry
- `XExposedDropdownMenuBox` / `XDropdownMenuItem` for coin and network selectors
- `XCircularProgressIndicator` (loading state)
- `XIcon`, `XText`, `XHorizontalDivider`

## Screenshots

- Success: `send.png`
- Loading: `send_loading.png`
- Failed: `send_failed.png`

---

## Color Audit

Default theme for design: **dark**

### Defined M3 Roles (already in XDarkColors)

| Role | Hex | Usage in Design |
|------|-----|-----------------|
| background | #0D0919 | Screen bg, app bar, bottom gradient |
| surface | #181228 | Transaction summary card |
| primary | #9D70FF | Buttons, icons, coin label, tints |
| onPrimary | #1A0054 | CTA button text |
| onSurface | #E9E0FF | Primary text (overrides slate-100/white from HTML) |
| onSurfaceVariant | #C5BCE0 | Muted text, labels (overrides slate-400/500 from HTML) |
| surfaceVariant | #231A38 | Input + selector backgrounds |
| outline | #4A3F6B | Input + selector borders |
| error | #FFB4AB | Failed state icon |

### Missing M3 Roles

*None — all design colors map to existing XDarkColors roles.*

### Custom Colors (XTheme.Colors.* — justified exceptions)

| Name | Hex | Justification |
|------|-----|---------------|
| XTheme.Colors.Success | #4ADE80 | Semantic status: fast/positive indicator — no M3 role for success states |
| Bitcoin yellow (#EAB308) | #EAB308 | Coin-specific brand color with no M3 equivalent — use as inline `Color(0xFFEAB308)` |

### Component Overrides (HTML CSS vs M3 role hex divergences)

| Component | Property | HTML CSS Hex | M3 Role Hex | Action |
|-----------|----------|-------------|-------------|--------|
| Section labels | color | slate-400 = #94A3B8 | onSurfaceVariant = #C5BCE0 | Use MaterialTheme.colorScheme.onSurfaceVariant |
| Amount value | color | text-white = #FFFFFF | onSurface = #E9E0FF | Use MaterialTheme.colorScheme.onSurface |
| Input text | color | slate-100 = #F1F5F9 | onSurface = #E9E0FF | Use MaterialTheme.colorScheme.onSurface |
| Placeholder text | color | slate-500 = #64748B | onSurfaceVariant = #C5BCE0 | Use MaterialTheme.colorScheme.onSurfaceVariant |
| Coin/network names | color | slate-100 = #F1F5F9 | onSurface = #E9E0FF | Use MaterialTheme.colorScheme.onSurface |
| Muted subtitles | color | slate-500 = #64748B | onSurfaceVariant = #C5BCE0 | Use MaterialTheme.colorScheme.onSurfaceVariant |
| Input corner radius | shape | rounded-xl = 24dp | XTextField default unknown | Apply RoundedCornerShape(24.dp) explicitly |
| Selector corner radius | shape | rounded-xl = 24dp | — | Apply RoundedCornerShape(24.dp) explicitly |
| CTA button corner radius | shape | rounded-xl = 24dp | XButton default unknown | Apply RoundedCornerShape(24.dp) explicitly |
| CTA button elevation | shadow | shadow-lg = 8dp | XButton default | Apply shadow(8.dp, RoundedCornerShape(24.dp)) |
| Bottom area background | gradient | bg-gradient-to-t from #0D0919 | — | Use Brush.verticalGradient (transparent → background) |
| Arrival time text+icon | color | green-400 = #4ADE80 | — (custom) | Use XTheme.Colors.Success |
| Bitcoin icon tint | color | yellow-500 = #EAB308 | — (custom) | Use Color(0xFFEAB308) inline |
