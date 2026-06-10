# Compose Implementation Blueprint: Swap

> **Decomposed** — 6 distinct visual sections (From Asset card, swap-direction toggle, To Asset card, rate row, Details card, sticky CTA). Screen dimensions: 780×1768px (success + shared loading/failed).
> Token inventory: `.claude/docs/swap/designs/extracted/tokens_success.md`
> HTML source: `.claude/docs/swap/designs/extracted/stitch_success.html`

---

## Design Tokens

| Hex | M3 Role | Usage |
|-----|---------|-------|
| #0F0D09 | background | Screen canvas, sticky footer bg (80% opacity, glass-footer) |
| #1C1910 | surface | From/To Asset cards, Details card background |
| #302B1C | surfaceVariant | "MAX" chip bg, swap-direction toggle bg, To-asset (ETH) icon circle bg |
| #EDE8D5 | onSurface | Coin names, amount input text, Estimated Total value |
| #C4BA94 | onSurfaceVariant | Coin tickers, balance label, "You receive" label, rate row text, Details labels, chevron, placeholder (30% alpha) |
| #3F3822 | outlineVariant | Card borders, hairline dividers, swap-toggle border |
| #F5D76E | primary | Back arrow, "MAX" text, swap_vert icon, sync icon, CTA fill |
| #2C1900 | onPrimary | CTA "Review Swap" text |
| #4A3200 | primaryContainer | From-asset (BTC) icon circle background |

---

## Typography Scale

> Typography is app-global (see `_shared/patterns.md` → "Typography"). Each text node maps to an **M3 type-scale role** exactly as each fill maps to an M3 color role — implementation uses `style = MaterialTheme.typography.{role}`, not raw `fontSize`.

| Usage | M3 Role | Size (sp) | Weight | Letter Spacing | Text Transform | Color Role |
|-------|---------|-----------|--------|----------------|-----------------|------------|
| App bar title "Swap" | headlineSmall (forced by XTopAppBar) | 24 (forced) | SemiBold (forced) | -0.025em (design, not applied) | none | onSurface |
| Coin name ("Bitcoin", "Ethereum") | titleMedium | 18 | Bold (700) | 0 | none | onSurface |
| Coin ticker ("BTC", "ETH") | labelSmall | 12 | Normal (400) | 0.05em | uppercase | onSurfaceVariant |
| "MAX" chip | labelSmall | 12 | Bold (700) | 0 | uppercase (literal) | primary |
| Balance label ("Balance: 0.4821 BTC") | labelLarge | 14 | Medium (500) | 0 | none | onSurfaceVariant |
| From-amount input ("0.00") | displaySmall | 36 | ExtraBold (800) | 0 | none | onSurface |
| "You receive" label | labelLarge | 14 | Medium (500) | 0 | none | onSurfaceVariant |
| To-amount value ("8.5994", shimmer) | displaySmall | 36 | ExtraBold (800) | 0 | none | shimmer gradient (onSurface↔primary) |
| Rate row ("1 BTC ≈ 17.84 ETH") | labelLarge | 14 | Medium (500) | 0 | none | onSurfaceVariant |
| Details label ("Network Fee" etc.) | bodyMedium | 14 | Normal (400) | 0 | none | onSurfaceVariant |
| Details value ("$7.20", "0.5%") | labelLarge | 14 | Medium (500) | 0 | none | onSurface |
| Estimated Total value ("0.4821 BTC") | titleMedium | 18 | Bold (700) | 0 | none | onSurface |
| CTA "Review Swap" | titleMedium | 18 | Bold (700) | 0 | none | onPrimary |

---

## Spacing Grid

| Context | Property | Value (dp) |
|---------|----------|------------|
| Screen | horizontal padding (main) | 16 |
| Screen | top padding (first content offset) | 8 |
| Screen | bottom padding (footer clearance) | 160 |
| Main content | vertical gap between sections | 16 |
| Asset card | padding | 24 all sides |
| Asset card | corner radius | 20 |
| Asset card header row | bottom margin | 16 |
| Asset card icon circle | size | 40 (icon image 24) |
| Asset card name/ticker | gap | 0 (Column, no explicit gap) |
| Asset card icon + name/ticker | gap | 12 |
| Balance row → amount input | top margin | 8 |
| "MAX" chip | padding | horizontal 12, vertical 6 |
| "MAX" chip | corner radius | 16 |
| Swap-direction toggle | size | 48 |
| Swap-direction toggle | corner radius | CircleShape |
| Swap-direction toggle row | height | 16 |
| Rate row | vertical padding | 8 |
| Rate row | gap (text ↔ icon) | 8 |
| Details card | padding | 24 all sides |
| Details card | corner radius | 20 |
| Details card | row vertical spacing | 16 |
| Details divider | height | 1 |
| Footer | padding | 24 all sides |
| CTA button | height | 56 |
| CTA button | corner radius | 24 |

---

## Component Tree

### Shared Scaffold (all states)

```
// → SwapScreen.kt
XScreen(
    topBar = {
        XTopAppBar(
            title = { XText(text = stringResource(Res.string.swap_title)) },  // "Swap"
            backgroundColor = Color.Transparent,
            navigationIcon = {
                XIconButton(
                    onClick = onBackClick,
                    colors = XIconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    XIcon(
                        painter = painterResource(DesignSystemResources.drawable.arrow_back),
                        contentDescription = stringResource(DesignSystemResources.string.cd_back)
                    )
                }
            }
            // alignment defaults to XTopAppBarAlignment.Start — matches the design's
            // back-arrow + left-adjacent title (no custom Row needed; default changed in 824693f)
        )
    },
    bottomBar = {
        // [state-specific — see Success State below; Loading/Failed render no bottomBar]
    }
) {
    // [state-specific content slot]
}
```

**XTopAppBar notes**:
- `alignment` defaults to `XTopAppBarAlignment.Start` (a plain `TopAppBar`), which left-aligns the title next to the navigation icon — matches the design's `<h1 class="ml-2">Swap</h1>` directly. No custom `Row` workaround needed (unlike receive, which needed `Center` override avoidance before this default existed).
- Title style is forced to `headlineSmall` SemiBold by `XTopAppBar` internals — design's 18sp/Bold cannot be applied; accept forced style.
- `backgroundColor = Color.Transparent` — matches `bg-background/80 backdrop-blur-sm` (transparent is the closest Compose approximation; blur is omitted as decorative).

---

### Success State

```
// → SwapScreen.kt (state routing + content scaffold)
Column(
    modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp)
        .padding(top = 8.dp, bottom = 160.dp),  // 160dp clears the glass-footer
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    FromAssetSection(...)        // → components/FromAssetSection.kt
    SwapDirectionToggle(...)     // → components/SwapDirectionToggle.kt
    ToAssetSection(...)          // → components/ToAssetSection.kt
    RateRow(...)                 // → components/RateRow.kt
    SwapDetailsCard(...)         // → components/SwapDetailsCard.kt
}

// Sticky footer — XScreen bottomBar slot:
// → SwapScreen.kt
Box(
    modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))  // glass-footer
        .windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime))
        .padding(24.dp)
) {
    XButton(
        onClick = onReviewSwapClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(elevation = 0.dp, shape = RoundedCornerShape(24.dp)),
            // [decorative gold-glow shadow: box-shadow 0 0 20px rgba(245,215,110,0.25) —
            //  implement via drawBehind Canvas if desired, omit if not critical]
        shape = RoundedCornerShape(24.dp),  // OVERRIDE: XButton default is CircleShape
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        XText(
            text = stringResource(Res.string.swap_review_cta),  // "Review Swap"
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}
```

---

### components/AssetCard.kt

```kotlin
// → components/AssetCard.kt
// Shared card shell for both From/To asset sections — surface bg, outlineVariant border, 20dp corners.
@Composable
fun AssetCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            .padding(24.dp),
        content = content
    )
}

// Private helper — shared header row (icon circle + name/ticker + trailing slot)
@Composable
fun AssetCardHeader(
    avatarUrl: String,
    avatarContentDescription: String,
    iconCircleColor: Color,
    coinName: String,
    coinTicker: String,
    trailing: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconCircleColor, CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    url = avatarUrl,
                    loadingResId = DesignSystemResources.drawable.ds_image_placeholder,
                    contentDescription = avatarContentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(24.dp).clip(CircleShape)
                )
            }
            Column {
                XText(
                    text = coinName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                XText(
                    text = coinTicker,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = (0.05 * 12).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        trailing()
    }
}
```

> **AssetCardHeader colors**: `coinTicker` and the row's trailing slot pass their own colors; `coinName` uses default `onSurface` (LocalContentColor inherits from `AssetCard`'s `Column` — no explicit color needed since `onSurface` is the default text color on `surface`).

---

### components/FromAssetSection.kt

```kotlin
// → components/FromAssetSection.kt
@Composable
fun FromAssetSection(
    avatarUrl: String,
    coinName: String,           // "Bitcoin"
    coinTicker: String,         // "BTC"
    balanceLabel: String,       // "Balance: 0.4821 BTC"
    amount: String,             // "0.00"
    onAmountChange: (String) -> Unit,
    onMaxClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AssetCard(modifier = modifier) {
        AssetCardHeader(
            avatarUrl = avatarUrl,
            avatarContentDescription = coinTicker,
            iconCircleColor = MaterialTheme.colorScheme.primaryContainer,
            coinName = coinName,
            coinTicker = coinTicker,
            trailing = {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                        .clickable(onClick = onMaxClick)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    XText(
                        text = stringResource(Res.string.swap_max_label),  // "MAX"
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            XText(
                text = stringResource(Res.string.swap_balance_template, balanceLabel),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            BasicTextField(
                value = amount,
                onValueChange = onAmountChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textStyle = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (amount.isEmpty()) {
                        XText(
                            text = stringResource(Res.string.swap_amount_placeholder),  // "0.00"
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            )
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}
```

> **`BasicTextField` choice**: matches send's `RecipientCard` pattern — `XTextField`'s 280dp min-width + CircleShape/RoundedCornerShape(20dp) + visible surface bg + 48dp min-height are far too divergent from the design's transparent, borderless, 36sp ExtraBold input (see Component Overrides).

---

### components/SwapDirectionToggle.kt

```kotlin
// → components/SwapDirectionToggle.kt
@Composable
fun SwapDirectionToggle(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth().height(16.dp),
        contentAlignment = Alignment.Center
    ) {
        XIconButton(
            onClick = onClick,
            modifier = Modifier
                .size(48.dp)
                .shadow(elevation = 8.dp, shape = CircleShape),  // shadow-lg
            colors = XIconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            XIcon(
                painter = painterResource(Res.drawable.swap_vert),
                contentDescription = stringResource(Res.string.cd_swap_direction),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
```

> **`XIconButton` override**: `containerColor = surfaceVariant` + `border = BorderStroke(1.dp, outlineVariant)` + explicit `Modifier.size(48.dp)` — default renders a smaller `surface`-colored circle with no border (see Component Overrides).

---

### components/ToAssetSection.kt

```kotlin
// → components/ToAssetSection.kt
@Composable
fun ToAssetSection(
    avatarUrl: String,
    coinName: String,           // "Ethereum"
    coinTicker: String,         // "ETH"
    receiveAmount: String,      // "8.5994"
    modifier: Modifier = Modifier
) {
    AssetCard(modifier = modifier) {
        AssetCardHeader(
            avatarUrl = avatarUrl,
            avatarContentDescription = coinTicker,
            iconCircleColor = MaterialTheme.colorScheme.surfaceVariant,
            coinName = coinName,
            coinTicker = coinTicker,
            trailing = {
                XText(
                    text = stringResource(Res.string.swap_you_receive),  // "You receive"
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
        // Shimmering receive amount — see motion/SwapMotion.kt for the gradient brush animation
        XText(
            text = receiveAmount,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.ExtraBold,
                brush = shimmerBrush()  // → motion/SwapMotion.kt
            )
        )
    }
}
```

---

### components/RateRow.kt

```kotlin
// → components/RateRow.kt
@Composable
fun RateRow(
    rateText: String,  // "1 BTC ≈ 17.84 ETH"
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        XText(
            text = rateText,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(8.dp))
        XIcon(
            painter = painterResource(Res.drawable.sync),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer { rotationZ = syncRotation() }  // → motion/SwapMotion.kt
        )
    }
}
```

---

### components/SwapDetailsCard.kt

```kotlin
// → components/SwapDetailsCard.kt
@Composable
fun SwapDetailsCard(
    networkFee: String,        // "$7.20"
    slippage: String,          // "0.5%"
    estimatedTotal: String,    // "0.4821 BTC"
    onSlippageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DetailsRow(
            label = stringResource(Res.string.swap_network_fee_label),
            value = networkFee
        )
        XHorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSlippageClick),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            XText(
                text = stringResource(Res.string.swap_slippage_label),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                XText(
                    text = slippage,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                XIcon(
                    painter = painterResource(DesignSystemResources.drawable.chevron_right),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        XHorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            XText(
                text = stringResource(Res.string.swap_estimated_total_label),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            XText(
                text = estimatedTotal,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

// Private helper — stays in SwapDetailsCard.kt
@Composable
private fun DetailsRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        XText(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        XText(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
```

---

### motion/SwapMotion.kt

```kotlin
// → presentation/ui/motion/SwapMotion.kt
@Composable
fun shimmerBrush(): Brush {
    if (rememberReducedMotion()) {
        // Reduced motion: static gradient, no animation — jump to end state
        return Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.onSurface
            )
        )
    }
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,  // bg-position 0% → 200% center (full sweep)
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = XMotion.SHIMMER, easing = XMotion.Linear),  // 3s, linear
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )
    val colors = listOf(
        MaterialTheme.colorScheme.onSurface,
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.onSurface
    )
    return Brush.linearGradient(
        colors = colors,
        start = Offset(offset * 1000f - 500f, 0f),
        end = Offset(offset * 1000f + 500f, 0f)
    )
}

@Composable
fun syncRotation(): Float {
    if (rememberReducedMotion()) return 0f  // reduced motion: static, no spin
    val transition = rememberInfiniteTransition(label = "sync")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = XMotion.Linear),  // 4s — inventory-specified, overrides XMotion.SHIMMER
            repeatMode = RepeatMode.Restart
        ),
        label = "syncRotation"
    )
    return rotation
}
```

> **4s duration**: `tokens_success.md` specifies `animation-duration: 4s` inline on the `sync` icon — this overrides the default `XMotion.SHIMMER` (2000ms) token; the literal `4000` is a design-specified deviation, not an ad-hoc value (matches the inventory's explicit inline style).

---

### Loading State

Shared screen — see: `.claude/docs/_shared/designs/loading.png`
Token inventory: `.claude/docs/_shared/designs/extracted/tokens_loading.md`

```
// → SwapScreen.kt — routes to shared AppLoadingState; XScreen bottomBar = {} for this state
AppLoadingState()
```

---

### Failed State

Shared screen — see: `.claude/docs/_shared/designs/failed.png`
Token inventory: `.claude/docs/_shared/designs/extracted/tokens_failed.md`

```
// → SwapScreen.kt — routes to shared AppErrorState; XScreen bottomBar = {} for this state
AppErrorState(
    title = stringResource(Res.string.swap_error_title),      // "Something went wrong"
    message = stringResource(Res.string.swap_error_message),  // "An unexpected error occurred..."
    onRetry = onRetry,
    secondaryAction = { /* "Return to Dashboard" — wired by implementer per AppErrorState contract */ }
)
```

---

## String Inventory

| Key | Default (English) value | Where used | Notes |
|-----|--------------------------|------------|-------|
| swap_title | Swap | top bar | |
| swap_max_label | MAX | From-asset chip | |
| swap_balance_template | Balance: %1$s | From-asset card | format arg = balance amount + ticker |
| swap_amount_placeholder | 0.00 | From-asset amount input | |
| swap_you_receive | You receive | To-asset card | |
| swap_rate_template | %1$s | rate row | repository-supplied "1 BTC ≈ 17.84 ETH" string — format arg only, not literal text |
| swap_network_fee_label | Network Fee | Details card | |
| swap_slippage_label | Slippage Tolerance | Details card | |
| swap_estimated_total_label | Estimated Total | Details card | |
| swap_review_cta | Review Swap | sticky CTA | |
| swap_error_title | Something went wrong | failed state (shared) | passed to `AppErrorState` |
| swap_error_message | An unexpected error occurred. Please try again or check your connection. | failed state (shared) | passed to `AppErrorState` |
| cd_swap_direction | Swap direction | swap-toggle icon button | content description |

> "Bitcoin"/"Ethereum"/"BTC"/"ETH"/"$7.20"/"0.5%"/"0.4821 BTC"/"8.5994" are repository-supplied data (coin names, tickers, amounts) — not string resources. Shared strings (Retry / "Return to Dashboard" wording, common errors) come from `DesignSystemResources` via `AppErrorState`.

---

## Motion

> Built from the Step 1.16 Motion Audit + `tokens_success.md` `## Motion Inventory`. Buckets and family→primitive mapping live in [`_shared/motion.md`](../../_shared/motion.md).

| Element | Family | Compose primitive | Params (dur/easing/repeat/trigger) | Magnitude | Target file |
|---------|--------|-------------------|------------------------------------|-----------|-------------|
| To-amount shimmer text ("8.5994", `.shimmer-glow`) | Loading/Attention loop | `XText(style = TextStyle(brush = Brush.linearGradient(...)))` driven by `rememberInfiniteTransition` offset | `XMotion.SHIMMER` (3s) / `XMotion.Linear` / infinite / on-screen | bg-position 200% center | feature `motion/SwapMotion.kt` |
| Rate-row `sync` icon (`animate-spin`, 4s) | Loading/Attention loop | `rememberInfiniteTransition` → `rotationZ` via `Modifier.graphicsLayer` | 4s (inventory-specified) / `XMotion.Linear` / infinite / on-screen | rotation 0deg → 360deg (infer — full turn) | feature `motion/SwapMotion.kt` |

**Reduced motion**: both rows gated by `rememberReducedMotion()` (DS `XMotion.kt` — `expect/actual`, reads OS setting). Durations/easings via `XMotion` tokens, not ad-hoc `tween(<literal>)`.

**Dropped (interaction + web-only)**: `active:scale-95` (back button, MAX chip), `hover:brightness-110` (MAX chip), `hover:border-outline/50` (From/To asset cards), `hover:border-primary/50` (swap-direction toggle), `active:rotate-180` (swap-direction toggle), `group-hover:text-primary` (Slippage chevron), `hover:shadow-gold-intense` + `active:scale-[0.98]` (Review Swap CTA), `transition-colors`, `transition-all`, `duration-500`, `duration-300`, `focus:ring-0` (amount input).

---

## Pre-Implementation Contract

> Architecture rules, color rules, and X-component defaults are project-wide and live in their canonical sources — do not restate them here:
> - Architecture rules → [`_shared/patterns.md`](../../_shared/patterns.md)
> - Color rules → [`m3-colors.md`](../references/m3-colors.md)
> - X-component default-render behavior → [`_shared/X_COMPONENTS_CATALOG.md`](../../_shared/X_COMPONENTS_CATALOG.md)
>
> This contract captures only feature-specific data the implementer cannot derive from those references.

### XTheme Updates Required

(none — all design colors map to existing `XDarkColors`/`XLightColors` roles)

### Typography Updates Required

**Font swap**: none — design uses Manrope, theme already ships Manrope (`manrope_variable`, matches current).

**Type-scale role overrides**:

| Node | Chosen Role | Stock Role Value | Measured Value | Override |
|------|-------------|-------------------|------------------|----------|
| Coin name ("Bitcoin", "Ethereum") | titleMedium | 16sp/Medium | 18sp/Bold | `style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)` |
| Coin ticker ("BTC", "ETH") | labelSmall | 11sp/Medium | 12sp/Normal | `style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Normal)` |
| "MAX" chip | labelSmall | 11sp/Medium | 12sp/Bold | `style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold)` |
| From-amount input ("0.00") | displaySmall | 36sp/Normal | 36sp/ExtraBold | `style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold)` |
| To-amount value ("8.5994") | displaySmall | 36sp/Normal | 36sp/ExtraBold | `style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold)` (+ shimmer brush) |
| Estimated Total value ("0.4821 BTC") | titleMedium | 16sp/Medium | 18sp/Bold | `style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)` |
| CTA "Review Swap" | titleMedium | 16sp/Medium | 18sp/Bold | `style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)` |
| Failed heading "Something went wrong" (shared) | titleLarge | 22sp/Normal | 20sp/SemiBold | `style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)` (handled inside shared `AppErrorState`) |
| Failed body text (shared) | bodySmall | 12sp/Normal | 14sp/Normal | `style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)` (handled inside shared `AppErrorState`) |
| Failed "Retry" button (shared) | bodyLarge | 16sp/Normal | 16sp/Bold | `style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)` (handled inside shared `AppErrorState`) |

> Roles needing no override (used as-is): App bar title (forced headlineSmall), Balance label / "You receive" / Rate row (labelLarge), Details labels (bodyMedium), Details values (labelLarge), Failed "Return to Dashboard" (labelLarge).

### Color Audit

#### Defined Roles

| Role | Hex | Usage |
|------|-----|-------|
| background | #0F0D09 | Screen canvas, sticky footer bg (80% opacity) |
| surface | #1C1910 | From/To Asset cards, Details card |
| surfaceVariant | #302B1C | "MAX" chip bg, swap-toggle bg, To-asset (ETH) icon circle |
| onSurface | #EDE8D5 | Coin names, amount input text, Estimated Total value |
| onSurfaceVariant | #C4BA94 | Tickers, balance label, "You receive", rate row, Details labels, chevron |
| outlineVariant | #3F3822 | Card borders, dividers, swap-toggle border |
| primary | #F5D76E | Back arrow, "MAX" text, swap_vert/sync icons, CTA fill |
| onPrimary | #2C1900 | CTA text |
| primaryContainer | #4A3200 | From-asset (BTC) icon circle bg |
| error | #FFB4AB | Warning icon (failed state, shared) |
| onError | #690005 | Failed state (shared) |

#### Missing Roles (must add before implementation)

(none)

#### Custom Colors (justified exceptions only)

(none)

#### Component Overrides (divergences from X-component defaults)

| Component | Property | HTML Value | X-component Default | Override Required |
|-----------|----------|-----------|---------------------|---------------------|
| `XButton` (CTA "Review Swap") | shape | 24dp (rounded-3xl) | CircleShape | `shape = RoundedCornerShape(24.dp)` |
| `XButton` (CTA "Review Swap") | gold-glow shadow | `box-shadow 0 0 20px rgba(245,215,110,0.25)` | none | `drawBehind` glow or omit (decorative) |
| `XIconButton` (back arrow) | containerColor | transparent | surface | `containerColor = Color.Transparent` |
| `XIconButton` (swap-direction toggle) | containerColor / shape / border | surfaceVariant bg, CircleShape, 1dp outlineVariant border, 48dp size | surface-colored circle, no border | `containerColor = MaterialTheme.colorScheme.surfaceVariant`, `border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)`, `modifier = Modifier.size(48.dp)` |
| From/To Asset cards | background, shape, border | surface, 20dp corners, 1dp outlineVariant border | — (custom `Column`, not `XCard`) | `Column.background(surface, RoundedCornerShape(20.dp)).border(1.dp, outlineVariant, RoundedCornerShape(20.dp))` |
| Details card | background, shape, border | surface, 20dp corners, 1dp outlineVariant border | XCard: surfaceVariant, 12dp, no border | `Column.background(surface, RoundedCornerShape(20.dp)).border(1.dp, outlineVariant, RoundedCornerShape(20.dp))` — do not use `XCard` |
| "MAX" chip | shape, bg, text color | 16dp corners, surfaceVariant bg, primary text | `XFilterChip`: CircleShape, surface bg, onSurfaceVariant text | Use custom `Box` — do not use `XFilterChip` |
| From-amount input | component | transparent `BasicTextField`, 36sp ExtraBold, no border/bg | `XTextField`: 280dp min-width, CircleShape/RoundedCornerShape(20dp), surface bg, 48dp min-height | Use `BasicTextField` (matches send's RecipientCard pattern) — `XTextField` defaults are far too divergent (visible bg/border/min-size) |

---

## Post-Implementation Checklist

- [ ] `FromAssetSection` implemented: `AssetCard` (surface, 20dp corners, outlineVariant border) with BTC icon (primaryContainer circle, `AsyncImage` avatar), coin name/ticker, "MAX" chip (custom `Box`, surfaceVariant bg, 16dp corners), balance label, transparent `BasicTextField` (36sp ExtraBold)
- [ ] `SwapDirectionToggle` implemented: 48dp `XIconButton` — `containerColor = surfaceVariant`, `border = BorderStroke(1.dp, outlineVariant)`, CircleShape, `swap_vert` icon (`Res.drawable.swap_vert`, primary tint)
- [ ] `ToAssetSection` implemented: `AssetCard` with ETH icon (surfaceVariant circle, `AsyncImage` avatar), coin name/ticker, "You receive" label, shimmering "8.5994" value (displaySmall ExtraBold, gradient brush per Motion table)
- [ ] `RateRow` implemented: "1 BTC ≈ 17.84 ETH" (labelLarge, onSurfaceVariant) + spinning `sync` icon (`Res.drawable.sync`, primary tint, 4s rotation per Motion table)
- [ ] `SwapDetailsCard` implemented: surface bg, 20dp corners, outlineVariant border, 3 rows (Network Fee / Slippage Tolerance with `chevron_right` / Estimated Total) separated by 1dp `outlineVariant` `XHorizontalDivider`s
- [ ] Sticky footer `Box` in `XScreen.bottomBar` — `background(background.copy(alpha = 0.8f))`, `windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime))`, 24dp padding, `XButton` (24dp corners, primary fill, onPrimary text, titleMedium Bold "Review Swap")
- [ ] `XTopAppBar` used directly (default `XTopAppBarAlignment.Start`) — back arrow `XIconButton` with `containerColor = Color.Transparent`, `contentColor = primary`; title "Swap"
- [ ] Every Material Symbol uses its `icons.json` `res_reference`: `arrow_back` → `DesignSystemResources.drawable.arrow_back`, `chevron_right` → `DesignSystemResources.drawable.chevron_right`, `swap_vert` → `Res.drawable.swap_vert`, `sync` → `Res.drawable.sync`, `warning` → `DesignSystemResources.drawable.warning` (failed state, inside `AppErrorState`) — no `Icons.Default.*`
- [ ] Both avatar images (`avatarUrl` BTC + ETH) rendered via design-system `AsyncImage(url = avatarUrl, loadingResId = DesignSystemResources.drawable.ds_image_placeholder, ...)` — wire each `avatarUrl` field on the data layer / `*UiModel` (2 sub-items: From-asset avatar, To-asset avatar)
- [ ] `failed_background` image (bundled, shared failed state) rendered via `Image(painterResource(DesignSystemResources.drawable.failed_background))` inside `AppErrorState` — already shared, verify no per-feature duplication
- [ ] `motion/SwapMotion.kt` created: shimmer gradient brush animation (To-amount value) + `sync` icon rotation (4s, linear, infinite) — both gated by `rememberReducedMotion()`, durations/easings via `XMotion` tokens
- [ ] Every text node uses `style = MaterialTheme.typography.{role}` (with the recorded `.copy(...)` overrides) — no raw `fontSize`/`fontWeight` outside this contract
- [ ] All colors use `MaterialTheme.colorScheme.{role}` — no raw `Color(hex)` values
- [ ] All 4 UI states wired: Uninitialized, Loading (`AppLoadingState`), Success (full swap form), Failed (`AppErrorState` with retry + "Return to Dashboard" secondary action)
- [ ] No tab nav bar in `XScreen.bottomBar` — confirmed: design has none (bottom nav explicitly suppressed per HTML comment)
- [ ] Every String Inventory key exists in `composeResources/values/strings.xml` and is referenced via `stringResource` — no hardcoded display literals
- [ ] Build passes: `./gradlew :feature:swap:assembleAndroidMain`
- [ ] Code formatted: `./gradlew :feature:swap:ktlintFormat`
