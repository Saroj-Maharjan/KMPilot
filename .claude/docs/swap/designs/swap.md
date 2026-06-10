# Swap Screen — Design Description

Crypto swap form screen (Bitcoin → Ethereum). Pushed screen (not a tab destination) — the bottom navigation bar is intentionally suppressed for this transactional, focused flow.

## Screen Summary

Top-to-bottom layout:
1. Transparent top app bar — back arrow (primary gold), title "Swap" (left-aligned next to back arrow)
2. "From Asset" card — Bitcoin icon (primary-container circle, BTC avatar), coin name + ticker, "MAX" chip button, balance label, large editable amount input ("0.00")
3. Swap-direction toggle — circular surface-variant button with `swap_vert` icon, overlapping the From/To cards
4. "To Asset" card — Ethereum icon (surface-variant circle, ETH avatar), coin name + ticker, "You receive" label, large shimmering amount value ("8.5994")
5. Rate row — "1 BTC ≈ 17.84 ETH" with a spinning `sync` icon
6. Details card — Network Fee, Slippage Tolerance (with chevron), Estimated Total rows, separated by hairline dividers
7. Sticky bottom CTA bar — full-width "Review Swap" primary button with gold-glow shadow

<!-- COLOR_AUDIT:BEGIN -->
## Color Audit

Default theme for design: dark

### Defined M3 Roles (already in active scheme — XDarkColors)

| Role | Hex (inventory) | Usage in Design |
|------|-----------------|-----------------|
| background | #0F0D09 | Screen canvas, sticky footer bg (80% opacity, glass-footer) |
| surface | #1C1910 | From/To Asset cards, Details card background |
| surfaceVariant | #302B1C | "MAX" chip bg, swap-direction toggle bg, To-asset (ETH) icon circle bg |
| onSurface | #EDE8D5 | Coin names ("Bitcoin"/"Ethereum"), amount input text, Estimated Total value, body text color (global) |
| onSurfaceVariant | #C4BA94 | Coin tickers (BTC/ETH), balance label, "You receive" label, rate row text, Network Fee/Slippage/Estimated Total labels, chevron icon, amount placeholder (30% alpha) |
| outline | #726A48 | Hover-state card borders (not directly visible in static design; shared state screen uses it for failed-screen body text) |
| outlineVariant | #3F3822 | From/To/Details card borders, hairline dividers, swap-toggle button border |
| primary | #F5D76E | Back arrow icon, "MAX" chip text, swap-toggle `swap_vert` icon, rate-row `sync` icon, CTA fill |
| onPrimary | #2C1900 | CTA "Review Swap" button text |
| primaryContainer | #4A3200 | From-asset (BTC) icon circle background |
| error | #FFB4AB | Warning icon on failed state (shared state screen) |
| onError | #690005 | Failed state shared screen (shared state screen) |
| errorContainer | #93000A | Tailwind config only — not rendered directly (shared state screen) |
| onErrorContainer | #FFDAD6 | Tailwind config only — not rendered directly (shared state screen) |

### Missing M3 Roles (must add to BOTH XLightColors and XDarkColors before implementation)

(none — all design colors map to existing XDarkColors roles)

### Custom Colors (XTheme.Colors.* — justified exceptions only)

| Name | Hex | Justification |
|------|-----|---------------|
| (none expected) | | All swap colors map to existing M3 roles already defined in `XTheme.kt`. |
<!-- COLOR_AUDIT:END -->

<!-- TYPOGRAPHY_AUDIT:BEGIN -->
## Typography Audit

**Design typeface**: Manrope (github-variable; weights 400, 500, 700 from fonts.json; HTML also uses 600, 800)
**Theme font**: Manrope (manrope_variable — `XFontFamily()` in XTheme.kt) — **matches current**

### Text node → M3 role

| Node (usage) | M3 Role | Measured (size/weight) | Override needed? |
|--------------|---------|------------------------|------------------|
| App bar title ("Swap") | headlineSmall (forced by XTopAppBar) | 18sp / 700 (design) vs 24sp / SemiBold (forced) | No — `XTopAppBar` internals force `headlineSmall` SemiBold; design's 18sp/Bold cannot be applied. Accept forced style (catalog limitation, same as send/receive). |
| Coin name ("Bitcoin", "Ethereum") | titleMedium | 18sp / 700 | Yes — stock titleMedium is 16sp/Medium; `.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)` |
| Coin ticker ("BTC", "ETH") | labelSmall | 12sp / 400 | Yes — stock labelSmall is 11sp/Medium; `.copy(fontSize = 12.sp, fontWeight = FontWeight.Normal)` |
| "MAX" chip | labelSmall | 12sp / 700 | Yes — stock labelSmall is 11sp/Medium; `.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold)` |
| "Balance: 0.4821 BTC" | labelLarge | 14sp / 500 | No — stock labelLarge is 14sp/Medium |
| From-amount input ("0.00") | displaySmall | 36sp / 800 | Yes — stock displaySmall is 36sp/Normal; `.copy(fontWeight = FontWeight.ExtraBold)` |
| "You receive" label | labelLarge | 14sp / 500 | No — stock labelLarge is 14sp/Medium |
| To-amount value ("8.5994", shimmer) | displaySmall | 36sp / 800 | Yes — stock displaySmall is 36sp/Normal; `.copy(fontWeight = FontWeight.ExtraBold)` (plus shimmer gradient brush — see Motion Audit) |
| Rate row ("1 BTC ≈ 17.84 ETH") | labelLarge | 14sp / 500 | No — stock labelLarge is 14sp/Medium |
| Details labels ("Network Fee", "Slippage Tolerance", "Estimated Total") | bodyMedium | 14sp / 400 | No — stock bodyMedium is 14sp/Normal |
| Details values ("$7.20", "0.5%") | labelLarge | 14sp / 500 | No — stock labelLarge is 14sp/Medium |
| Estimated Total value ("0.4821 BTC") | titleMedium | 18sp / 700 | Yes — stock titleMedium is 16sp/Medium; `.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)` |
| CTA button ("Review Swap") | titleMedium | 18sp / 700 | Yes — stock titleMedium is 16sp/Medium; `.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)` |
| Failed screen heading "Something went wrong" (shared) | titleLarge | 20sp / 600 | Yes — stock titleLarge is 22sp/Normal; `.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)` |
| Failed screen body text (shared) | bodySmall | 14sp / 400 | Yes — stock bodySmall is 12sp/Normal; `.copy(fontSize = 14.sp)` |
| Failed screen "Retry" button (shared) | bodyLarge | 16sp / 700 | Yes — stock bodyLarge is 16sp/Normal; `.copy(fontWeight = FontWeight.Bold)` |
| Failed screen "Return to Dashboard" (shared) | labelLarge | 14sp / 500 | No — stock labelLarge is 14sp/Medium |
<!-- TYPOGRAPHY_AUDIT:END -->

<!-- MOTION_AUDIT:BEGIN -->
## Motion Audit

**Motion present**: yes

### Kept motion → Compose

| Element | Family | Compose primitive | Params (dur/easing/repeat/trigger) | Magnitude | Target file |
|---------|--------|-------------------|------------------------------------|-----------|-------------|
| To-amount shimmer text ("8.5994", `.shimmer-glow`) | Loading/Attention loop | `XText(style = TextStyle(brush = Brush.linearGradient(...)))` animated via `rememberInfiniteTransition` offset | `XMotion.SHIMMER` (3s — closest token) / `XMotion.Linear` / infinite / on-screen | bg-position 200% center | feature `motion/SwapMotion.kt` |
| Rate-row `sync` icon (`animate-spin`, 4s) | Loading/Attention loop | `rememberInfiniteTransition` → `rotationZ` 0→360 via `Modifier.graphicsLayer` | 4s (inventory-specified, overrides `XMotion.SHIMMER` default) / `XMotion.Linear` / infinite / on-screen | rotation 0deg → 360deg (infer — standard full-turn spin) | feature `motion/SwapMotion.kt` |

**Reduced motion**: all kept rows gated by `rememberReducedMotion()` (DS `XMotion.kt` — `expect/actual`, reads OS setting). Durations/easings via `XMotion` tokens, not ad-hoc `tween(<literal>)`.

**Dropped (interaction + web-only)**: `active:scale-95` (back button, MAX chip), `hover:brightness-110` (MAX chip), `hover:border-outline/50` (From/To asset cards), `hover:border-primary/50` (swap-direction toggle), `active:rotate-180` (swap-direction toggle), `group-hover:text-primary` (Slippage chevron), `hover:shadow-gold-intense` + `active:scale-[0.98]` (Review Swap CTA), `transition-colors`, `transition-all`, `duration-500`, `duration-300`, `focus:ring-0` (amount input).
<!-- MOTION_AUDIT:END -->
