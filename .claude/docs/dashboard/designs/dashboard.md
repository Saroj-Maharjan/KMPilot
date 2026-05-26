# KMPilot Gold Dashboard

**Approved**: 2026-05-11

## Design Description

A premium dark finance dashboard with a champagne gold (#F5D76E) accent theme on a warm obsidian (#0F0D09) background. The screen is fully scrollable with 9 sections: balance hero card, quick actions, smart insight banner, monthly summary, budgets (2-column grid), savings goals, upcoming bills, portfolio (3-column grid), and recent transactions (individual cards).

The header is a sticky inline Column — no XTopAppBar — showing "Good morning," in muted text and "Dashboard" in bold white. No avatar or notification bell.

All cards use surface (#1C1910) background with 1dp outline (#726A48) border and 24dp corner radius. Progress bars use surfaceVariant (#302B1C) as track color.

## Visual Specifications

- **Background**: `#0F0D09` (M3: background)
- **Card/Surface**: `#1C1910` (M3: surface)
- **Surface variant / progress tracks**: `#302B1C` (M3: surfaceVariant)
- **Primary accent (gold)**: `#F5D76E` (M3: primary)
- **Text on primary**: `#2C1900` (M3: onPrimary)
- **Primary container (30% opacity on actions)**: `#4A3200` (M3: primaryContainer)
- **On primary container**: `#FFF0C0` (M3: onPrimaryContainer)
- **Primary text**: `#EDE8D5` (M3: onSurface / onBackground)
- **Muted text**: `#C4BA94` (M3: onSurfaceVariant)
- **Card borders**: `#726A48` (M3: outline)
- **Dividers / progress tracks**: `#3F3822` (M3: outlineVariant)
- **Income/positive**: `#4ADE80` (XTheme.Colors.Success)
- **Expense/negative**: `#FF6B6B` (XTheme.Colors.Danger)
- **Typography**: Manrope font throughout
- **Card shape**: 24dp (rounded-2xl = 1.5rem from Tailwind config)
- **Section gap**: 24dp
- **Screen padding**: 24dp horizontal

## Screenshots

- Success: `dashboard.png`
- Loading: `.claude/docs/_shared/designs/loading.png` (shared)
- Failed: `.claude/docs/_shared/designs/failed.png` (shared)

---

<!-- COLOR_AUDIT:BEGIN -->
## Color Audit

Default theme for design: dark

### Defined M3 Roles (already in active scheme — XDarkColors)

| Role | Hex (inventory) | Usage in Design |
|------|-----------------|-----------------|
| background | #0F0D09 | Screen background, header background, loading/failed screen background |
| surface | #1C1910 | All card backgrounds (balance, insight, summary, budget, savings, bills, portfolio, transactions) |
| surfaceVariant | #302B1C | Progress bar tracks, neutral transaction/bill icon containers |
| onSurface | #EDE8D5 | Primary text — section titles, balance amounts, card labels, transaction names |
| onSurfaceVariant | #C4BA94 | Muted text — "Good morning," greeting, quick action labels, secondary info, failed screen heading |
| primary | #F5D76E | Balance amount, 3dp accent bar, quick action icon tints, insight icon, on-track budget progress fills, savings icon tints |
| primaryContainer | #4A3200 | Quick action button circles (@ 30% opacity), insight icon bg (@ 10% opacity), BTC/SOL portfolio icon circles (@ 10% opacity) |
| onPrimary | #2C1900 | Text on primary-colored Retry button (failed state) |
| outline | #726A48 | Balance Card border (1dp) |
| outlineVariant | #3F3822 | Most card borders, bill section dividers (@ 30%), loading screen ring border |
| error | #FFB4AB | Failed state warning icon, failed state glow background |

### Missing M3 Roles (must add to BOTH XLightColors and XDarkColors before implementation)

None — all design colors map to existing XDarkColors roles or XTheme.Colors custom extensions.

### Custom Colors (XTheme.Colors.* — justified exceptions only)

| Name | Hex | Justification |
|------|-----|---------------|
| Success | #4ADE80 | Income amounts, savings progress bars, positive % changes, income/freelance transaction icon circles (@ 10%), savings goal completion % — M3 has no semantic success role |
| Danger | #FF6B6B | Expense amounts, over-budget borders/bars/text, OVERDUE badge, overdue bill amount, negative % changes — financially distinct from M3 error (#FFB4AB coral) |
<!-- COLOR_AUDIT:END -->
