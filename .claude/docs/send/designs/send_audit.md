# UI Audit Report: Send Feature
> Verified: 2026-05-01 | Attempts: 5

---

## X-Component Constraint Catalog

### XButton
- `LocalMinimumInteractiveComponentSize provides Dp.Unspecified` → M3's 48dp min touch target **disabled**
- Default shape: `CircleShape`
- Default contentPadding: `ButtonDefaults.ContentPadding` = 24dp horizontal, 8dp vertical

### XIconButton (delegates to XButton)
- **Default containerColor: `MaterialTheme.colorScheme.surface`** ← visible background rendered when no `colors` override passed
- Default contentPadding: `PaddingValues(0dp)` (zero)
- Default shape: `CircleShape`

### XTextField
- `defaultMinSize(minWidth = 280dp, minHeight = 48dp)`
- Default shape (singleLine=true): `CircleShape`
- Default contentPadding: 16dp all sides
- `trailingIcon` slot auto-reserves space — no explicit right padding needed

### XTopAppBar
- Uses `CenterAlignedTopAppBar` → title is always **center-aligned**
- `windowInsets = WindowInsets(0, 0, 0, 0)`

---

## Three-Way Token Audit

### Screen Container
| Property | HTML | Blueprint | Code | Verdict |
|----------|------|-----------|------|---------|
| Background | `bg-background-dark` = `#0d0919` | `colorScheme.background` | `XScaffold(containerColor = colorScheme.background)` | OK |

### Top App Bar
| Property | HTML | Blueprint | Code | Verdict |
|----------|------|-----------|------|---------|
| Background | `bg-background-dark` | `colorScheme.background` | `XTopAppBar(backgroundColor = colorScheme.background)` | OK |
| **Title alignment** | `ml-4` only → **left-aligned** | left-aligned | **`CenterAlignedTopAppBar` → center-aligned** | **MINOR** |
| Title font size | `text-xl` = 20sp | 20sp | 20sp | OK |
| Title font weight | `font-bold` | Bold | FontWeight.Bold | OK |
| Title letter spacing | `tracking-tight` ≈ −0.5sp | −0.5sp | `letterSpacing = (−0.5).sp` | OK |

### Back Button
| Property | HTML | Blueprint | Code | Verdict |
|----------|------|-----------|------|---------|
| Padding | `p-2` = 8dp | 8dp | `Modifier.padding(8.dp)` | OK |
| Background | no bg (hover-only) | transparent | `containerColor = Color.Transparent` | OK |
| Shape | `rounded-full` | CircleShape | XIconButton default = CircleShape | OK |

### Recipient Address Input
| Property | HTML | Blueprint | Code | Verdict |
|----------|------|-----------|------|---------|
| Section top margin | `mt-4` = 16dp | 16dp | `Spacer(height = 16.dp)` | OK |
| Label size | `text-sm` = 14sp | 14sp | 14sp | OK |
| Label weight | `font-medium` | Medium | FontWeight.Medium | OK |
| Label color | `text-slate-400` ≈ muted | `colorScheme.onSurfaceVariant` | `colorScheme.onSurfaceVariant` | OK |
| Label bottom margin | `mb-2` = 8dp | 8dp | `padding(bottom = 8.dp)` | OK |
| Input background | `bg-surface-variant` = `#231a38` | `colorScheme.surfaceVariant` | `surfaceVariant` | OK |
| Input border | `border-outline` = `#4a3f6b`, 1dp | outline, 1dp | `colorScheme.outline`, 1dp | OK |
| Input corner radius | `rounded-xl` = 24dp | 24dp | `RoundedCornerShape(24.dp)` | OK |
| Input content padding | `py-4 pl-4` = 16dp | 16dp | XTextField default = 16dp all | OK |
| **Paste button background** | **no bg class — transparent** | **transparent** | **XIconButton default = `colorScheme.surface` (#181228) — visible circle** | **CRITICAL** |
| Paste icon tint | `text-primary` | `colorScheme.primary` | `tint = colorScheme.primary` | OK |
| Paste button padding | `p-1` = 4dp | 4dp | `PaddingValues(4.dp)` | OK |

### Amount Section
| Property | HTML | Blueprint | Code | Verdict |
|----------|------|-----------|------|---------|
| Section top margin | `mt-8` = 32dp | 32dp | `Spacer(height = 32.dp)` | OK |
| Label size | `text-sm` = 14sp | 14sp | 14sp | OK |
| Amount value size | `text-[40px]` = 40sp | 40sp | 40sp | OK |
| Amount value weight | `font-bold` | Bold | FontWeight.Bold | OK |
| **Amount value color** | **`text-white` = #FFFFFF** | `colorScheme.onSurface` | **`colorScheme.onSurface` = #E9E0FF** | **MINOR** |
| Amount value line height | `leading-none` = 40sp | 40sp | `lineHeight = 40.sp` | OK |
| Coin symbol size | `text-xl` = 20sp | 20sp | 20sp | OK |
| Coin symbol weight | `font-semibold` | SemiBold | FontWeight.SemiBold | OK |
| Coin symbol color | `text-primary` | primary | `colorScheme.primary` | OK |
| Balance text margin | `mt-2` = 8dp | 8dp | `padding(top = 8.dp)` | OK |
| % buttons top margin | `mt-4` = 16dp | 16dp | `padding(top = 16.dp)` | OK |
| % buttons gap | `gap-2` = 8dp | 8dp | `Arrangement.spacedBy(8.dp)` | OK |
| 25%/50% padding | `px-4 py-1.5` = 16dp/6dp | 16dp/6dp | `PaddingValues(horizontal=16.dp, vertical=6.dp)` | OK |
| 25%/50% background | `bg-primary/10` | `primary.copy(0.1f)` | `primary.copy(alpha = 0.1f)` | OK |
| 25%/50% border | `border-primary/20` 1dp | 1dp, `primary.copy(0.2f)` | `BorderStroke(1.dp, primary.copy(0.2f))` | OK |
| MAX background | `bg-primary` | primary | `containerColor = primary` | OK |
| MAX text color | `text-primary-dark` = `#1a0054` | onPrimary | `contentColor = onPrimary` | OK |

### Asset Selector Rows
| Property | HTML | Blueprint | Code | Verdict |
|----------|------|-----------|------|---------|
| Section top margin | `mt-10` = 40dp | 40dp | `Spacer(height = 40.dp)` | OK |
| Row spacing | `space-y-4` = 16dp | 16dp | `Arrangement.spacedBy(16.dp)` | OK |
| Row background | `bg-surface-variant` | `colorScheme.surfaceVariant` | `colorScheme.surfaceVariant` | OK |
| Row border | `border-outline` 1dp | outline 1dp | `border(1.dp, colorScheme.outline, ...)` | OK |
| Row corner radius | `rounded-xl` = 24dp | 24dp | `RoundedCornerShape(24.dp)` | OK |
| Row padding | `p-4` = 16dp | 16dp | `padding(16.dp)` | OK |
| Icon circle size | `w-10 h-10` = 40dp | 40dp | `Modifier.size(40.dp)` | OK |
| Icon+text gap | `gap-3` = 12dp | 12dp | `Arrangement.spacedBy(12.dp)` | OK |
| BTC icon bg | `bg-yellow-500/20` | `#EAB308.copy(0.2f)` | `Color(0xFFEAB308).copy(alpha = 0.2f)` | OK |
| Name size | default 16sp | 16sp | 16sp | OK |
| Name weight | `font-bold` | Bold | FontWeight.Bold | OK |
| Subtitle size | `text-xs` = 12sp | 12sp | 12sp | OK |
| Chevron tint | `text-slate-400` | `colorScheme.onSurfaceVariant` | `colorScheme.onSurfaceVariant` | OK |

### Transaction Summary Card
| Property | HTML | Blueprint | Code | Verdict |
|----------|------|-----------|------|---------|
| Card top margin | `mt-8` = 32dp | 32dp | `Spacer(height = 32.dp)` | OK |
| Card background | `bg-surface` = `#181228` | `colorScheme.surface` | `colorScheme.surface` | OK |
| Card corner radius | `rounded-xl` = 24dp | 24dp | `RoundedCornerShape(24.dp)` | OK |
| Card padding | `p-4` = 16dp | 16dp | `padding(16.dp)` | OK |
| Card border | `border-outline/30` | `outline.copy(0.3f)` | `colorScheme.outline.copy(alpha = 0.3f)` | OK |
| Header size | `text-xs` = 12sp | 12sp | 12sp | OK |
| Header letter spacing | `tracking-wider` = 0.6sp | 0.6sp | `letterSpacing = 0.6.sp` | OK |
| Header bottom margin | `mb-4` = 16dp | 16dp | `padding(bottom = 16.dp)` | OK |
| Fee rows spacing | `space-y-3` = 12dp | 12dp | `Arrangement.spacedBy(12.dp)` | OK |
| Divider margin before | `mt-1` = 4dp | 4dp | `Spacer(height = 4.dp)` | OK |
| Divider padding after | `pt-3` = 12dp | 12dp | `Spacer(height = 12.dp)` | OK |
| Divider color | `border-outline/20` | `outline.copy(0.2f)` | `colorScheme.outline.copy(alpha = 0.2f)` | OK |
| Arrival value color | `text-green-400` = `#4ADE80` | `XTheme.Colors.Success` | `XTheme.Colors.Success = #4ADE80` | OK |
| Arrival icon size | `text-sm` = 14sp | 14dp | `Modifier.size(14.dp)` | OK |

### Bottom Action Button
| Property | HTML | Blueprint | Code | Verdict |
|----------|------|-----------|------|---------|
| Container padding | `p-6` = 24dp | 24dp | `padding(24.dp)` | OK |
| Gradient | top=transparent, bottom=bg | ✓ | `verticalGradient(Transparent, bg.95, bg)` | OK |
| Background | `bg-primary` | primary | `containerColor = primary` | OK |
| Text color | `text-primary-dark` | onPrimary | `contentColor = onPrimary` | OK |
| Vertical padding | `py-4` = 16dp | 16dp | `PaddingValues(vertical = 16.dp)` | OK |
| Corner radius | `rounded-xl` = 24dp | 24dp | `RoundedCornerShape(24.dp)` | OK |

### Failed State
| Property | HTML | Blueprint | Code | Verdict |
|----------|------|-----------|------|---------|
| Horizontal padding | `px-6` = 24dp | 24dp | `padding(horizontal = 24.dp)` | OK |
| Error circle bottom margin | `mb-8` = 32dp | 32dp | `padding(bottom = 32.dp)` | OK |
| Error circle padding | `p-6` = 24dp | 24dp | `padding(24.dp)` | OK |
| Error circle bg | `bg-error-soft/10` | `error.copy(0.1f)` | `colorScheme.error.copy(alpha = 0.1f)` | OK |
| Error icon size | `text-[80px]` = 80dp | 80dp | `Modifier.size(80.dp)` | OK |
| Error icon tint | `text-error-soft` = error | `colorScheme.error` | `colorScheme.error` | OK |
| Error icon fill | `FILL 0, wght 300` | ErrorOutline | `Icons.Default.ErrorOutline` | OK |
| Title size | `text-2xl` = 24sp | 24sp | 24sp | OK |
| Title color | `text-text-heading` = `#e9e0ff` | `colorScheme.onSurface` | `colorScheme.onSurface` | OK |
| Message line height | `leading-relaxed` = 26sp | 26sp | `lineHeight = 26.sp` | OK |
| Message max width | `max-w-[280px]` = 280dp | 280dp | `widthIn(max = 280.dp)` | OK |

### Loading State
| Property | HTML | Blueprint | Code | Verdict |
|----------|------|-----------|------|---------|
| Spinner size | `h-12 w-12` = 48dp | 48dp | `Modifier.size(48.dp)` | OK |
| Spinner color | `text-primary` | primary | `color = colorScheme.primary` | OK |

---

## Catalog-Driven Reverse Sweep — XIconButton Instances

| Instance | Design Background | XIconButton Default | Feature Override | Verdict |
|----------|-----------------|---------------------|-----------------|---------|
| `SendScreen.kt:105` back button | transparent | `colorScheme.surface` | `Color.Transparent` ✓ | OK |
| `SendScreen.kt:122` QR button | transparent | `colorScheme.surface` | `Color.Transparent` ✓ | OK |
| **`RecipientAddressInput.kt:63`** paste button | **transparent** | **`colorScheme.surface` = #181228** | **none — visible dark circle rendered** | **CRITICAL** |

---

## X-Components Compliance

| File | Import | Verdict |
|------|--------|---------|
| All | `ButtonDefaults`, `OutlinedTextFieldDefaults`, `MaterialTheme` | ✅ Config helpers / theme — allowed |
| All | No M3 UI components (`Button`, `Card`, `Text`, etc.) | ✅ PASS |

**Violations: 0 — PASS**

---

## Critical Fix Required

### Fix: Paste button in RecipientAddressInput
**File:** `RecipientAddressInput.kt:63`  
**Root cause:** `XIconButton` default `containerColor = colorScheme.surface` renders a visible dark circle  
**Fix:** Pass explicit transparent colors:

```kotlin
XIconButton(
    onClick = onPasteClick,
    contentPadding = PaddingValues(4.dp),
    colors = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
    ),
) {
```

---

## Results Summary

| State | Critical | Minor | Status |
|-------|----------|-------|--------|
| Success | 1 | 2 | ❌ FAIL |
| Loading | 0 | 0 | ✅ PASS |
| Failed | 0 | 0 | ✅ PASS |
| X-Components | 0 violations | — | ✅ PASS |

**Minor observations (no fix required):**
1. Title centered (XTopAppBar/CenterAlignedTopAppBar behavior) vs left-aligned in mockup — design system behavior is authoritative
2. Amount value `#E9E0FF` vs `#FFFFFF` — semantic theme mapping is correct
