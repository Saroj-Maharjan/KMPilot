# Token Inventory: stitch_success.html

## Tailwind Config Overrides

- **colors.background**: `#0F0D09`
- **colors.danger**: `#FF6B6B`
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
- **colors.success**: `#4ADE80`
- **colors.surface**: `#1C1910`
- **colors.surface-variant**: `#302B1C`
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
            background-color: #0F0D09;
            color: #EDE8D5;
            font-family: 'Manrope', sans-serif;
            -webkit-tap-highlight-color: transparent;
        }
        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
        }
        .chart-glow {
            filter: drop-shadow(0 0 8px rgba(245, 215, 110, 0.3));
        }
        .avatar-stack {
            display: flex;
            align-items: center;
        }
        .avatar-stack > div {
            margin-right: -12px;
            border: 2px solid #0F0D09;
        }
body {
      min-height: max(884px, 100dvh);
    }
```

## Motion Inventory

_(no motion detected — static design)_

## Elements

Each class is followed by its deterministic token interpretation when one applies (e.g. `mt-4 → margin-top: 16dp`). Classes with no annotation are layout primitives, state variants, or unrecognised — interpret them yourself.

**Element formats:**
- **Visual elements** (any class converts to a visual token, or has inline style) get a full block with one line per class.
- **Layout-only elements** (only structural classes like `flex`, `items-center`, `justify-between`) get a single compact line — they still appear in order so structural mismatches (Row vs Column, arrangement, alignment) remain visible.
- **Classless text children** (e.g. `<span>Label</span>` inside a button) also appear as a one-liner with their text, so sibling DOM order inside a flex container is preserved — compare it against the Compose content lambda order.

- [1] `<html>` `dark`
**[2] `<body>`**

1. `antialiased`
2. `pb-32` → bottom padding: 128dp

### <!-- Top AppBar (JSON Source: TopAppBar) -->

**[3] `<header>`**

1. `fixed` (positioning: fixed — Compose: Box overlay or BottomBar slot)
2. `top-0` → top: 0dp
3. `left-0` → left: 0dp
4. `right-0` → right: 0dp
5. `z-50` (z-index — Compose has no z-index; layering is order-based)
6. `bg-transparent` → background: transparent
7. `flex`
8. `justify-between`
9. `items-center`
10. `w-full` → width: 100%
11. `px-6` → horizontal padding: 24dp
12. `py-4` → vertical padding: 16dp

**[4] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp

**[5] `<button>`**

1. `p-2` → padding: 8dp
2. `-ml-2` → left margin: -8dp
3. `text-primary` → color: primary (#F5D76E)
4. `hover:bg-surface-variant` (motion: web-only — DROP — pointer/hover)
5. `transition-colors` (motion: transition hint — Entrance/Value family)
6. `rounded-full` → corner-radius: CircleShape
7. `active:scale-95` (motion: interaction — DROP — touch press feedback)
8. `duration-150` (motion: transition hint — Entrance/Value family)

**[6] `<span>` — "arrow_back"**

1. `material-symbols-outlined`
- _inline style_: `font-size: 28px;`

**[7] `<h1>` — "Bitcoin"**

1. `font-headline`
2. `text-headline-lg`
3. `font-bold` → font-weight: 700 (Bold)
4. `text-on-surface` → color: on-surface (#EDE8D5)

### <!-- Section 1: Hero Section -->

**[8] `<section>`**

1. `relative`
2. `pt-24` → top padding: 96dp
3. `pb-8` → bottom padding: 32dp
4. `flex`
5. `flex-col`
6. `items-center`
7. `justify-center`
8. `overflow-hidden`

**[9] `<div>`**

1. `absolute` (positioning: absolute — Compose: Box overlay or BottomBar slot)
2. `inset-0` → inset: 0dp
3. `bg-gradient-to-b`
4. `from-primary-container`
5. `to-background`
6. `opacity-60` → opacity: 0.6
7. `h-[240px]` → height: 240px

**[10] `<div>`**

1. `relative`
2. `z-10` (z-index — Compose has no z-index; layering is order-based)
3. `flex`
4. `flex-col`
5. `items-center`
6. `text-center`
7. `px-6` → horizontal padding: 24dp

### <!-- Bitcoin Logo -->

**[11] `<div>`**

1. `w-16` → width: 64dp
2. `h-16` → height: 64dp
3. `bg-primary` → background: primary (#F5D76E)
4. `rounded-full` → corner-radius: CircleShape
5. `flex`
6. `items-center`
7. `justify-center`
8. `shadow-lg` → shadow: ~8dp elevation
9. `mb-4` → bottom margin: 16dp

**[12] `<span>` — "currency_bitcoin"**

1. `material-symbols-outlined`
2. `text-on-primary` → color: on-primary (#2C1900)
3. `text-4xl` → font-size: 36sp
- _inline style_: `font-variation-settings: 'FILL' 1;`

**[13] `<h2>` — "Bitcoin (BTC)"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-lg` → font-size: 18sp
3. `font-bold` → font-weight: 700 (Bold)
4. `mb-1` → bottom margin: 4dp

**[14] `<div>` — "$67,420.50"**

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `text-4xl` → font-size: 36sp
3. `font-extrabold` → font-weight: 800 (ExtraBold)
4. `tracking-tight` → letter-spacing: -0.025em (× font-size for sp)
5. `mb-3` → bottom margin: 12dp

**[15] `<div>`**

1. `bg-success/10` → background: success (#4ADE80) @ 10%
2. `text-success` → color: success (#4ADE80)
3. `px-3` → horizontal padding: 12dp
4. `py-1` → vertical padding: 4dp
5. `rounded-full` → corner-radius: CircleShape
6. `flex`
7. `items-center`
8. `gap-1` → gap: 4dp
9. `text-sm` → font-size: 14sp
10. `font-bold` → font-weight: 700 (Bold)
11. `border` → border-width: 1dp
12. `border-success/20` → border-color: success (#4ADE80) @ 20%

**[16] `<span>` — "arrow_upward 2.34%"**

1. `material-symbols-outlined`
2. `text-base` → font-size: 16sp

### <!-- Section 2: Chart Section -->

**[17] `<section>`**

1. `px-6` → horizontal padding: 24dp
2. `mb-8` → bottom margin: 32dp

**[18] `<div>`**

1. `relative`
2. `h-[180px]` → height: 180px
3. `w-full` → width: 100%
4. `mt-4` → top margin: 16dp

### <!-- Y-Axis Labels -->

**[19] `<div>`**

1. `absolute` (positioning: absolute — Compose: Box overlay or BottomBar slot)
2. `left-0` → left: 0dp
3. `top-0` → top: 0dp
4. `bottom-0` → bottom: 0dp
5. `flex`
6. `flex-col`
7. `justify-between`
8. `text-[10px]` → font-size: 10sp
9. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
10. `font-medium` → font-weight: 500 (Medium)

- [20] `<span>` — "$68k"
- [21] `<span>` — "$67k"
- [22] `<span>` — "$66k"
- [23] `<span>` — "$65k"
### <!-- The Chart -->

**[24] `<div>`**

1. `ml-10` → left margin: 40dp
2. `h-full` → height: 100%
3. `relative`

**[25] `<svg>`**

1. `w-full` → width: 100%
2. `h-full` → height: 100%
3. `preserve-3d`
4. `overflow-visible`

### <!-- Area Fill -->

### <!-- Line -->

- [31] `<path>` `chart-glow`
### <!-- X-Axis Labels -->

**[32] `<div>`**

1. `ml-10` → left margin: 40dp
2. `mt-2` → top margin: 8dp
3. `flex`
4. `justify-between`
5. `text-[10px]` → font-size: 10sp
6. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
7. `font-medium` → font-weight: 500 (Medium)

- [33] `<span>` — "Mon"
- [34] `<span>` — "Tue"
- [35] `<span>` — "Wed"
- [36] `<span>` — "Thu"
- [37] `<span>` — "Fri"
### <!-- Section 3: Time Selector -->

**[38] `<section>`**

1. `px-6` → horizontal padding: 24dp
2. `mb-10` → bottom margin: 40dp
3. `flex`
4. `gap-2` → gap: 8dp
5. `overflow-x-auto`
6. `no-scrollbar`

**[39] `<button>` — "1D"**

1. `bg-primary` → background: primary (#F5D76E)
2. `text-on-primary` → color: on-primary (#2C1900)
3. `px-5` → horizontal padding: 20dp
4. `py-2` → vertical padding: 8dp
5. `rounded-full` → corner-radius: CircleShape
6. `text-sm` → font-size: 14sp
7. `font-bold` → font-weight: 700 (Bold)
8. `transition-all` (motion: transition hint — Entrance/Value family)
9. `active:scale-95` (motion: interaction — DROP — touch press feedback)

**[40] `<button>` — "1W"**

1. `bg-transparent` → background: transparent
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
5. `px-5` → horizontal padding: 20dp
6. `py-2` → vertical padding: 8dp
7. `rounded-full` → corner-radius: CircleShape
8. `text-sm` → font-size: 14sp
9. `font-bold` → font-weight: 700 (Bold)
10. `hover:bg-surface-variant` (motion: web-only — DROP — pointer/hover)
11. `transition-all` (motion: transition hint — Entrance/Value family)

**[41] `<button>` — "1M"**

1. `bg-transparent` → background: transparent
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
5. `px-5` → horizontal padding: 20dp
6. `py-2` → vertical padding: 8dp
7. `rounded-full` → corner-radius: CircleShape
8. `text-sm` → font-size: 14sp
9. `font-bold` → font-weight: 700 (Bold)
10. `hover:bg-surface-variant` (motion: web-only — DROP — pointer/hover)
11. `transition-all` (motion: transition hint — Entrance/Value family)

**[42] `<button>` — "1Y"**

1. `bg-transparent` → background: transparent
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
5. `px-5` → horizontal padding: 20dp
6. `py-2` → vertical padding: 8dp
7. `rounded-full` → corner-radius: CircleShape
8. `text-sm` → font-size: 14sp
9. `font-bold` → font-weight: 700 (Bold)
10. `hover:bg-surface-variant` (motion: web-only — DROP — pointer/hover)
11. `transition-all` (motion: transition hint — Entrance/Value family)

**[43] `<button>` — "All"**

1. `bg-transparent` → background: transparent
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
5. `px-5` → horizontal padding: 20dp
6. `py-2` → vertical padding: 8dp
7. `rounded-full` → corner-radius: CircleShape
8. `text-sm` → font-size: 14sp
9. `font-bold` → font-weight: 700 (Bold)
10. `hover:bg-surface-variant` (motion: web-only — DROP — pointer/hover)
11. `transition-all` (motion: transition hint — Entrance/Value family)

### <!-- Section 4: Stats Grid -->

**[44] `<section>`**

1. `px-6` → horizontal padding: 24dp
2. `mb-10` → bottom margin: 40dp

**[45] `<div>`**

1. `grid`
2. `grid-cols-2`
3. `gap-4` → gap: 16dp

**[46] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `p-5` → padding: 20dp
5. `rounded-xl` → corner-radius: 24dp
6. `shadow-sm` → shadow: ~1dp elevation

**[47] `<p>` — "Market Cap"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-xs` → font-size: 12sp
3. `font-medium` → font-weight: 500 (Medium)
4. `mb-1` → bottom margin: 4dp

**[48] `<p>` — "$1.32T"**

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `text-lg` → font-size: 18sp
3. `font-bold` → font-weight: 700 (Bold)

**[49] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `p-5` → padding: 20dp
5. `rounded-xl` → corner-radius: 24dp
6. `shadow-sm` → shadow: ~1dp elevation

**[50] `<p>` — "24h Vol"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-xs` → font-size: 12sp
3. `font-medium` → font-weight: 500 (Medium)
4. `mb-1` → bottom margin: 4dp

**[51] `<p>` — "$38.4B"**

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `text-lg` → font-size: 18sp
3. `font-bold` → font-weight: 700 (Bold)

**[52] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `p-5` → padding: 20dp
5. `rounded-xl` → corner-radius: 24dp
6. `shadow-sm` → shadow: ~1dp elevation

**[53] `<p>` — "Circ Supply"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-xs` → font-size: 12sp
3. `font-medium` → font-weight: 500 (Medium)
4. `mb-1` → bottom margin: 4dp

**[54] `<p>` — "19.7M BTC"**

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `text-lg` → font-size: 18sp
3. `font-bold` → font-weight: 700 (Bold)

**[55] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-primary/20` → border-color: primary (#F5D76E) @ 20%
4. `p-5` → padding: 20dp
5. `rounded-xl` → corner-radius: 24dp
6. `shadow-md` → shadow: ~4dp elevation
7. `ring-1`
8. `ring-primary/10` → ring-color: primary (#F5D76E) @ 10%

**[56] `<p>` — "Your Holdings"**

1. `text-primary` → color: primary (#F5D76E)
2. `text-xs` → font-size: 12sp
3. `font-bold` → font-weight: 700 (Bold)
4. `mb-1` → bottom margin: 4dp

**[57] `<p>` — "0.0842 BTC"**

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `text-lg` → font-size: 18sp
3. `font-bold` → font-weight: 700 (Bold)

**[58] `<p>` — "~$5,679.42"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-[10px]` → font-size: 10sp

### <!-- Section 5: Activity -->

**[59] `<section>`**

1. `px-6` → horizontal padding: 24dp
2. `mb-10` → bottom margin: 40dp

**[60] `<div>`**

1. `flex`
2. `justify-between`
3. `items-center`
4. `mb-4` → bottom margin: 16dp

**[61] `<h3>` — "Recent Activity"**

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `text-xl` → font-size: 20sp
3. `font-bold` → font-weight: 700 (Bold)
4. `tracking-tight` → letter-spacing: -0.025em (× font-size for sp)

**[62] `<button>` — "See all"**

1. `text-primary` → color: primary (#F5D76E)
2. `text-sm` → font-size: 14sp
3. `font-bold` → font-weight: 700 (Bold)

**[63] `<div>`**

1. `space-y-4` → children spaced 16dp vertically

### <!-- Row 1 -->

**[64] `<div>`**

1. `flex`
2. `items-center`
3. `justify-between`
4. `bg-surface/50` → background: surface (#1C1910) @ 50%
5. `p-4` → padding: 16dp
6. `rounded-xl` → corner-radius: 24dp
7. `border` → border-width: 1dp
8. `border-outline-variant/30` → border-color: outline-variant (#3F3822) @ 30%

**[65] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp

**[66] `<div>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-success/10` → background: success (#4ADE80) @ 10%
5. `flex`
6. `items-center`
7. `justify-center`
8. `text-success` → color: success (#4ADE80)

- [67] `<span>` `material-symbols-outlined` — "call_received"
**[69] `<p>` — "Received"**

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `font-bold` → font-weight: 700 (Bold)
3. `text-sm` → font-size: 14sp

**[70] `<p>` — "Today, 10:42 AM"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-xs` → font-size: 12sp

- [71] `<div>` `text-right`
**[72] `<p>` — "+0.0120 BTC"**

1. `text-success` → color: success (#4ADE80)
2. `font-bold` → font-weight: 700 (Bold)
3. `text-sm` → font-size: 14sp

**[73] `<p>` — "$809.04"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-xs` → font-size: 12sp

### <!-- Row 2 -->

**[74] `<div>`**

1. `flex`
2. `items-center`
3. `justify-between`
4. `bg-surface/50` → background: surface (#1C1910) @ 50%
5. `p-4` → padding: 16dp
6. `rounded-xl` → corner-radius: 24dp
7. `border` → border-width: 1dp
8. `border-outline-variant/30` → border-color: outline-variant (#3F3822) @ 30%

**[75] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp

**[76] `<div>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-danger/10` → background: danger (#FF6B6B) @ 10%
5. `flex`
6. `items-center`
7. `justify-center`
8. `text-danger` → color: danger (#FF6B6B)

- [77] `<span>` `material-symbols-outlined` — "call_made"
**[79] `<p>` — "Sent"**

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `font-bold` → font-weight: 700 (Bold)
3. `text-sm` → font-size: 14sp

**[80] `<p>` — "Yesterday, 4:15 PM"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-xs` → font-size: 12sp

- [81] `<div>` `text-right`
**[82] `<p>` — "-0.0050 BTC"**

1. `text-danger` → color: danger (#FF6B6B)
2. `font-bold` → font-weight: 700 (Bold)
3. `text-sm` → font-size: 14sp

**[83] `<p>` — "$337.10"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-xs` → font-size: 12sp

### <!-- Row 3 -->

**[84] `<div>`**

1. `flex`
2. `items-center`
3. `justify-between`
4. `bg-surface/50` → background: surface (#1C1910) @ 50%
5. `p-4` → padding: 16dp
6. `rounded-xl` → corner-radius: 24dp
7. `border` → border-width: 1dp
8. `border-outline-variant/30` → border-color: outline-variant (#3F3822) @ 30%

**[85] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp

**[86] `<div>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-success/10` → background: success (#4ADE80) @ 10%
5. `flex`
6. `items-center`
7. `justify-center`
8. `text-success` → color: success (#4ADE80)

- [87] `<span>` `material-symbols-outlined` — "bolt"
**[89] `<p>` — "Staked"**

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `font-bold` → font-weight: 700 (Bold)
3. `text-sm` → font-size: 14sp

**[90] `<p>` — "Sep 12, 09:00 AM"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-xs` → font-size: 12sp

- [91] `<div>` `text-right`
**[92] `<p>` — "+0.0004 BTC"**

1. `text-success` → color: success (#4ADE80)
2. `font-bold` → font-weight: 700 (Bold)
3. `text-sm` → font-size: 14sp

**[93] `<p>` — "$26.97"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-xs` → font-size: 12sp

### <!-- Section 6: Top Holders -->

**[94] `<section>`**

1. `px-6` → horizontal padding: 24dp
2. `mb-12` → bottom margin: 48dp

**[95] `<h3>` — "Top Holders Community"**

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `text-lg` → font-size: 18sp
3. `font-bold` → font-weight: 700 (Bold)
4. `mb-4` → bottom margin: 16dp

- [96] `<div>` `flex items-center justify-between`
- [97] `<div>` `avatar-stack`
**[98] `<div>` — "AK"**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-surface-variant` → background: surface-variant (#302B1C)
5. `flex`
6. `items-center`
7. `justify-center`
8. `text-primary` → color: primary (#F5D76E)
9. `text-xs` → font-size: 12sp
10. `font-bold` → font-weight: 700 (Bold)

**[99] `<div>` — "MW"**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-surface-variant` → background: surface-variant (#302B1C)
5. `flex`
6. `items-center`
7. `justify-center`
8. `text-primary` → color: primary (#F5D76E)
9. `text-xs` → font-size: 12sp
10. `font-bold` → font-weight: 700 (Bold)

**[100] `<div>` — "PR"**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-surface-variant` → background: surface-variant (#302B1C)
5. `flex`
6. `items-center`
7. `justify-center`
8. `text-primary` → color: primary (#F5D76E)
9. `text-xs` → font-size: 12sp
10. `font-bold` → font-weight: 700 (Bold)

**[101] `<div>` — "JL"**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-surface-variant` → background: surface-variant (#302B1C)
5. `flex`
6. `items-center`
7. `justify-center`
8. `text-primary` → color: primary (#F5D76E)
9. `text-xs` → font-size: 12sp
10. `font-bold` → font-weight: 700 (Bold)

**[102] `<div>` — "ST"**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-surface-variant` → background: surface-variant (#302B1C)
5. `flex`
6. `items-center`
7. `justify-center`
8. `text-primary` → color: primary (#F5D76E)
9. `text-xs` → font-size: 12sp
10. `font-bold` → font-weight: 700 (Bold)

**[103] `<div>` — "+42"**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-primary-container` → background: primary-container (#4A3200)
5. `flex`
6. `items-center`
7. `justify-center`
8. `text-primary` → color: primary (#F5D76E)
9. `text-xs` → font-size: 12sp
10. `font-bold` → font-weight: 700 (Bold)
11. `border-2` → border-width: 2dp
12. `border-background` → border-color: background (#0F0D09)

**[104] `<button>`**

1. `flex`
2. `items-center`
3. `gap-2` → gap: 8dp
4. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
5. `hover:text-primary` (motion: web-only — DROP — pointer/hover)
6. `transition-colors` (motion: transition hint — Entrance/Value family)

**[105] `<span>` — "Join Group"**

1. `text-sm` → font-size: 14sp
2. `font-bold` → font-weight: 700 (Bold)

**[106] `<span>` — "chevron_right"**

1. `material-symbols-outlined`
2. `text-lg` → font-size: 18sp

### <!-- Sticky Footer -->

**[107] `<footer>`**

1. `fixed` (positioning: fixed — Compose: Box overlay or BottomBar slot)
2. `bottom-0` → bottom: 0dp
3. `left-0` → left: 0dp
4. `right-0` → right: 0dp
5. `z-50` (z-index — Compose has no z-index; layering is order-based)
6. `bg-surface` → background: surface (#1C1910)
7. `border-t` → border-t width: 1dp
8. `border-outline-variant` → border-color: outline-variant (#3F3822)
9. `px-6` → horizontal padding: 24dp
10. `pt-4` → top padding: 16dp
11. `pb-10` → bottom padding: 40dp
12. `flex`
13. `gap-4` → gap: 16dp

**[108] `<button>` — "Sell"**

1. `w-1/2` → width: 50.0%
2. `h-14` → height: 56dp
3. `border-2` → border-width: 2dp
4. `border-primary` → border-color: primary (#F5D76E)
5. `text-primary` → color: primary (#F5D76E)
6. `font-bold` → font-weight: 700 (Bold)
7. `rounded-xl` → corner-radius: 24dp
8. `active:scale-95` (motion: interaction — DROP — touch press feedback)
9. `transition-all` (motion: transition hint — Entrance/Value family)

**[109] `<button>` — "Buy"**

1. `w-1/2` → width: 50.0%
2. `h-14` → height: 56dp
3. `bg-primary` → background: primary (#F5D76E)
4. `text-on-primary` → color: on-primary (#2C1900)
5. `font-bold` → font-weight: 700 (Bold)
6. `rounded-xl` → corner-radius: 24dp
7. `shadow-lg` → shadow: ~8dp elevation
8. `active:scale-95` (motion: interaction — DROP — touch press feedback)
9. `transition-all` (motion: transition hint — Entrance/Value family)

**[110] `<div>`**

1. `fixed` (positioning: fixed — Compose: Box overlay or BottomBar slot)
2. `inset-0` → inset: 0dp
3. `z-50` (z-index — Compose has no z-index; layering is order-based)
4. `bg-[#0F0D09]/60` → background: #0F0D09 @ 60%

**[111] `<div>`**

1. `fixed` (positioning: fixed — Compose: Box overlay or BottomBar slot)
2. `bottom-0` → bottom: 0dp
3. `left-0` → left: 0dp
4. `right-0` → right: 0dp
5. `z-[60]` (z-index — Compose has no z-index; layering is order-based)
6. `bg-[#1C1910]` → background: #1C1910
7. `rounded-t-[24px]` → corner-radius (t): 24px
8. `flex`
9. `flex-col`
10. `pb-10` → bottom padding: 40dp

**[112] `<div>`**

1. `w-10` → width: 40dp
2. `h-1` → height: 4dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-[#3F3822]` → background: #3F3822
5. `mx-auto` → horizontal margin: auto
6. `mt-3` → top margin: 12dp
7. `mb-4` → bottom margin: 16dp

**[113] `<div>`**

1. `px-5` → horizontal padding: 20dp
2. `mb-6` → bottom margin: 24dp

**[114] `<h3>` — "Buy Bitcoin"**

1. `text-[18px]` → font-size: 18sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-[#EDE8D5]` → color: #EDE8D5

**[115] `<p>` — "BTC / USD"**

1. `text-[13px]` → font-size: 13sp
2. `text-[#C4BA94]` → color: #C4BA94

**[116] `<div>`**

1. `px-5` → horizontal padding: 20dp
2. `mb-6` → bottom margin: 24dp

**[117] `<div>`**

1. `h-14` → height: 56dp
2. `bg-[#302B1C]` → background: #302B1C
3. `border-[1.5px]` → border-width: 1.5px
4. `border-[#F5D76E]` → border-width: #F5D76E
5. `rounded-xl` → corner-radius: 24dp
6. `flex`
7. `items-center`
8. `px-4` → horizontal padding: 16dp
9. `justify-between`

**[118] `<div>`**

1. `flex`
2. `items-center`
3. `gap-2` → gap: 8dp

**[119] `<span>` — "$"**

1. `text-base` → font-size: 16sp
2. `text-[#C4BA94]` → color: #C4BA94

**[120] `<span>` — "500.00"**

1. `text-xl` → font-size: 20sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-[#EDE8D5]` → color: #EDE8D5

**[121] `<span>` — "≈ 0.0074 BTC"**

1. `text-xs` → font-size: 12sp
2. `text-[#C4BA94]` → color: #C4BA94

**[122] `<div>`**

1. `px-5` → horizontal padding: 20dp
2. `mb-8` → bottom margin: 32dp

**[123] `<div>`**

1. `relative`
2. `h-1` → height: 4dp
3. `w-full` → width: 100%
4. `bg-[#3F3822]` → background: #3F3822
5. `rounded-full` → corner-radius: CircleShape
6. `mb-6` → bottom margin: 24dp

**[124] `<div>`**

1. `absolute` (positioning: absolute — Compose: Box overlay or BottomBar slot)
2. `left-0` → left: 0dp
3. `top-0` → top: 0dp
4. `h-full` → height: 100%
5. `w-[35%]` → width: 35%
6. `bg-[#F5D76E]` → background: #F5D76E
7. `rounded-full` → corner-radius: CircleShape

**[125] `<div>`**

1. `absolute` (positioning: absolute — Compose: Box overlay or BottomBar slot)
2. `left-[35%]` → left: 35%
3. `top-1/2` → top: 50.0%
4. `-translate-y-1/2` (50% Y translate — typically for vertical centering)
5. `-translate-x-1/2`
6. `w-5` → width: 20dp
7. `h-5` → height: 20dp
8. `bg-[#F5D76E]` → background: #F5D76E
9. `rounded-full` → corner-radius: CircleShape
10. `flex`
11. `items-center`
12. `justify-center`

**[126] `<div>`**

1. `w-1.5` → width: 6dp
2. `h-1.5` → height: 6dp
3. `bg-white` → background: white
4. `rounded-full` → corner-radius: CircleShape

**[127] `<div>`**

1. `flex`
2. `justify-between`
3. `gap-3` → gap: 12dp

**[128] `<button>` — "25%"**

1. `flex-1`
2. `py-2` → vertical padding: 8dp
3. `bg-[#302B1C]` → background: #302B1C
4. `text-[#C4BA94]` → color: #C4BA94
5. `text-xs` → font-size: 12sp
6. `font-bold` → font-weight: 700 (Bold)
7. `rounded-lg` → corner-radius: 16dp

**[129] `<button>` — "50%"**

1. `flex-1`
2. `py-2` → vertical padding: 8dp
3. `bg-[#F5D76E]` → background: #F5D76E
4. `text-[#2C1900]` → color: #2C1900
5. `text-xs` → font-size: 12sp
6. `font-bold` → font-weight: 700 (Bold)
7. `rounded-lg` → corner-radius: 16dp

**[130] `<button>` — "75%"**

1. `flex-1`
2. `py-2` → vertical padding: 8dp
3. `bg-[#302B1C]` → background: #302B1C
4. `text-[#C4BA94]` → color: #C4BA94
5. `text-xs` → font-size: 12sp
6. `font-bold` → font-weight: 700 (Bold)
7. `rounded-lg` → corner-radius: 16dp

**[131] `<button>` — "Max"**

1. `flex-1`
2. `py-2` → vertical padding: 8dp
3. `bg-[#302B1C]` → background: #302B1C
4. `text-[#C4BA94]` → color: #C4BA94
5. `text-xs` → font-size: 12sp
6. `font-bold` → font-weight: 700 (Bold)
7. `rounded-lg` → corner-radius: 16dp

**[132] `<div>`**

1. `px-5` → horizontal padding: 20dp
2. `mb-8` → bottom margin: 32dp

**[133] `<p>` — "Pay with"**

1. `text-[13px]` → font-size: 13sp
2. `text-[#C4BA94]` → color: #C4BA94
3. `mb-2` → bottom margin: 8dp

**[134] `<div>`**

1. `h-[52px]` → height: 52px
2. `bg-[#302B1C]` → background: #302B1C
3. `border` → border-width: 1dp
4. `border-[#3F3822]` → border-width: #3F3822
5. `rounded-xl` → corner-radius: 24dp
6. `flex`
7. `items-center`
8. `px-4` → horizontal padding: 16dp
9. `justify-between`

**[135] `<div>`**

1. `flex`
2. `items-center`
3. `gap-3` → gap: 12dp

**[136] `<span>` — "account_balance_wallet"**

1. `material-symbols-outlined`
2. `text-[#F5D76E]` → color: #F5D76E

**[137] `<span>` — "Main Wallet · $12,450.00"**

1. `text-sm` → font-size: 14sp
2. `text-[#EDE8D5]` → color: #EDE8D5

**[138] `<span>` — "expand_more"**

1. `material-symbols-outlined`
2. `text-[#C4BA94]` → color: #C4BA94

**[139] `<div>`**

1. `px-5` → horizontal padding: 20dp

**[140] `<button>` — "Confirm Purchase"**

1. `w-full` → width: 100%
2. `h-14` → height: 56dp
3. `bg-[#F5D76E]` → background: #F5D76E
4. `text-[#2C1900]` → color: #2C1900
5. `text-base` → font-size: 16sp
6. `font-bold` → font-weight: 700 (Bold)
7. `rounded-xl` → corner-radius: 24dp

---
**Total elements**: 140 | **Visual**: 113 | **Layout-only**: 19 | **Total classes**: 601 | **Auto-converted**: 436 (72%)

