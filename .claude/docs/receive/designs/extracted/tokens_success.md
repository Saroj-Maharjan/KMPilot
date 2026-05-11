# Token Inventory: stitch_success.html

## Tailwind Config Overrides

- **colors.background**: `#0F0D09`
- **colors.error**: `#FFB4AB`
- **colors.error-container**: `#93000A`
- **colors.on-error**: `#690005`
- **colors.on-error-container**: `#FFDAD6`
- **colors.on-primary**: `#2C1900`
- **colors.on-primary-container**: `#FFF0C0`
- **colors.on-surface**: `#EDE8D5`
- **colors.on-surface-variant**: `#C4BA94`
- **colors.outline**: `#726A48`
- **colors.outline-variant**: `#3F3822`
- **colors.primary**: `#F5D76E`
- **colors.primary-container**: `#4A3200`
- **colors.surface**: `#1C1910`
- **colors.surface-variant**: `#302B1C`
- **borderRadius.2xl**: `20px`
- **borderRadius.DEFAULT**: `0.5rem`
- **borderRadius.full**: `9999px`
- **borderRadius.lg**: `1rem`
- **borderRadius.xl**: `1.5rem`
- **fontFamily.body**: `Manrope`
- **fontFamily.display**: `Manrope`
- **fontFamily.headline**: `Manrope`
- **fontFamily.label**: `Manrope`

## Global Styles

Inline `<style>` rules that apply globally — these affect every matching element regardless of class list.

```css
body { font-family: 'Manrope', sans-serif; background-color: #0F0D09; color: #EDE8D5; }
        .material-symbols-outlined { font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24; }
        .qr-gradient-halo {
            background: radial-gradient(circle, rgba(245, 215, 110, 0.15) 0%, rgba(15, 13, 9, 0) 70%);
        }
body {
      min-height: max(884px, 100dvh);
    }
```

## Elements

Each class is followed by its deterministic token interpretation when one applies (e.g. `mt-4 → margin-top: 16dp`). Classes with no annotation are layout primitives, state variants, or unrecognised — interpret them yourself.

**Element formats:**
- **Visual elements** (any class converts to a visual token, or has inline style) get a full block with one line per class.
- **Layout-only elements** (only structural classes like `flex`, `items-center`, `justify-between`) get a single compact line — they still appear in order so structural mismatches (Row vs Column, arrangement, alignment) remain visible.

- [1] `<html>` `dark`
**[2] `<body>`**

1. `antialiased`
2. `min-h-screen` → min-height: 100vh/vw
3. `flex`
4. `flex-col`

### <!-- Top App Bar -->

**[3] `<header>`**

1. `fixed` (positioning: fixed — Compose: Box overlay or BottomBar slot)
2. `top-0` → top: 0dp
3. `w-full` → width: 100%
4. `z-50` (z-index — Compose has no z-index; layering is order-based)
5. `flex`
6. `items-center`
7. `px-4` → horizontal padding: 16dp
8. `h-16` → height: 64dp
9. `bg-transparent` → background: transparent

**[4] `<button>`**

1. `flex`
2. `items-center`
3. `justify-center`
4. `w-10` → width: 40dp
5. `h-10` → height: 40dp
6. `rounded-full` → corner-radius: CircleShape
7. `active:scale-90` (state variant — handled by Compose interaction states)
8. `transition-transform` (CSS transition — no Compose equivalent at token level)
- _inline style_: `color: #F5D76E;`

- [5] `<span>` `material-symbols-outlined` — "arrow_back"
**[6] `<h1>` — "Receive"**

1. `ml-2` → left margin: 8dp
2. `font-bold` → font-weight: 700 (Bold)
3. `tracking-tight` → letter-spacing: -0.025em (× font-size for sp)
4. `text-[#EDE8D5]` → color: #EDE8D5
5. `text-xl` → font-size: 20sp
- _inline style_: `letter-spacing: -0.025em;`

**[7] `<div>`**

1. `ml-auto` → left margin: auto
2. `w-10` → width: 40dp
3. `h-10` → height: 40dp

**[8] `<main>`**

1. `flex-1`
2. `mt-16` → top margin: 64dp
3. `px-4` → horizontal padding: 16dp
4. `pb-32` → bottom padding: 128dp

### <!-- Asset Selector -->

**[9] `<section>`**

1. `mt-6` → top margin: 24dp

**[10] `<button>`**

1. `w-full` → width: 100%
2. `bg-surface` → background: surface (#1C1910)
3. `border` → border-width: 1dp
4. `border-outline` → border-color: outline (#726A48)
5. `rounded-full` → corner-radius: CircleShape
6. `p-3` → padding: 12dp
7. `flex`
8. `items-center`
9. `justify-between`
10. `hover:bg-surface-variant` (state variant — handled by Compose interaction states)
11. `transition-colors` (CSS transition — no Compose equivalent at token level)
12. `active:scale-[0.98]` (state variant — handled by Compose interaction states)

**[11] `<div>`**

1. `flex`
2. `items-center`
3. `gap-3` → gap: 12dp

**[12] `<div>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-[#F7931A]` → background: #F7931A
5. `flex`
6. `items-center`
7. `justify-center`
8. `shadow-lg` → shadow: ~8dp elevation

**[13] `<span>` — "₿"**

1. `text-white` → color: white
2. `font-bold` → font-weight: 700 (Bold)
3. `text-xl` → font-size: 20sp

- [14] `<div>` `flex flex-col items-start`
**[15] `<span>` — "Bitcoin"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-[#EDE8D5]` → color: #EDE8D5
3. `text-sm` → font-size: 14sp

**[16] `<span>` — "Bitcoin Network"**

1. `text-[#C4BA94]` → color: #C4BA94
2. `text-xs` → font-size: 12sp

**[17] `<span>` — "expand_more"**

1. `material-symbols-outlined`
2. `text-outline` → color: outline (#726A48)
3. `pr-2` → right padding: 8dp

### <!-- QR Code Card -->

**[18] `<section>`**

1. `mt-6` → top margin: 24dp
2. `relative`

**[19] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-primary` → border-color: primary (#F5D76E)
4. `rounded-2xl` → corner-radius: 20dp
5. `p-8` → padding: 32dp
6. `flex`
7. `flex-col`
8. `items-center`
9. `relative`
10. `overflow-hidden`

### <!-- Subtle Glow effect -->

**[20] `<span>` — "Your Bitcoin address"**

1. `text-[#C4BA94]` → color: #C4BA94
2. `text-sm` → font-size: 14sp
3. `mb-6` → bottom margin: 24dp
4. `font-medium` → font-weight: 500 (Medium)

### <!-- Address Pill -->

**[21] `<div>`**

1. `w-full` → width: 100%
2. `bg-surface-variant` → background: surface-variant (#302B1C)
3. `border` → border-width: 1dp
4. `border-outline` → border-color: outline (#726A48)
5. `rounded-xl` → corner-radius: 24dp
6. `px-4` → horizontal padding: 16dp
7. `py-3` → vertical padding: 12dp
8. `flex`
9. `items-center`
10. `justify-between`
11. `group`
12. `active:bg-outline-variant` (state variant — handled by Compose interaction states)
13. `transition-colors` (CSS transition — no Compose equivalent at token level)

**[22] `<span>` — "bc1qxy2kgf86gnvkv0u9vnm7p9nq3f9z6n77h0wlh"**

1. `font-mono`
2. `text-xs` → font-size: 12sp
3. `text-[#EDE8D5]` → color: #EDE8D5
4. `tracking-tight` → letter-spacing: -0.025em (× font-size for sp)

**[23] `<span>` — "content_copy"**

1. `material-symbols-outlined`
2. `text-primary` → color: primary (#F5D76E)
3. `text-xl` → font-size: 20sp

### <!-- Network Warning -->

**[24] `<section>`**

1. `mt-6` → top margin: 24dp

**[25] `<div>`**

1. `bg-error-container/20` → background: error-container (#93000A) @ 20%
2. `border` → border-width: 1dp
3. `border-error/40` → border-color: error (#FFB4AB) @ 40%
4. `rounded-xl` → corner-radius: 24dp
5. `p-4` → padding: 16dp
6. `flex`
7. `gap-3` → gap: 12dp

**[26] `<span>` — "warning"**

1. `material-symbols-outlined`
2. `text-error` → color: error (#FFB4AB)
3. `shrink-0`

**[27] `<div>`**

1. `flex`
2. `flex-col`
3. `gap-1` → gap: 4dp

**[28] `<p>` — "Only send Bitcoin (BTC)"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-[#EDE8D5]` → color: #EDE8D5
3. `text-sm` → font-size: 14sp

**[29] `<p>` — "Sending any other asset to this address may result in perman..."**

1. `text-[#C4BA94]` → color: #C4BA94
2. `text-xs` → font-size: 12sp
3. `leading-relaxed` → line-height: 1.625× font-size

### <!-- Bottom Action Bar -->

**[30] `<footer>`**

1. `fixed` (positioning: fixed — Compose: Box overlay or BottomBar slot)
2. `bottom-0` → bottom: 0dp
3. `w-full` → width: 100%
4. `z-50` (z-index — Compose has no z-index; layering is order-based)
5. `bg-[#1C1910]` → background: #1C1910
6. `rounded-t-2xl` → corner-radius (t): 20dp
7. `shadow-[0_-4px_24px_rgba(0,0,0,0.6)]` → shadow-color: 0_-4px_24px_rgba(0,0,0,0.6)
8. `px-4` → horizontal padding: 16dp
9. `pt-4` → top padding: 16dp
10. `pb-8` → bottom padding: 32dp
11. `border-t` → border-t width: 1dp
12. `border-[#726A48]/30` → border-color: #726A48 @ 30%

**[31] `<div>`**

1. `flex`
2. `gap-4` → gap: 16dp

**[32] `<button>`**

1. `flex-1`
2. `h-14` → height: 56dp
3. `bg-surface-variant` → background: surface-variant (#302B1C)
4. `border` → border-width: 1dp
5. `border-outline` → border-color: outline (#726A48)
6. `rounded-xl` → corner-radius: 24dp
7. `flex`
8. `items-center`
9. `justify-center`
10. `gap-2` → gap: 8dp
11. `text-[#EDE8D5]` → color: #EDE8D5
12. `font-semibold` → font-weight: 600 (SemiBold)
13. `active:scale-95` (state variant — handled by Compose interaction states)
14. `transition-transform` (CSS transition — no Compose equivalent at token level)

**[33] `<span>` — "share Share"**

1. `material-symbols-outlined`
2. `text-xl` → font-size: 20sp

**[34] `<button>`**

1. `flex-1`
2. `h-14` → height: 56dp
3. `bg-primary` → background: primary (#F5D76E)
4. `rounded-xl` → corner-radius: 24dp
5. `flex`
6. `items-center`
7. `justify-center`
8. `gap-2` → gap: 8dp
9. `text-on-primary` → color: on-primary (#2C1900)
10. `font-bold` → font-weight: 700 (Bold)
11. `active:scale-95` (state variant — handled by Compose interaction states)
12. `transition-transform` (CSS transition — no Compose equivalent at token level)
13. `shadow-[0_4px_12px_rgba(245,215,110,0.2)]` → shadow-color: 0_4px_12px_rgba(245,215,110,0.2)

**[35] `<span>` — "content_copy Copy Address"**

1. `material-symbols-outlined`
2. `text-xl` → font-size: 20sp
- _inline style_: `font-variation-settings: 'FILL' 1;`

---
**Total elements**: 35 | **Visual**: 32 | **Layout-only**: 3 | **Total classes**: 174 | **Auto-converted**: 108 (62%)

