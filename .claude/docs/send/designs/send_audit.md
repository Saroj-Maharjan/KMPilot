# Three-Way Audit Report: Send

**Date**: 2026-02-25
**Auditor**: verify-ui skill
**States audited**: Success, Loading, Failed
**Total properties checked**: 72
**Critical mismatches**: 1
**Minor mismatches**: 5

---

## Summary

| State | Critical | Minor | Status |
|-------|----------|-------|--------|
| Success | 1 | 3 | FAIL |
| Loading | 0 | 1 | WARN |
| Failed | 0 | 1 | WARN |

---

## Critical Mismatches

### RecipientAddressInput — Input end padding

| Property | HTML (ground truth) | Blueprint | Code | Verdict |
|----------|-------------------|-----------|------|---------|
| Input end padding | `pr-12` = 48dp (room for paste icon) | `contentPadding end=48.dp` | XTextField hardcodes `end=16.dp` | **CODE MISMATCH** |

**Impact**: Long wallet addresses (e.g. `1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2`, 34 chars) will overflow visually under the paste icon button. The overlay paste button starts ~60dp from the right edge, but text only receives 16dp right clearance.

**Root cause**: `XTextField` does not expose a `contentPadding` parameter — padding is hardcoded internally at `start=16dp, top=10dp, end=16dp, bottom=10dp`.

**Fix options** (prefer option A):
- **Option A**: Move paste button from Box overlay to `XTextField`'s `trailingIcon` slot — this is cleaner and makes the field automatically manage text clearance.
- **Option B**: Add a transparent `Spacer` as the `trailingIcon` to push text away from the right edge.

**File**: `feature/send/src/commonMain/kotlin/thisissadeghi/send/presentation/ui/components/RecipientAddressInput.kt`

---

## Minor Mismatches

### 1. App bar title alignment

| Property | HTML | Blueprint | Code | Verdict |
|----------|------|-----------|------|---------|
| Title alignment | left-aligned (flex row `ml-4`) | not specified | centered (CenterAlignedTopAppBar) | BLUEPRINT + CODE MISMATCH |

**Note**: `XTopAppBar` wraps `CenterAlignedTopAppBar` unconditionally. This is an XTopAppBar architectural decision. Not fixable at the feature level without customizing the component.

---

### 2. Icon button backgrounds

| Property | HTML | Blueprint | Code | Verdict |
|----------|------|-----------|------|---------|
| Back/QR button bg | transparent (hover-only `bg-primary/10`) | not specified | visible dark circle | BLUEPRINT + CODE MISMATCH |

**Note**: `XIconButton` renders with a default background shape visible in dark mode. In the Stitch design, buttons are transparent with hover-only styling. Not fixable at the feature level without XIconButton changes.

---

### 3. Input vertical padding

| Property | HTML | Blueprint | Code | Verdict |
|----------|------|-----------|------|---------|
| Input top padding | `py-4` = 16dp | 16dp | XTextField hardcodes 10dp | CODE MISMATCH |
| Input bottom padding | `py-4` = 16dp | 16dp | XTextField hardcodes 10dp | CODE MISMATCH |

**Note**: The 48dp `minHeight` constraint partially compensates, yielding ~48dp total height vs the designed ~52dp. Visual difference is ~4dp on a typical device. Same root cause as Critical #1 (XTextField hardcoded padding).

---

### 4. Loading state — QR icon visible

| Property | HTML | Blueprint | Code | Verdict |
|----------|------|-----------|------|---------|
| QR icon in loading | `opacity-0 pointer-events-none` | noted as hidden | always visible | CODE MISMATCH |

**Fix**: Conditionally hide the QR icon in `SendScreenRoot` based on `uiState.state`:
```kotlin
actions = {
    if (uiState.state !is UiState.Loading) {
        XIconButton(onClick = onQrScanClick, ...) { ... }
    }
}
```
**File**: `feature/send/src/commonMain/kotlin/thisissadeghi/send/presentation/ui/SendScreen.kt`

---

### 5. Failed state — QR icon present

| Property | HTML | Blueprint | Code | Verdict |
|----------|------|-----------|------|---------|
| QR icon in failed | absent | not specified | visible | CODE MISMATCH |

**Fix**: Same as Minor #4 — also exclude QR icon in `UiState.Failed` state.

---

## Passing Properties (67/72)

All of the following match exactly across HTML → Blueprint → Code:

**Success state**: Main horizontal padding (16dp), recipient top spacing (16dp), label styles (14sp Medium onSurfaceVariant, 8dp bottom), input shape (RoundedCornerShape 24dp), input colors (surfaceVariant fill, outline border, onSurface text), paste icon offset (12dp), amount spacing (32dp top), amount value (40sp Bold onSurface lineHeight=40sp), ticker (20sp SemiBold primary), amount-ticker gap (8dp), balance text (8dp top, 14sp onSurfaceVariant), % buttons row (16dp top, 8dp gap), 25%/50% style (16/6dp padding, primary/10 bg, CircleShape, primary/20 border), MAX style (primary bg, onPrimary, CircleShape), asset section top (40dp), asset gap (16dp), selector label (14sp Medium mb-8dp), selector container (surfaceVariant bg, 1dp outline, RoundedCornerShape 24dp, 16dp padding), icon size (40dp), icon gap (12dp), BTC icon tint (Color(0xFFEAB308)), BTC bg (yellow/20), name style (16sp Bold onSurface), subtitle (12sp onSurfaceVariant), expand icon (onSurfaceVariant), summary top (32dp), summary container (surface bg, outline/30 border, RoundedCornerShape 24dp, 16dp padding), header (12sp Bold 0.6sp tracking mb-16dp), row gap (12dp), fee row (14sp onSurfaceVariant/onSurface), total weight (SemiBold), divider spacers (4dp + 12dp), divider color (outline/20), arrival icon (14dp, XTheme.Colors.Success), arrival text (12sp XTheme.Colors.Success), arrival gap (4dp), bottom bar padding (24dp), gradient (verticalGradient transparent→bg/95→bg), Send button (primary bg, onPrimary, RoundedCornerShape 24dp, 16dp py, 8dp elevation), home spacer (16dp).

**Loading state**: Spinner size (48dp), spinner color (primary).

**Failed state**: Horizontal padding (24dp), error container bottom (32dp), error container bg (error/10 CircleShape), error container padding (24dp), error icon size (80dp), error icon color (error), heading (24sp Bold onSurface), heading bottom (16dp), heading horizontal (16dp), subtitle (16sp onSurfaceVariant lineHeight=26sp), subtitle max-width (280dp), retry button (identical to send button, correct).

---

## Fix Instructions

### Critical — Fix 1: RecipientAddressInput paste button overlap

**File**: `feature/send/src/commonMain/kotlin/thisissadeghi/send/presentation/ui/components/RecipientAddressInput.kt`

Replace the `Box { XTextField(...) XIconButton(...) }` with `XTextField` using its `trailingIcon` slot:

```kotlin
XTextField(
    value = value,
    onValueChange = onValueChange,
    placeholder = { ... },
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(24.dp),
    colors = OutlinedTextFieldDefaults.colors(...),
    singleLine = true,
    trailingIcon = {
        XIconButton(onClick = onPasteClick, contentPadding = PaddingValues(4.dp)) {
            XIcon(
                imageVector = Icons.Default.ContentPaste,
                contentDescription = "Paste",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    },
)
```

This eliminates the overlay pattern and lets XTextField automatically compute safe text clearance for the trailing icon.

### Minor — Fix 2+3: QR icon visibility in Loading and Failed states

**File**: `feature/send/src/commonMain/kotlin/thisissadeghi/send/presentation/ui/SendScreen.kt`

In `SendScreenRoot`, update the `actions` slot to conditionally render the QR button:

```kotlin
actions = {
    val showQr = uiState.state is UiState.Success || uiState.state is UiState.Uninitialized
    if (showQr) {
        XIconButton(onClick = onQrScanClick, modifier = Modifier.padding(8.dp)) {
            XIcon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "Scan QR")
        }
    }
},
```
