# Compose Implementation Blueprint: Receive

> Decomposed layout — 5 visual sections (App Bar, Asset Selector, Address Card, Warning Banner, Bottom Bar).
> Source: Stitch HTML exports for success (6264B / 35 elements), loading (shared), failed (shared).
> Screen dimensions: 780×1768px.

---

## Design Tokens

| Hex | M3 Role | Usage |
|-----|---------|-------|
| #0F0D09 | background | Screen canvas |
| #1C1910 | surface | Asset selector bg, address card bg, bottom bar bg |
| #302B1C | surfaceVariant | Address pill bg, Share button bg |
| #F5D76E | primary | Back arrow, address card border, copy icon, Copy Address button fill |
| #2C1900 | onPrimary | Copy Address button label |
| #EDE8D5 | onSurface | App bar title, coin name, address text, warning title, Share label |
| #C4BA94 | onSurfaceVariant | Network subtitle, address card label, warning body |
| #726A48 | outline | Asset selector border, address pill border, Share border, chevron, footer top border |
| #FFB4AB | error | Warning icon, warning border (40% alpha) |
| #93000A | errorContainer | Warning banner bg (20% alpha) |
| #F7931A | XTheme.Colors.Bitcoin | Bitcoin coin icon container bg |
| #FFFFFF | Color.White | "₿" symbol text on Bitcoin orange circle |

---

## Typography Scale

| Usage | Size (sp) | Weight | Letter Spacing | Color Role |
|-------|-----------|--------|----------------|------------|
| App bar title "Receive" | 20 | Bold 700 | -0.025em | onSurface |
| Coin name "Bitcoin" | 14 | Bold 700 | 0 | onSurface |
| Network "Bitcoin Network" | 12 | Normal 400 | 0 | onSurfaceVariant |
| Address label "Your Bitcoin address" | 14 | Medium 500 | 0 | onSurfaceVariant |
| Address text (monospace) | 12 | Normal 400 | -0.025em | onSurface |
| Warning title | 14 | Bold 700 | 0 | onSurface |
| Warning body | 12 | Normal 400 | 0 | onSurfaceVariant |
| Share button | 14 | SemiBold 600 | 0 | onSurface |
| Copy Address button | 14 | Bold 700 | 0 | onPrimary |

---

## Spacing Grid

| Context | Property | Value (dp) |
|---------|----------|------------|
| Screen | horizontal padding | 16 |
| Main body | top offset (below fixed app bar) | 64 |
| Main body | bottom padding | 128 |
| Asset selector → Address card | vertical gap | 24 |
| Address card → Warning banner | vertical gap | 24 |
| Address card | padding (all sides) | 32 |
| Address label | bottom margin | 24 |
| Warning banner | padding (all sides) | 16 |
| Warning icon ↔ text | gap | 12 |
| Warning title ↔ body | gap | 4 |
| Bottom bar | horizontal padding | 16 |
| Bottom bar | top padding | 16 |
| Bottom bar | bottom padding | 32 |
| Share ↔ Copy Address | gap | 16 |
| Buttons | height | 56 |
| Back button / right spacer | size | 40×40 |

---

## Component Tree

### Shared Scaffold (all states)

```
// → ReceiveScreen.kt
XScaffold(
  containerColor = MaterialTheme.colorScheme.background,
  topBar = {
    // Custom Row — XTopAppBar centers titles and cannot be used here
    Row(
      Modifier
        .fillMaxWidth()
        .height(64.dp)
        .background(Color.Transparent)
        .padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      XIconButton(
        onClick = onBackClick,
        modifier = Modifier.size(40.dp),
        colors = XIconButtonDefaults.iconButtonColors(
          containerColor = Color.Transparent,
          contentColor = MaterialTheme.colorScheme.primary   // #F5D76E
        )
      ) {
        XIcon(Icons.AutoMirrored.Filled.ArrowBack)
      }
      XText(
        text = "Receive",
        modifier = Modifier.padding(start = 8.dp),
        style = TextStyle(
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = (-0.025).em,
          color = MaterialTheme.colorScheme.onSurface
        )
      )
      Spacer(Modifier.size(40.dp).weight(1f, fill = false))  // right balance spacer
    }
  },
  bottomBar = { ReceiveBottomBar(onShareClick, onCopyClick) },
  content = { paddingValues -> [state-specific content] }
)
```

### Success State

```
// → ReceiveScreen.kt (state routing + top-level column)
Column(
  Modifier
    .fillMaxSize()
    .padding(paddingValues)
    .padding(horizontal = 16.dp)
    .padding(top = 64.dp, bottom = 128.dp)
) {
  AssetSelectorCard(
    coinName = uiState.coinName,
    networkName = uiState.networkName,
    onClick = onAssetSelectorClick
  )
  Spacer(Modifier.height(24.dp))
  AddressCard(walletAddress = uiState.walletAddress, onCopyClick = onCopyClick)
  Spacer(Modifier.height(24.dp))
  NetworkWarningBanner()
}
```

```
// → components/AssetSelectorCard.kt
@Composable
fun AssetSelectorCard(coinName: String, networkName: String, onClick: () -> Unit) {
  Button(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth().height(64.dp),  // 12dp padding + 40dp icon = ~64dp
    shape = CircleShape,
    colors = ButtonDefaults.buttonColors(
      containerColor = MaterialTheme.colorScheme.surface,     // #1C1910
      contentColor = MaterialTheme.colorScheme.onSurface
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),  // #726A48
    contentPadding = PaddingValues(12.dp)
  ) {
    Row(
      Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Bitcoin icon circle
        Box(
          Modifier
            .size(40.dp)
            .background(XTheme.Colors.Bitcoin, CircleShape),  // #F7931A
          contentAlignment = Alignment.Center
        ) {
          XText("₿", style = TextStyle(color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold))
        }
        Column(horizontalAlignment = Alignment.Start) {
          XText(
            text = coinName,
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold,
                              color = MaterialTheme.colorScheme.onSurface)
          )
          XText(
            text = networkName,
            style = TextStyle(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }
      }
      XIcon(
        Icons.Default.ExpandMore,
        modifier = Modifier.padding(end = 8.dp),
        tint = MaterialTheme.colorScheme.outline
      )
    }
  }
}
```

```
// → components/AddressCard.kt  (contains AddressPill internally)
@Composable
fun AddressCard(walletAddress: String, onCopyClick: () -> Unit) {
  Box(
    Modifier
      .fillMaxWidth()
      .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))  // gold border
      .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
      .drawBehind {
        // Decorative gold radial glow — optional, may omit for simplicity
        drawCircle(
          brush = Brush.radialGradient(
            listOf(
              MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
              Color.Transparent
            )
          ),
          radius = size.minDimension * 0.7f
        )
      }
      .padding(32.dp)
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      XText(
        text = "Your Bitcoin address",
        modifier = Modifier.align(Alignment.Start).padding(bottom = 24.dp),
        style = TextStyle(
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      )
      AddressPill(walletAddress = walletAddress, onCopyClick = onCopyClick)
    }
  }
}

// AddressPill — internal to AddressCard.kt
@Composable
private fun AddressPill(walletAddress: String, onCopyClick: () -> Unit) {
  Row(
    Modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
      .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    XText(
      text = walletAddress,
      modifier = Modifier.weight(1f),
      overflow = TextOverflow.Ellipsis,
      maxLines = 1,
      style = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        letterSpacing = (-0.025).em,
        color = MaterialTheme.colorScheme.onSurface
      )
    )
    XIconButton(
      onClick = onCopyClick,
      colors = XIconButtonDefaults.iconButtonColors(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary
      )
    ) {
      XIcon(Icons.Default.ContentCopy, modifier = Modifier.size(20.dp))
    }
  }
}
```

```
// → components/NetworkWarningBanner.kt
@Composable
fun NetworkWarningBanner() {
  Row(
    Modifier
      .fillMaxWidth()
      .background(
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
        RoundedCornerShape(24.dp)
      )
      .border(
        1.dp,
        MaterialTheme.colorScheme.error.copy(alpha = 0.4f),
        RoundedCornerShape(24.dp)
      )
      .padding(16.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    XIcon(
      Icons.Default.Warning,
      modifier = Modifier.size(24.dp),
      tint = MaterialTheme.colorScheme.error
    )
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
      XText(
        text = "Only send Bitcoin (BTC)",
        style = TextStyle(
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
      )
      XText(
        text = "Sending any other asset to this address may result in permanent loss.",
        style = TextStyle(
          fontSize = 12.sp,
          lineHeight = (12 * 1.625).sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      )
    }
  }
}
```

```
// → ReceiveScreen.kt (bottom bar — structural glue, wires two buttons)
@Composable
private fun ReceiveBottomBar(onShareClick: () -> Unit, onCopyClick: () -> Unit) {
  Row(
    Modifier
      .fillMaxWidth()
      .background(
        MaterialTheme.colorScheme.surface,    // #1C1910
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
      )
      .shadow(8.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
      .border(
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
      )
      .padding(horizontal = 16.dp, vertical = 16.dp)
      .padding(bottom = 32.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Share button (secondary)
    Button(
      onClick = onShareClick,
      modifier = Modifier.weight(1f).height(56.dp),
      shape = RoundedCornerShape(24.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface
      ),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        XIcon(Icons.Default.Share, modifier = Modifier.size(20.dp))
        XText("Share", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight(600)))
      }
    }
    // Copy Address button (primary gold)
    XButton(
      onClick = onCopyClick,
      modifier = Modifier
        .weight(1f)
        .height(56.dp)
        .shadow(
          elevation = 4.dp,
          shape = RoundedCornerShape(24.dp),
          ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
          spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        ),
      shape = RoundedCornerShape(24.dp),     // override: default XButton is CircleShape
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
      )
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        XIcon(Icons.Filled.ContentCopy, modifier = Modifier.size(20.dp))  // FILL=1
        XText("Copy Address", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold))
      }
    }
  }
}
```

### Loading State
Shared screen — see: `.claude/docs/_shared/designs/loading.png`
Token inventory: `.claude/docs/_shared/designs/extracted/tokens_loading.md`
- `Box(Modifier.fillMaxSize(), contentAlignment = Center)` → `XCircularProgressIndicator(color = primary)`

### Failed State
Shared screen — see: `.claude/docs/_shared/designs/failed.png`
Token inventory: `.claude/docs/_shared/designs/extracted/tokens_failed.md`
- `Column(Modifier.fillMaxSize(), Center)` → `XIcon(warning, 80dp, error color)` + `XText("Something went wrong")` + `XButton("Retry", onRetry)`

---

## Pre-Implementation Contract

> Architecture rules, color rules, and X-component defaults are project-wide and live in their canonical sources — do not restate them here:
> - Architecture rules → [`_shared/patterns.md`](../../_shared/patterns.md)
> - Color rules → [`m3-colors.md`](../references/m3-colors.md)
> - X-component default-render behavior → [`_shared/X_COMPONENTS_CATALOG.md`](../../_shared/X_COMPONENTS_CATALOG.md)
>
> This contract captures only feature-specific data the implementer cannot derive from those references.

### XTheme Updates Required

*None — all design colors are already defined in both XLightColors and XDarkColors. No XTheme.kt changes needed.*

### Color Audit

#### Defined Roles
| Role | Hex | Usage |
|------|-----|-------|
| background | #0F0D09 | Screen canvas |
| surface | #1C1910 | Asset selector, address card, bottom bar |
| surfaceVariant | #302B1C | Address pill bg, Share button bg |
| primary | #F5D76E | Back arrow, card border, copy icon, CTA fill |
| onPrimary | #2C1900 | CTA label |
| onSurface | #EDE8D5 | Title, coin name, address, warning title, Share label |
| onSurfaceVariant | #C4BA94 | Subtitle, address label, warning body |
| outline | #726A48 | All borders, chevron |
| error | #FFB4AB | Warning icon, warning border (40% alpha) |
| errorContainer | #93000A | Warning bg (20% alpha) |

#### Missing Roles
*None.*

#### Custom Colors
| Name | Hex | Justification |
|------|-----|---------------|
| XTheme.Colors.Bitcoin | #F7931A | Bitcoin brand orange — already in XTheme.kt |
| Color.White | #FFFFFF | "₿" symbol on Bitcoin orange circle — no M3 role applies |

### Component Overrides

| Component | Property | HTML Value | X-component Default | Override Required |
|-----------|----------|-----------|---------------------|-------------------|
| `XTopAppBar` | title alignment | left-aligned (next to back button) | center-aligned (CenterAlignedTopAppBar) | Use custom `Row` layout — do NOT use `XTopAppBar` |
| `XTopAppBar` | background | transparent | `colorScheme.surface` | Custom `Row` with `Color.Transparent` |
| `XButton` (Copy Address) | shape | `RoundedCornerShape(24.dp)` | `CircleShape` | Pass `shape = RoundedCornerShape(24.dp)` |
| `XCard` (AddressCard) | shape | `RoundedCornerShape(20.dp)` | `RoundedCornerShape(12.dp)` | Pass `shape = RoundedCornerShape(20.dp)` or use `Box` with explicit border/background |
| `XCard` (AddressCard) | containerColor | `surface` (#1C1910) | `surfaceVariant` (CardDefaults) | Pass `containerColor = MaterialTheme.colorScheme.surface` |
| `XIconButton` (back arrow) | containerColor | `Color.Transparent` | `surface` (#1C1910 — audit trap) | Pass `containerColor = Color.Transparent` |
| `XScaffold` | containerColor | `background` (#0F0D09) | `XTheme.Colors.PaleLavender` (undefined — compile risk) | Always pass `containerColor = MaterialTheme.colorScheme.background` |

---

## Post-Implementation Checklist

- [ ] No XTheme.kt changes needed (all roles already defined)
- [ ] Custom `Row` top bar used instead of `XTopAppBar` (required for left-aligned title)
- [ ] `XIconButton` back arrow passes `containerColor = Color.Transparent` (prevents surface-colored circle audit trap)
- [ ] `XScaffold` passes explicit `containerColor = MaterialTheme.colorScheme.background`
- [ ] `AddressCard` uses explicit `surface` bg and `primary` 1dp border (not `XCard` defaults)
- [ ] `AddressCard` shape is `RoundedCornerShape(20.dp)` (not `XCard` default 12dp)
- [ ] `XButton` (Copy Address) shape is `RoundedCornerShape(24.dp)` (not default `CircleShape`)
- [ ] All colors use `MaterialTheme.colorScheme.{role}` or `XTheme.Colors.*` — no raw `Color()` hex
- [ ] `XTheme.Colors.Bitcoin` used for Bitcoin circle bg (already in XTheme.kt)
- [ ] `Color.White` used for "₿" symbol text (justified hardcode)
- [ ] Build passes: `./gradlew :feature:receive:assembleAndroidMain`
- [ ] Code formatted: `./gradlew :feature:receive:ktlintFormat`
