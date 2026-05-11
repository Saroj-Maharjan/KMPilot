# Compose Implementation Blueprint: Send

> **Decomposed** — 6 distinct visual sections. Screen dimensions: 800×1774px (success), 780×1768px (loading/failed, shared).
> Token inventory: `.claude/docs/send/designs/extracted/tokens_success.md`
> HTML source: `.claude/docs/send/designs/extracted/stitch_success.html`

---

## Design Tokens

| Hex | M3 Role | Usage |
|-----|---------|-------|
| #0F0D09 | background | Screen canvas, footer bg (80% opacity), decorative ambient glows |
| #1C1910 | surface | Recipient card, Asset card, Network card background |
| #302B1C | surfaceVariant | Quick chip fill, network icon bg, transaction summary card bg |
| #EDE8D5 | onSurface | Hero amount, coin names, summary values, input text |
| #C4BA94 | onSurfaceVariant | Balance label, section labels, placeholder (50%), coin subtitles, summary labels |
| #3F3822 | outlineVariant | All card borders, chip borders, summary divider |
| #726A48 | outline | Hover-state borders only (not static) |
| #F5D76E | primary | Back arrow, BTC pill text, cursor line, chip text, icon buttons, chevrons, left accent bar, summary border (30% alpha), CTA fill |
| #2C1900 | onPrimary | CTA button text |
| #4ADE80 | XTheme.Colors.Success | Estimated Arrival value + bolt icon |
| #F7931A | XTheme.Colors.Bitcoin (add to XTheme.kt) | Bitcoin coin icon background |

---

## Typography Scale

| Usage | Size (sp) | Weight | Letter Spacing | Notes |
|-------|-----------|--------|----------------|-------|
| App bar title | headline-sm (forced by XTopAppBar) | SemiBold (forced) | -0.025em | XTopAppBar overrides to headlineSmall SemiBold |
| Hero amount "0.00" | 64 | ExtraBold 800 | -0.05em (tracking-tighter) | `FontWeight.ExtraBold` |
| BTC pill | 12 | Bold 700 | 0.1em (tracking-widest) | uppercase |
| Balance text (label) | 14 | Medium 500 | 0 | |
| Balance dollar value | 14 | Medium 500 | 0 | onSurface inline |
| Quick chips | 12 | Bold 700 | 0 | MAX is uppercase |
| Card section labels | 10 | Bold 700 | 0.1em (tracking-widest) | uppercase |
| Input placeholder | 14 | Normal 400 | 0 | italic |
| Coin name | 14 | Bold 700 | 0 | |
| Coin subtitle (BTC / ERC-20) | 10 | Normal 400 | 0 | onSurfaceVariant |
| Summary label | 14 | Normal 400 | 0 | onSurfaceVariant |
| Summary value (Fee) | 14 | Medium 500 | 0 | onSurface (implied) |
| Summary value (Total Deduct) | 14 | Bold 700 | 0 | onSurface |
| Estimated Arrival value | 14 | Bold 700 | 0 | XTheme.Colors.Success |
| Security badge | 10 | Bold 700 | 0.2em | uppercase |
| CTA button | ~16 (text-md) | Bold 700 | 0 | |

---

## Spacing Grid

| Context | Property | Value (dp) |
|---------|----------|------------|
| Screen | horizontal padding (main) | 24 |
| Screen | top padding (app bar clearance) | 96 |
| Screen | bottom padding (footer clearance) | 128 |
| Hero section | bottom margin | 40 |
| Amount row | bottom margin | 8 |
| Cursor underline | bottom margin | 16 |
| Balance text | bottom margin | 24 |
| Quick chips | gap | 12 |
| Recipient card | bottom margin | 20 |
| Recipient card | padding | 20 all sides |
| Recipient label | bottom margin | 12 |
| Recipient input row | gap between input and icons | 16 |
| Recipient icon buttons | gap | 12 |
| Selector grid | gap | 16 |
| Selector grid | bottom margin | 32 |
| Asset/Network card | padding | 16 all sides |
| Summary card | padding | 20 all sides |
| Summary card | bottom margin | 32 |
| Summary rows | vertical spacing | 16 (space-y-4) |
| Estimated Arrival row | top padding + top border | 12dp top padding |
| Estimated Arrival icon+text | gap | 6 |
| Security badge | gap between icon and text | 8 |
| Security badge | bottom margin | 16 |
| Footer | padding all sides | 24 |
| BTC pill | horizontal padding | 12, vertical padding: 4 |
| Chip | horizontal padding | 20, vertical padding: 8 |

---

## Component Tree

### Shared Scaffold (all states)

```
// → SendScreen.kt
XScaffold(
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
        XTopAppBar(
            backgroundColor = Color.Transparent,         // transparent — no surface bg
            title = "Send",
            navigationIcon = {
                XIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    colors = XIconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary   // gold back arrow
                    ),
                    onClick = onBackClick
                )
            }
        )
    }
) { paddingValues ->
    // [state-specific content slot]
}
```

**XTopAppBar notes**:
- Title is always center-aligned (XTopAppBar uses CenterAlignedTopAppBar) — design appears left-aligned due to `ml-2` but XTopAppBar cannot replicate this exactly. This is a known catalog limitation; accept center-aligned title.
- Title style is forced to `headlineSmall SemiBold` by XTopAppBar internals.

---

### Success State

```
// → SendScreen.kt (state routing + LazyColumn scaffold)
Column(
    modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(paddingValues)
        .padding(horizontal = 24.dp, top = 8.dp, bottom = 128.dp)  // 96dp top = paddingValues handles ~88dp status bar; use 8dp extra
) {
    HeroAmountSection()        // → components/HeroAmountSection.kt
    Spacer(40.dp)
    RecipientCard(...)         // → components/RecipientCard.kt
    Spacer(20.dp)
    AssetNetworkGrid(...)      // → components/AssetNetworkGrid.kt
    Spacer(32.dp)
    TransactionSummaryCard(...)// → components/TransactionSummaryCard.kt
    Spacer(32.dp)
    SecurityBadge()            // inline in SendScreen.kt (structural glue)
}

// Sticky footer — XScaffold bottomBar slot:
// → SendScreen.kt
Box(
    modifier = Modifier
        .fillMaxWidth()
        .background(
            Brush.verticalGradient(
                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background.copy(alpha = 0.8f)),
            )
        )
        .padding(24.dp)
) {
    XButton(
        text = "Send Bitcoin",
        leadingIcon = Icons.AutoMirrored.Filled.Send,    // "send" material icon
        onClick = onSendClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(elevation = 0.dp, shape = RoundedCornerShape(24.dp))
            .drawBehind {
                // gold-glow: box-shadow 0 0 20px rgba(245,215,110,0.15)
                // [decorative glow — implement via drawBehind Canvas if desired, omit if not critical]
            },
        shape = RoundedCornerShape(24.dp),              // OVERRIDE: XButton default is CircleShape
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
```

---

### components/HeroAmountSection.kt

```kotlin
// → components/HeroAmountSection.kt
@Composable
fun HeroAmountSection(
    amount: String,
    coinSymbol: String,
    balanceBtc: String,
    balanceUsd: String,
    onQuickAmountClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = CenterHorizontally
    ) {
        // Amount + BTC pill
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            XText(
                text = amount,         // "0.00"
                style = TextStyle(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.05 * 64).sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 64.sp   // leading-none
                )
            )
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    )
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                XText(
                    text = coinSymbol,   // "BTC"
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (0.1 * 12).sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        Spacer(16.dp)

        // Gold cursor underline (128dp wide, 1px tall, primary color with glow)
        Box(
            modifier = Modifier
                .width(128.dp)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.primary)
                // [gold glow: shadow-[0_0_8px_rgba(245,215,110,0.5)] — use drawBehind if desired]
        )

        Spacer(16.dp)

        // Balance row: "Balance 1.24 BTC · $78,420"
        Row {
            XText(
                text = "Balance $balanceBtc · ",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            XText(
                text = "$${balanceUsd}",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        Spacer(24.dp)

        // Quick chips row: 25% / 50% / MAX
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("25%", "50%", "MAX").forEach { label ->
                QuickChip(label = label, onClick = { onQuickAmountClick(label) })
            }
        }
    }
}

// Private helper — stays in HeroAmountSection.kt
@Composable
private fun QuickChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        XText(
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}
```

> **Note**: `XFilterChip` defaults (CircleShape, surface bg, onSurfaceVariant text) diverge from design. Use custom `Box` composable for chips — avoids excessive param overrides.

---

### components/RecipientCard.kt

```kotlin
// → components/RecipientCard.kt
@Composable
fun RecipientCard(
    address: String,
    onAddressChange: (String) -> Unit,
    onPasteClick: () -> Unit,
    onQrClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        // Gold left accent bar (absolute-positioned, 4dp wide, full height)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-20).dp)    // overlap with card edge
                .width(4.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary)
                .clip(RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp))
        )
        // NOTE: The gold accent bar is absolute-positioned on the left edge of the card.
        // Implement as a Box overlay inside a Box with clip + overflow-hidden.

        Column {
            XText(
                text = "TO RECIPIENT",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = (0.1 * 10).sp
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BasicTextField(
                    value = address,
                    onValueChange = onAddressChange,
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic
                    ),
                    decorationBox = { innerTextField ->
                        if (address.isEmpty()) {
                            XText(
                                text = "Wallet address or ENS name",
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 14.sp,
                                    fontStyle = FontStyle.Italic
                                )
                            )
                        }
                        innerTextField()
                    }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    XIconButton(
                        icon = Icons.Default.ContentPaste,
                        onClick = onPasteClick,
                        colors = XIconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent,   // REQUIRED: avoid surface circle default
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    XIconButton(
                        icon = Icons.Default.QrCodeScanner,
                        onClick = onQrClick,
                        colors = XIconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent,   // REQUIRED
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}
```

> **XIconButton override**: Must pass `containerColor = Color.Transparent` — default renders a visible `surface`-colored circle.

---

### components/AssetNetworkGrid.kt

```kotlin
// → components/AssetNetworkGrid.kt
@Composable
fun AssetNetworkGrid(
    coinName: String,
    coinSymbol: String,
    networkName: String,
    networkSubtitle: String,
    onAssetClick: () -> Unit,
    onNetworkClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AssetSelectorCard(
            label = "ASSET",
            iconContent = {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(XTheme.Colors.Bitcoin, CircleShape),  // #F7931A
                    contentAlignment = Alignment.Center
                ) {
                    XIcon(
                        icon = Icons.Default.CurrencyBitcoin,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            },
            primaryText = coinName,
            secondaryText = coinSymbol,
            onClick = onAssetClick,
            modifier = Modifier.weight(1f)
        )
        AssetSelectorCard(
            label = "NETWORK",
            iconContent = {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    XIcon(
                        icon = Icons.Default.Public,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            },
            primaryText = networkName,
            secondaryText = networkSubtitle,
            onClick = onNetworkClick,
            modifier = Modifier.weight(1f)
        )
    }
}

// Private helper — stays in AssetNetworkGrid.kt
@Composable
private fun AssetSelectorCard(
    label: String,
    iconContent: @Composable () -> Unit,
    primaryText: String,
    secondaryText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        XText(
            text = label,
            style = TextStyle(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (0.1 * 10).sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            iconContent()
            Column(modifier = Modifier.weight(1f)) {
                XText(
                    text = primaryText,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                XText(
                    text = secondaryText,
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            XIcon(
                icon = Icons.Default.ChevronRight,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
```

> **XTheme.Colors.Bitcoin**: Must add `val Bitcoin = Color(0xFFF7931A)` to `XTheme.Colors` in `XTheme.kt` before implementation.

---

### components/TransactionSummaryCard.kt

```kotlin
// → components/TransactionSummaryCard.kt
@Composable
fun TransactionSummaryCard(
    networkFee: String,
    totalDeduct: String,
    estimatedArrival: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),  // border-primary/30
                RoundedCornerShape(24.dp)
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)   // space-y-4
    ) {
        // Row 1: Network Fee
        SummaryRow(
            label = "Network Fee",
            value = networkFee,           // "~$7.54"
            valueStyle = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        // Row 2: Total Deduct
        SummaryRow(
            label = "Total Deduct",
            value = totalDeduct,          // "0.00 BTC"
            valueStyle = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        // Row 3: Estimated Arrival (with top border — partial divider)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    // border-t border-outline-variant/30 — top border only
                    drawLine(
                        color = Color(0xFF3F3822).copy(alpha = 0.3f),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                .padding(top = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                XText(
                    text = "Estimated Arrival",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    XIcon(
                        icon = Icons.Default.Bolt,
                        tint = XTheme.Colors.Success,
                        modifier = Modifier.size(14.dp)
                    )
                    XText(
                        text = estimatedArrival,    // "Fast · ~10 min"
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = XTheme.Colors.Success
                        )
                    )
                }
            }
        }
    }
}

// Private helper
@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueStyle: TextStyle
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        XText(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        XText(text = value, style = valueStyle)
    }
}
```

---

### SecurityBadge (inline in SendScreen.kt)

```kotlin
// → inline in SendScreen.kt (structural glue, too simple for own file)
Row(
    modifier = Modifier
        .fillMaxWidth()
        .alpha(0.5f),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
) {
    XIcon(
        icon = Icons.Default.VerifiedUser,
        modifier = Modifier.size(12.dp),
        tint = LocalContentColor.current
    )
    Spacer(8.dp)
    XText(
        text = "SECURED BY KMPILOT VAULT",
        style = TextStyle(
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (0.2 * 10).sp
        )
    )
}
```

---

### Decorative Ambient Glows (optional)

```kotlin
// The HTML has two fixed radial blur glow overlays at top-right and bottom-left corners.
// These use `bg-primary/5` (primary at 5% opacity) with massive blur (120-150px).
// In Compose: Box(Modifier.fillMaxSize()) with two Box overlays using drawBehind + BlurMaskFilter.
// [decorative — implement only if visual fidelity is critical; omit for initial implementation]
```

---

### Loading State

Shared screen — see: `.claude/docs/_shared/designs/loading.png`
Token inventory: `.claude/docs/_shared/designs/extracted/tokens_loading.md`

```
Box(Modifier.fillMaxSize(), contentAlignment = Center) {
    XCircularProgressIndicator()
}
```

---

### Failed State

Shared screen — see: `.claude/docs/_shared/designs/failed.png`
Token inventory: `.claude/docs/_shared/designs/extracted/tokens_failed.md`

```
Column(Modifier.fillMaxSize(), verticalArrangement = Center, horizontalAlignment = CenterHorizontally) {
    XIcon(Icons.Default.Warning, Modifier.size(80.dp), tint = MaterialTheme.colorScheme.error)
    Spacer(32.dp)
    XText("Something went wrong", style = TextStyle(fontSize = 20.sp, fontWeight = SemiBold, color = onSurfaceVariant))
    Spacer(8.dp)
    XText("An unexpected error occurred...", style = TextStyle(fontSize = 14.sp, color = outline))
    Spacer(32.dp)
    XButton("Retry", onClick = onRetry, shape = RoundedCornerShape(12.dp), modifier = Modifier.width(200.dp).height(56.dp))
}
```

---

## Pre-Implementation Contract

> Architecture rules, color rules, and X-component defaults are project-wide and live in their canonical sources — do not restate them here:
> - Architecture rules → [`_shared/patterns.md`](../../_shared/patterns.md)
> - Color rules → [`m3-colors.md`](../references/m3-colors.md)
> - X-component default-render behavior → [`_shared/X_COMPONENTS_CATALOG.md`](../../_shared/X_COMPONENTS_CATALOG.md)
>
> This contract captures only feature-specific data the implementer cannot derive from those references.

### XTheme Updates Required

| Action | File | Change |
|--------|------|--------|
| Add `Bitcoin` custom color | `XTheme.kt` → `XTheme.Colors` object | `val Bitcoin = Color(0xFFF7931A)` |

> **No M3 role changes needed** — all design colors already map to existing XDarkColors/XLightColors roles.

### Color Audit

#### Defined Roles

| Role | Hex | Usage |
|------|-----|-------|
| background | #0F0D09 | Screen canvas, footer gradient |
| surface | #1C1910 | Recipient, Asset, Network cards |
| surfaceVariant | #302B1C 	| Chip fill, network icon bg, summary card bg |
| onSurface | #EDE8D5 | Hero amount, coin names, total deduct value |
| onSurfaceVariant | #C4BA94 | Labels, balance text, placeholder, subtitles |
| outlineVariant | #3F3822 | Card borders, chip borders, summary divider |
| primary | #F5D76E | Gold accents, CTA fill, cursor, left bar |
| onPrimary | #2C1900 | CTA button text |

#### Missing Roles

*None.*

#### Custom Colors

| Name | Hex | Justification |
|------|-----|---------------|
| XTheme.Colors.Success | #4ADE80 | Semantic success green — already in XTheme.Colors |
| XTheme.Colors.Bitcoin | #F7931A | Bitcoin brand orange for coin icon — add to XTheme.Colors |

### Component Overrides

| Component | Property | HTML Value | X-component Default | Override Required |
|-----------|----------|-----------|-------------------|------------------|
| `XButton` (CTA) | shape | 24dp (rounded-xl) | CircleShape | `shape = RoundedCornerShape(24.dp)` |
| `XButton` (CTA) | gold-glow shadow | `box-shadow 0 0 20px rgba(245,215,110,0.15)` | none | `drawBehind` glow or omit (decorative) |
| `XIconButton` (paste) | containerColor | transparent | surface | `containerColor = Color.Transparent` |
| `XIconButton` (QR) | containerColor | transparent | surface | `containerColor = Color.Transparent` |
| `XTopAppBar` | navigationIcon contentColor | primary (#F5D76E) | contentColor (surface-based) | Pass `contentColor = primary` via `XTopAppBar` colors param |
| `XTopAppBar` | title alignment | left-adjacent to back arrow (ml-2) | always center-aligned | Accept center alignment — catalog limitation |
| Recipient card | background | surface (#1C1910) | — (custom Box, not XCard) | Use `Box.background(surface, RoundedCornerShape(24.dp))` |
| Asset/Network cards | background, shape | surface, 24dp | XCard: surfaceVariant, 12dp | Use `Column.background(surface, RoundedCornerShape(24.dp))` not XCard |
| Transaction summary | background, shape, border | surfaceVariant, 24dp, primary/30 | XCard: surfaceVariant, 12dp, no border | Use `Column.background(surfaceVariant, RoundedCornerShape(24.dp))` + border modifier |
| Quick chips | shape, bg, text color | 16dp, surfaceVariant, primary | XFilterChip: CircleShape, surface, onSurfaceVariant | Use custom `Box` — do not use XFilterChip |

---

## Post-Implementation Checklist

- [ ] `XTheme.Colors.Bitcoin = Color(0xFFF7931A)` added to `XTheme.kt` `XTheme.Colors` object
- [ ] `HeroAmountSection` component implemented with 64sp ExtraBold amount, BTC pill, gold cursor underline, balance row, 3 quick chips
- [ ] `RecipientCard` with gold left accent bar (4dp), transparent `BasicTextField`, paste + QR `XIconButton`s with `Color.Transparent` containerColor
- [ ] `AssetNetworkGrid` 2-column `Row` with `AssetSelectorCard` (private helper)
- [ ] `TransactionSummaryCard` with `primary.copy(0.3f)` border, `drawBehind` top-border divider before Estimated Arrival row
- [ ] Security badge inline in `SendScreen.kt` with `Modifier.alpha(0.5f)`
- [ ] Sticky footer `Box` in `XScaffold` bottomBar with vertical gradient + `XButton` (24dp corners)
- [ ] `XIconButton` paste and QR: `containerColor = Color.Transparent` — verify no surface circle visible
- [ ] `XButton` CTA: `shape = RoundedCornerShape(24.dp)` — verify not CircleShape
- [ ] No raw `Color(hex)` values in feature code except `XTheme.Colors.*` references
- [ ] No `MaterialTheme` direct imports — use `MaterialTheme.colorScheme.*` for all M3 roles
- [ ] All 4 UI states wired: Uninitialized, Loading (`XCircularProgressIndicator`), Success (full form), Failed (error icon + retry)
- [ ] Build passes: `./gradlew :feature:send:assembleAndroidMain`
- [ ] Code formatted: `./gradlew :feature:send:ktlintFormat`
