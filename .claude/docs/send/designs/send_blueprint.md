# Compose Implementation Blueprint: Send

> Decomposed layout — >3 visual sections (Recipient, Amount, Asset, Network, Summary, Button).
> Source: Stitch HTML exports for success (6670B), loading (78 lines), failed (85 lines).
> Screen dimensions: success 780×1874px, loading 780×1768px, failed 780×1768px.

---

## Design Tokens

| Hex | M3 Role | Usage |
|-----|---------|-------|
| #0D0919 | background | Screen bg, app bar, bottom gradient |
| #181228 | surface | Transaction summary card |
| #9D70FF | primary | CTA buttons, paste icon, coin ticker label, quick-% tint |
| #1A0054 | onPrimary | CTA button text |
| #E9E0FF | onSurface | Amount value, coin/network names, fee values |
| #C5BCE0 | onSurfaceVariant | Section labels, muted subtitles, expand icons |
| #231A38 | surfaceVariant | Input fill, asset selector fill |
| #4A3F6B | outline | Input + selector 1dp borders |
| #FFB4AB | error | Failed state error icon |
| #4ADE80 | XTheme.Colors.Success | "Fast" arrival indicator |
| #EAB308 | inline Color(0xFFEAB308) | Bitcoin coin icon tint (brand color) |

### Tailwind Config (per-state, verified identical across all 3 HTML files)

```
borderRadius:
  DEFAULT (rounded):  0.5rem = 8dp
  lg (rounded-lg):    1rem   = 16dp
  xl (rounded-xl):    1.5rem = 24dp
  full (rounded-full): 9999px = CircleShape
font: Manrope
```

---

## Typography Scale

| Usage | Size (sp) | Weight | Letter Spacing | Notes |
|-------|-----------|--------|----------------|-------|
| App bar title | 20 | Bold | -0.5sp | text-xl tracking-tight (-0.025em × 20) |
| Section labels | 14 | Medium | 0 | text-sm font-medium |
| Summary section header | 12 | Bold | 0.6sp | text-xs tracking-wider (0.05em × 12), uppercase |
| Amount value | 40 | Bold | 0 | text-[40px] |
| Coin ticker (beside amount) | 20 | SemiBold | 0 | text-xl font-semibold |
| Balance / muted text | 14 | Normal | 0 | text-sm |
| Coin/network primary name | 16 | Bold | 0 | font-bold (no text class = text-base = 16sp) |
| Coin/network subtitle | 12 | Normal | 0 | text-xs |
| Quick % button labels | 12 | Bold | 0 | text-xs font-bold |
| Fee row labels/values | 14 | Normal | 0 | text-sm |
| Total row value | 14 | SemiBold | 0 | text-sm font-semibold |
| Arrival label/value | 12 | Normal | 0 | text-xs |
| Error heading | 24 | Bold | 0 | text-2xl (failed state) |
| Error subtitle | 16 | Normal | 1.625 line-height | text-base leading-relaxed (failed state) |
| Button labels | 16 | Bold | 0 | font-bold, no text class = 16sp |

---

## Spacing Grid

| Context | Property | Value (dp) |
|---------|----------|------------|
| App bar | horizontal padding | 16 (px-4) |
| App bar | vertical padding | 16 (py-4) |
| Main column | horizontal padding | 16 (px-4) |
| Main column | bottom padding | 128 (pb-32, absorbed by XScaffold paddingValues) |
| Recipient section | top spacing | 16 (mt-4) |
| Input field | vertical padding | 16 (py-4) |
| Input field | leading padding | 16 (pl-4) |
| Input field | trailing padding | 48 (pr-12, room for paste icon) |
| Label → input | bottom gap | 8 (mb-2 on label) |
| Amount section | top spacing | 32 (mt-8) |
| Amount value + ticker | gap | 8 (gap-2) |
| Amount → balance | top spacing | 8 (mt-2) |
| Balance → % buttons | top spacing | 16 (mt-4) |
| % buttons | gap | 8 (gap-2) |
| % button | horizontal padding | 16 (px-4) |
| % button | vertical padding | 6 (py-1.5) |
| Asset selectors section | top spacing | 40 (mt-10) |
| Asset selectors | vertical gap | 16 (space-y-4) |
| Asset selector row | all-sides padding | 16 (p-4) |
| Coin icon | size | 40×40 (w-10 h-10) |
| Coin icon → text | gap | 12 (gap-3) |
| Summary card | top spacing | 32 (mt-8) |
| Summary card | all-sides padding | 16 (p-4) |
| Summary header | bottom spacing | 16 (mb-4) |
| Summary rows | vertical gap | 12 (space-y-3) |
| Divider → arrival row | top spacing | 4+12=16 (mt-1 + pt-3) |
| Arrival icon → text | gap | 4 (gap-1) |
| Bottom button area | all-sides padding | 24 (p-6) |
| Button (Send/Retry) | vertical padding | 16 (py-4) |
| Home indicator spacer | height | 16 (h-4) |
| Failed main | horizontal padding | 24 (px-6, differs from success 16dp) |
| Error icon container | bottom spacing | 32 (mb-8) |
| Error icon container | all-sides padding | 24 (p-6) |
| Error icon | size | 80 |
| Error heading | bottom spacing | 16 (mb-4) |
| Error heading | horizontal padding | 16 (px-4) |
| Error subtitle | max width | 280 |
| Loading spinner | size | 48×48 (h-12 w-12) |

---

## Component Tree

### Shared Scaffold (all states)

```
// → SendScreen.kt
XScaffold(
  topBar = XTopAppBar(
    title = "Send",
    navigationIcon = XIconButton(
      icon = Icons.Default.ArrowBack,
      onClick = onBackClick,
      modifier = Modifier.padding(8.dp),  // p-2
      shape = CircleShape                 // rounded-full
    ),
    actions = [
      XIconButton(
        icon = Icons.Default.QrCodeScanner,
        onClick = onQrScanClick,          // hidden (opacity-0) in loading state
        modifier = Modifier.padding(8.dp)
      )
    ],
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.background
    )
  ),
  bottomBar = [state-specific bottom bar slot],
  containerColor = MaterialTheme.colorScheme.background
) { paddingValues ->
  [state-specific content slot]
}
```

---

### Success State

#### Bottom Bar

```
// → SendScreen.kt
Box(
  modifier = Modifier
    .fillMaxWidth()
    .background(
      brush = Brush.verticalGradient(
        colors = listOf(
          Color.Transparent,
          MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
          MaterialTheme.colorScheme.background
        )
      )
    )
    .padding(24.dp)                       // p-6
) {
  Column {
    XButton(
      text = "Send Bitcoin",
      onClick = onSendClick,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),  // rounded-xl = 24dp
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
      ),
      elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
      // [omitted: shadow-primary/20 — decorative color shadow, use default elevation shadow]
      // [omitted: hover:scale-[0.98] active:scale-95 — no Compose equivalent]
      contentPadding = PaddingValues(vertical = 16.dp)
    )
    Spacer(modifier = Modifier.height(16.dp))  // h-4 home indicator
  }
}
```

#### Content

```
// → SendScreen.kt (orchestrator — sequences all sections)
Column(
  modifier = Modifier
    .fillMaxSize()
    .padding(paddingValues)
    .verticalScroll(rememberScrollState())
    .padding(horizontal = 16.dp)         // px-4 on main
) {
  Spacer(modifier = Modifier.height(16.dp))          // mt-4
  RecipientAddressInput(                             // → components/RecipientAddressInput.kt
    value = uiState.recipientAddress,
    onValueChange = onAddressChange,
    onPasteClick = onPasteClick
  )
  Spacer(modifier = Modifier.height(32.dp))          // mt-8
  AmountInput(                                       // → components/AmountInput.kt
    amount = uiState.amount,
    coinSymbol = uiState.selectedCoin.symbol,
    balance = uiState.availableBalance,
    onPercentClick = onPercentClick,
    onMaxClick = onMaxClick
  )
  Spacer(modifier = Modifier.height(40.dp))          // mt-10
  Column(
    verticalArrangement = Arrangement.spacedBy(16.dp) // space-y-4
  ) {
    AssetSelectorRow(                                // → components/AssetSelectorRow.kt
      label = "Asset",
      iconVector = Icons.Default.CurrencyBitcoin,
      iconTint = Color(0xFFEAB308),                  // yellow-500 — BTC brand color
      iconBackground = Color(0xFFEAB308).copy(alpha = 0.2f),
      name = uiState.selectedCoin.name,
      subtitle = uiState.selectedCoin.symbol,
      onClick = onCoinSelectClick
    )
    AssetSelectorRow(                                // → components/AssetSelectorRow.kt
      label = "Network",
      iconVector = Icons.Default.Lan,
      iconTint = MaterialTheme.colorScheme.primary,
      iconBackground = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
      name = uiState.selectedNetwork.name,
      subtitle = uiState.selectedNetwork.description,
      onClick = onNetworkSelectClick
    )
  }
  Spacer(modifier = Modifier.height(32.dp))          // mt-8
  TransactionSummaryCard(                            // → components/TransactionSummaryCard.kt
    networkFee = uiState.networkFee,
    totalDeduct = uiState.totalDeduct,
    estimatedArrival = uiState.estimatedArrival
  )
  Spacer(modifier = Modifier.height(24.dp))
}
```

---

### Loading State

#### Bottom Bar
```
// → SendScreen.kt
// No bottom bar in loading state — pass null or empty Box
```

#### Content
```
// → SendScreen.kt
Box(
  modifier = Modifier
    .fillMaxSize()
    .padding(paddingValues),
  contentAlignment = Alignment.Center
) {
  XCircularProgressIndicator(
    modifier = Modifier.size(48.dp),     // h-12 w-12 = 48dp
    color = MaterialTheme.colorScheme.primary
    // opacity-25 on track: XCircularProgressIndicator handles track opacity internally
  )
}
```

---

### Failed State

#### Bottom Bar
```
// → SendScreen.kt
// Same gradient Box as Success state, different button label and action
Box(
  modifier = Modifier
    .fillMaxWidth()
    .background(
      brush = Brush.verticalGradient(
        colors = listOf(
          Color.Transparent,
          MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
          MaterialTheme.colorScheme.background
        )
      )
    )
    .padding(24.dp)
) {
  Column {
    XButton(
      text = "Retry",
      onClick = onRetryClick,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
      ),
      elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
      contentPadding = PaddingValues(vertical = 16.dp)
    )
    Spacer(modifier = Modifier.height(16.dp))
  }
}
```

#### Content
```
// → SendScreen.kt
// NOTE: failed state uses px-6 = 24dp horizontal padding (NOT 16dp like success)
Column(
  modifier = Modifier
    .fillMaxSize()
    .padding(paddingValues)
    .padding(horizontal = 24.dp),        // px-6 (different from success px-4)
  horizontalAlignment = Alignment.CenterHorizontally,
  verticalArrangement = Arrangement.Center
) {
  Box(
    modifier = Modifier
      .padding(bottom = 32.dp)           // mb-8
      .background(
        color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),  // bg-error-soft/10
        shape = CircleShape
      )
      .padding(24.dp),                   // p-6
    contentAlignment = Alignment.Center
  ) {
    XIcon(
      imageVector = Icons.Default.ErrorOutline,  // material-symbols error, FILL=0, wght=300
      contentDescription = "Error",
      modifier = Modifier.size(80.dp),   // text-[80px]
      tint = MaterialTheme.colorScheme.error
    )
  }
  XText(
    text = "Transaction Failed",
    style = TextStyle(
      fontSize = 24.sp,                  // text-2xl
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface  // text-text-heading = #E9E0FF = onSurface
    ),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, bottom = 16.dp),  // px-4 mb-4
    textAlign = TextAlign.Center
  )
  XText(
    text = "Something went wrong. Please check your details and try again.",
    style = TextStyle(
      fontSize = 16.sp,                  // text-base (no text class)
      color = MaterialTheme.colorScheme.onSurfaceVariant,  // text-text-muted = #C5BCE0
      lineHeight = 26.sp                 // leading-relaxed = 1.625 × 16sp ≈ 26sp
    ),
    modifier = Modifier.widthIn(max = 280.dp),        // max-w-[280px]
    textAlign = TextAlign.Center
  )
}
```

---

## Named Components

### RecipientAddressInput
**File**: `components/RecipientAddressInput.kt`
**Reason**: Self-contained form field with label, text input, and paste button — independent unit with its own internal structure.

```
@Composable
fun RecipientAddressInput(
  value: String,
  onValueChange: (String) -> Unit,
  onPasteClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier) {
    XText(
      text = "Recipient Address",
      style = TextStyle(
        fontSize = 14.sp,                // text-sm
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant  // override: slate-400 → onSurfaceVariant
      ),
      modifier = Modifier.padding(bottom = 8.dp)  // mb-2
    )
    Box {
      XTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { XText("Paste or scan address", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        // override: placeholder slate-500 → onSurfaceVariant
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),   // rounded-xl = 24dp
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
          unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
          focusedBorderColor = MaterialTheme.colorScheme.primary,  // focus:ring-primary
          unfocusedBorderColor = MaterialTheme.colorScheme.outline,
          focusedTextColor = MaterialTheme.colorScheme.onSurface,   // override: slate-100 → onSurface
          unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(
          top = 16.dp, bottom = 16.dp,
          start = 16.dp, end = 48.dp   // pl-4 pr-12
        )
        // [omitted: transition-all, focus:ring-2 — no Compose equivalent]
      )
      XIconButton(
        icon = Icons.Default.ContentPaste,
        onClick = onPasteClick,
        modifier = Modifier
          .align(Alignment.CenterEnd)
          .padding(end = 12.dp),       // right-3 = 12dp
        tint = MaterialTheme.colorScheme.primary,
        contentPadding = PaddingValues(4.dp)  // p-1
      )
    }
  }
}
```

---

### AmountInput
**File**: `components/AmountInput.kt`
**Reason**: Self-contained amount display with balance info and quick percentage shortcuts — own visual identity and domain logic.

```
@Composable
fun AmountInput(
  amount: String,
  coinSymbol: String,
  balance: String,
  onPercentClick: (Int) -> Unit,
  onMaxClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    XText(
      text = "Amount",
      style = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant  // override: slate-400 → onSurfaceVariant
      ),
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp),       // mb-2
      textAlign = TextAlign.Center
    )
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      XText(
        text = amount,                 // "0.00"
        style = TextStyle(
          fontSize = 40.sp,            // text-[40px]
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,  // override: text-white → onSurface
          lineHeight = 40.sp           // leading-none
        )
      )
      Spacer(modifier = Modifier.width(8.dp))  // gap-2
      XText(
        text = coinSymbol,             // "BTC"
        style = TextStyle(
          fontSize = 20.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.primary
        )
      )
    }
    XText(
      text = balance,                  // "Balance: 1.24 BTC (~$78,420.00)"
      style = TextStyle(
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant  // override: slate-500 → onSurfaceVariant
      ),
      modifier = Modifier.padding(top = 8.dp)  // mt-2
    )
    Row(
      modifier = Modifier.padding(top = 16.dp),  // mt-4
      horizontalArrangement = Arrangement.spacedBy(8.dp)  // gap-2
    ) {
      // 25% button
      OutlinedButton(
        onClick = { onPercentClick(25) },
        shape = CircleShape,           // rounded-full
        colors = ButtonDefaults.outlinedButtonColors(
          containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),  // bg-primary/10
          contentColor = MaterialTheme.colorScheme.primary
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),  // border-primary/20
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)  // px-4 py-1.5
      ) { XText("25%", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)) }
      // 50% button — same as 25%
      OutlinedButton(
        onClick = { onPercentClick(50) },
        shape = CircleShape,
        colors = ButtonDefaults.outlinedButtonColors(
          containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
          contentColor = MaterialTheme.colorScheme.primary
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
      ) { XText("50%", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)) }
      // MAX button — filled primary
      XButton(
        text = "MAX",
        onClick = onMaxClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
      )
    }
  }
}
```

---

### AssetSelectorRow
**File**: `components/AssetSelectorRow.kt`
**Reason**: Reused twice (Coin + Network) with same structure — extracted repeated pattern (2 occurrences).

```
@Composable
fun AssetSelectorRow(
  label: String,
  iconVector: ImageVector,
  iconTint: Color,
  iconBackground: Color,
  name: String,
  subtitle: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier) {
    XText(
      text = label,
      style = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant  // override: slate-400 → onSurfaceVariant
      ),
      modifier = Modifier.padding(bottom = 8.dp)  // mb-2
    )
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          color = MaterialTheme.colorScheme.surfaceVariant,  // bg-surface-variant
          shape = RoundedCornerShape(24.dp)                  // rounded-xl = 24dp
        )
        .border(
          width = 1.dp,
          color = MaterialTheme.colorScheme.outline,         // border-outline
          shape = RoundedCornerShape(24.dp)
        )
        .clickable(onClick = onClick)
        // [omitted: hover:border-primary/50 — no Compose hover equivalent]
        .padding(16.dp),                                     // p-4
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp) // gap-3
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)                                     // w-10 h-10
            .background(color = iconBackground, shape = CircleShape),  // rounded-full
          contentAlignment = Alignment.Center
        ) {
          XIcon(
            imageVector = iconVector,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = iconTint
          )
        }
        Column {
          XText(
            text = name,
            style = TextStyle(
              fontSize = 16.sp,                // font-bold text-base
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface  // override: slate-100 → onSurface
            )
          )
          XText(
            text = subtitle,
            style = TextStyle(
              fontSize = 12.sp,               // text-xs
              color = MaterialTheme.colorScheme.onSurfaceVariant  // override: slate-500 → onSurfaceVariant
            )
          )
        }
      }
      XIcon(
        imageVector = Icons.Default.ExpandMore,
        contentDescription = null,
        modifier = Modifier.size(24.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant  // override: slate-400 → onSurfaceVariant
      )
    }
  }
}
```

---

### TransactionSummaryCard
**File**: `components/TransactionSummaryCard.kt`
**Reason**: Self-contained card with its own internal layout (rows, divider, arrival indicator) — independent visual unit.

```
@Composable
fun TransactionSummaryCard(
  networkFee: String,
  totalDeduct: String,
  estimatedArrival: String,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(
        color = MaterialTheme.colorScheme.surface,        // bg-surface = #181228
        shape = RoundedCornerShape(24.dp)                 // rounded-xl = 24dp
      )
      .border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),  // border-outline/30
        shape = RoundedCornerShape(24.dp)
      )
      .padding(16.dp)                                     // p-4
  ) {
    XText(
      text = "TRANSACTION SUMMARY",                       // uppercase via textTransform
      style = TextStyle(
        fontSize = 12.sp,                                 // text-xs
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.6.sp,                          // tracking-wider = 0.05em × 12sp
        color = MaterialTheme.colorScheme.onSurfaceVariant  // override: slate-500 → onSurfaceVariant
      ),
      modifier = Modifier.padding(bottom = 16.dp)        // mb-4
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {  // space-y-3
      // Network Fee row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        XText(
          text = "Network Fee",
          style = TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          // override: slate-400 → onSurfaceVariant
        )
        XText(
          text = networkFee,
          style = TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
          // override: slate-100 → onSurface
        )
      }
      // Total to Deduct row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        XText(
          text = "Total to Deduct",
          style = TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
        XText(
          text = totalDeduct,
          style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,            // font-semibold
            color = MaterialTheme.colorScheme.onSurface
          )
        )
      }
    }
    // Spacer for mt-1 = 4dp, then divider, then pt-3 = 12dp padding
    Spacer(modifier = Modifier.height(4.dp))             // mt-1
    XHorizontalDivider(
      color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)  // border-outline/20
    )
    Spacer(modifier = Modifier.height(12.dp))            // pt-3
    // Estimated arrival row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      XText(
        text = "Est. Arrival Time",
        style = TextStyle(
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant  // override: slate-500 → onSurfaceVariant
        )
      )
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)  // gap-1
      ) {
        XIcon(
          imageVector = Icons.Default.Bolt,
          contentDescription = null,
          modifier = Modifier.size(14.dp),               // text-sm ≈ 14dp
          tint = XTheme.Colors.Success                   // green-400 = #4ADE80
        )
        XText(
          text = estimatedArrival,                       // "Fast (10 min)"
          style = TextStyle(fontSize = 12.sp, color = XTheme.Colors.Success)
        )
      }
    }
  }
}
```

---

## Pre-Implementation Contract

### XTheme Updates Required

*No missing roles. All design colors map to existing XDarkColors roles.*

### Architecture Rules

- Use X-components exclusively (no Material3 directly) — see `:core:designsystem`
- Follow ScreenRoot pattern: `SendScreen` (ViewModel wrapper) + `SendScreenRoot` (testable)
- Handle all 4 UI states: `Uninitialized` / `Loading` / `Success` / `Failed`
- Use `_uiState.setState { copy() }` for state updates — never `_state.value =`
- Use `ImmutableList` for collections in state
- Callbacks for navigation (`onBackClick`) — never `navController`
- ViewModel invokes repository directly — no UseCases

### Color Rules

- ALL colors MUST use `MaterialTheme.colorScheme.{role}` — never raw `Color()` hex values
- Exception: `XTheme.Colors.Success` (#4ADE80) for arrival indicator
- Exception: `Color(0xFFEAB308)` for Bitcoin coin brand tint (no M3 equivalent)
- Bottom gradient: `Brush.verticalGradient` using `MaterialTheme.colorScheme.background`

### Color Audit

#### Defined Roles (all in XDarkColors)

| Role | Hex | Usage |
|------|-----|-------|
| background | #0D0919 | Screen bg, app bar bg, bottom gradient |
| surface | #181228 | Transaction summary card bg |
| primary | #9D70FF | CTA buttons, paste icon, coin ticker label, tint |
| onPrimary | #1A0054 | CTA button text |
| onSurface | #E9E0FF | Amount value, names, fee values |
| onSurfaceVariant | #C5BCE0 | Labels, muted text, expand icons |
| surfaceVariant | #231A38 | Input field fill, selector fill |
| outline | #4A3F6B | Input/selector borders |
| error | #FFB4AB | Failed state error icon |

#### Missing Roles (must add before implementation)

*None.*

#### Custom Colors (justified exceptions only)

| Name | Hex | Justification |
|------|-----|---------------|
| XTheme.Colors.Success | #4ADE80 | Semantic success/fast status indicator — no M3 role |
| Bitcoin brand yellow | #EAB308 | Coin-specific brand color — no M3 equivalent |

#### Component Overrides (divergences from X-component defaults)

| Component | Property | HTML CSS Value | M3 Role Hex | Action |
|-----------|----------|---------------|-------------|--------|
| All section labels | color | slate-400 = #94A3B8 | onSurfaceVariant = #C5BCE0 | Use onSurfaceVariant |
| Amount value | color | text-white = #FFFFFF | onSurface = #E9E0FF | Use onSurface |
| Input text | color | slate-100 = #F1F5F9 | onSurface = #E9E0FF | Use onSurface |
| Placeholder text | color | slate-500 = #64748B | onSurfaceVariant = #C5BCE0 | Use onSurfaceVariant |
| Coin/network names | color | slate-100 = #F1F5F9 | onSurface = #E9E0FF | Use onSurface |
| Muted subtitles | color | slate-500 = #64748B | onSurfaceVariant = #C5BCE0 | Use onSurfaceVariant |
| XTextField corner | shape | rounded-xl = 24dp | XTextField default | Apply RoundedCornerShape(24.dp) |
| Asset selector corner | shape | rounded-xl = 24dp | — | Apply RoundedCornerShape(24.dp) |
| CTA button corner | shape | rounded-xl = 24dp | XButton default | Apply RoundedCornerShape(24.dp) |
| CTA button elevation | shadow | shadow-lg = 8dp | XButton default | Apply elevation 8.dp |
| Bottom area bg | brush | bg-gradient-to-t from #0D0919 | — | Brush.verticalGradient(transparent→background) |
| Arrival indicator | color | green-400 = #4ADE80 | — custom | XTheme.Colors.Success |
| Bitcoin icon | color | yellow-500 = #EAB308 | — custom | Color(0xFFEAB308) inline |
| Failed horizontal pad | value | px-6 = 24dp | — | Use 24dp (not 16dp like success) |

---

## Post-Implementation Checklist

- [ ] All XTheme missing roles added to BOTH XLightColors and XDarkColors *(none required)*
- [ ] `SendScreen` + `SendScreenRoot` composables exist with ViewModel wrapper + testable root
- [ ] All 4 UI states handled: Uninitialized, Loading, Success, Failed
- [ ] `RecipientAddressInput` component exists in `components/RecipientAddressInput.kt`
- [ ] `AmountInput` component exists in `components/AmountInput.kt`
- [ ] `AssetSelectorRow` component exists in `components/AssetSelectorRow.kt` (reused for Coin + Network)
- [ ] `TransactionSummaryCard` component exists in `components/TransactionSummaryCard.kt`
- [ ] Success state: Column with correct section spacing (16/32/40/32dp spacers)
- [ ] Failed state: 24dp horizontal padding (NOT 16dp)
- [ ] Bottom gradient `Brush.verticalGradient` applied to both Send and Retry bottom bars
- [ ] All `rounded-xl` = `RoundedCornerShape(24.dp)` applied explicitly
- [ ] CTA button elevation = 8.dp applied
- [ ] All slate-* colors replaced with proper M3 roles (onSurface/onSurfaceVariant)
- [ ] `XTheme.Colors.Success` used for arrival indicator (not raw `Color(0xFF4ADE80)`)
- [ ] Bitcoin yellow `Color(0xFFEAB308)` used inline for BTC icon only
- [ ] `XHorizontalDivider` with `outline.copy(alpha=0.2f)` in TransactionSummaryCard
- [ ] Loading state: `XCircularProgressIndicator(Modifier.size(48.dp))`
- [ ] Failed state: error icon 80dp, container `error.copy(alpha=0.1f)` CircleShape with 24dp padding
- [ ] Build passes: `./gradlew :feature:send:assembleAndroidMain`
- [ ] Code formatted: `./gradlew :feature:send:ktlintFormat`
