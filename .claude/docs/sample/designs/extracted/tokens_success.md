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
- **borderRadius.2xl**: `1.5rem`
- **borderRadius.DEFAULT**: `0.5rem`
- **borderRadius.full**: `9999px`
- **borderRadius.lg**: `1rem`
- **borderRadius.xl**: `1.25rem`
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
      }
      .material-symbols-outlined {
        font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
      }
      .custom-scrollbar::-webkit-scrollbar {
        width: 0px;
        background: transparent;
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
4. `pb-12` → bottom padding: 48dp

### <!-- TopAppBar -->

**[3] `<header>`**

1. `bg-background` → background: background (#0F0D09)
2. `text-primary` → color: primary (#F5D76E)
3. `font-headline`
4. `text-on-surface` → color: on-surface (#EDE8D5)
5. `w-full` → width: 100%
6. `top-0` → top: 0dp
7. `sticky` (positioning: sticky — Compose: Box overlay or BottomBar slot)
8. `flex`
9. `justify-between`
10. `items-center`
11. `px-6` → horizontal padding: 24dp
12. `py-4` → vertical padding: 16dp
13. `w-full` → width: 100%
14. `z-50` (z-index — Compose has no z-index; layering is order-based)

- [4] `<div>` `flex flex-col`
**[5] `<span>` — "Good morning,"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-sm` → font-size: 14sp
3. `font-medium` → font-weight: 500 (Medium)

**[6] `<h1>` — "Dashboard"**

1. `text-xl` → font-size: 20sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-on-surface` → color: on-surface (#EDE8D5)
4. `tracking-tight` → letter-spacing: -0.025em (× font-size for sp)

**[7] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp

**[8] `<main>`**

1. `px-6` → horizontal padding: 24dp
2. `space-y-6` → children spaced 24dp vertically

### <!-- BALANCE CARD -->

**[9] `<section>`**

1. `relative`
2. `bg-surface` → background: surface (#1C1910)
3. `border` → border-width: 1dp
4. `border-outline` → border-color: outline (#726A48)
5. `rounded-2xl` → corner-radius: 24dp
6. `p-6` → padding: 24dp
7. `overflow-hidden`

**[10] `<div>`**

1. `absolute` (positioning: absolute — Compose: Box overlay or BottomBar slot)
2. `top-0` → top: 0dp
3. `left-0` → left: 0dp
4. `w-full` → width: 100%
5. `h-[3px]` → height: 3px
6. `bg-primary` → background: primary (#F5D76E)

**[11] `<p>` — "TOTAL NET WORTH"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-xs` → font-size: 12sp
3. `font-bold` → font-weight: 700 (Bold)
4. `tracking-widest` → letter-spacing: 0.1em (× font-size for sp)
5. `uppercase`
6. `mb-2` → bottom margin: 8dp

**[12] `<div>`**

1. `flex`
2. `flex-col`
3. `gap-2` → gap: 8dp

**[13] `<h2>` — "$48,520.00"**

1. `text-4xl` → font-size: 36sp
2. `font-extrabold` → font-weight: 800 (ExtraBold)
3. `text-primary` → color: primary (#F5D76E)
4. `tracking-tight` → letter-spacing: -0.025em (× font-size for sp)

**[14] `<div>`**

1. `flex`
2. `items-center`
3. `gap-2` → gap: 8dp

**[15] `<span>`**

1. `flex`
2. `items-center`
3. `bg-success/10` → background: success (#4ADE80) @ 10%
4. `text-success` → color: success (#4ADE80)
5. `text-xs` → font-size: 12sp
6. `font-bold` → font-weight: 700 (Bold)
7. `px-2.5` → horizontal padding: 10dp
8. `py-1` → vertical padding: 4dp
9. `rounded-full` → corner-radius: CircleShape

**[16] `<span>` — "trending_up +2.4% ($1,140)"**

1. `material-symbols-outlined`
2. `text-sm` → font-size: 14sp
3. `mr-1` → right margin: 4dp

**[17] `<span>` — "vs last month"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-xs` → font-size: 12sp

### <!-- QUICK ACTIONS -->

**[18] `<section>`**

1. `grid`
2. `grid-cols-4`
3. `gap-4` → gap: 16dp

**[19] `<div>`**

1. `flex`
2. `flex-col`
3. `items-center`
4. `gap-2` → gap: 8dp

**[20] `<button>`**

1. `w-14` → width: 56dp
2. `h-14` → height: 56dp
3. `bg-primary-container/30` → background: primary-container (#4A3200) @ 30%
4. `border` → border-width: 1dp
5. `border-outline-variant` → border-color: outline-variant (#3F3822)
6. `rounded-full` → corner-radius: CircleShape
7. `flex`
8. `items-center`
9. `justify-center`
10. `text-primary` → color: primary (#F5D76E)
11. `active:scale-90` (state variant — handled by Compose interaction states)
12. `transition-transform` (CSS transition — no Compose equivalent at token level)

- [21] `<span>` `material-symbols-outlined` — "send"
**[22] `<span>` — "Send"**

1. `text-xs` → font-size: 12sp
2. `font-medium` → font-weight: 500 (Medium)
3. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[23] `<div>`**

1. `flex`
2. `flex-col`
3. `items-center`
4. `gap-2` → gap: 8dp

**[24] `<button>`**

1. `w-14` → width: 56dp
2. `h-14` → height: 56dp
3. `bg-primary-container/30` → background: primary-container (#4A3200) @ 30%
4. `border` → border-width: 1dp
5. `border-outline-variant` → border-color: outline-variant (#3F3822)
6. `rounded-full` → corner-radius: CircleShape
7. `flex`
8. `items-center`
9. `justify-center`
10. `text-primary` → color: primary (#F5D76E)
11. `active:scale-90` (state variant — handled by Compose interaction states)
12. `transition-transform` (CSS transition — no Compose equivalent at token level)

- [25] `<span>` `material-symbols-outlined` — "download"
**[26] `<span>` — "Receive"**

1. `text-xs` → font-size: 12sp
2. `font-medium` → font-weight: 500 (Medium)
3. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[27] `<div>`**

1. `flex`
2. `flex-col`
3. `items-center`
4. `gap-2` → gap: 8dp

**[28] `<button>`**

1. `w-14` → width: 56dp
2. `h-14` → height: 56dp
3. `bg-primary-container/30` → background: primary-container (#4A3200) @ 30%
4. `border` → border-width: 1dp
5. `border-outline-variant` → border-color: outline-variant (#3F3822)
6. `rounded-full` → corner-radius: CircleShape
7. `flex`
8. `items-center`
9. `justify-center`
10. `text-primary` → color: primary (#F5D76E)
11. `active:scale-90` (state variant — handled by Compose interaction states)
12. `transition-transform` (CSS transition — no Compose equivalent at token level)

- [29] `<span>` `material-symbols-outlined` — "payments"
**[30] `<span>` — "Pay"**

1. `text-xs` → font-size: 12sp
2. `font-medium` → font-weight: 500 (Medium)
3. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[31] `<div>`**

1. `flex`
2. `flex-col`
3. `items-center`
4. `gap-2` → gap: 8dp

**[32] `<button>`**

1. `w-14` → width: 56dp
2. `h-14` → height: 56dp
3. `bg-primary-container/30` → background: primary-container (#4A3200) @ 30%
4. `border` → border-width: 1dp
5. `border-outline-variant` → border-color: outline-variant (#3F3822)
6. `rounded-full` → corner-radius: CircleShape
7. `flex`
8. `items-center`
9. `justify-center`
10. `text-primary` → color: primary (#F5D76E)
11. `active:scale-90` (state variant — handled by Compose interaction states)
12. `transition-transform` (CSS transition — no Compose equivalent at token level)

- [33] `<span>` `material-symbols-outlined` — "add_circle"
**[34] `<span>` — "Top Up"**

1. `text-xs` → font-size: 12sp
2. `font-medium` → font-weight: 500 (Medium)
3. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

### <!-- INSIGHT BANNER -->

**[35] `<section>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 24dp
5. `p-5` → padding: 20dp
6. `flex`
7. `items-start`
8. `gap-4` → gap: 16dp

**[36] `<div>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `bg-primary/10` → background: primary (#F5D76E) @ 10%
4. `rounded-xl` → corner-radius: 20dp
5. `flex`
6. `items-center`
7. `justify-center`
8. `shrink-0`

**[37] `<span>` — "lightbulb"**

1. `material-symbols-outlined`
2. `text-primary` → color: primary (#F5D76E)

**[39] `<h3>` — "Smart Insight"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-on-surface` → color: on-surface (#EDE8D5)

**[40] `<p>` — "You spent 12% less on dining this month. Keep it up!"**

1. `text-sm` → font-size: 14sp
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
3. `leading-relaxed` → line-height: 1.625× font-size

### <!-- MONTHLY SUMMARY -->

**[41] `<section>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 24dp
5. `p-6` → padding: 24dp

**[42] `<div>`**

1. `flex`
2. `justify-between`
3. `items-end`
4. `mb-6` → bottom margin: 24dp

**[44] `<h3>` — "Income"**

1. `text-sm` → font-size: 14sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
4. `uppercase`
5. `tracking-wider` → letter-spacing: 0.05em (× font-size for sp)

**[45] `<p>` — "$6,200"**

1. `text-2xl` → font-size: 24sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-success` → color: success (#4ADE80)

- [46] `<div>` `text-right`
**[47] `<h3>` — "Expenses"**

1. `text-sm` → font-size: 14sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
4. `uppercase`
5. `tracking-wider` → letter-spacing: 0.05em (× font-size for sp)

**[48] `<p>` — "$3,840"**

1. `text-2xl` → font-size: 24sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-danger` → color: danger (#FF6B6B)

**[49] `<div>`**

1. `relative`
2. `h-3` → height: 12dp
3. `w-full` → width: 100%
4. `bg-surface-variant` → background: surface-variant (#302B1C)
5. `rounded-full` → corner-radius: CircleShape
6. `flex`
7. `overflow-hidden`

**[50] `<div>`**

1. `h-full` → height: 100%
2. `bg-success` → background: success (#4ADE80)
3. `rounded-l-full` → corner-radius (l): CircleShape
- _inline style_: `width: 61.7%`

**[51] `<div>`**

1. `h-full` → height: 100%
2. `bg-danger` → background: danger (#FF6B6B)
- _inline style_: `width: 38.3%`

### <!-- BUDGETS -->

**[52] `<section>`**

1. `space-y-4` → children spaced 16dp vertically

**[53] `<h3>` — "Monthly Budgets"**

1. `text-lg` → font-size: 18sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-on-surface` → color: on-surface (#EDE8D5)

**[54] `<div>`**

1. `grid`
2. `grid-cols-2`
3. `gap-4` → gap: 16dp

**[55] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 24dp
5. `p-4` → padding: 16dp
6. `space-y-3` → children spaced 12dp vertically

- [56] `<div>` `flex justify-between items-center`
**[57] `<span>` — "Shopping"**

1. `text-xs` → font-size: 12sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[58] `<span>` — "$340/500"**

1. `text-xs` → font-size: 12sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-on-surface` → color: on-surface (#EDE8D5)

**[59] `<div>`**

1. `h-1.5` → height: 6dp
2. `w-full` → width: 100%
3. `bg-surface-variant` → background: surface-variant (#302B1C)
4. `rounded-full` → corner-radius: CircleShape

**[60] `<div>`**

1. `h-full` → height: 100%
2. `bg-primary` → background: primary (#F5D76E)
3. `rounded-full` → corner-radius: CircleShape
- _inline style_: `width: 68%`

**[61] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 24dp
5. `p-4` → padding: 16dp
6. `space-y-3` → children spaced 12dp vertically

- [62] `<div>` `flex justify-between items-center`
**[63] `<span>` — "Dining"**

1. `text-xs` → font-size: 12sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[64] `<span>` — "$280/300"**

1. `text-xs` → font-size: 12sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-on-surface` → color: on-surface (#EDE8D5)

**[65] `<div>`**

1. `h-1.5` → height: 6dp
2. `w-full` → width: 100%
3. `bg-surface-variant` → background: surface-variant (#302B1C)
4. `rounded-full` → corner-radius: CircleShape

**[66] `<div>`**

1. `h-full` → height: 100%
2. `bg-primary` → background: primary (#F5D76E)
3. `rounded-full` → corner-radius: CircleShape
- _inline style_: `width: 93%`

**[67] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 24dp
5. `p-4` → padding: 16dp
6. `space-y-3` → children spaced 12dp vertically

- [68] `<div>` `flex justify-between items-center`
**[69] `<span>` — "Transport"**

1. `text-xs` → font-size: 12sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[70] `<span>` — "$180/200"**

1. `text-xs` → font-size: 12sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-on-surface` → color: on-surface (#EDE8D5)

**[71] `<div>`**

1. `h-1.5` → height: 6dp
2. `w-full` → width: 100%
3. `bg-surface-variant` → background: surface-variant (#302B1C)
4. `rounded-full` → corner-radius: CircleShape

**[72] `<div>`**

1. `h-full` → height: 100%
2. `bg-primary` → background: primary (#F5D76E)
3. `rounded-full` → corner-radius: CircleShape
- _inline style_: `width: 90%`

**[73] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-danger/30` → border-color: danger (#FF6B6B) @ 30%
4. `rounded-2xl` → corner-radius: 24dp
5. `p-4` → padding: 16dp
6. `space-y-3` → children spaced 12dp vertically

- [74] `<div>` `flex justify-between items-center`
**[75] `<span>` — "Entertainment"**

1. `text-xs` → font-size: 12sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-danger` → color: danger (#FF6B6B)

**[76] `<span>` — "$520/400"**

1. `text-xs` → font-size: 12sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-danger` → color: danger (#FF6B6B)

**[77] `<div>`**

1. `h-1.5` → height: 6dp
2. `w-full` → width: 100%
3. `bg-surface-variant` → background: surface-variant (#302B1C)
4. `rounded-full` → corner-radius: CircleShape

**[78] `<div>`**

1. `h-full` → height: 100%
2. `bg-danger` → background: danger (#FF6B6B)
3. `rounded-full` → corner-radius: CircleShape
- _inline style_: `width: 100%`

### <!-- SAVINGS GOALS -->

**[79] `<section>`**

1. `space-y-4` → children spaced 16dp vertically

**[80] `<h3>` — "Savings Goals"**

1. `text-lg` → font-size: 18sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-on-surface` → color: on-surface (#EDE8D5)

**[81] `<div>`**

1. `space-y-3` → children spaced 12dp vertically

**[82] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 24dp
5. `p-5` → padding: 20dp

**[83] `<div>`**

1. `flex`
2. `justify-between`
3. `items-center`
4. `mb-3` → bottom margin: 12dp

**[84] `<div>`**

1. `flex`
2. `items-center`
3. `gap-3` → gap: 12dp

**[85] `<span>` — "savings"**

1. `material-symbols-outlined`
2. `text-primary` → color: primary (#F5D76E)

**[86] `<span>` — "Emergency Fund"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-on-surface` → color: on-surface (#EDE8D5)

**[87] `<span>` — "64%"**

1. `text-sm` → font-size: 14sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-success` → color: success (#4ADE80)

**[88] `<div>`**

1. `h-2` → height: 8dp
2. `w-full` → width: 100%
3. `bg-surface-variant` → background: surface-variant (#302B1C)
4. `rounded-full` → corner-radius: CircleShape

**[89] `<div>`**

1. `h-full` → height: 100%
2. `bg-success` → background: success (#4ADE80)
3. `rounded-full` → corner-radius: CircleShape
- _inline style_: `width: 64%`

**[90] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 24dp
5. `p-5` → padding: 20dp

**[91] `<div>`**

1. `flex`
2. `justify-between`
3. `items-center`
4. `mb-3` → bottom margin: 12dp

**[92] `<div>`**

1. `flex`
2. `items-center`
3. `gap-3` → gap: 12dp

**[93] `<span>` — "flight"**

1. `material-symbols-outlined`
2. `text-primary` → color: primary (#F5D76E)

**[94] `<span>` — "Vacation"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-on-surface` → color: on-surface (#EDE8D5)

**[95] `<span>` — "60%"**

1. `text-sm` → font-size: 14sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-success` → color: success (#4ADE80)

**[96] `<div>`**

1. `h-2` → height: 8dp
2. `w-full` → width: 100%
3. `bg-surface-variant` → background: surface-variant (#302B1C)
4. `rounded-full` → corner-radius: CircleShape

**[97] `<div>`**

1. `h-full` → height: 100%
2. `bg-success` → background: success (#4ADE80)
3. `rounded-full` → corner-radius: CircleShape
- _inline style_: `width: 60%`

### <!-- UPCOMING BILLS -->

**[98] `<section>`**

1. `space-y-4` → children spaced 16dp vertically

**[99] `<h3>` — "Upcoming Bills"**

1. `text-lg` → font-size: 18sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-on-surface` → color: on-surface (#EDE8D5)

**[100] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 24dp
5. `divide-y`
6. `divide-outline-variant/30` → divide-color: outline-variant (#3F3822) @ 30%

**[101] `<div>`**

1. `p-4` → padding: 16dp
2. `flex`
3. `justify-between`
4. `items-center`

**[102] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp

**[103] `<div>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `bg-surface-variant` → background: surface-variant (#302B1C)
4. `rounded-xl` → corner-radius: 20dp
5. `flex`
6. `items-center`
7. `justify-center`

**[104] `<span>` — "subscriptions"**

1. `material-symbols-outlined`
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[106] `<p>` — "Netflix"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-on-surface` → color: on-surface (#EDE8D5)

**[107] `<p>` — "May 10"**

1. `text-xs` → font-size: 12sp
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[108] `<span>` — "$15.99"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-on-surface` → color: on-surface (#EDE8D5)

**[109] `<div>`**

1. `p-4` → padding: 16dp
2. `flex`
3. `justify-between`
4. `items-center`

**[110] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp

**[111] `<div>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `bg-surface-variant` → background: surface-variant (#302B1C)
4. `rounded-xl` → corner-radius: 20dp
5. `flex`
6. `items-center`
7. `justify-center`

**[112] `<span>` — "wifi"**

1. `material-symbols-outlined`
2. `text-danger` → color: danger (#FF6B6B)

**[114] `<p>` — "Internet"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-on-surface` → color: on-surface (#EDE8D5)

**[115] `<span>` — "OVERDUE"**

1. `text-[10px]` → font-size: 10sp
2. `bg-danger/20` → background: danger (#FF6B6B) @ 20%
3. `text-danger` → color: danger (#FF6B6B)
4. `font-bold` → font-weight: 700 (Bold)
5. `px-1.5` → horizontal padding: 6dp
6. `py-0.5` → vertical padding: 2dp
7. `rounded` → corner-radius: 8dp
8. `uppercase`

**[116] `<span>` — "$75.00"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-danger` → color: danger (#FF6B6B)

**[117] `<div>`**

1. `p-4` → padding: 16dp
2. `flex`
3. `justify-between`
4. `items-center`

**[118] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp

**[119] `<div>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `bg-surface-variant` → background: surface-variant (#302B1C)
4. `rounded-xl` → corner-radius: 20dp
5. `flex`
6. `items-center`
7. `justify-center`

**[120] `<span>` — "bolt"**

1. `material-symbols-outlined`
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[122] `<p>` — "Electricity"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-on-surface` → color: on-surface (#EDE8D5)

**[123] `<p>` — "May 18"**

1. `text-xs` → font-size: 12sp
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[124] `<span>` — "$124.50"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-on-surface` → color: on-surface (#EDE8D5)

### <!-- PORTFOLIO -->

**[125] `<section>`**

1. `space-y-4` → children spaced 16dp vertically

**[126] `<h3>` — "Portfolio"**

1. `text-lg` → font-size: 18sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-on-surface` → color: on-surface (#EDE8D5)

**[127] `<div>`**

1. `grid`
2. `grid-cols-3`
3. `gap-3` → gap: 12dp

**[128] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 24dp
5. `p-4` → padding: 16dp
6. `flex`
7. `flex-col`
8. `items-center`
9. `gap-2` → gap: 8dp

**[129] `<div>`**

1. `w-8` → width: 32dp
2. `h-8` → height: 32dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-primary/10` → background: primary (#F5D76E) @ 10%
5. `flex`
6. `items-center`
7. `justify-center`
8. `text-primary` → color: primary (#F5D76E)
9. `mb-1` → bottom margin: 4dp

- [130] `<span>` `material-symbols-outlined` — "currency_bitcoin"
**[131] `<span>` — "BTC"**

1. `font-bold` → font-weight: 700 (Bold)

**[132] `<span>` — "+5.2%"**

1. `text-xs` → font-size: 12sp
2. `text-success` → color: success (#4ADE80)
3. `font-bold` → font-weight: 700 (Bold)

**[133] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 24dp
5. `p-4` → padding: 16dp
6. `flex`
7. `flex-col`
8. `items-center`
9. `gap-2` → gap: 8dp

**[134] `<div>`**

1. `w-8` → width: 32dp
2. `h-8` → height: 32dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-on-surface-variant/10` → background: on-surface-variant (#C4BA94) @ 10%
5. `flex`
6. `items-center`
7. `justify-center`
8. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
9. `mb-1` → bottom margin: 4dp

- [135] `<span>` `material-symbols-outlined` — "currency_exchange"
**[136] `<span>` — "ETH"**

1. `font-bold` → font-weight: 700 (Bold)

**[137] `<span>` — "-1.3%"**

1. `text-xs` → font-size: 12sp
2. `text-danger` → color: danger (#FF6B6B)
3. `font-bold` → font-weight: 700 (Bold)

**[138] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 24dp
5. `p-4` → padding: 16dp
6. `flex`
7. `flex-col`
8. `items-center`
9. `gap-2` → gap: 8dp

**[139] `<div>`**

1. `w-8` → width: 32dp
2. `h-8` → height: 32dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-primary/10` → background: primary (#F5D76E) @ 10%
5. `flex`
6. `items-center`
7. `justify-center`
8. `text-primary` → color: primary (#F5D76E)
9. `mb-1` → bottom margin: 4dp

- [140] `<span>` `material-symbols-outlined` — "token"
**[141] `<span>` — "SOL"**

1. `font-bold` → font-weight: 700 (Bold)

**[142] `<span>` — "+8.7%"**

1. `text-xs` → font-size: 12sp
2. `text-success` → color: success (#4ADE80)
3. `font-bold` → font-weight: 700 (Bold)

### <!-- RECENT TRANSACTIONS -->

**[143] `<section>`**

1. `space-y-4` → children spaced 16dp vertically

**[144] `<h3>` — "Recent Transactions"**

1. `text-lg` → font-size: 18sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-on-surface` → color: on-surface (#EDE8D5)

**[145] `<div>`**

1. `space-y-3` → children spaced 12dp vertically

**[146] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 24dp
5. `p-4` → padding: 16dp
6. `flex`
7. `justify-between`
8. `items-center`

**[147] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp

**[148] `<div>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `bg-success/10` → background: success (#4ADE80) @ 10%
4. `text-success` → color: success (#4ADE80)
5. `rounded-full` → corner-radius: CircleShape
6. `flex`
7. `items-center`
8. `justify-center`

- [149] `<span>` `material-symbols-outlined` — "work"
**[151] `<p>` — "Salary"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-on-surface` → color: on-surface (#EDE8D5)

**[152] `<p>` — "Employer Inc."**

1. `text-xs` → font-size: 12sp
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[153] `<span>` — "+$5,200.00"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-success` → color: success (#4ADE80)

**[154] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 24dp
5. `p-4` → padding: 16dp
6. `flex`
7. `justify-between`
8. `items-center`

**[155] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp

**[156] `<div>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `bg-surface-variant` → background: surface-variant (#302B1C)
4. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
5. `rounded-full` → corner-radius: CircleShape
6. `flex`
7. `items-center`
8. `justify-center`

- [157] `<span>` `material-symbols-outlined` — "coffee"
**[159] `<p>` — "Starbucks"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-on-surface` → color: on-surface (#EDE8D5)

**[160] `<p>` — "Beverage"**

1. `text-xs` → font-size: 12sp
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[161] `<span>` — "-$8.50"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-on-surface` → color: on-surface (#EDE8D5)

**[162] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 24dp
5. `p-4` → padding: 16dp
6. `flex`
7. `justify-between`
8. `items-center`

**[163] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp

**[164] `<div>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `bg-surface-variant` → background: surface-variant (#302B1C)
4. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
5. `rounded-full` → corner-radius: CircleShape
6. `flex`
7. `items-center`
8. `justify-center`

- [165] `<span>` `material-symbols-outlined` — "shopping_bag"
**[167] `<p>` — "Amazon"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-on-surface` → color: on-surface (#EDE8D5)

**[168] `<p>` — "Retail"**

1. `text-xs` → font-size: 12sp
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[169] `<span>` — "-$67.20"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-on-surface` → color: on-surface (#EDE8D5)

**[170] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 24dp
5. `p-4` → padding: 16dp
6. `flex`
7. `justify-between`
8. `items-center`

**[171] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp

**[172] `<div>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `bg-success/10` → background: success (#4ADE80) @ 10%
4. `text-success` → color: success (#4ADE80)
5. `rounded-full` → corner-radius: CircleShape
6. `flex`
7. `items-center`
8. `justify-center`

- [173] `<span>` `material-symbols-outlined` — "laptop_mac"
**[175] `<p>` — "Freelance"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-on-surface` → color: on-surface (#EDE8D5)

**[176] `<p>` — "Web Project"**

1. `text-xs` → font-size: 12sp
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[177] `<span>` — "+$1,000.00"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-success` → color: success (#4ADE80)

**[178] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 24dp
5. `p-4` → padding: 16dp
6. `flex`
7. `justify-between`
8. `items-center`

**[179] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp

**[180] `<div>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `bg-surface-variant` → background: surface-variant (#302B1C)
4. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
5. `rounded-full` → corner-radius: CircleShape
6. `flex`
7. `items-center`
8. `justify-center`

- [181] `<span>` `material-symbols-outlined` — "directions_car"
**[183] `<p>` — "Uber"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-on-surface` → color: on-surface (#EDE8D5)

**[184] `<p>` — "Transport"**

1. `text-xs` → font-size: 12sp
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[185] `<span>` — "-$12.00"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-on-surface` → color: on-surface (#EDE8D5)

---
**Total elements**: 185 | **Visual**: 156 | **Layout-only**: 19 | **Total classes**: 665 | **Auto-converted**: 466 (70%)

