# Compose Implementation Blueprint: AssetDetail

## Design Tokens

| Hex | M3 Role / Custom | Usage |
|-----|-----------------|-------|
| #F5D76E | `primary` | Back icon, coin circle bg, selected chip, Sell border/text, Buy bg, slider thumb/fill, wallet icon, Confirm bg |
| #2C1900 | `onPrimary` | Coin icon, Buy text, selected chip text, Confirm text |
| #4A3200 | `primaryContainer` | Hero gradient start, "+42" avatar bg |
| #FFF0C0 | `onPrimaryContainer` | Config reference only — no direct text use |
| #0F0D09 | `background` | Screen bg, avatar ring border, scrim (#0F0D09 @ 60%), hero gradient end |
| #1C1910 | `surface` | Stats card bg, activity row bg (@ 50% alpha), sticky footer bg, bottom sheet bg |
| #EDE8D5 | `onSurface` | Screen title, price, coin name, stats values, activity titles, section headers, amount input value |
| #C4BA94 | `onSurfaceVariant` | Chart axis labels, unselected chip text, stats labels, timestamps, fiat equivs, "Join Group", currency prefix, approx BTC, Pay-with label |
| #302B1C | `surfaceVariant` | Avatar bg circles, amount input bg, unselected quick-amount chip bg, Pay-with dropdown bg |
| #726A48 | `outline` | Failed screen body text (shared state screen) |
| #3F3822 | `outlineVariant` | Unselected chip border, stats card border, activity row border (@30% alpha), footer top border, drag handle, Pay-with border, slider track |
| #FFB4AB | `error` | Warning icon on failed state (shared state screen) |
| #690005 | `onError` | Failed state (shared state screen) |
| #93000A | `errorContainer` | Failed state (shared state screen) |
| #FFDAD6 | `onErrorContainer` | Failed state (shared state screen) |
| #4ADE80 | `XTheme.Colors.Success` | Positive %-badge, received/staked amount text, activity icon circle tint |
| #FF6B6B | `XTheme.Colors.Danger` | Negative %-badge, sent amount text, sent activity icon circle tint |

## Typography Scale

> Typography is app-global (see `_shared/patterns.md` → "Typography"). Each text node maps to an **M3 type-scale role** — implementation uses `style = MaterialTheme.typography.{role}`, not raw `fontSize`. The `Size`/`Weight` columns are the design's measured values from the token inventory; they flag divergences that need `.copy(...)` overrides recorded under *Typography Updates Required*.

| Usage | M3 Role | Size (sp) | Weight | Color Role |
|-------|---------|-----------|--------|------------|
| Screen title "Bitcoin" | `titleLarge` | 22 | Bold (700) | `onSurface` |
| Coin name + ticker "Bitcoin (BTC)" | `titleMedium` | 18 | Bold (700) | `onSurfaceVariant` |
| Price "$67,420.50" | `displaySmall` | 36 | ExtraBold (800) | `onSurface` |
| % change badge | `labelLarge` | 14 | Bold (700) | `XTheme.Colors.Success` / `XTheme.Colors.Danger` |
| Chart axis labels (Y/X) | `labelSmall` | 10 | Medium (500) | `onSurfaceVariant` |
| Time period chip text | `labelLarge` | 14 | Bold (700) | `onPrimary` (selected) / `onSurfaceVariant` (unselected) |
| Stats card label | `labelMedium` | 12 | Medium (500) | `onSurfaceVariant` |
| Stats card value | `titleMedium` | 18 | Bold (700) | `onSurface` |
| Holdings fiat equiv "~$5,679.42" | `labelSmall` | 10 | Normal (400) | `onSurfaceVariant` |
| Section headers ("Recent Activity", "Top Holders Community") | `titleLarge` | 20 | Bold (700) | `onSurface` |
| "See all" text button | `labelLarge` | 14 | Bold (700) | `primary` |
| "Join Group" text button | `labelLarge` | 14 | Bold (700) | `onSurfaceVariant` |
| Activity row title | `labelLarge` | 14 | Bold (700) | `onSurface` |
| Activity timestamp | `labelMedium` | 12 | Normal (400) | `onSurfaceVariant` |
| Activity signed amount | `labelLarge` | 14 | Bold (700) | `XTheme.Colors.Success` / `XTheme.Colors.Danger` |
| Activity fiat equiv | `labelSmall` | 12 | Normal (400) | `onSurfaceVariant` |
| Avatar initials | `labelSmall` | 12 | Bold (700) | `primary` |
| Bottom sheet title "Buy Bitcoin" | `titleMedium` | 18 | Bold (700) | `onSurface` |
| Bottom sheet subtitle "BTC / USD" | `bodySmall` | 13 | Normal (400) | `onSurfaceVariant` |
| Amount input value "500.00" | `titleMedium` | 20 | Bold (700) | `onSurface` |
| Currency prefix "$" | `bodyLarge` | 16 | Normal (400) | `onSurfaceVariant` |
| Approx BTC "≈ 0.0074 BTC" | `labelMedium` | 12 | Normal (400) | `onSurfaceVariant` |
| "Pay with" label | `bodySmall` | 13 | Normal (400) | `onSurfaceVariant` |
| Wallet label "Main Wallet · $12,450.00" | `labelLarge` | 14 | Normal (400) | `onSurface` |
| Quick-amount chips (25%/50%/75%/Max) | `labelSmall` | 12 | Bold (700) | `onPrimary` (selected) / `onSurfaceVariant` (unselected) |
| "Confirm Purchase" button | `bodyLarge` | 16 | Bold (700) | `onPrimary` |
| Failed screen heading "Something went wrong" (shared) | `titleLarge` | 20 | SemiBold (600) | `onSurfaceVariant` |
| Failed screen body text (shared) | `bodySmall` | 14 | Normal (400) | `outline` |

## Spacing Grid

| Context | Property | Value (dp) |
|---------|----------|------------|
| Screen content | horizontal padding | 24 |
| Hero section | top padding (first content element, after XScreen stacks topBar) | 16 (body pt-24 = header clearance — absorbed by XScreen; use 16dp for first content offset) |
| Hero section | bottom padding | 32 |
| Bitcoin logo circle | size | 64×64; bottom margin | 16 |
| Hero gradient overlay | height | 240dp |
| Price | bottom margin | 12 |
| Chart section | bottom margin | 32 |
| Chart SVG area | height | 180dp |
| Chart Y-axis labels column | left edge | 0; chart area left margin | 40dp |
| Chart X-axis labels | top margin | 8 |
| Time period chips section | bottom margin | 40 |
| Chip | horizontal padding | 20; vertical padding | 8; gap between | 8 |
| Stats grid | bottom margin | 40; grid gap | 16 |
| Stats card | padding | 20; corner radius | 24dp |
| Activity section | bottom margin | 40 |
| Activity header row | bottom margin | 16 |
| Activity rows | gap (space-y-4) | 16 |
| Activity row | padding | 16; corner radius | 24dp |
| Activity icon circle | size | 40×40; gap to text | 16 |
| Top Holders section | bottom margin | 48 |
| Avatar circle | size | 40×40; ring border | 2dp background-color |
| Avatar overlap offset | margin-right | −12dp |
| Sticky footer | horizontal padding | 24; top padding | 16; bottom padding | 40; button gap | 16 |
| Sell/Buy buttons | height | 56dp; corner radius | 24dp |
| Bottom sheet drag handle | width | 40dp; height | 4dp; top margin | 12; bottom margin | 16 |
| Bottom sheet header | horizontal padding | 20; bottom margin | 24 |
| Amount input field | height | 56dp; horizontal padding | 16; corner radius | 24dp; border | 1.5dp |
| Slider section | horizontal padding | 20; bottom margin | 32 |
| Quick-amount chips | gap | 12; vertical padding | 8; corner radius | 16dp |
| Pay-with dropdown | height | 52dp; horizontal padding | 16; corner radius | 24dp; border | 1dp |
| Confirm button section | horizontal padding | 20 |
| Confirm button | width | fillMaxWidth; height | 56dp; corner radius | 24dp |

## Component Tree

### Shared Screen Container (all states)

```kotlin
// → AssetDetailScreen.kt
XScreen(
    topBar = {
        XTopAppBar(
            title = {
                XText(
                    text = stringResource(Res.string.assetdetail_title),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
            },
            navigationIcon = {
                XIcon(
                    painter = painterResource(Res.drawable.arrow_back),
                    contentDescription = stringResource(Res.string.cd_back),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp),
                )
            },
            backgroundColor = Color.Transparent,  // OVERRIDE: default is surface
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    },
    bottomBar = { /* SellBuyBar for Success state; empty for Loading/Failed */ },
)
// content slot → state-specific content below
```

---

### Loading State

Shared screen — `.claude/docs/_shared/designs/loading.png`
Token inventory: `.claude/docs/_shared/designs/extracted/tokens_loading.md`

```kotlin
// AppLoadingState from designsystem.app — not per-feature (Rule 4)
AppLoadingState()
// XScreen.bottomBar = {} (empty) for this state
```

The shared loading design shows: full-screen `background` bg, centred 64dp circular arc indicator (`surfaceVariant` track, `primary` arc, 75° sweep @ 45° rotation), 8dp primary glow dot, subtle radial gradient bg texture @ 20% opacity.

---

### Failed State

Shared screen — `.claude/docs/_shared/designs/failed.png`
Token inventory: `.claude/docs/_shared/designs/extracted/tokens_failed.md`

```kotlin
// AppErrorState from designsystem.app — not per-feature (Rule 4)
AppErrorState(
    title = stringResource(Res.string.assetdetail_error_title),
    message = stringResource(Res.string.assetdetail_error_message),
    onRetry = onRetry,
)
// Decorative image inside AppErrorState shell:
Image(
    painter = painterResource(DesignSystemResources.drawable.failed_background),  // bundled
    contentDescription = null,
    contentScale = ContentScale.Crop,
    modifier = Modifier
        .fillMaxWidth()
        .height(265.dp)
        .align(Alignment.BottomCenter)
        .alpha(0.2f),
)
// XScreen.bottomBar = {} (empty) for this state
```

The shared failed design: `background` screen, centred column — 80dp `error`-colored warning icon with `error @10%` glow, `onSurfaceVariant` heading, `outline` body, `primary`-bg Retry button (200dp max-w, 56dp h, 12dp corner), `onSurfaceVariant` "Return to Dashboard" text link. Background decorative image (delivery: bundled) at bottom, 265dp tall, 20% opacity.

---

### Success State

```kotlin
// → AssetDetailScreen.kt  (routing + bottomBar)
when (uiModel.dataState) {
    is UiState.Loading  -> AppLoadingState()
    is UiState.Failed   -> AppErrorState(title = ..., message = ..., onRetry = onRetry)
    is UiState.Success  -> {
        // bottomBar slot wired to SellBuyBar here
        // content:
        AssetDetailContent(
            uiModel = uiModel,
            onSeeAllActivity = onSeeAllActivity,
            onJoinGroup = onJoinGroup,
            onPeriodSelect = onPeriodSelect,
            onSellClick = onSellClick,
            onBuyClick = onBuyClick,
        )
    }
    else -> Unit
}
```

#### `AssetDetailContent` — `// → components/AssetDetailContent.kt`

```kotlin
LazyColumn(
    modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(bottom = 16.dp),  // body pb-32 absorbed by XScreen.bottomBar
) {
    item { HeroSection(uiModel) }
    item { ChartSection(uiModel.chartData, uiModel.selectedPeriod, onPeriodSelect) }
    item { StatsGrid(uiModel) }
    item { ActivitySection(uiModel.activities, onSeeAllActivity) }
    item { TopHoldersSection(uiModel.holders, uiModel.holderOverflowCount, onJoinGroup) }
}
```

#### `HeroSection` — `// → components/HeroSection.kt`

```kotlin
Box(modifier = Modifier.fillMaxWidth()) {
    // Hero gradient backdrop (absolute, full-width, 240dp tall)
    // from-primary-container to-background at 60% opacity
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        MaterialTheme.colorScheme.background.copy(alpha = 0f),
                    )
                )
            )
    )
    // Centered coin info column
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Bitcoin logo: 64×64 primary circle, shadow-lg (8dp), mb-4
        Box(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .size(64.dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            XIcon(
                painter = painterResource(Res.drawable.currency_bitcoin_fill),  // FILL=1 variant
                contentDescription = stringResource(Res.string.cd_bitcoin_icon),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(36.dp),
            )
        }
        // Coin name + ticker: onSurfaceVariant, 18sp/700, mb-1
        XText(
            text = uiModel.coinNameTicker,  // repo-supplied ("Bitcoin (BTC)")
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        // Price: onSurface, 36sp/800, tracking-tight, mb-3
        XText(
            text = uiModel.formattedPrice,  // repo-supplied
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.025f * 36).sp,
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        // % change badge
        PriceChangeBadge(
            changePercent = uiModel.priceChangePercent,  // repo-supplied
            isPositive = uiModel.isPriceUp,
        )
    }
}
```

#### `PriceChangeBadge` — `// → components/PriceChangeBadge.kt`

```kotlin
val badgeColor = if (isPositive) XTheme.Colors.Success else XTheme.Colors.Danger
Row(
    modifier = Modifier
        .background(badgeColor.copy(alpha = 0.10f), CircleShape)
        .border(BorderStroke(1.dp, badgeColor.copy(alpha = 0.20f)), CircleShape)
        .padding(horizontal = 12.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
) {
    XIcon(
        painter = painterResource(Res.drawable.arrow_upward),
        tint = badgeColor,
        modifier = Modifier.size(16.dp),
        // For negative: flip vertically with graphicsLayer { rotationX = 180f } or use arrow_downward icon
    )
    XText(
        text = "$changePercent%",  // repo-supplied — "%" is a glyph, not a localized string
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        color = badgeColor,
    )
}
```

#### `ChartSection` — `// → components/ChartSection.kt`

```kotlin
Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 32.dp),
) {
    // Chart area box: 180dp tall, mt-4
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(top = 16.dp),
    ) {
        // Y-axis labels (positioned left, full height)
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // "$68k", "$67k", "$66k", "$65k" — repo-supplied axis data
            uiModel.yAxisLabels.forEach { label ->
                XText(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        // Chart drawing area (ml-10 = 40dp from Y-axis)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 40.dp),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Area fill: Brush.verticalGradient primary→transparent
                // Line path: primary stroke with chart-glow effect (drop-shadow decorative)
                // [Canvas/Path decoration — draw from chartData points; chart-glow → drawBehind with shadow effect]
            }
        }
    }
    // X-axis labels (40dp left offset, space-between), mt-2
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 40.dp, top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // "Mon", "Tue", "Wed", "Thu", "Fri" — repo-supplied axis data
        uiModel.xAxisLabels.forEach { label ->
            XText(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    // Time period chip selector
    TimePeriodChips(
        selectedPeriod = uiModel.selectedPeriod,
        onPeriodSelect = onPeriodSelect,
        modifier = Modifier.padding(top = 8.dp, bottom = 40.dp),
    )
}
```

#### `TimePeriodChips` — `// → components/TimePeriodChips.kt`

```kotlin
// 5 chips: "1D" (selected by default), "1W", "1M", "1Y", "All"
// Design: selected = primary bg + onPrimary text + CircleShape
//         unselected = transparent bg + outlineVariant border 1dp + onSurfaceVariant text + CircleShape
// XFilterChip defaults do NOT match (selectedContainerColor = primaryContainer, not primary) → use XButton/XOutlinedButton pair
Row(
    modifier = modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
) {
    listOf("1D", "1W", "1M", "1Y", "All").forEach { period ->
        val isSelected = selectedPeriod == period
        if (isSelected) {
            XButton(
                onClick = { onPeriodSelect(period) },
                shape = CircleShape,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            ) {
                XText(
                    text = period,  // repo-supplied label
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                )
                // XButton default: primary bg, onPrimary text — matches design
            }
        } else {
            XOutlinedButton(
                onClick = { onPeriodSelect(period) },
                shape = CircleShape,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            ) {
                XText(
                    text = period,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
```

#### `StatsGrid` — `// → components/StatsGrid.kt`

```kotlin
// 2×2 grid of stat cards
Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 40.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        StatCard(
            label = stringResource(Res.string.section_market_cap),
            value = uiModel.marketCap,  // repo-supplied
            isHighlighted = false,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            label = stringResource(Res.string.section_volume_24h),
            value = uiModel.volume24h,  // repo-supplied
            isHighlighted = false,
            modifier = Modifier.weight(1f),
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        StatCard(
            label = stringResource(Res.string.section_circ_supply),
            value = uiModel.circSupply,  // repo-supplied
            isHighlighted = false,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            label = stringResource(Res.string.section_your_holdings),
            value = uiModel.holdingsBtc,  // repo-supplied
            fiatValue = uiModel.holdingsFiat,  // repo-supplied
            isHighlighted = true,
            modifier = Modifier.weight(1f),
        )
    }
}
```

#### `StatCard` — `// → components/StatCard.kt`

```kotlin
// bg-surface, border 1dp outlineVariant (normal) / primary@20% (highlighted), p-5, rounded-xl 24dp
// NOT XCard — XCard default shape is 12dp; design requires 24dp → use Column+background
Column(
    modifier = Modifier
        .then(modifier)
        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
        .then(
            if (isHighlighted)
                Modifier
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)), RoundedCornerShape(24.dp))
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp))  // shadow-md
            else
                Modifier
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), RoundedCornerShape(24.dp))
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(24.dp))  // shadow-sm
        )
        .padding(20.dp),
) {
    XText(
        text = label,
        style = MaterialTheme.typography.labelMedium,  // 12sp/Medium — stock matches
        color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 4.dp),
    )
    XText(
        text = value,  // repo-supplied
        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
    )
    if (fiatValue != null) {
        XText(
            text = fiatValue,  // repo-supplied
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Normal),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
```

#### `ActivitySection` — `// → components/ActivitySection.kt`

```kotlin
Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 40.dp),
) {
    // Section header row: "Recent Activity" + "See all"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        XText(
            text = stringResource(Res.string.section_recent_activity),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )
        XTextButton(onClick = onSeeAll) {
            XText(
                text = stringResource(Res.string.activity_see_all),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
    // 3 activity rows (space-y-4 = 16dp gap)
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        activities.forEach { activity ->
            ActivityRow(activity = activity)
        }
    }
}
```

#### `ActivityRow` — `// → components/ActivityRow.kt` (repeated pattern × 3, extracted)

```kotlin
// bg-surface/50 = surface@50%, border outlineVariant/30, p-4, rounded-xl 24dp
Row(
    modifier = Modifier
        .fillMaxWidth()
        .background(
            MaterialTheme.colorScheme.surface.copy(alpha = 0.50f),
            RoundedCornerShape(24.dp),
        )
        .border(
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f)),
            RoundedCornerShape(24.dp),
        )
        .padding(16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
) {
    // Left: icon circle + title/timestamp
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Activity type icon circle: 40×40, type-color@10% bg, type-color icon
        val iconTint = if (activity.isPositive) XTheme.Colors.Success else XTheme.Colors.Danger
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconTint.copy(alpha = 0.10f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            XIcon(
                painter = painterResource(activity.iconRes),
                // Received → Res.drawable.call_received (Success tint)
                // Sent     → Res.drawable.call_made (Danger tint)
                // Staked   → DesignSystemResources.drawable.bolt (Success tint)
                tint = iconTint,
                modifier = Modifier.size(20.dp),
            )
        }
        Column {
            XText(
                text = stringResource(activity.titleRes),
                // activity_received / activity_sent / activity_staked
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            XText(
                text = activity.timestamp,  // repo-supplied
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    // Right: signed amount + fiat equiv
    Column(horizontalAlignment = Alignment.End) {
        val amountColor = if (activity.isPositive) XTheme.Colors.Success else XTheme.Colors.Danger
        XText(
            text = activity.signedAmount,  // repo-supplied "+0.0120 BTC" / "-0.0050 BTC"
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = amountColor,
        )
        XText(
            text = activity.fiatEquiv,  // repo-supplied "$809.04"
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
```

#### `TopHoldersSection` — `// → components/TopHoldersSection.kt`

```kotlin
Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 48.dp),
) {
    XText(
        text = stringResource(Res.string.section_top_holders),
        style = MaterialTheme.typography.titleLarge.copy(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        ),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Avatar stack: 5 overlapping circles + "+42" overflow
        // CSS avatar-stack: margin-right −12dp per circle; 40dp width → 28dp effective stride
        Box(modifier = Modifier.wrapContentWidth()) {
            holders.forEachIndexed { index, holder ->
                AvatarCircle(
                    initials = holder.initials,  // repo-supplied
                    modifier = Modifier.offset(x = (index * 28).dp),
                )
            }
            // "+42" overflow circle: primaryContainer bg, primary text, 2dp background border
            Box(
                modifier = Modifier
                    .offset(x = (holders.size * 28).dp)
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .border(BorderStroke(2.dp, MaterialTheme.colorScheme.background), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                XText(
                    text = "+${overflowCount}",  // repo-supplied
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        // "Join Group" clickable row: onSurfaceVariant text + chevron_right
        Row(
            modifier = Modifier.clickable(onClick = onJoinGroup),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            XText(
                text = stringResource(Res.string.holders_join_group),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            XIcon(
                painter = painterResource(Res.drawable.chevron_right),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
```

#### `AvatarCircle` — `// → components/AvatarCircle.kt` (repeated pattern × 5+, extracted)

```kotlin
// 40×40 circle, surfaceVariant bg, 2dp background border ring, primary text initials
Box(
    modifier = Modifier
        .then(modifier)
        .size(40.dp)
        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        .border(BorderStroke(2.dp, MaterialTheme.colorScheme.background), CircleShape),
    contentAlignment = Alignment.Center,
) {
    XText(
        text = initials,  // repo-supplied (e.g. "AK", "MW", "PR", "JL", "ST")
        style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        ),
        color = MaterialTheme.colorScheme.primary,
    )
}
```

#### `SellBuyBar` — `// → components/SellBuyBar.kt` (XScreen.bottomBar slot)

```kotlin
// fixed footer: surface bg, border-t 1dp outlineVariant (top only), px-24dp, pt-16dp
// bottom inset owned by this bottomBar composable (Case A — no shell nav bar)
Row(
    modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface)
        .drawBehind {
            // top border only: 1dp outlineVariant
            drawLine(
                color = outlineVariantColor,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx(),
            )
        }
        .padding(horizontal = 24.dp, vertical = 16.dp)
        .windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime)),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
) {
    // Sell: outlined, 2dp primary border, primary text, h-56dp, rounded-xl 24dp
    // OVERRIDE: XOutlinedButton default shape = CircleShape; border = 1dp contentColor
    XOutlinedButton(
        onClick = onSellClick,
        modifier = Modifier.weight(1f).height(56.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
        ),
    ) {
        XText(
            text = stringResource(Res.string.action_sell),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        )
    }
    // Buy: primary bg, onPrimary text, h-56dp, rounded-xl 24dp, shadow-lg 8dp
    // OVERRIDE: XButton default shape = CircleShape
    XButton(
        onClick = onBuyClick,
        modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
    ) {
        XText(
            text = stringResource(Res.string.action_buy),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        )
    }
}
```

#### `BuyBottomSheet` — `// → components/BuyBottomSheet.kt`

```kotlin
// XModalBottomSheet: drag handle rendered by default (no override needed),
// containerColor = surface (matches #1C1910), rounded-t 24dp shape
XModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp),
    ) {
        // --- Header ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            XText(
                text = stringResource(Res.string.buy_sheet_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            XText(
                text = stringResource(Res.string.buy_sheet_subtitle),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // --- Amount input field ---
        // Custom styled Row (NOT XTextField — XTextField min-width 280dp breaks layout)
        // surfaceVariant bg, 1.5dp primary border, 56dp height, 24dp corner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .height(56.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
                .border(BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary), RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                XText(
                    text = "$",  // glyph — not localized (Rule 12 exception)
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                XText(
                    text = uiModel.amountText,  // repo-supplied ("500.00")
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            XText(
                text = uiModel.approxBtc,  // repo-supplied ("≈ 0.0074 BTC")
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // --- Slider + quick-amount chips ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
        ) {
            // XSlider: track outlineVariant, fill+thumb primary (M3 default — matches)
            XSlider(
                value = uiModel.sliderValue,
                onValueChange = onSliderChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
            )
            // Quick-amount chips: 25%, 50% (selected), 75%, Max
            // rounded-lg 16dp; selected = primary bg + onPrimary text; unselected = surfaceVariant + onSurfaceVariant
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                listOf("25%", "50%", "75%", "Max").forEach { chip ->
                    val isSelected = uiModel.selectedQuickAmount == chip
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(16.dp),
                            )
                            .clickable { onQuickAmountSelect(chip) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        XText(
                            text = chip,  // repo-supplied label (% is a glyph, not localized — Rule 12 exception)
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        // --- Pay-with dropdown ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
        ) {
            XText(
                text = stringResource(Res.string.buy_pay_with_label),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            // XExposedDropdownMenuBox trigger: surfaceVariant bg, outlineVariant border 1dp, 52dp h, 24dp corner
            XExposedDropdownMenuBox(
                expanded = uiModel.walletDropdownExpanded,
                onExpandedChange = onDropdownToggle,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), RoundedCornerShape(24.dp))
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        XIcon(
                            painter = painterResource(Res.drawable.account_balance_wallet),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                        )
                        XText(
                            text = uiModel.selectedWalletLabel,  // repo-supplied ("Main Wallet · $12,450.00")
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    XIcon(
                        painter = painterResource(Res.drawable.expand_more),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                }
                // XDropdownMenuItem items for wallet selection
                uiModel.wallets.forEach { wallet ->
                    XDropdownMenuItem(
                        text = { XText(wallet.label) },
                        onClick = { onWalletSelect(wallet) },
                    )
                }
            }
        }

        // --- Confirm button ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
            // OVERRIDE: XButton default shape = CircleShape; design uses rounded-xl 24dp
            XButton(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp),
            ) {
                XText(
                    text = stringResource(Res.string.buy_confirm_button),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                )
            }
        }
    }
}
```

## String Inventory

> Every user-facing text node → a proposed string-resource key (Rule 12). Excluded: repository-supplied data (prices, amounts, timestamps, coin names, wallet names, tickers, axis labels), single-glyph symbols (`$`, `%`, `₿`, `≈`), and control sentinels. Shared strings (Retry, Cancel) come from `DesignSystemResources`.

| Key | Default (English) value | Where used | Notes |
|-----|-------------------------|------------|-------|
| `assetdetail_title` | Bitcoin | Top app bar title | Static key; coin name is also repo data for dynamic screens |
| `cd_back` | Back | Navigation icon `contentDescription` | Accessibility |
| `cd_bitcoin_icon` | Bitcoin icon | Hero coin icon `contentDescription` | Accessibility |
| `assetdetail_error_title` | Something went wrong | Failed state — `AppErrorState(title=)` | Shared screen copy |
| `assetdetail_error_message` | An unexpected error occurred. Please try again or check your connection. | Failed state — `AppErrorState(message=)` | Shared screen copy |
| `section_market_cap` | Market Cap | Stats grid card label | |
| `section_volume_24h` | 24h Vol | Stats grid card label | |
| `section_circ_supply` | Circ Supply | Stats grid card label | |
| `section_your_holdings` | Your Holdings | Stats grid highlighted card label | |
| `section_recent_activity` | Recent Activity | Activity section header | |
| `section_top_holders` | Top Holders Community | Top Holders section header | |
| `activity_see_all` | See all | Activity section text button | |
| `activity_received` | Received | Activity row title — received type | |
| `activity_sent` | Sent | Activity row title — sent type | |
| `activity_staked` | Staked | Activity row title — staked type | |
| `holders_join_group` | Join Group | Top Holders row action button | |
| `action_sell` | Sell | Sticky footer Sell button | |
| `action_buy` | Buy | Sticky footer Buy button | |
| `buy_sheet_title` | Buy Bitcoin | Bottom sheet title | |
| `buy_sheet_subtitle` | BTC / USD | Bottom sheet subtitle | |
| `buy_pay_with_label` | Pay with | Bottom sheet Pay-with label | |
| `buy_confirm_button` | Confirm Purchase | Bottom sheet Confirm button | |

## Motion

| Element | Family | Compose primitive | Params (dur / easing / repeat / trigger) | Magnitude | Target file |
|---------|--------|-------------------|------------------------------------------|-----------|-------------|
| Loading skeleton blocks (all screen sections during loading state) | Loading/Attention loop | `Modifier.shimmer(baseColor, highlightColor, sweepFraction)` | `XMotion.SHIMMER` / `LinearEasing` / infinite / on-screen | bg-position −200%→200% (infer) | DS `motion/` (`Modifier.shimmer`) |
| Screen sections on first load (hero, chart, stats, activity, holders) | Entrance | `AnimatedVisibility(fadeIn() + slideInVertically())` staggered per section index | `XMotion.EaseOutExpo` / once / first composition | translateY 30dp→0; opacity 0→1 (infer) | DS `motion/` (`RevealOnAppear`) |
| Price value counter "$67,420.50" | Value-driven | `animateFloatAsState` on numeric price value | spring / — / on value change | 0→target price float (infer) | feature `motion/AssetDetailMotion.kt` |
| % change badge color (positive ↔ negative) | Value-driven | `animateColorAsState` | `XMotion.Standard` / — / on value change | `XTheme.Colors.Success` ↔ `XTheme.Colors.Danger` (infer) | feature `motion/AssetDetailMotion.kt` |

**Reduced motion**: all rows gated by `rememberReducedMotion()` (DS `XMotion.kt`, an `expect/actual` reading the OS setting) — reduced ⇒ skip to end/target state. Durations/easings come from `XMotion` tokens, never ad-hoc `tween(<literal>)`.

**Dropped (interaction + web-only)**: `active:scale-95` (back button, Sell/Buy buttons, time-period chip buttons), `hover:bg-surface-variant` (unselected chips, "Join Group"), `hover:text-primary` ("Join Group"), `hover:opacity-90` (Retry button on failed state), `transition-colors`, `transition-all`, `duration-150` — no Compose output (per `_shared/motion.md` Web-Motion Policy).

## Pre-Implementation Contract

> Architecture rules, color rules, and X-component defaults are project-wide and live in their canonical sources — do not restate them here:
> - Architecture rules → [`_shared/patterns.md`](../../_shared/patterns.md)
> - Color rules → [`m3-colors.md`](../references/m3-colors.md)
> - X-component default-render behavior → [`_shared/X_COMPONENTS_CATALOG.md`](../../_shared/X_COMPONENTS_CATALOG.md)
>
> This contract captures only feature-specific data the implementer cannot derive from those references.

### XTheme Updates Required

(none — all design colors are already in XDarkColors / XTheme.Colors; no missing M3 roles)

### Typography Updates Required

**Font swap**: none — design typeface (Manrope) matches the theme's current `XFontFamily()` (`manrope_variable` in `XTheme.kt` — already materialized).

**Type-scale role overrides** (emit a row only when measured size/weight diverges from the M3 role's stock value):

| Node | Chosen Role | Stock Role Value | Measured Value | Override |
|------|-------------|------------------|----------------|----------|
| Screen title "Bitcoin" | `titleLarge` | 22sp / Normal | 22sp / 700 | `MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)` |
| Coin name + ticker | `titleMedium` | 16sp / Medium | 18sp / 700 | `MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)` |
| Price "$67,420.50" | `displaySmall` | 36sp / Normal | 36sp / 800 | `MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold)` |
| % change badge | `labelLarge` | 14sp / Medium | 14sp / 700 | `MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)` |
| Chart axis labels (Y/X) | `labelSmall` | 11sp / Medium | 10sp / 500 | `MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)` |
| Time period chips | `labelLarge` | 14sp / Medium | 14sp / 700 | `MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)` |
| Stats card value | `titleMedium` | 16sp / Medium | 18sp / 700 | `MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)` |
| Holdings fiat equiv | `labelSmall` | 11sp / Medium | 10sp / 400 | `MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Normal)` |
| Section headers | `titleLarge` | 22sp / Normal | 20sp / 700 | `MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold)` |
| "See all" / "Join Group" text buttons | `labelLarge` | 14sp / Medium | 14sp / 700 | `MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)` |
| Activity row title | `labelLarge` | 14sp / Medium | 14sp / 700 | `MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)` |
| Activity timestamp | `labelMedium` | 12sp / Medium | 12sp / 400 | `MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal)` |
| Activity signed amount | `labelLarge` | 14sp / Medium | 14sp / 700 | `MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)` |
| Activity fiat equiv | `labelSmall` | 11sp / Medium | 12sp / 400 | `MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Normal)` |
| Avatar initials | `labelSmall` | 11sp / Medium | 12sp / 700 | `MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold)` |
| Bottom sheet title | `titleMedium` | 16sp / Medium | 18sp / 700 | `MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)` |
| Bottom sheet subtitle | `bodySmall` | 12sp / Normal | 13sp / 400 | `MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)` |
| Amount input value | `titleMedium` | 16sp / Medium | 20sp / 700 | `MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold)` |
| Approx BTC label | `labelMedium` | 12sp / Medium | 12sp / 400 | `MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal)` |
| "Pay with" label | `bodySmall` | 12sp / Normal | 13sp / 400 | `MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)` |
| Wallet label | `labelLarge` | 14sp / Medium | 14sp / 400 | `MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal)` |
| Quick-amount chips | `labelSmall` | 11sp / Medium | 12sp / 700 | `MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold)` |
| "Confirm Purchase" button | `bodyLarge` | 16sp / Normal | 16sp / 700 | `MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)` |
| Failed screen heading (shared) | `titleLarge` | 22sp / Normal | 20sp / 600 | `MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)` |
| Failed screen body (shared) | `bodySmall` | 12sp / Normal | 14sp / 400 | `MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)` |

### Color Audit

#### Defined Roles

| Role | Hex | Usage |
|------|-----|-------|
| `primary` | #F5D76E | Back icon, coin circle bg, selected chip, Sell border/text, Buy bg, slider thumb/fill, wallet icon, Confirm bg |
| `onPrimary` | #2C1900 | Coin icon, Buy text, selected chip text, Confirm text |
| `primaryContainer` | #4A3200 | Hero gradient start, "+42" avatar bg |
| `onPrimaryContainer` | #FFF0C0 | Config reference only |
| `background` | #0F0D09 | Screen bg, avatar ring border, scrim bg, hero gradient end |
| `surface` | #1C1910 | Stats cards, activity rows (@50%), sticky footer, bottom sheet |
| `onSurface` | #EDE8D5 | All primary text nodes |
| `onSurfaceVariant` | #C4BA94 | All secondary text nodes, axis labels, timestamps, Join Group, Pay-with |
| `surfaceVariant` | #302B1C | Avatar bg, amount input bg, unselected chip bg, dropdown bg |
| `outline` | #726A48 | Failed screen body text (shared state screen) |
| `outlineVariant` | #3F3822 | Chip borders, card borders, footer border, drag handle, Pay-with border, slider track |
| `error` | #FFB4AB | Warning icon on failed state (shared state screen) |
| `onError` | #690005 | Failed state (shared state screen) |
| `errorContainer` | #93000A | Failed state (shared state screen) |
| `onErrorContainer` | #FFDAD6 | Failed state (shared state screen) |

#### Missing Roles (must add before implementation)

(none)

#### Custom Colors (justified exceptions only)

| Name | Hex | Justification |
|------|-----|---------------|
| `XTheme.Colors.Success` | #4ADE80 | Positive %-badge, received/staked amounts, activity icon tint — semantic gain status, no M3 role equivalent |
| `XTheme.Colors.Danger` | #FF6B6B | Negative %-badge, sent amounts, sent activity icon tint — semantic loss status, no M3 role equivalent |

#### Component Overrides (divergences from X-component defaults — CRITICAL checks for `/verify-ui`)

| Component | Property | HTML Value | X-component Default | Override Required |
|-----------|----------|-----------|-------------------|------------------|
| `XTopAppBar` | `backgroundColor` | `transparent` | `colorScheme.surface` | `backgroundColor = Color.Transparent` |
| `XTopAppBar` | title alignment | left-aligned (design shows nav icon + title in a left-aligned `flex` row) | `Start` (default — `TopAppBar`, not `CenterAlignedTopAppBar`) | No override needed — default is already `Start` |
| `XButton` (Buy, Confirm) | `shape` | `RoundedCornerShape(24.dp)` | `CircleShape` | `shape = RoundedCornerShape(24.dp)` |
| `XOutlinedButton` (Sell) | `shape` | `RoundedCornerShape(24.dp)` | `CircleShape` | `shape = RoundedCornerShape(24.dp)` |
| `XOutlinedButton` (Sell) | `border` width | 2dp | 1dp contentColor | `border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)` |
| `StatCard` | shape (container) | `RoundedCornerShape(24.dp)` | `XCard` default 12dp | Use `Column + Modifier.background(surface, RoundedCornerShape(24.dp))` — not `XCard` |
| Time-period chips (unselected) | bg + border | transparent bg + 1dp outlineVariant | `XFilterChip` defaults (primaryContainer selected bg) | Use `XOutlinedButton(shape=CircleShape, border=1dp outlineVariant)` |
| Amount input field | component | custom styled Row | `XTextField` (minWidth 280dp — unsuitable) | Use `Row + background(surfaceVariant) + border(1.5dp primary)` |
| Quick-amount chips | component | custom per-chip Box | `XFilterChip` defaults don't match | Use `Box + background + clickable` per chip |
| `XSlider` | colors | primary fill/thumb, outlineVariant track | `SliderDefaults.colors()` — primary fill/thumb matches; track resolves to M3 default | Verify inactive track color matches outlineVariant at runtime |

## Post-Implementation Checklist

- [ ] Font swap: **none needed** — Manrope already in `XFontFamily()` in `XTheme.kt`
- [ ] Every text node uses `style = MaterialTheme.typography.{role}` (or `.copy(...)` per Type-scale overrides table above) — no raw `fontSize`/`fontWeight` outside recorded overrides
- [ ] No missing M3 roles — XTheme already complete; `XTheme.Colors.Success` and `XTheme.Colors.Danger` already defined
- [ ] Every component in blueprint Component Tree exists in implementation:
  - [ ] `AssetDetailContent` (LazyColumn, 5 item slots)
  - [ ] `HeroSection` (Box with gradient + Column)
  - [ ] `PriceChangeBadge` (Row with dynamic color)
  - [ ] `ChartSection` (Column with Canvas + TimePeriodChips)
  - [ ] `TimePeriodChips` (Row, XButton/XOutlinedButton per period)
  - [ ] `StatsGrid` (2×2 Column/Row layout)
  - [ ] `StatCard` (Column+background, highlighted variant)
  - [ ] `ActivitySection` (Column with header row + activity list)
  - [ ] `ActivityRow` (Row, icon circle + text + amounts — extracted pattern)
  - [ ] `TopHoldersSection` (Column with avatar stack + Join Group)
  - [ ] `AvatarCircle` (Box, initials — extracted pattern)
  - [ ] `SellBuyBar` (Row, Sell + Buy — XScreen.bottomBar slot)
  - [ ] `BuyBottomSheet` (XModalBottomSheet with amount input, XSlider, chips, dropdown, Confirm)
- [ ] Every Modifier in blueprint (border, shadow, alpha, padding, size, background, windowInsetsPadding) present in code
- [ ] Decorative background image (failed state): `Image(painter = painterResource(DesignSystemResources.drawable.failed_background), contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(265.dp).alpha(0.2f))` — delivery: bundled; no remote URL
- [ ] All colors use `MaterialTheme.colorScheme.{role}` or `XTheme.Colors.{Name}` — no raw `Color()` hex values
- [ ] All Component Override rows applied (transparent top bar, 24dp shapes on buttons, 24dp stats card corners, custom chip styling, custom amount input Row)
- [ ] Every String Inventory key exists in `composeResources/values/strings.xml` and referenced via `stringResource(Res.string.*)` — no hardcoded display literals (Rule 12)
- [ ] All `## Motion` rows implemented in `motion/` files:
  - [ ] Loading shimmer → DS `motion/` (`Modifier.shimmer`)
  - [ ] Entrance sections → DS `motion/` (`RevealOnAppear`)
  - [ ] Price counter `animateFloatAsState` → `feature/assetdetail/.../presentation/ui/motion/AssetDetailMotion.kt`
  - [ ] Badge color `animateColorAsState` → `feature/assetdetail/.../presentation/ui/motion/AssetDetailMotion.kt`
  - [ ] All motion gated by `rememberReducedMotion()` — no ad-hoc `tween(<literal>)` durations
  - [ ] No interaction/hover motion in code (all dropped per Web-Motion Policy)
- [ ] Build passes: `./gradlew :feature:assetdetail:assembleAndroidMain`
- [ ] Code formatted: `./gradlew :feature:assetdetail:ktlintFormat`
