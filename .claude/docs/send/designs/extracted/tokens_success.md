# Token Inventory: stitch_success.html

## Tailwind Config Overrides

- **colors.background**: `#0F0D09`
- **colors.error**: `#FFB4AB`
- **colors.on-error**: `#690005`
- **colors.on-primary**: `#2C1900`
- **colors.on-primary-container**: `#FFF0C0`
- **colors.on-surface**: `#EDE8D5`
- **colors.on-surface-variant**: `#C4BA94`
- **colors.outline**: `#726A48`
- **colors.outline-variant**: `#3F3822`
- **colors.primary**: `#F5D76E`
- **colors.primary-container**: `#4A3200`
- **colors.success**: `#4ADE80`
- **colors.surface**: `#1C1910`
- **colors.surface-variant**: `#302B1C`
- **borderRadius.2xl**: `2rem`
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
body {
            font-family: 'Manrope', sans-serif;
            background-color: #0F0D09;
            color: #EDE8D5;
            -webkit-font-smoothing: antialiased;
        }
        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
        }
        .gold-glow {
            box-shadow: 0 0 20px rgba(245, 215, 110, 0.15);
        }
        .glass-surface {
            background: linear-gradient(135deg, rgba(28, 25, 16, 0.8) 0%, rgba(28, 25, 16, 1) 100%);
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

1. `bg-background` → background: background (#0F0D09)
2. `text-on-surface` → color: on-surface (#EDE8D5)
3. `min-h-screen` → min-height: 100vh/vw
4. `flex`
5. `flex-col`

### <!-- TopAppBar -->

**[3] `<header>`**

1. `flex`
2. `items-center`
3. `w-full` → width: 100%
4. `px-4` → horizontal padding: 16dp
5. `h-16` → height: 64dp
6. `bg-transparent` → background: transparent
7. `fixed` (positioning: fixed — Compose: Box overlay or BottomBar slot)
8. `top-0` → top: 0dp
9. `z-50` (z-index — Compose has no z-index; layering is order-based)

**[4] `<button>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `flex`
4. `items-center`
5. `justify-center`
6. `rounded-full` → corner-radius: CircleShape
7. `hover:bg-surface-variant/20` (state variant — handled by Compose interaction states)
8. `transition-colors` (CSS transition — no Compose equivalent at token level)
9. `duration-200`

**[5] `<span>` — "arrow_back"**

1. `material-symbols-outlined`
2. `text-primary` → color: primary (#F5D76E)

**[6] `<h1>` — "Send"**

1. `ml-2` → left margin: 8dp
2. `text-headline-sm`
3. `font-headline`
4. `tracking-tight` → letter-spacing: -0.025em (× font-size for sp)
5. `text-on-surface` → color: on-surface (#EDE8D5)
6. `font-bold` → font-weight: 700 (Bold)

**[7] `<div>`**

1. `ml-auto` → left margin: auto
2. `w-10` → width: 40dp
3. `h-10` → height: 40dp

### <!-- Spacer for balance -->

**[8] `<main>`**

1. `flex-1`
2. `w-full` → width: 100%
3. `max-w-lg`
4. `mx-auto` → horizontal margin: auto
5. `px-6` → horizontal padding: 24dp
6. `pt-24` → top padding: 96dp
7. `pb-32` → bottom padding: 128dp

### <!-- Hero Amount Section -->

**[9] `<section>`**

1. `flex`
2. `flex-col`
3. `items-center`
4. `mb-10` → bottom margin: 40dp

**[10] `<div>`**

1. `flex`
2. `items-baseline`
3. `gap-2` → gap: 8dp
4. `mb-2` → bottom margin: 8dp

**[11] `<span>` — "0.00"**

1. `text-[64px]` → font-size: 64sp
2. `font-extrabold` → font-weight: 800 (ExtraBold)
3. `tracking-tighter` → letter-spacing: -0.05em (× font-size for sp)
4. `text-on-surface` → color: on-surface (#EDE8D5)
5. `leading-none` → line-height: 1.0× font-size

**[12] `<div>`**

1. `bg-surface-variant` → background: surface-variant (#302B1C)
2. `px-3` → horizontal padding: 12dp
3. `py-1` → vertical padding: 4dp
4. `rounded-full` → corner-radius: CircleShape
5. `flex`
6. `items-center`
7. `gap-1.5` → gap: 6dp
8. `border` → border-width: 1dp
9. `border-outline-variant` → border-color: outline-variant (#3F3822)

**[13] `<span>` — "BTC"**

1. `text-xs` → font-size: 12sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-primary` → color: primary (#F5D76E)
4. `tracking-widest` → letter-spacing: 0.1em (× font-size for sp)

### <!-- Animated Cursor Underline -->

**[14] `<div>`**

1. `w-32` → width: 128dp
2. `h-[1px]` → height: 1px
3. `bg-primary` → background: primary (#F5D76E)
4. `mb-4` → bottom margin: 16dp
5. `shadow-[0_0_8px_rgba(245,215,110,0.5)]` → shadow-color: 0_0_8px_rgba(245,215,110,0.5)

**[15] `<p>` — "Balance 1.24 BTC ·"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `font-medium` → font-weight: 500 (Medium)
3. `text-sm` → font-size: 14sp
4. `mb-6` → bottom margin: 24dp

**[16] `<span>` — "$78,420"**

1. `text-on-surface` → color: on-surface (#EDE8D5)

### <!-- Quick Chips -->

**[17] `<div>`**

1. `flex`
2. `gap-3` → gap: 12dp

**[18] `<button>` — "25%"**

1. `px-5` → horizontal padding: 20dp
2. `py-2` → vertical padding: 8dp
3. `rounded-lg` → corner-radius: 16dp
4. `bg-surface-variant` → background: surface-variant (#302B1C)
5. `text-primary` → color: primary (#F5D76E)
6. `text-xs` → font-size: 12sp
7. `font-bold` → font-weight: 700 (Bold)
8. `border` → border-width: 1dp
9. `border-outline-variant` → border-color: outline-variant (#3F3822)
10. `hover:border-primary` (state variant — handled by Compose interaction states)
11. `transition-all` (CSS transition — no Compose equivalent at token level)
12. `active:scale-95` (state variant — handled by Compose interaction states)

**[19] `<button>` — "50%"**

1. `px-5` → horizontal padding: 20dp
2. `py-2` → vertical padding: 8dp
3. `rounded-lg` → corner-radius: 16dp
4. `bg-surface-variant` → background: surface-variant (#302B1C)
5. `text-primary` → color: primary (#F5D76E)
6. `text-xs` → font-size: 12sp
7. `font-bold` → font-weight: 700 (Bold)
8. `border` → border-width: 1dp
9. `border-outline-variant` → border-color: outline-variant (#3F3822)
10. `hover:border-primary` (state variant — handled by Compose interaction states)
11. `transition-all` (CSS transition — no Compose equivalent at token level)
12. `active:scale-95` (state variant — handled by Compose interaction states)

**[20] `<button>` — "MAX"**

1. `px-5` → horizontal padding: 20dp
2. `py-2` → vertical padding: 8dp
3. `rounded-lg` → corner-radius: 16dp
4. `bg-surface-variant` → background: surface-variant (#302B1C)
5. `text-primary` → color: primary (#F5D76E)
6. `text-xs` → font-size: 12sp
7. `font-bold` → font-weight: 700 (Bold)
8. `border` → border-width: 1dp
9. `border-outline-variant` → border-color: outline-variant (#3F3822)
10. `hover:border-primary` (state variant — handled by Compose interaction states)
11. `transition-all` (CSS transition — no Compose equivalent at token level)
12. `active:scale-95` (state variant — handled by Compose interaction states)
13. `uppercase`

### <!-- Recipient Card -->

**[21] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-xl` → corner-radius: 24dp
5. `p-5` → padding: 20dp
6. `mb-5` → bottom margin: 20dp
7. `relative`
8. `overflow-hidden`

**[22] `<div>`**

1. `absolute` (positioning: absolute — Compose: Box overlay or BottomBar slot)
2. `left-0` → left: 0dp
3. `top-0` → top: 0dp
4. `bottom-0` → bottom: 0dp
5. `w-1` → width: 4dp
6. `bg-primary` → background: primary (#F5D76E)

**[23] `<label>` — "To Recipient"**

1. `block`
2. `text-xs` → font-size: 12sp
3. `font-bold` → font-weight: 700 (Bold)
4. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
5. `uppercase`
6. `tracking-widest` → letter-spacing: 0.1em (× font-size for sp)
7. `mb-3` → bottom margin: 12dp

**[24] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp

**[25] `<input>`**

1. `bg-transparent` → background: transparent
2. `border-none`
3. `p-0` → padding: 0dp
4. `flex-1`
5. `text-on-surface` → color: on-surface (#EDE8D5)
6. `placeholder:text-on-surface-variant/50`
7. `italic`
8. `focus:ring-0` (state variant — handled by Compose interaction states)
9. `text-md`

**[26] `<div>`**

1. `flex`
2. `items-center`
3. `gap-3` → gap: 12dp

**[27] `<button>`**

1. `text-primary` → color: primary (#F5D76E)
2. `hover:opacity-80` (state variant — handled by Compose interaction states)

- [28] `<span>` `material-symbols-outlined` — "content_paste"
**[29] `<button>`**

1. `text-primary` → color: primary (#F5D76E)
2. `hover:opacity-80` (state variant — handled by Compose interaction states)

- [30] `<span>` `material-symbols-outlined` — "qr_code_scanner"
### <!-- Asset & Network Row -->

**[31] `<div>`**

1. `grid`
2. `grid-cols-2`
3. `gap-4` → gap: 16dp
4. `mb-8` → bottom margin: 32dp

### <!-- Asset Card -->

**[32] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-xl` → corner-radius: 24dp
5. `p-4` → padding: 16dp
6. `hover:border-outline` (state variant — handled by Compose interaction states)
7. `transition-colors` (CSS transition — no Compose equivalent at token level)
8. `cursor-pointer`
9. `group`

**[33] `<span>` — "Asset"**

1. `text-[10px]` → font-size: 10sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
4. `uppercase`
5. `tracking-widest` → letter-spacing: 0.1em (× font-size for sp)
6. `mb-2` → bottom margin: 8dp
7. `block`

**[34] `<div>`**

1. `flex`
2. `items-center`
3. `gap-2` → gap: 8dp

**[35] `<div>`**

1. `w-8` → width: 32dp
2. `h-8` → height: 32dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-[#F7931A]` → background: #F7931A
5. `flex`
6. `items-center`
7. `justify-center`
8. `text-white` → color: white

**[36] `<span>` — "currency_bitcoin"**

1. `material-symbols-outlined`
2. `text-sm` → font-size: 14sp
3. `font-bold` → font-weight: 700 (Bold)

- [37] `<div>` `flex-1 overflow-hidden`
**[38] `<p>` — "Bitcoin"**

1. `text-sm` → font-size: 14sp
2. `font-bold` → font-weight: 700 (Bold)
3. `truncate`

**[39] `<p>` — "BTC"**

1. `text-[10px]` → font-size: 10sp
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[40] `<span>` — "chevron_right"**

1. `material-symbols-outlined`
2. `text-primary` → color: primary (#F5D76E)
3. `text-sm` → font-size: 14sp
4. `group-hover:translate-x-0.5` (state variant — handled by Compose interaction states)
5. `transition-transform` (CSS transition — no Compose equivalent at token level)

### <!-- Network Card -->

**[41] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-xl` → corner-radius: 24dp
5. `p-4` → padding: 16dp
6. `hover:border-outline` (state variant — handled by Compose interaction states)
7. `transition-colors` (CSS transition — no Compose equivalent at token level)
8. `cursor-pointer`
9. `group`

**[42] `<span>` — "Network"**

1. `text-[10px]` → font-size: 10sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
4. `uppercase`
5. `tracking-widest` → letter-spacing: 0.1em (× font-size for sp)
6. `mb-2` → bottom margin: 8dp
7. `block`

**[43] `<div>`**

1. `flex`
2. `items-center`
3. `gap-2` → gap: 8dp

**[44] `<div>`**

1. `w-8` → width: 32dp
2. `h-8` → height: 32dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-surface-variant` → background: surface-variant (#302B1C)
5. `flex`
6. `items-center`
7. `justify-center`
8. `border` → border-width: 1dp
9. `border-outline-variant` → border-color: outline-variant (#3F3822)

**[45] `<span>` — "public"**

1. `material-symbols-outlined`
2. `text-primary` → color: primary (#F5D76E)
3. `text-sm` → font-size: 14sp

- [46] `<div>` `flex-1 overflow-hidden`
**[47] `<p>` — "Bitcoin"**

1. `text-sm` → font-size: 14sp
2. `font-bold` → font-weight: 700 (Bold)
3. `truncate`

**[48] `<p>` — "ERC-20"**

1. `text-[10px]` → font-size: 10sp
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[49] `<span>` — "chevron_right"**

1. `material-symbols-outlined`
2. `text-primary` → color: primary (#F5D76E)
3. `text-sm` → font-size: 14sp
4. `group-hover:translate-x-0.5` (state variant — handled by Compose interaction states)
5. `transition-transform` (CSS transition — no Compose equivalent at token level)

### <!-- Transaction Summary -->

**[50] `<div>`**

1. `bg-surface-variant` → background: surface-variant (#302B1C)
2. `border` → border-width: 1dp
3. `border-primary/30` → border-color: primary (#F5D76E) @ 30%
4. `rounded-xl` → corner-radius: 24dp
5. `p-5` → padding: 20dp
6. `mb-8` → bottom margin: 32dp

**[51] `<div>`**

1. `space-y-4` → children spaced 16dp vertically

- [52] `<div>` `flex justify-between items-center`
**[53] `<span>` — "Network Fee"**

1. `text-sm` → font-size: 14sp
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[54] `<span>` — "~$7.54"**

1. `text-sm` → font-size: 14sp
2. `font-medium` → font-weight: 500 (Medium)

- [55] `<div>` `flex justify-between items-center`
**[56] `<span>` — "Total Deduct"**

1. `text-sm` → font-size: 14sp
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[57] `<span>` — "0.00 BTC"**

1. `text-sm` → font-size: 14sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-on-surface` → color: on-surface (#EDE8D5)

**[58] `<div>`**

1. `pt-3` → top padding: 12dp
2. `border-t` → border-t width: 1dp
3. `border-outline-variant/30` → border-color: outline-variant (#3F3822) @ 30%
4. `flex`
5. `justify-between`
6. `items-center`

**[59] `<span>` — "Estimated Arrival"**

1. `text-sm` → font-size: 14sp
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[60] `<div>`**

1. `flex`
2. `items-center`
3. `gap-1.5` → gap: 6dp

**[61] `<span>` — "bolt"**

1. `material-symbols-outlined`
2. `text-success` → color: success (#4ADE80)
3. `text-sm` → font-size: 14sp

**[62] `<span>` — "Fast · ~10 min"**

1. `text-sm` → font-size: 14sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-success` → color: success (#4ADE80)
4. `text-on-surface` → color: on-surface (#EDE8D5)

### <!-- Security Badge -->

**[63] `<div>`**

1. `flex`
2. `items-center`
3. `justify-center`
4. `gap-2` → gap: 8dp
5. `opacity-50` → opacity: 0.5
6. `mb-4` → bottom margin: 16dp

**[64] `<span>` — "verified_user"**

1. `material-symbols-outlined`
2. `text-xs` → font-size: 12sp

**[65] `<span>` — "Secured by KMPilot Vault"**

1. `text-[10px]` → font-size: 10sp
2. `uppercase`
3. `font-bold` → font-weight: 700 (Bold)
4. `tracking-[0.2em]` → letter-spacing: 0.2em

### <!-- Sticky Footer -->

**[66] `<footer>`**

1. `fixed` (positioning: fixed — Compose: Box overlay or BottomBar slot)
2. `bottom-0` → bottom: 0dp
3. `left-0` → left: 0dp
4. `w-full` → width: 100%
5. `p-6` → padding: 24dp
6. `bg-background/80` → background: background (#0F0D09) @ 80%
7. `backdrop-blur-md`
8. `z-50` (z-index — Compose has no z-index; layering is order-based)

**[67] `<button>`**

1. `w-full` → width: 100%
2. `h-14` → height: 56dp
3. `bg-primary` → background: primary (#F5D76E)
4. `text-on-primary` → color: on-primary (#2C1900)
5. `font-bold` → font-weight: 700 (Bold)
6. `text-md`
7. `rounded-xl` → corner-radius: 24dp
8. `gold-glow`
9. `hover:opacity-90` (state variant — handled by Compose interaction states)
10. `active:scale-[0.98]` (state variant — handled by Compose interaction states)
11. `transition-all` (CSS transition — no Compose equivalent at token level)
12. `duration-150`
13. `flex`
14. `items-center`
15. `justify-center`
16. `gap-2` → gap: 8dp

- [69] `<span>` `material-symbols-outlined` — "send"
### <!-- Background Decorative Element -->

**[70] `<div>`**

1. `fixed` (positioning: fixed — Compose: Box overlay or BottomBar slot)
2. `top-0` → top: 0dp
3. `right-0` → right: 0dp
4. `-z-10`
5. `w-[300px]` → width: 300px
6. `h-[300px]` → height: 300px
7. `bg-primary/5` → background: primary (#F5D76E) @ 5%
8. `blur-[120px]`
9. `rounded-full` → corner-radius: CircleShape
10. `pointer-events-none`

**[71] `<div>`**

1. `fixed` (positioning: fixed — Compose: Box overlay or BottomBar slot)
2. `bottom-0` → bottom: 0dp
3. `left-0` → left: 0dp
4. `-z-10`
5. `w-[400px]` → width: 400px
6. `h-[400px]` → height: 400px
7. `bg-primary/5` → background: primary (#F5D76E) @ 5%
8. `blur-[150px]`
9. `rounded-full` → corner-radius: CircleShape
10. `pointer-events-none`

---
**Total elements**: 71 | **Visual**: 62 | **Layout-only**: 8 | **Total classes**: 345 | **Auto-converted**: 214 (62%)

