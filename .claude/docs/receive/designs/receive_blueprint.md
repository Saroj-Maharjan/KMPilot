# Compose Implementation Blueprint: Receive

> Flat layout — 3 visual sections (Asset Selector, Address Pill, Warning Banner) + sticky bottom bar.
> Source: Stitch HTML exports for success (9106B / 33 elements), loading (6855B / 10 elements), failed (7041B / 13 elements).
> Screen dimensions: 780×1768px across all states.

---

## Design Tokens

| Hex | M3 Role | Usage |
|-----|---------|-------|
| #0D0919 | `background` | Screen bg, app bar bg, bottom button area backdrop |
| #181228 | `surface` | Warning banner background |
| #231A38 | `surfaceVariant` | Asset selector fill, address pill fill, Share button fill |
| #4A3F6B | `outline` | Asset selector border, address pill border, Share button border |
| #E9E0FF | `onSurface` | App bar title, coin name, address text, warning heading, CTA label |
| #C5BCE0 | `onSurfaceVariant` | Network subtitle, expand icon, warning body text, failed subtitle |
| #9D70FF | `primary` | Copy icon, Copy Address button bg, loading spinner border |
| #1A0054 | `onPrimary` | Copy Address button label |
| #FFB4AB | `error` | Warning icon, warning border (@40% alpha), failed icon + container bg (@10% alpha) |
| #EAB308 | inline `Color(0xFFEAB308)` | Bitcoin coin circle bg (brand color — not an M3 role) |

### Tailwind Config (success state — verified)

```
borderRadius:
  DEFAULT (rounded):  0.25rem = 4dp
  lg (rounded-lg):    0.5rem  = 8dp
  xl (rounded-xl):    0.75rem = 12dp
  2xl (rounded-2xl):  1.5rem  = 24dp
  full (rounded-full): 9999px = CircleShape
font: Manrope (body-md, body-lg, label-md, label-lg), JetBrains Mono (mono)
```

---

## Typography Scale

| Usage | Size (sp) | Weight | Letter Spacing | Notes |
|-------|-----------|--------|----------------|-------|
| App bar title | 20 | Bold (700) | −0.5sp | `text-[20px] font-bold tracking-[-0.5px]` |
| Coin name | 16 | Bold (700) | 0 | `text-body-lg font-bold` |
| Network subtitle | 12 | Medium (500) | 0.5sp | `text-label-md` |
| Address text | 14 | Normal (400) | 0 | `text-body-md font-mono` (JetBrains Mono) |
| Warning heading | 14 | Bold (700) | 0 | `text-body-md font-bold` |
| Warning body | 12 | Medium (500) | 0.5sp | `text-label-md leading-relaxed` (line-height 1.625×) |
| Failed heading | 24 | Bold (700) | 0 | `text-[24px] font-bold` |
| Failed subtitle | 16 | Normal (400) | 0 | `text-[16px] font-normal leading-relaxed` |
| Coin icon letter "B" | 20 | Bold (700) | 0 | `text-xl font-bold text-white` |
| Share / Copy button label | varies | SemiBold/Bold | 0 | Share: 600, Copy: 700 |

---

## Spacing Grid

| Context | Property | Value (dp) |
|---------|----------|------------|
| App bar | horizontal padding | 16 (px-4) |
| App bar | vertical padding | 16 (py-4) |
| Main content | horizontal padding | 16 (px-4) |
| Main content | vertical padding | 24 (py-6) |
| Main content | bottom padding | 192 (pb-48, absorbed by XScaffold paddingValues + bottomBar) |
| Sections | vertical gap | 24 (space-y-6) |
| Asset selector | inner padding | 16 (p-4) |
| Asset selector | icon→text gap | 16 (gap-4) |
| Asset selector | corner radius | 24 (rounded-2xl) |
| Coin icon | size | 48×48 (w-12 h-12) |
| Coin icon | border | 2dp white/10% |
| Address pill | left padding | 24 (pl-6) |
| Address pill | right padding | 8 (pr-2) |
| Address pill | vertical padding | 8 (py-2) |
| Address pill | corner radius | CircleShape (rounded-full) |
| Address text | right margin | 16 (mr-4) |
| Copy icon button | size | 40×40 (w-10 h-10) |
| Copy icon | size | 20sp (text-[20px]) |
| Warning banner | inner padding | 16 (p-4) |
| Warning banner | icon→content gap | 16 (gap-4) |
| Warning banner | corner radius | 12 (rounded-xl) |
| Warning banner | text rows gap | 4 (space-y-1) |
| Bottom gradient | height | 96 (h-24) |
| Bottom bar area | horizontal padding | 24 (px-6) |
| Bottom bar area | bottom padding | 40 (pb-10) |
| Bottom bar area | top padding | 16 (pt-4) |
| Bottom bar | button gap | 16 (gap-4) |
| Share button | height | 56 (h-14) |
| Copy Address button | height | 56 (h-14) |
| Button icon→label gap | 8 (gap-2) | |
| Failed icon container | size | 96×96 (w-24 h-24) |
| Failed icon container | corner radius | 24 (rounded-3xl) |
| Failed icon container | bottom margin | 24 (mb-6) |
| Failed icon | size | 80sp (text-[80px]) |
| Failed heading | bottom margin | 12 (mb-3) |
| Failed content | max width | 280 (max-w-[280px]) |

---

## Component Tree

### Shared Scaffold (all states)

```
XScaffold(
    containerColor = MaterialTheme.colorScheme.background,  // #0D0919
    topBar = {
        XTopAppBar(
            title = { XText("Receive", fontSize = 20.sp, fontWeight = Bold, letterSpacing = (-0.5).sp, color = onSurface) },
            backgroundColor = MaterialTheme.colorScheme.background,  // flush bg — no elevation
            navigationIcon = {
                XIconButton(
                    onClick = onBackClick,
                    colors = XIconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) { XIcon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back") }
            }
        )
    },
    bottomBar = { **[state-specific — see per-state below]** },
) { paddingValues ->
    **[state-specific content]**
}
```

> **XTopAppBar note**: `XTopAppBar` always center-aligns its title via `CenterAlignedTopAppBar`. The design also shows a centered title — this matches. The `backgroundColor` must be set to `background` explicitly since `XTopAppBar` defaults to `surface`. Title text style is forced to `headlineSmall/SemiBold` internally — override by passing a custom `XText` with explicit style in the title slot.

---

### Success State

**bottomBar**:
```
// → ReceiveScreen.kt (structural glue — exists only to wire state to buttons)
Box {
    // Bottom gradient overlay (96dp tall, fades transparent → background)
    Box(
        Modifier
            .fillMaxWidth()
            .height(96.dp)
            .align(Alignment.TopCenter)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                )
            )
    )
    // Button row
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
            .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 40.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Share button (flex-1 = weight(1f))
        XOutlinedButton(
            onClick = onShareClick,
            modifier = Modifier.weight(1f).height(56.dp),
            shape = CircleShape,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            XIcon(Icons.Default.Share, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            XText("Share", fontWeight = SemiBold)
        }
        // Copy Address button (flex-[1.5] = weight(1.5f))
        XButton(
            onClick = onCopyClick,
            modifier = Modifier.weight(1.5f).height(56.dp)
                .shadow(elevation = 0.dp, shape = CircleShape)  // [decorative glow shadow-[0_0_20px_rgba(157,112,255,0.3)] — omit or use drawBehind]
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            XIcon(Icons.Default.ContentCopy, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            XText("Copy Address", fontWeight = Bold)
        }
    }
}
```

**content**:
```
// → ReceiveScreen.kt (top-level layout scaffold)
Column(
    Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp, vertical = 24.dp)
        .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(24.dp)
) {
    AssetSelectorCard(
        coinName = uiModel.coinName,         // "Bitcoin (BTC)"
        networkName = uiModel.networkName,   // "Bitcoin Network"
        onClick = onAssetSelectorClick
    )
    AddressPill(
        address = uiModel.walletAddress,     // "bc1qxy2k...5mdq3w0c0"
        onCopyClick = onCopyClick
    )
    NetworkWarningBanner(
        heading = "Bitcoin Network only",
        body = "Sending coins or tokens via any other network will result in permanent loss."
    )
}
```

**`AssetSelectorCard`** → `components/AssetSelectorCard.kt`
```
// Tappable row card for coin + network selection
XCard(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
) {
    Row(
        Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bitcoin coin circle
            Box(
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEAB308))  // inline brand color
                    .border(2.dp, Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                XText("B", color = Color.White, fontWeight = Bold, fontSize = 20.sp)
            }
            Column {
                XText(
                    coinName,           // "Bitcoin (BTC)"
                    fontWeight = Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                XText(
                    networkName,        // "Bitcoin Network"
                    fontSize = 12.sp,
                    fontWeight = Medium,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        XIcon(
            Icons.Default.ExpandMore,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

> **XCard note**: `XCard` defaults to `MaterialTheme.shapes.medium` (12dp). Override to `RoundedCornerShape(24.dp)` explicitly. `XCard` also defaults `containerColor` to `CardDefaults.cardColors()` (surfaceVariant in M3 — which maps to #231A38 in XDarkColors, matching the design). Pass the `border` param explicitly since `XCard` has no default border.

**`AddressPill`** → `components/AddressPill.kt`
```
// Pill row: monospace address + copy icon button
Row(
    Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
        .padding(start = 24.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    XText(
        text = address,
        fontFamily = FontFamily.Monospace,  // JetBrains Mono
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.weight(1f).padding(end = 16.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    XIconButton(
        onClick = onCopyClick,
        modifier = Modifier.size(40.dp),
        colors = XIconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        XIcon(Icons.Default.ContentCopy, Modifier.size(20.dp))
    }
}
```

> **XIconButton note**: Default `containerColor` is `surface` (renders a surface-colored circle). Must pass explicit `containerColor = primary.copy(alpha = 0.1f)` and `contentColor = primary` to match the design.

**`NetworkWarningBanner`** → `components/NetworkWarningBanner.kt`
```
// Warning banner: surface bg + error-tinted border + warning icon + text
Row(
    Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
        .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
        .padding(16.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.Top
) {
    XIcon(
        Icons.Default.Warning,
        tint = MaterialTheme.colorScheme.error,
        modifier = Modifier.size(24.dp)
    )
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        XText(
            heading,
            fontSize = 14.sp,
            fontWeight = Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        XText(
            body,
            fontSize = 12.sp,
            fontWeight = Medium,
            letterSpacing = 0.5.sp,
            lineHeight = (12 * 1.625).sp,  // leading-relaxed = 1.625×
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

---

### Loading State

**bottomBar**: _(empty — no buttons shown in loading state)_

**content**:
```
// → ReceiveScreen.kt
Box(
    Modifier.fillMaxSize().padding(paddingValues),
    contentAlignment = Alignment.Center
) {
    XCircularProgressIndicator(
        modifier = Modifier.size(48.dp),
        color = MaterialTheme.colorScheme.primary,
        strokeWidth = 4.dp
    )
}
```

> **XCircularProgressIndicator note**: Default color is `primary` (#9D70FF) — matches design. Default strokeWidth is 4dp — matches `border-4` in the HTML. No override needed.

---

### Failed State

**bottomBar**: _(empty — no buttons shown in failed state)_

**content**:
```
// → ReceiveScreen.kt
Box(
    Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp),
    contentAlignment = Alignment.Center
) {
    Column(
        Modifier.widthIn(max = 280.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Error icon container
        Box(
            Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                .padding(bottom = 24.dp),  // mb-6 on container → Spacer below instead
            contentAlignment = Alignment.Center
        ) {
            XIcon(
                Icons.Default.Error,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }
        Spacer(Modifier.height(24.dp))  // mb-6 on icon container
        XText(
            "Failed to Load Address",
            fontSize = 24.sp,
            fontWeight = Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        XText(
            "Unable to retrieve your wallet address. Please try again.",
            fontSize = 16.sp,
            fontWeight = Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = (16 * 1.625).sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
```

---

## Pre-Implementation Contract

> Architecture rules, color rules, and X-component defaults are project-wide and live in their canonical sources — do not restate them here:
> - Architecture rules → [`_shared/patterns.md`](../../_shared/patterns.md)
> - Color rules → [`m3-colors.md`](m3-colors.md)
> - X-component default-render behavior → [`_shared/X_COMPONENTS_CATALOG.md`](../../_shared/X_COMPONENTS_CATALOG.md)
>
> This contract captures only feature-specific data the implementer cannot derive from those references.

### XTheme Updates Required

_(none — all design colors are already defined in both XLightColors and XDarkColors)_

### Color Audit

#### Defined Roles

| Role | Hex | Usage |
|------|-----|-------|
| `background` | #0D0919 | Screen bg, app bar bg, bottom bar backdrop |
| `surface` | #181228 | Warning banner bg |
| `surfaceVariant` | #231A38 | Asset selector fill, address pill fill, Share button fill |
| `outline` | #4A3F6B | Asset selector border, address pill border, Share button border |
| `onSurface` | #E9E0FF | App bar title, coin name, address text, warning heading, CTA text |
| `onSurfaceVariant` | #C5BCE0 | Network subtitle, expand icon, warning body, failed subtitle |
| `primary` | #9D70FF | Copy icon button bg (@10%), Copy Address fill, loading spinner |
| `onPrimary` | #1A0054 | Copy Address button label |
| `error` | #FFB4AB | Warning icon, warning border (@40%), failed icon + container bg (@10%) |

#### Missing Roles

_(none)_

#### Custom Colors

| Name | Hex | Justification |
|------|-----|---------------|
| Bitcoin orange (inline) | #EAB308 | BTC coin brand color — decorative, no semantic M3 role |

### Component Overrides (divergences from X-component defaults)

| Component | Property | HTML Value | X-component Default | Override Required |
|-----------|----------|-----------|---------------------|-------------------|
| `XTopAppBar` | `backgroundColor` | #0D0919 (`background`) | `surface` (#181228) | Pass `backgroundColor = MaterialTheme.colorScheme.background` |
| `XTopAppBar` | title text style | 20sp Bold −0.5sp | `headlineSmall/SemiBold` (forced internally) | Pass custom `XText` in title slot with explicit `fontSize`, `fontWeight`, `letterSpacing` |
| `XIconButton` (copy) | `containerColor` | #9D70FF @10% | `surface` | Pass `containerColor = primary.copy(alpha = 0.1f)` |
| `XIconButton` (copy) | `contentColor` | #9D70FF | `contentColorFor(surface)` | Pass `contentColor = primary` |
| `XIconButton` (back) | `containerColor` | transparent | `surface` | Pass `containerColor = Color.Transparent` |
| `XIconButton` (back) | `contentColor` | #E9E0FF (`onSurface`) | `contentColorFor(surface)` | Pass `contentColor = MaterialTheme.colorScheme.onSurface` |
| `XCard` (asset selector) | `shape` | 24dp | `shapes.medium` (12dp) | Pass `shape = RoundedCornerShape(24.dp)` |
| `XOutlinedButton` (Share) | `containerColor` | #231A38 (`surfaceVariant`) | `transparent` | Pass `containerColor = surfaceVariant` |
| `XOutlinedButton` (Share) | `contentColor` | #E9E0FF (`onSurface`) | `primary` | Pass `contentColor = onSurface` |
| `XOutlinedButton` (Share) | `border` | 1dp #4A3F6B (`outline`) | border follows contentColor | Pass `border = BorderStroke(1.dp, outline)` |
| `XButton` (Copy Address) | `shape` | CircleShape | CircleShape | _(matches — no override)_ |
| `XScaffold` | `containerColor` | #0D0919 | `XTheme.Colors.PaleLavender` (unresolved) | Pass `containerColor = background` explicitly |

---

## Post-Implementation Checklist

- [ ] No XTheme changes needed (all roles already defined)
- [ ] `AssetSelectorCard` component exists at `components/AssetSelectorCard.kt`
- [ ] `AddressPill` component exists at `components/AddressPill.kt`
- [ ] `NetworkWarningBanner` component exists at `components/NetworkWarningBanner.kt`
- [ ] `XTopAppBar` `backgroundColor` overridden to `background` (not `surface`)
- [ ] Both `XIconButton` instances (back + copy) have explicit `containerColor` + `contentColor`
- [ ] `XCard` (asset selector) shape overridden to `RoundedCornerShape(24.dp)`
- [ ] `XOutlinedButton` (Share) `containerColor`, `contentColor`, and `border` overridden
- [ ] `XScaffold` `containerColor` set explicitly to `background`
- [ ] Loading state shows no bottomBar content
- [ ] Failed state shows no bottomBar content
- [ ] All colors use `MaterialTheme.colorScheme.{role}` — no raw `Color()` hex (except `Color(0xFFEAB308)` for Bitcoin brand orange)
- [ ] Address text uses `FontFamily.Monospace` (JetBrains Mono equivalent)
- [ ] Build passes: `./gradlew :feature:receive:assembleAndroidMain`
- [ ] Code formatted: `./gradlew :feature:receive:ktlintFormat`
