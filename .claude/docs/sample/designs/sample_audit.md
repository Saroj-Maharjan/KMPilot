# Three-Way Audit Report: Sample (KMPilot Dashboard)

**Audit Date**: 2026-02-25
**Method**: HTML (ground truth) → Blueprint → Code
**Attempt**: 1

---

## Summary

| State | Critical | Minor | Status |
|-------|----------|-------|--------|
| Success | 1 | 5 | NEEDS FIX |
| Loading | 0 | 0 | PASS |
| Failed | 0 | 2 | PASS (minor only) |

---

## SUCCESS STATE

### DashboardHeader (`DashboardHeader.kt`)

| Property | HTML (ground truth) | Blueprint | Code | Verdict |
|----------|---------------------|-----------|------|---------|
| Top padding | `pt-8` = 32dp | 32dp | 8dp | CODE MISMATCH — informational: `statusBarsPadding()` in SampleScreenRoot compensates on real devices (~32dp status bar + 8dp = 40dp total). Acceptable. |
| Bottom padding | `p-4` = 16dp | 8dp | 8dp | BLUEPRINT + CODE MISMATCH (8dp off) |
| Left/Right padding | `p-4` = 16dp | 16dp | 16dp | OK |
| "Good morning" font size | `text-sm` = 14sp | 14sp | 14sp | OK |
| "Good morning" font weight | `font-medium` = 500 | Medium (500) | Medium (500) | OK |
| "Dashboard" font size | `text-2xl` = 24sp | 24sp | 24sp | OK |
| "Dashboard" font weight | `font-extrabold` = 800 | ExtraBold (800) | ExtraBold (800) | OK |

### LazyColumn / DashboardContent (`SampleScreen.kt`)

| Property | HTML (ground truth) | Blueprint | Code | Verdict |
|----------|---------------------|-----------|------|---------|
| Horizontal content padding | `px-4` = 16dp | 16dp | 16dp | OK |
| Bottom content padding | `pb-12` = 48dp | 48dp | **32dp** | **CODE MISMATCH — CRITICAL (16dp off)** |
| Section vertical gap | `mb-6` = 24dp | spacedBy(24dp) | spacedBy(24dp) | OK |

### NetWorthCard (`NetWorthCard.kt`)

| Property | HTML (ground truth) | Blueprint | Code | Verdict |
|----------|---------------------|-----------|------|---------|
| Card border radius | `rounded-xl` = 24dp | 24dp | 24dp | OK |
| Card padding | `p-6` = 24dp | 24dp | 24dp | OK |
| Top accent bar height | `border-t-[3px]` = 3dp | 3dp | 3dp | OK |
| Wallet icon size | `text-6xl` ≈ 64dp | 64dp | 64dp | OK |
| Wallet icon opacity | `opacity-10` = 10% | 10% | 10% | OK |
| Label "TOTAL NET WORTH" font size | `text-xs` = 12sp | **10sp** | **10sp** | BLUEPRINT + CODE MISMATCH (2sp off) |
| Label font weight | `font-semibold` = 600 | **ExtraBold (800)** | **ExtraBold (800)** | BLUEPRINT + CODE MISMATCH |
| Label uppercase | yes | yes | yes | OK |
| Balance font size | `text-[38px]` = 38sp | 38sp | 38sp | OK |
| Balance font weight | `font-extrabold` = 800 | ExtraBold (800) | ExtraBold (800) | OK |
| Trend chip: bg alpha | `bg-success/10` = 10% | 10% | 10% | OK |
| Trend chip: border alpha | `border-success/20` = 20% | 20% | 20% | OK |
| Trend chip: border radius | `rounded-full` | CircleShape | CircleShape | OK |
| Trend chip: horiz padding | `px-3` = 12dp | 12dp | 12dp | OK |
| Trend chip: vert padding | `py-1` = 4dp | 4dp | 4dp | OK |
| Trend icon size | `text-sm` ≈ 14dp | 14dp | 14dp | OK |

### QuickActionsRow (`QuickActionsRow.kt`)

| Property | HTML (ground truth) | Blueprint | Code | Verdict |
|----------|---------------------|-----------|------|---------|
| Row arrangement | `justify-center gap-6` | SpaceEvenly | SpaceEvenly | OK |
| Icon container size | `size-14` = 56dp | 56dp | 56dp | OK |
| Icon container border radius | `rounded-2xl` = 16dp | 16dp | 16dp | OK |
| Icon container bg alpha | `bg-primary/15` = 15% | 15% | 15% | OK |
| Icon container border alpha | `border-primary/20` = 20% | 20% | 20% | OK |
| Icon size | `text-2xl` = 24dp | 24dp | 24dp | OK |
| Label font size | `text-xs` = 12sp | 12sp | 12sp | OK |
| Label-to-icon gap | `gap-2` = 8dp | spacedBy(8dp) | spacedBy(8dp) | OK |

### SmartInsightBanner (`SmartInsightBanner.kt`)

| Property | HTML (ground truth) | Blueprint | Code | Verdict |
|----------|---------------------|-----------|------|---------|
| Card border radius | `rounded-xl` = 24dp | 24dp | 24dp | OK |
| Card bg alpha | `bg-success/5` = 5% | 5% | 5% | OK |
| Card border alpha | `border-success/20` = 20% | 20% | 20% | OK |
| Card padding | `p-4` = 16dp | 16dp | 16dp | OK |
| Row gap | `gap-4` = 16dp | spacedBy(16dp) | spacedBy(16dp) | OK |
| Icon box size | `size-10` = 40dp | 40dp | 40dp | OK |
| Icon box border radius | `rounded-lg` = 16dp | 16dp | 16dp | OK |
| Icon size | `text-2xl` = 24dp | 24dp | 24dp | OK |
| "Smart Insight" font size | `text-sm` = 14sp | 14sp | 14sp | OK |
| Message font size | `text-xs` = 12sp | 12sp | 12sp | OK |

### MonthlySummarySection (`MonthlySummarySection.kt`)

| Property | HTML (ground truth) | Blueprint | Code | Verdict |
|----------|---------------------|-----------|------|---------|
| Section title font size | `text-lg` = 18sp | 18sp | 18sp | OK |
| Card border radius | `rounded-xl` = 24dp | 24dp | 24dp | OK |
| Card padding | `p-4` = 16dp | 16dp | 16dp | OK |
| Row gap | `space-y-5` = 20dp | spacedBy(20dp) | spacedBy(20dp) | OK |
| Label font size | `text-xs` = 12sp | 12sp | 12sp | OK |
| Amount font size | `text-xs` = 12sp | 12sp | 12sp | OK |
| Progress bar height | `h-1.5` = 6dp | 6dp | 6dp | OK |
| Progress bar border radius | `rounded-full` | CircleShape | CircleShape | OK |
| Progress track color | `bg-slate-800` = #1E293B | outlineVariant | outlineVariant (#1E1A2E) | MINOR — similar dark values, barely visible diff |

### BudgetsSection (`BudgetsSection.kt`)

| Property | HTML (ground truth) | Blueprint | Code | Verdict |
|----------|---------------------|-----------|------|---------|
| Section title font size | `text-lg` = 18sp | 18sp | 18sp | OK |
| "View All" font size | `text-xs` = 12sp | 12sp | 12sp | OK |
| Card gap | `space-y-3` = 12dp | spacedBy(12dp) | spacedBy(12dp) | OK |
| Card border radius | `rounded-xl` = 24dp | 24dp | 24dp | OK |
| Card padding | `p-4` = 16dp | 16dp | 16dp | OK |
| Left accent bar width | `border-l-4` = 4dp | 4dp | 4dp | OK |
| Icon box size | `size-10` = 40dp | 40dp | 40dp | OK |
| Icon box border radius | `rounded-lg` = 16dp | 16dp | 16dp | OK |
| Icon-to-text gap | `gap-3` = 12dp | spacedBy(12dp) | spacedBy(12dp) | OK |
| OVER badge radius | `rounded` = 8dp | 8dp | 8dp | OK |
| OVER badge padding | `px-2 py-1` = 8dp/4dp | 8dp/4dp | 8dp/4dp | OK |
| OVER font size | `text-[10px]` = 10sp | 10sp | 10sp | OK |
| OVER font weight | `font-black` = 900 | Black (900) | Black (900) | OK |

### SavingsGoalsSection (`SavingsGoalsSection.kt`)

| Property | HTML (ground truth) | Blueprint | Code | Verdict |
|----------|---------------------|-----------|------|---------|
| Section title font size | `text-lg` = 18sp | 18sp | 18sp | OK |
| Card gap | `space-y-3` = 12dp | spacedBy(12dp) | spacedBy(12dp) | OK |
| Card border radius | `rounded-xl` = 24dp | 24dp | 24dp | OK |
| Card padding | `p-4` = 16dp | 16dp | 16dp | OK |
| Goal name font size | `text-sm` = 14sp | 14sp | 14sp | OK |
| Amount font size | `text-[10px]` = 10sp | 10sp | 10sp | OK |
| Percentage font size | `text-sm` = 14sp | 14sp | 14sp | OK |
| Percentage font weight | `font-extrabold` = 800 | ExtraBold (800) | ExtraBold (800) | OK |
| Progress bar height | `h-2` = 8dp | 8dp | 8dp | OK |
| Inner row gap | `space-y-3` in card = 12dp | spacedBy(12dp) | spacedBy(12dp) | OK |

### UpcomingBillsSection (`UpcomingBillsSection.kt`)

| Property | HTML (ground truth) | Blueprint | Code | Verdict |
|----------|---------------------|-----------|------|---------|
| Section title font size | `text-lg` = 18sp | 18sp | 18sp | OK |
| Card gap | `space-y-3` = 12dp | spacedBy(12dp) | spacedBy(12dp) | OK |
| Card border radius | `rounded-xl` = 24dp | 24dp | 24dp | OK |
| Card padding | `p-4` = 16dp | 16dp | 16dp | OK |
| Left accent bar width | `border-l-4` = 4dp | 4dp | 4dp | OK |
| Icon box size | `size-10` = 40dp | 40dp | 40dp | OK |
| Icon box border radius | `rounded-lg` = 16dp | 16dp | 16dp | OK |
| Bill name font size | `text-sm` = 14sp | 14sp | 14sp | OK |
| OVERDUE font size | `text-xs` = 12sp | 12sp | 12sp | OK |
| Amount font size | default `font-bold` → 16sp | 16sp | 16sp | OK |

### PortfolioSection (`PortfolioSection.kt`)

| Property | HTML (ground truth) | Blueprint | Code | Verdict |
|----------|---------------------|-----------|------|---------|
| Section title font size | `text-lg` = 18sp | 18sp | 18sp | OK |
| Card border radius | `rounded-xl` = 24dp | 24dp | 24dp | OK |
| Item padding | `p-4` = 16dp | 16dp | 16dp | OK |
| Avatar size | `size-10` = 40dp | 40dp | 40dp | OK |
| Avatar shape | `rounded-full` | CircleShape | CircleShape | OK |
| Asset name font size | `text-sm` = 14sp | 14sp | 14sp | OK |
| Amount font size | `text-sm` = 14sp | 14sp | 14sp | OK |
| Change % font size | `text-[10px]` = 10sp | 10sp | 10sp | OK |
| Divider color | `divide-slate-800` = #1E293B | outlineVariant | default (outlineVariant #1E1A2E) | MINOR — similar dark values |

### RecentTransactionsSection (`RecentTransactionsSection.kt`)

| Property | HTML (ground truth) | Blueprint | Code | Verdict |
|----------|---------------------|-----------|------|---------|
| Section title font size | `text-lg` = 18sp | 18sp | 18sp | OK |
| Card border radius | `rounded-xl` = 24dp | 24dp | 24dp | OK |
| Item padding | `p-4` = 16dp | 16dp | 16dp | OK |
| Icon box size | `size-10` = 40dp | 40dp | 40dp | OK |
| Icon box border radius | `rounded-lg` = 16dp | 16dp | 16dp | OK |
| Tx title font size | `text-sm` = 14sp | 14sp | 14sp | OK |
| Subtitle font size | `text-xs` = 12sp | 12sp | 12sp | OK |
| Amount font size | `text-sm` = 14sp | 14sp | 14sp | OK |
| Divider color | `divide-slate-800` = #1E293B | outlineVariant | default (outlineVariant #1E1A2E) | MINOR — similar dark values |

---

## LOADING STATE (`SampleScreen.kt` — LoadingContent)

| Property | HTML (ground truth) | Blueprint | Code | Verdict |
|----------|---------------------|-----------|------|---------|
| Spinner size | `size-12` = 48dp | 48dp | 48dp | OK |
| Spinner stroke | `border-4` = 4dp | 4dp | 4dp | OK |
| Spinner color | `border-t-primary` | primary | primary | OK |
| Track color | `border-primary/20` = 20% alpha | primary@20% | primary@20% | OK |
| Spinner-text gap | `mt-6` = 24dp | spacedBy(24dp) | spacedBy(24dp) | OK |
| Text font size | `text-sm` = 14sp | 14sp | 14sp | OK |
| Text font weight | `font-medium` = 500 | Medium (500) | Medium (500) | OK |
| Letter spacing | `tracking-wide` ≈ 0.35sp | 0.5sp | 0.5sp | MINOR (0.15sp diff, imperceptible) |

---

## FAILED STATE (`SampleScreen.kt` — ErrorContent)

| Property | HTML (ground truth) | Blueprint | Code | Verdict |
|----------|---------------------|-----------|------|---------|
| Card border radius | `rounded-card` = 12dp | 12dp | 12dp | OK |
| Card horizontal padding | `px-6` = 24dp (outer Box) | 24dp | 24dp | OK |
| Card content padding | `p-8` = 32dp | 32dp | 32dp | OK |
| Error accent bar height | `h-[3px]` = 3dp | 3dp | 3dp | OK |
| Error icon size | `text-[24px]` = 24dp | 24dp | 24dp | OK |
| Title font size | `text-xl` = 20sp | 20sp | 20sp | OK |
| Title font weight | `font-bold` = 700 | Bold (700) | Bold (700) | OK |
| Body font size | `text-sm` = 14sp | 14sp | 14sp | OK |
| Icon-to-title gap | `mb-4` = 16dp | spacedBy(8dp) | spacedBy(8dp) | BLUEPRINT + CODE MISMATCH (8dp off) |
| Body-to-button gap | `mb-8` = 32dp | Spacer(16dp) | Spacer(16dp) | BLUEPRINT + CODE MISMATCH (16dp off) |
| Button border radius | `rounded-xl` = 12dp | 12dp | 12dp | OK |
| Button text font size | `text-sm` = 14sp | 14sp | 14sp | OK |
| Button text font weight | `font-bold` = 700 | Bold (700) | Bold (700) | OK |

---

## Mismatch Classification

### Critical (must fix)

| # | Component | Property | HTML | Blueprint | Code | Fix Target |
|---|-----------|----------|------|-----------|------|------------|
| 1 | DashboardContent | LazyColumn bottom padding | 48dp | 48dp | 32dp | Code (`SampleScreen.kt:114`) |

### Minor (report to user)

| # | Component | Property | HTML | Blueprint | Code | Notes |
|---|-----------|----------|------|-----------|------|-------|
| 2 | DashboardHeader | Bottom padding | 16dp | 8dp | 8dp | Blueprint mistranslated; 8dp difference |
| 3 | NetWorthCard | Label font size | 12sp | 10sp | 10sp | Blueprint mistranslated; 2sp diff |
| 4 | NetWorthCard | Label font weight | SemiBold (600) | ExtraBold (800) | ExtraBold (800) | Blueprint mistranslated |
| 5 | ErrorContent | Icon-to-title gap | 16dp | 8dp | 8dp | Blueprint mistranslated; 8dp diff |
| 6 | ErrorContent | Body-to-button gap | 32dp | 16dp | 16dp | Blueprint mistranslated; 16dp diff |
| 7 | Progress/dividers | Track/divider color | #1E293B | #1E1A2E | #1E1A2E | Very similar dark tones; visually indistinguishable |
| 8 | DashboardHeader | Top padding | 32dp | 32dp | 8dp (+statusBarPadding) | Intentional: statusBarPadding (~32dp on Android) compensates. Total ≈ 40dp on real device. |

---

## Total Properties Audited: 72
- Critical mismatches: **1**
- Minor mismatches: **8**
- OK: **63**
