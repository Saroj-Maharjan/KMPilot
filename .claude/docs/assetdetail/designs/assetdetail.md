# Asset Detail Screen — Design Description

Crypto asset detail screen showing Bitcoin. Pushed screen (not a tab destination).

## Screen Summary

Top-to-bottom layout:
1. Transparent top app bar — back arrow (primary gold), title "Bitcoin"
2. Hero header — circular Bitcoin icon on primary-colored circle, coin name + ticker, large price, 24h % change badge (success green / danger red)
3. Full-width price line chart with gradient area fill; Y/X axis labels
4. Time period segmented selector: 1D / 1W / 1M / 1Y / All chips
5. 2×2 stats grid cards: Market Cap, 24h Volume, Circ. Supply, Your Holdings (holdings card highlighted with primary border)
6. Recent Activity section — 3 transaction rows with icon circles, title/timestamp, signed amounts
7. Top Holders row — overlapping avatar circles + "Join Group" chevron button
8. Sticky bottom CTA bar — "Sell" outlined + "Buy" filled primary buttons
9. Buy bottom sheet overlay (ModalBottomSheet) — drag handle, amount input field, XSlider + quick-amount chips, Pay-with XExposedDropdownMenuBox, Confirm button

<!-- COLOR_AUDIT:BEGIN -->
## Color Audit

Default theme for design: dark

### Defined M3 Roles (already in active scheme — XDarkColors)

| Role | Hex (inventory) | Usage in Design |
|------|-----------------|-----------------|
| primary | #F5D76E | Back icon, coin circle bg, selected time-period chip bg/text-owner, "Your Holdings" label, avatar initials, "See all"/"Join Group" text, Sell button border/text, Buy button bg, slider thumb/fill, wallet icon, quick-amount selected chip bg, Confirm button bg |
| onPrimary | #2C1900 | Coin icon inside circle, selected chip text, Buy button text, Confirm button text, quick-amount selected chip text |
| primaryContainer | #4A3200 | Hero gradient-from color (top of bg gradient), "+42" overflow avatar bg |
| onPrimaryContainer | #FFF0C0 | Referenced in Tailwind config; no direct text use in success state |
| background | #0F0D09 | Screen bg, avatar ring-border color, hero gradient-to color, scrim overlay bg (#0F0D09 @ 60%) |
| surface | #1C1910 | Stats card bg, activity row bg (@ 50% alpha), sticky footer bg, bottom sheet container bg, amount input field bg (#1C1910 direct) |
| onSurface | #EDE8D5 | Screen title "Bitcoin", price "$67,420.50", coin name+ticker, stats values, activity row titles, section headers, bottom sheet title, amount input value, wallet label (#EDE8D5 direct) |
| onSurfaceVariant | #C4BA94 | Chart axis labels (Y/X), unselected chip text, stats card labels, activity timestamps, activity fiat equivalents, "Join Group" text, currency prefix "$", "≈ 0.0074 BTC" approx, "Pay with" label, expand_more icon (#C4BA94 direct) |
| surfaceVariant | #302B1C | Avatar bg circles, bottom sheet amount input bg, quick-amount unselected chip bg, Pay-with dropdown bg (#302B1C direct) |
| outline | #726A48 | Body/description text on failed state shared screen (shared state screen) |
| outlineVariant | #3F3822 | Unselected chip border, stats card border, activity row border (@ 30% alpha), sticky footer top border, bottom sheet drag handle (#3F3822 direct), bottom sheet Pay-with border, slider track bg |
| error | #FFB4AB | Warning icon on failed state shared screen (shared state screen) |
| onError | #690005 | Failed state shared screen (shared state screen) |
| errorContainer | #93000A | Failed state shared screen (shared state screen) |
| onErrorContainer | #FFDAD6 | Failed state shared screen (shared state screen) |

### Missing M3 Roles (must add to BOTH XLightColors and XDarkColors before implementation)

(none — all design colors map to existing XDarkColors roles or XTheme.Colors.* extensions)

### Custom Colors (XTheme.Colors.* — justified exceptions only)

| Name | Hex | Justification |
|------|-----|---------------|
| XTheme.Colors.Success | #4ADE80 | Positive % change badge, received/staked amount text, activity icon circle bg tint (@ 10%), up-arrow icon — semantic gain/positive status with no M3 role equivalent |
| XTheme.Colors.Danger | #FF6B6B | Negative % change badge, sent amount text, activity icon circle bg tint (@ 10%), down-arrow icon — semantic loss/negative status with no M3 role equivalent |
<!-- COLOR_AUDIT:END -->

<!-- TYPOGRAPHY_AUDIT:BEGIN -->
## Typography Audit

**Design typeface**: Manrope (github-variable; weights 400, 500, 700 from fonts.json; HTML also uses 600, 800)
**Theme font**: Manrope (manrope_variable — `XFontFamily()` in XTheme.kt) — **matches current**

### Text node → M3 role

| Node (usage) | M3 Role | Measured (size/weight) | Override needed? |
|--------------|---------|------------------------|------------------|
| Screen title ("Bitcoin") | titleLarge | 22sp / 700 | Yes — stock titleLarge is 22sp/Normal (medium); `.copy(fontWeight = FontWeight.Bold)` |
| Coin name + ticker ("Bitcoin (BTC)") | titleMedium | 18sp / 700 | Yes — stock titleMedium is 16sp/Medium; `.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)` |
| Price ("$67,420.50") | displaySmall | 36sp / 800 | Yes — stock displaySmall is 36sp/Normal; `.copy(fontWeight = FontWeight.ExtraBold)` |
| % change badge | labelLarge | 14sp / 700 | Yes — stock labelLarge is 14sp/Medium; `.copy(fontWeight = FontWeight.Bold)` |
| Chart axis labels (Y/X) | labelSmall | 10sp / 500 | Yes — stock labelSmall is 11sp/Medium; `.copy(fontSize = 10.sp)` |
| Time period chips text | labelLarge | 14sp / 700 | Yes — `.copy(fontWeight = FontWeight.Bold)` |
| Stats card label ("Market Cap" etc.) | labelMedium | 12sp / 500 | No — stock labelMedium is 12sp/Medium |
| Stats card value ("$1.32T" etc.) | titleMedium | 18sp / 700 | Yes — stock titleMedium is 16sp/Medium; `.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)` |
| Holdings fiat equiv ("~$5,679.42") | labelSmall | 10sp / 400 | Yes — stock labelSmall is 11sp/Medium; `.copy(fontSize = 10.sp, fontWeight = FontWeight.Normal)` |
| Section header ("Recent Activity", "Top Holders Community") | titleLarge | 20sp / 700 | Yes — stock titleLarge is 22sp/Normal; `.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold)` |
| "See all" text button | labelLarge | 14sp / 700 | Yes — `.copy(fontWeight = FontWeight.Bold)` |
| "Join Group" text button | labelLarge | 14sp / 700 | Yes — `.copy(fontWeight = FontWeight.Bold)` |
| Activity row title ("Received", "Sent", "Staked") | labelLarge | 14sp / 700 | Yes — stock labelLarge is 14sp/Medium; `.copy(fontWeight = FontWeight.Bold)` |
| Activity timestamp | labelMedium | 12sp / 400 | Yes — stock labelMedium is 12sp/Medium; `.copy(fontWeight = FontWeight.Normal)` |
| Activity signed amount ("+0.0120 BTC") | labelLarge | 14sp / 700 | Yes — `.copy(fontWeight = FontWeight.Bold)` |
| Activity fiat equiv ("$809.04") | labelSmall | 12sp / 400 | Yes — `.copy(fontSize = 12.sp, fontWeight = FontWeight.Normal)` |
| Avatar initials | labelSmall | 12sp / 700 | Yes — stock labelSmall is 11sp/Medium; `.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold)` |
| Bottom sheet title ("Buy Bitcoin") | titleMedium | 18sp / 700 | Yes — stock titleMedium is 16sp/Medium; `.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)` |
| Bottom sheet subtitle ("BTC / USD") | bodySmall | 13sp / 400 | Yes — stock bodySmall is 12sp/Normal; `.copy(fontSize = 13.sp)` |
| Amount input value ("500.00") | titleMedium | 20sp / 700 | Yes — `.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold)` |
| Currency prefix ("$") | bodyLarge | 16sp / 400 | No — stock bodyLarge is 16sp/Normal |
| Approx BTC ("≈ 0.0074 BTC") | labelMedium | 12sp / 400 | Yes — stock labelMedium is 12sp/Medium; `.copy(fontWeight = FontWeight.Normal)` |
| "Pay with" label | bodySmall | 13sp / 400 | Yes — stock bodySmall is 12sp/Normal; `.copy(fontSize = 13.sp)` |
| Wallet label ("Main Wallet · $12,450.00") | labelLarge | 14sp / 400 | Yes — stock labelLarge is 14sp/Medium; `.copy(fontWeight = FontWeight.Normal)` |
| Quick-amount chips (25%/50%/75%/Max) | labelSmall | 12sp / 700 | Yes — stock labelSmall is 11sp/Medium; `.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold)` |
| "Confirm Purchase" button | bodyLarge | 16sp / 700 | Yes — stock bodyLarge is 16sp/Normal; `.copy(fontWeight = FontWeight.Bold)` |
| Failed screen heading "Something went wrong" (shared) | titleLarge | 20sp / 600 | Yes — stock titleLarge is 22sp/Normal; `.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)` |
| Failed screen body text (shared) | bodySmall | 14sp / 400 | Yes — stock bodySmall is 12sp/Normal; `.copy(fontSize = 14.sp)` |
<!-- TYPOGRAPHY_AUDIT:END -->

<!-- MOTION_AUDIT:BEGIN -->
## Motion Audit

**Motion present**: yes

> The `## Motion Inventory` in `tokens_success.md` records the success screen as a static design (no CSS `@keyframes`, `@tailwind animation` config, or JS animation in the HTML — all `transition-*`/`active:*`/`hover:*` entries are interaction/web-only and fall in the DROP bucket). The loading and failed shared-state HTMLs are also static. The 4 KEPT rows below are **design-intent** entries: they follow from the screen's component types (price counter, entrance, loading shimmer, value-driven badge). All magnitudes are marked `infer` because no keyframe data exists in any token inventory; magnitudes must be inferred during implementation.

### Kept motion → Compose

| Element | Family | Compose primitive | Params (dur/easing/repeat/trigger) | Magnitude | Target file |
|---------|--------|-------------------|------------------------------------|-----------|-------------|
| Loading skeleton blocks (all screen sections during loading state) | Loading/Attention loop | `Modifier.shimmer(baseColor, highlightColor, sweepFraction)` | `XMotion.SHIMMER` / `LinearEasing` / infinite / on-screen | bg-position −200%→200% (infer) | DS `motion/` (`Modifier.shimmer`) |
| Screen sections on first load (hero, chart, stats, activity, holders) | Entrance | `AnimatedVisibility(fadeIn() + slideInVertically())` with staggered `delayMillis` per section index | `XMotion.EaseOutExpo` / once / first composition | translateY 30dp→0; opacity 0→1 (infer) | DS `motion/` (`RevealOnAppear`) |
| Price value counter ("$67,420.50") | Value-driven | `animateFloatAsState` on the numeric value | spring / — / on value change | 0→target price float (infer) | feature `motion/AssetDetailMotion.kt` |
| % change badge color (positive ↔ negative flip) | Value-driven | `animateColorAsState` | `XMotion.Standard` / — / on value change | `XTheme.Colors.Success` ↔ `XTheme.Colors.Danger` (infer) | feature `motion/AssetDetailMotion.kt` |

**Reduced motion**: all kept rows gated by `rememberReducedMotion()` (DS `XMotion.kt`, an `expect/actual` reading the OS setting) — reduced ⇒ skip to end/target state. Durations/easings come from `XMotion` tokens, never ad-hoc `tween(<literal>)`.

**Dropped (interaction + web-only)**: `active:scale-95` (back button, Sell/Buy buttons, time-period chip buttons), `hover:bg-surface-variant` (unselected time-period chips, "Join Group" row), `hover:text-primary` ("Join Group" text), `hover:opacity-90` (Retry button on failed state), `transition-colors`, `transition-all`, `duration-150` — no Compose output (per `_shared/motion.md` Web-Motion Policy).
<!-- MOTION_AUDIT:END -->
