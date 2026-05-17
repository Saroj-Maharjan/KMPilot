# UI Audit Report: Receive

**Date:** 2026-05-17
**Audited against:** `.claude/docs/receive/designs/extracted/tokens_success.md` (success), `_shared/designs/extracted/tokens_loading.md` (loading), `_shared/designs/extracted/tokens_failed.md` (failed)
**Blueprint Component Overrides:** All 7 overrides satisfied — no issues.

---

## Token Audit: Receive — Success

**Audited: 32 visual elements, 3 layout containers.**
**Mismatches: 3 (2 Critical, 1 Minor).**

### CRITICAL — Top content padding too small

- **Where:** `ReceiveScreen.kt:170`
- **HTML:** `[9] <section>` has `mt-6` = 24dp top margin (first content section below main)
- **Code:** `padding(top = 16.dp)` on content Column = 16dp — 8dp short
- **Fix:** Change `.padding(top = 16.dp)` to `.padding(top = 24.dp)`

### CRITICAL — Bottom bar bottom padding overcounted

- **Where:** `ReceiveScreen.kt:199`
- **HTML:** `[30] <footer>` has `pb-8` = 32dp bottom padding, `pt-4` = 16dp top padding
- **Code:** `.padding(horizontal = 16.dp, vertical = 16.dp).padding(bottom = 32.dp)` stacks to 16dp top + 48dp bottom (16+32)
- **Fix:** Replace with `.padding(horizontal = 16.dp).padding(top = 16.dp).padding(bottom = 32.dp)` to get exact pt-4/pb-8 match

### MINOR — "Your Bitcoin address" label alignment

- **Where:** `AddressCard.kt:50`
- **HTML:** `[19]` parent column has `items-center` — label is center-aligned
- **Code:** `.align(Alignment.Start)` overrides the column, making label left-aligned
- **Fix:** Remove `.align(Alignment.Start)` from the `XText` modifier

---

## Token Audit: Receive — Loading

**Audited: 10 visual elements, 2 layout containers.**
**Mismatches: 1 (1 Critical).**

### CRITICAL — Loading spinner too small

- **Where:** `ReceiveScreen.kt:150`
- **HTML:** `[6]` background track = `w-16 h-16` = 64dp; `[7]` arc overlay = 64dp
- **Code:** `XCircularProgressIndicator(modifier = Modifier.size(48.dp))` = 48dp — 16dp short
- **Fix:** Change `Modifier.size(48.dp)` to `Modifier.size(64.dp)`

---

## Token Audit: Receive — Failed

**Audited: 13 visual elements, 2 layout containers.**
**Mismatches: 6 (5 Critical, 1 Minor).**

### CRITICAL — Failed title font size wrong

- **Where:** `ReceiveScreen.kt:279`
- **HTML:** `[8]` title: `text-[20px]` = 20sp
- **Code:** `fontSize = 24.sp`
- **Fix:** Change `fontSize = 24.sp` to `fontSize = 20.sp`

### CRITICAL — Failed title font weight wrong

- **Where:** `ReceiveScreen.kt:280`
- **HTML:** `[8]` title: `font-semibold` = SemiBold (600)
- **Code:** `fontWeight = FontWeight.Bold` (700)
- **Fix:** Change `FontWeight.Bold` to `FontWeight.SemiBold`

### CRITICAL — Failed title color wrong

- **Where:** `ReceiveScreen.kt:281`
- **HTML:** `[8]` title: `text-[#C4BA94]` = `onSurfaceVariant`
- **Code:** `color = MaterialTheme.colorScheme.onSurface`
- **Fix:** Change to `color = MaterialTheme.colorScheme.onSurfaceVariant`

### CRITICAL — Failed body text color wrong

- **Where:** `ReceiveScreen.kt:293`
- **HTML:** `[9]` body: `text-[#726A48]` = `outline`
- **Code:** `color = MaterialTheme.colorScheme.onSurfaceVariant`
- **Fix:** Change to `color = MaterialTheme.colorScheme.outline`

### CRITICAL — Missing Retry button in failed state

- **Where:** `ReceiveScreen.kt:248–298` (`ReceiveFailedContent`)
- **HTML:** `[11]` Retry button: `bg-primary`, `text-onPrimary`, `font-bold`, `text-base` (16sp), `rounded-md` (12dp corner), `h-[56px]`
- **Code:** No retry button present; `onRetry` callback exists but is not wired to `ReceiveFailedContent`
- **Fix:**
  1. Add `onRetry: () -> Unit` param to `ReceiveFailedContent`
  2. Pass `onRetry = onRetry` from `ReceiveScreenRoot`
  3. Add an `XButton` below the body text:
     ```kotlin
     Spacer(modifier = Modifier.height(24.dp))
     XButton(
         onClick = onRetry,
         modifier = Modifier.fillMaxWidth().height(56.dp).widthIn(max = 200.dp),
         shape = RoundedCornerShape(12.dp),
         colors = ButtonDefaults.buttonColors(
             containerColor = MaterialTheme.colorScheme.primary,
             contentColor = MaterialTheme.colorScheme.onPrimary,
         ),
     ) {
         XText(text = "Retry", fontSize = 16.sp, fontWeight = FontWeight.Bold)
     }
     ```

### MINOR — Failed body text font size slightly off

- **Where:** `ReceiveScreen.kt:289`
- **HTML:** `[9]` body: `text-sm` = 14sp
- **Code:** `fontSize = 16.sp` (2sp over)
- **Fix:** Change `fontSize = 16.sp` to `fontSize = 14.sp` (also update `lineHeight = (14 * 1.625).sp`)

---

## X-Components Compliance Report: Receive

| File | Line | Violation | Correct Alternative |
|------|------|-----------|---------------------|
| — | — | None | — |

**Result: PASS** — No forbidden Material3 component imports. `ButtonDefaults` and `MaterialTheme` usages are allowed.

---

## Summary

| State | Critical | Minor | Status |
|-------|----------|-------|--------|
| Success | 2 | 1 | FAIL |
| Loading | 1 | 0 | FAIL |
| Failed | 5 | 1 | FAIL |

| Check | Violations | Status |
|-------|------------|--------|
| No Material3 usage | 0 | PASS |
