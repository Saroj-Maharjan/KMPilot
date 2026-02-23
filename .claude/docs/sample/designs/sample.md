# KMPilot Sample Dashboard

**Approved**: 2026-02-23

## Design Description

A premium dark financial dashboard displaying total net worth, quick actions, smart insight, monthly summary, budgets, savings goals, upcoming bills, portfolio assets, and recent transactions.

The design uses a deep dark indigo background (`#0D0919`) with a purple accent (`#9D70FF`). All cards use a slightly elevated surface (`#181228`). Success/income data uses green (`#4ADE80`); danger/expense data uses red (`#FF6B6B`).

The header is an inline column (no top app bar) showing "Good morning" in muted text + "Dashboard" in bold bright text.

## Visual Specifications

- **Background**: `#0D0919` (M3: background)
- **Card surfaces**: `#181228` (M3: surface)
- **Primary accent**: `#9D70FF` (M3: primary)
- **Primary text**: `#E9E0FF` (M3: onBackground / onSurface)
- **Muted text**: `#C5BCE0` (M3: onSurfaceVariant)
- **Dividers / progress track**: `#1E1A2E` (M3: outlineVariant)
- **Error accent**: `#FFB4AB` (M3: error)
- **Custom Success**: `#4ADE80` (XTheme.Colors.Success)
- **Custom Danger**: `#FF6B6B` (XTheme.Colors.Danger)
- **Typography**: System default (Manrope in design — no custom font loaded yet)
- **Card shape**: rounded-xl = 1.5rem = 24dp (from tailwind config)
- **Section gap**: 24dp between sections
- **Screen padding**: 16dp horizontal

## Screenshots

- Success: `sample.png`
- Loading: `sample_loading.png`
- Failed: `sample_failed.png`

---

## Color Audit

Default theme for design: **dark**

### Defined M3 Roles (already in XDarkColors)

| Role | Hex | Usage in Design |
|------|-----|-----------------|
| background | #0D0919 | Screen background |
| surface | #181228 | Card backgrounds (card-dark) |
| primary | #9D70FF | Balance amount, icons, buttons, accent borders |
| onPrimary | #1A0054 | Text on primary-colored button |
| onBackground | #E9E0FF | Dashboard title, primary text labels |
| onSurface | #E9E0FF | Text inside cards |
| onSurfaceVariant | #C5BCE0 | Muted/subtitle text, section labels |
| outlineVariant | #1E1A2E | Progress bar track, card dividers (≈slate-800) |
| error | #FFB4AB | Error accent bar + icon in failed state |
| onError | #690005 | (not visible, for completeness) |

### Missing M3 Roles

None — all design colors are mapped to existing XDarkColors roles.

### Custom Colors (XTheme.Colors.* — justified exceptions)

| Name | Hex | Justification |
|------|-----|---------------|
| Success | #4ADE80 | Income/positive state — M3 has no "success" semantic role |
| Danger | #FF6B6B | Over-budget/expense — distinct from error (#FFB4AB coral); represents destructive financial status |

### Component Overrides / Divergences

| Component | CSS Color | M3 Role | XDarkColors Hex | Action |
|-----------|-----------|---------|-----------------|--------|
| Progress track | slate-800 (#1E293B) | outlineVariant | #1E1A2E | Minor divergence — use `outlineVariant` |
| Portfolio asset circles | bg-primary (#9D70FF) | primary | #9D70FF | ✓ Exact match |
| Error accent bar | #FFB4AB | error | #FFB4AB | ✓ Exact match |
| "Try Again" button text | #1A0054 | onPrimary | #1A0054 | ✓ Exact match |

All component sizes extracted from HTML (Tailwind):
- Quick action icons: 56×56dp (`size-14`), rounded-2xl → not in tailwind config → standard 16dp
- Budget/bill icon containers: 40×40dp (`size-10`), rounded-lg = 1rem = 16dp
- Loading spinner: 48×48dp (`size-12`)
- Card top accent bar: 3dp height
- Error top accent bar: 3dp height (failed screen)
