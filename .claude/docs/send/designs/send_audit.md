# UI Verification Audit: Send

**Audited**: 2026-05-15
**Sources**:
- HTML: `.claude/docs/send/designs/extracted/stitch_success.html`, `.claude/docs/_shared/designs/extracted/stitch_{loading,failed}.html`
- Tokens: `tokens_success.md`, `tokens_loading.md`, `tokens_failed.md`
- Code: `feature/send/src/commonMain/kotlin/thisissadeghi/send/presentation/ui/`
- Catalog: `.claude/skills/_shared/X_COMPONENTS_CATALOG.md`

---

## Token Audit: Send — Success

**Audited: 62 visual elements, 8 layout containers.**
**Mismatches: 11 (6 CRITICAL, 5 MINOR).**

### CRITICAL — Main content top spacing under TopAppBar
- **Where:** `SendScreen.kt:189` (and HTML element index `[8]`)
- **HTML:** `<main>` has `pt-24` = 96dp from viewport top; with the fixed 64dp `<header>`, this leaves **32dp** of gap below the bar.
- **Code:** `Column.padding(paddingValues).padding(top = 8.dp, …)` — scaffold consumes the 64dp top inset, then only 8dp is added below it.
- **Fix:** Change `top = 8.dp` to `top = 32.dp` in the `SuccessContent` Column padding.

### CRITICAL — Hero amount → gold cursor underline spacer
- **Where:** `HeroAmountSection.kt:75` (HTML element `[10]` mb-2 + `[14]`)
- **HTML:** amount row has `mb-2` = 8dp before the cursor underline.
- **Code:** `Spacer(modifier = Modifier.height(16.dp))` between the amount row and the cursor `Box`.
- **Fix:** `Spacer(modifier = Modifier.height(8.dp))`.

### CRITICAL — "TO RECIPIENT" label font-size and tracking
- **Where:** `RecipientCard.kt:62-66` (HTML element `[23]`)
- **HTML:** `text-xs` = **12sp**, `tracking-widest` (0.1em × 12sp) = **1.2sp**.
- **Code:** `fontSize = 10.sp, letterSpacing = (1.0).sp`.
- **Fix:** `fontSize = 12.sp, letterSpacing = (1.2).sp`.

### CRITICAL — Recipient input field font-size
- **Where:** `RecipientCard.kt:82` (HTML element `[25]`)
- **HTML:** `text-md` is non-standard; the absence of a sized parent makes it inherit the body default (~16sp). Effective rendered size **≥16sp**.
- **Code:** `BasicTextField(textStyle = TextStyle(fontSize = 14.sp, …))` and matching placeholder at 14sp.
- **Fix:** Raise both `BasicTextField` `fontSize` and the placeholder `XText` `fontSize` to `16.sp`.

### CRITICAL — Bottom scroll padding short by 16dp
- **Where:** `SendScreen.kt:189` (HTML elements `[8]` pb-32 + `[63]` mb-4)
- **HTML:** `<main>` `pb-32` (128dp) plus security-badge `mb-4` (16dp) → 144dp of bottom space before the fixed footer.
- **Code:** `padding(bottom = 128.dp)`, no extra spacer after the security badge.
- **Fix:** Either add `Spacer(modifier = Modifier.height(16.dp))` after the security-badge `Row`, or change bottom padding to `144.dp`.

### CRITICAL — Send CTA icon is on the wrong side of the label
- **Where:** `SendScreen.kt:319-333` (HTML elements `[67]` button children, in DOM order: `<span>Send Bitcoin</span>` then `<span class="material-symbols-outlined">send</span>`)
- **HTML:** Label "Send Bitcoin" comes first, the `send` glyph follows it → icon renders on the **right**.
- **Code:** `XIcon(Icons.AutoMirrored.Filled.Send)` is placed first inside the `XButton` content lambda, then `Spacer(8.dp)`, then `XText("Send Bitcoin")` → icon renders on the **left**.
- **Fix:** Swap the children order inside the `XButton`:
  ```kotlin
  XText(text = "Send Bitcoin", style = …)
  Spacer(modifier = Modifier.width(8.dp))
  XIcon(
      imageVector = Icons.AutoMirrored.Filled.Send,
      contentDescription = null,
      modifier = Modifier.size(18.dp),
  )
  ```

### MINOR — XTopAppBar title font-weight is forced to SemiBold
- **Where:** `SendScreen.kt:97-106` (HTML element `[6]`)
- **HTML:** "Send" is `font-bold` (700).
- **Code:** Caller passes `FontWeight.SemiBold`, but per catalog `XTopAppBar` forces `headlineSmall.copy(fontWeight = SemiBold)` anyway.
- **Fix:** Design-system limitation — no fix without forking `XTopAppBar`.

### MINOR — XTopAppBar title alignment
- **Where:** `SendScreen.kt:97` (HTML element `[6]`)
- **HTML:** Title sits at `ml-2` immediately right of the back arrow.
- **Code:** `XTopAppBar` is built on `CenterAlignedTopAppBar` — always centers.
- **Fix:** Design-system limitation (catalog trap #4).

### MINOR — Asset/Network inner icons rendered 2dp larger
- **Where:** `AssetNetworkGrid.kt:61, 85` (HTML elements `[36]`, `[45]`)
- **HTML:** Material symbols at `text-sm` (14sp) → ~14dp icon.
- **Code:** `XIcon(modifier = Modifier.size(16.dp))`.
- **Fix:** Reduce both to `Modifier.size(14.dp)` (also applies to the chevron icons at lines 151 and 155 of the same file).

### MINOR — Send CTA leading icon size
- **Where:** `SendScreen.kt:320-324` (HTML element `[69]` material-symbols "send")
- **HTML:** Icon inside CTA inherits no explicit size; renders at default 24px.
- **Code:** `Modifier.size(18.dp)`.
- **Fix:** `Modifier.size(20.dp)` to match `XButtonDefaults.IconSize`, or `24.dp` to mirror HTML default — pick one.

### MINOR — Send CTA `gold-glow` shadow + footer `bg-background/80` solid fill omitted
- **Where:** `SendScreen.kt:293-335`
- **HTML:** Footer uses `bg-background/80` (solid background @ 80% alpha) plus a `box-shadow 0 0 20px rgba(245,215,110,0.15)` on the CTA.
- **Code:** Footer uses a vertical gradient `Transparent → background@80%`; no glow on the button.
- **Fix:** Optional decorative parity — replace gradient with `background.copy(alpha = 0.8f)` and add a `drawBehind` glow if exact fidelity is desired. Blueprint already marks the glow as decorative-optional.

---

## Loading state

### CRITICAL — Circular indicator diameter
- **Where:** `SendScreen.kt:166` (HTML elements `[6]`/`[7]`)
- **HTML:** Indicator is `w-16 h-16` = **64dp**.
- **Code:** `XCircularProgressIndicator(modifier = Modifier.size(48.dp))`.
- **Fix:** `Modifier.size(64.dp)`.

### MINOR — Decorative ring + center dot omitted
- **Where:** `SendScreen.kt:158-169` (HTML elements `[5]`, `[8]`)
- **HTML:** 96dp outline glow ring around the indicator and an 8dp primary center dot with shadow.
- **Code:** Plain spinner only.
- **Fix:** Decorative — optional.

---

## Failed state

### CRITICAL — Horizontal padding off by 8dp
- **Where:** `SendScreen.kt:256` (HTML element `[3]` `<main>`)
- **HTML:** `px-8` = **32dp**.
- **Code:** `.padding(horizontal = 24.dp)`.
- **Fix:** `.padding(horizontal = 32.dp)`.

### CRITICAL — Subtitle max-width off by 40dp
- **Where:** `SendScreen.kt:286` (HTML element `[9]`)
- **HTML:** `max-w-[240px]` = 240dp.
- **Code:** `Modifier.widthIn(max = 280.dp)`.
- **Fix:** `Modifier.widthIn(max = 240.dp)`.

### CRITICAL — Retry button shape, width, and placement diverge from HTML
- **Where:** `SendScreen.kt:339-374` (`RetryBottomBar`) (HTML element `[11]`)
- **HTML:** Retry sits **inline** inside the centered column after the body text, with `max-w-[200px]` and `rounded-md` = **12dp** corners.
- **Code:** Retry is rendered in the scaffold's `bottomBar` slot, `fillMaxWidth`, with `shape = RoundedCornerShape(24.dp)` and a gradient surround.
- **Fix:** Move the retry CTA out of `bottomBar` and into the centered failed-state Column, then set:
  - `Modifier.fillMaxWidth().widthIn(max = 200.dp).height(56.dp)`
  - `shape = RoundedCornerShape(12.dp)`
  - Drop the `Brush.verticalGradient` wrapper (the failed state has no sticky-footer chrome).

### CRITICAL — Missing secondary "Return to Dashboard" action
- **Where:** `SendScreen.kt:250-290` (`FailedContent`) (HTML element `[12]`)
- **HTML:** A second, ghost-style action sits below the retry button: `text-[#C4BA94]`, `font-medium`, `text-sm`=14sp, `py-2`=8dp.
- **Code:** Only the retry action is rendered.
- **Fix:** Add an `XTextButton` (or text-styled `XButton`) below the retry CTA with text "Return to Dashboard", `fontSize = 14.sp, fontWeight = Medium, color = onSurfaceVariant`, vertical padding 8dp. Wire it to a new `onReturnToDashboard` callback on `SendScreenRoot` (or reuse `onBackClick`).

### MINOR — Failed title letter-spacing and line-height not applied
- **Where:** `SendScreen.kt:267-277` (HTML element `[8]`)
- **HTML:** `tracking-tight` (-0.025em × 20sp = -0.5sp), `leading-relaxed` (1.625 × 20sp ≈ 32.5sp).
- **Code:** Neither `letterSpacing` nor `lineHeight` is set on the title `TextStyle`.
- **Fix:** Add `letterSpacing = (-0.5).sp, lineHeight = 32.5.sp` to the title style.

---

## Trap Checklist (Step 5.3)

| # | Trap | Status |
|---|------|--------|
| 1 | `XIconButton` default `containerColor = surface` | **PASS** — back, paste, QR all pass `Color.Transparent`. |
| 2 | `XTextField` `defaultMinSize` | N/A — feature uses `BasicTextField`. |
| 3 | `XTextField` label extra 8dp top padding | N/A. |
| 4 | `XTopAppBar` always center-aligned title | **MINOR** — reported above. |
| 5 | `XDialog` always 90% width | N/A. |
| 6 | `XPrimaryScrollableTabRow` no divider | N/A. |
| 7 | `XRadioButton` unselected = primary | N/A. |

## Component Overrides Check (Step 5.4)

All 10 rows from the blueprint's `Component Overrides` table are satisfied in code (CTA `RoundedCornerShape(24.dp)`, paste/QR `Color.Transparent`, recipient/asset/network/summary custom `Box`/`Column` surfaces, custom quick chips). The decorative `gold-glow` is the only omitted entry and is already covered under MINOR above.

---

## X-Components Compliance Report: Send

| File | Line | Violation | Correct Alternative |
|------|------|-----------|---------------------|

**0 violations.** All `androidx.compose.material3` imports are non-component utilities (`MaterialTheme`, `ButtonDefaults`, `LocalContentColor`) — permitted per skill spec. No `coil3.compose.AsyncImage`. No forbidden M3 components.

---

## Summary

| State | Critical | Minor | Status |
|-------|----------|-------|--------|
| Success | 6 | 5 | FAIL |
| Loading | 1 | 1 | FAIL |
| Failed | 4 | 1 | FAIL |
| X-Components | 0 | — | PASS |

**Total critical: 11.** Run `/modifying-kmp-feature send fix all UI audit issues based on @.claude/docs/send/designs/send_audit.md` to apply the fixes.
