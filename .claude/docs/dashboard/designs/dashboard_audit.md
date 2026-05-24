# UI Audit: dashboard

**Audit date:** 2026-05-15
**Build status:** PASSING (`./gradlew :feature:dashboard:assembleAndroidMain`)
**X-components compliance:** PASS — 0 forbidden Material3 component imports.

---

## Token Audit: dashboard — Success

**Audited: 156 visual elements, 19 layout containers.**
**Mismatches: 3 (2 CRITICAL, 1 MINOR).**

### CRITICAL — Extra "Monthly Summary" heading not in design
- **Where:** `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/presentation/ui/components/MonthlySummaryCard.kt:32-37`
- **HTML:** Section [41] — the monthly summary card has NO heading above it. The HTML `<!-- MONTHLY SUMMARY -->` token (line 135 of `stitch_success.html`) is a comment, not a visible element. Only "Monthly Budgets", "Savings Goals", "Upcoming Bills", "Portfolio", "Recent Transactions" have visible headings.
- **Code:** Renders an `XText("Monthly Summary", fontSize = 18.sp, FontWeight.Bold, ...)` above the card.
- **Fix:** Delete the `XText("Monthly Summary", ...)` block in `MonthlySummaryCard.kt:32-37` and remove the surrounding `Column(verticalArrangement = Arrangement.spacedBy(16.dp))` wrapper, leaving the surface card as the root composable.

### CRITICAL — Portfolio icon circle missing 4dp bottom margin (mb-1)
- **Where:** `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/presentation/ui/components/PortfolioSection.kt:103`
- **HTML:** Element [129] icon circle has `mb-1` (4dp bottom margin) on top of parent `gap-2` (8dp gap). Total spacing icon → "BTC" text = 12dp.
- **Code:** `Spacer(Modifier.height(0.dp))` between the icon Box and the symbol XText. With parent `verticalArrangement = Arrangement.spacedBy(8.dp)`, total spacing = 8dp.
- **Fix:** Change `Spacer(Modifier.height(0.dp))` → `Spacer(Modifier.height(4.dp))` (`PortfolioSection.kt:103`).

### MINOR — Recent Transactions: extra "• {date}" suffix on category line
- **Where:** `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/presentation/ui/components/RecentTransactionsSection.kt:101`
- **HTML:** Each transaction row's secondary line is a single token (e.g. "Employer Inc.", "Beverage", "Web Project", "Transport") — no bullet, no date.
- **Code:** Renders `"${tx.category} • ${tx.date}"`, adding bullet + date.
- **Fix:** Render only the secondary descriptor (e.g. `tx.category` or merchant): `XText(tx.category, fontSize = 12.sp, color = onSurfaceVariant)`.

---

## Token Audit: dashboard — Loading

### CRITICAL — Loading state replaces elaborate design with bare XCircularProgressIndicator
- **Where:** `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/presentation/ui/DashboardScreen.kt:125-129`
- **HTML:** Shared loading screen ([1]–[12]) renders a layered loader: 96dp decorative outline ring (outline-variant @ 30%, blur-sm), 64dp background track (4dp border, surface-variant), 64dp partial-border arc (4dp border-t-primary + border-r-primary @ 40%, rotate-45), 8dp central primary dot with glow shadow, and a subtle 48×4dp bottom branding gradient.
- **Code:** Single `XCircularProgressIndicator()` centered in a Box. None of the layered visual structure is reproduced.
- **Fix:** Build a layered Box that mirrors the design — outer 96dp Box with 1dp outline-variant @ 30% border (CircleShape), inner 64dp track + arc (use a custom `Canvas` for the partial arc, or `XCircularProgressIndicator` with a 4dp stroke wrapped over a 64dp surface-variant ring), central 8dp primary dot with shadow.

---

## Token Audit: dashboard — Failed

**Mismatches: 4 (1 CRITICAL, 3 MINOR).**

### CRITICAL — Missing "Return to Dashboard" secondary action
- **Where:** `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/presentation/ui/DashboardScreen.kt:131-173` (`ErrorContent`)
- **HTML:** Element [12] — a secondary text button "Return to Dashboard" with `text-[#C4BA94]` (onSurfaceVariant), `font-medium` (500), `text-sm` (14sp), `py-2` (8dp vertical padding).
- **Code:** Only the primary "Retry" button is rendered.
- **Fix:** Add a secondary action below the Retry button:
  ```kotlin
  Spacer(Modifier.height(16.dp))
  XTextButton(onClick = onBackToDashboard) {
      XText("Return to Dashboard", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
  }
  ```
  The screen will need a new `onBackToDashboard: () -> Unit` callback wired through `DashboardScreen` and `DashboardScreenRoot`.

### MINOR — "Something went wrong" headline missing letter-spacing and lineHeight
- **Where:** `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/presentation/ui/DashboardScreen.kt:148-155`
- **HTML:** Element [8] adds `tracking-tight` (-0.025em × 20sp = -0.5sp) and `leading-relaxed` (1.625 × 20sp = 32.5sp lineHeight).
- **Code:** No `letterSpacing` or `lineHeight` set.
- **Fix:** `XText("Something went wrong", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = (-0.5).sp, lineHeight = 32.5.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())`.

### MINOR — Extra spacing between subtitle and Retry button
- **Where:** `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/presentation/ui/DashboardScreen.kt:164`
- **HTML:** Subtitle [9] and actions container [10] are adjacent children of `<main>` with no inter-element margin (only [10] has `gap-4` between its own children).
- **Code:** `Spacer(Modifier.height(32.dp))` adds 32dp before the Retry button.
- **Fix:** Remove the `Spacer(Modifier.height(32.dp))` on line 164 (or shrink to ≤4dp).

### MINOR — Missing subtle red glow behind warning icon
- **Where:** `feature/dashboard/src/commonMain/kotlin/thisissadeghi/dashboard/presentation/ui/DashboardScreen.kt:141-146`
- **HTML:** Element [6] — `absolute inset-0 bg-[#FFB4AB] opacity-10 blur-3xl rounded-full` glow layer behind the warning icon.
- **Code:** No glow rendered.
- **Fix:** Wrap the warning icon in a Box and place a 80dp circular `error.copy(alpha = 0.1f)` behind it (Compose has no `blur-3xl` equivalent without `Modifier.blur()` which is API 31+; visual approximation is acceptable since it's decorative).

---

## X-Components Compliance Report: dashboard

| File | Line | Violation | Correct Alternative |
|------|------|-----------|---------------------|
| _none_ | — | — | — |

All `androidx.compose.material3.*` imports are `MaterialTheme` only, which is allowed (XTheme wraps MaterialTheme).

---

## Trap Checklist (Step 5.3) — all skipped

| # | Trap | Used in feature? |
|---|------|------------------|
| 1 | XIconButton default surface color | NO |
| 2 | XTextField defaultMinSize 280×48 | NO |
| 3 | XTextField + label adds 8dp top padding | NO |
| 4 | XTopAppBar always center-aligned | NO (custom DashboardHeader Box) |
| 5 | XDialog 90% width | NO |
| 6 | XPrimaryScrollableTabRow no divider | NO |
| 7 | XRadioButton unselected = primary | NO |

## Component Overrides Check (Step 5.4) — all applied correctly

All 11 rows from the blueprint's Pre-Implementation Contract → Component Overrides table are honored:
- All cards use Box/Column with `RoundedCornerShape(24.dp)` + explicit `Modifier.background(surface)` (no `XCard`).
- `XScaffold` passes `containerColor = MaterialTheme.colorScheme.background` (`DashboardScreen.kt:72`).
- Quick-action buttons use raw clickable Box, not `XIconButton` (`QuickActionsSection.kt:46-63`).
- `XHorizontalDivider` between bills passes `color = outlineVariant.copy(alpha = 0.3f)` (`UpcomingBillsCard.kt:55`).
- Retry `XButton` passes `shape = RoundedCornerShape(12.dp)` (`DashboardScreen.kt:168`).
- Monthly summary uses single split Box with two `weight()` fills (`MonthlySummaryCard.kt:84-107`).
- Budget bars: 6dp custom Box (`BudgetsSection.kt:98`).
- Savings bars: 8dp custom Box (`SavingsGoalsSection.kt:88`).
- ETH portfolio circle: `onSurfaceVariant.copy(0.1f)` (`PortfolioSection.kt:67-72`).
- Insight icon circle: `RoundedCornerShape(20.dp)` (`InsightBanner.kt:44`).
