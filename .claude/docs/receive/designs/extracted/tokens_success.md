# Token Inventory: stitch_success.html

## Tailwind Config Overrides

_(none found)_

## Global Styles

Inline `<style>` rules that apply globally — these affect every matching element regardless of class list.

```css
body { background-color: #0D0919; color: #e7e0ec; }
        .material-symbols-outlined { font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24; }
        .qr-card { background-color: #FFFFFF; }
        .warning-border { border-color: rgba(255, 180, 171, 0.4); }
        .bottom-gradient { background: linear-gradient(to top, #0D0919 0%, transparent 100%); }
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

1. `font-body-md`
2. `antialiased`
3. `min-h-screen` → min-height: 100vh/vw
4. `flex`
5. `flex-col`

### <!-- TopAppBar -->

**[3] `<nav>`**

1. `flex`
2. `justify-between`
3. `items-center`
4. `px-4` → horizontal padding: 16dp
5. `py-4` → vertical padding: 16dp
6. `w-full` → width: 100%
7. `bg-[#0D0919]` → background: #0D0919
8. `sticky` (positioning: sticky — Compose: Box overlay or BottomBar slot)
9. `top-0` → top: 0dp
10. `z-50` (z-index — Compose has no z-index; layering is order-based)

**[4] `<button>`**

1. `text-[#E9E0FF]` → color: #E9E0FF
2. `hover:bg-white/5` (state variant — handled by Compose interaction states)
3. `transition-colors` (CSS transition — no Compose equivalent at token level)
4. `active:scale-95` (state variant — handled by Compose interaction states)
5. `duration-200`
6. `p-2` → padding: 8dp
7. `rounded-full` → corner-radius: CircleShape

- [5] `<span>` `material-symbols-outlined` — "arrow_back"
**[6] `<h1>` — "Receive"**

1. `text-[20px]` → font-size: 20sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-[#E9E0FF]` → color: #E9E0FF
4. `font-manrope`
5. `antialiased`
6. `tracking-[-0.5px]` → letter-spacing: -0.5px

**[7] `<div>`**

1. `w-11` → width: 44dp

### <!-- Main Content -->

**[8] `<main>`**

1. `flex-1`
2. `px-4` → horizontal padding: 16dp
3. `py-6` → vertical padding: 24dp
4. `overflow-y-auto`
5. `pb-48` → bottom padding: 192dp

**[9] `<div>`**

1. `max-w-md`
2. `mx-auto` → horizontal margin: auto
3. `space-y-6` → children spaced 24dp vertically

### <!-- Asset Selector -->

**[10] `<button>`**

1. `w-full` → width: 100%
2. `flex`
3. `items-center`
4. `justify-between`
5. `p-4` → padding: 16dp
6. `bg-[#231A38]` → background: #231A38
7. `border` → border-width: 1dp
8. `border-[#4A3F6B]` → border-width: #4A3F6B
9. `rounded-2xl` → corner-radius: 16dp
10. `hover:bg-[#2c2146]` (state variant — handled by Compose interaction states)
11. `transition-colors` (CSS transition — no Compose equivalent at token level)
12. `active:scale-[0.98]` (state variant — handled by Compose interaction states)
13. `duration-200`

**[11] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp

**[12] `<div>`**

1. `w-12` → width: 48dp
2. `h-12` → height: 48dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-[#EAB308]` → background: #EAB308
5. `flex`
6. `items-center`
7. `justify-center`
8. `border-2` → border-width: 2dp
9. `border-white/10` → border-color: white @ 10%

**[13] `<span>` — "B"**

1. `text-white` → color: white
2. `font-bold` → font-weight: 700 (Bold)
3. `text-xl` → font-size: 20sp

- [14] `<div>` `text-left`
**[15] `<p>` — "Bitcoin (BTC)"**

1. `text-body-lg`
2. `font-bold` → font-weight: 700 (Bold)
3. `text-[#E9E0FF]` → color: #E9E0FF

**[16] `<p>` — "Bitcoin Network"**

1. `text-label-md`
2. `text-[#C5BCE0]` → color: #C5BCE0

**[17] `<span>` — "expand_more"**

1. `material-symbols-outlined`
2. `text-[#C5BCE0]` → color: #C5BCE0

### <!-- Wallet Address Pill -->

**[18] `<div>`**

1. `w-full` → width: 100%
2. `flex`
3. `items-center`
4. `justify-between`
5. `pl-6` → left padding: 24dp
6. `pr-2` → right padding: 8dp
7. `py-2` → vertical padding: 8dp
8. `bg-[#231A38]` → background: #231A38
9. `border` → border-width: 1dp
10. `border-[#4A3F6B]` → border-width: #4A3F6B
11. `rounded-full` → corner-radius: CircleShape

**[19] `<span>` — "bc1qxy2k...5mdq3w0c0"**

1. `font-mono`
2. `text-body-md`
3. `text-[#E9E0FF]` → color: #E9E0FF
4. `truncate`
5. `mr-4` → right margin: 16dp

**[20] `<button>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `flex`
4. `items-center`
5. `justify-center`
6. `bg-[#9D70FF]/10` → background: #9D70FF @ 10%
7. `rounded-full` → corner-radius: CircleShape
8. `hover:bg-[#9D70FF]/20` (state variant — handled by Compose interaction states)
9. `active:scale-90` (state variant — handled by Compose interaction states)
10. `transition-all` (CSS transition — no Compose equivalent at token level)

**[21] `<span>` — "content_copy"**

1. `material-symbols-outlined`
2. `text-[#9D70FF]` → color: #9D70FF
3. `text-[20px]` → font-size: 20sp

### <!-- Warning Banner -->

**[22] `<div>`**

1. `p-4` → padding: 16dp
2. `bg-[#181228]` → background: #181228
3. `border` → border-width: 1dp
4. `warning-border`
5. `rounded-xl` → corner-radius: 12dp
6. `flex`
7. `gap-4` → gap: 16dp

**[23] `<span>` — "warning"**

1. `material-symbols-outlined`
2. `text-[#FFB4AB]` → color: #FFB4AB
3. `flex-shrink-0`

**[24] `<div>`**

1. `space-y-1` → children spaced 4dp vertically

**[25] `<p>` — "Bitcoin Network only"**

1. `text-body-md`
2. `font-bold` → font-weight: 700 (Bold)
3. `text-[#E9E0FF]` → color: #E9E0FF

**[26] `<p>` — "Sending coins or tokens via any other network will result in..."**

1. `text-label-md`
2. `text-[#C5BCE0]` → color: #C5BCE0
3. `leading-relaxed` → line-height: 1.625× font-size

### <!-- Sticky Bottom Actions -->

**[27] `<div>`**

1. `fixed` (positioning: fixed — Compose: Box overlay or BottomBar slot)
2. `bottom-0` → bottom: 0dp
3. `left-0` → left: 0dp
4. `w-full` → width: 100%
5. `z-40` (z-index — Compose has no z-index; layering is order-based)
6. `pointer-events-none`

**[28] `<div>`**

1. `h-24` → height: 96dp
2. `bottom-gradient`

**[29] `<div>`**

1. `bg-[#0D0919]/80` → background: #0D0919 @ 80%
2. `backdrop-blur-xl`
3. `px-6` → horizontal padding: 24dp
4. `pb-10` → bottom padding: 40dp
5. `pt-4` → top padding: 16dp
6. `flex`
7. `gap-4` → gap: 16dp
8. `pointer-events-auto`

**[30] `<button>`**

1. `flex-1`
2. `h-14` → height: 56dp
3. `flex`
4. `items-center`
5. `justify-center`
6. `gap-2` → gap: 8dp
7. `bg-[#231A38]` → background: #231A38
8. `border` → border-width: 1dp
9. `border-[#4A3F6B]` → border-width: #4A3F6B
10. `rounded-full` → corner-radius: CircleShape
11. `text-[#E9E0FF]` → color: #E9E0FF
12. `font-semibold` → font-weight: 600 (SemiBold)
13. `hover:bg-[#2c2146]` (state variant — handled by Compose interaction states)
14. `active:scale-95` (state variant — handled by Compose interaction states)
15. `transition-all` (CSS transition — no Compose equivalent at token level)

**[31] `<span>` — "share Share"**

1. `material-symbols-outlined`
2. `text-[20px]` → font-size: 20sp

**[32] `<button>`**

1. `flex-[1.5]`
2. `h-14` → height: 56dp
3. `flex`
4. `items-center`
5. `justify-center`
6. `gap-2` → gap: 8dp
7. `bg-[#9D70FF]` → background: #9D70FF
8. `rounded-full` → corner-radius: CircleShape
9. `text-[#1A0054]` → color: #1A0054
10. `font-bold` → font-weight: 700 (Bold)
11. `shadow-[0_0_20px_rgba(157,112,255,0.3)]` → shadow-color: 0_0_20px_rgba(157,112,255,0.3)
12. `hover:opacity-90` (state variant — handled by Compose interaction states)
13. `active:scale-95` (state variant — handled by Compose interaction states)
14. `transition-all` (CSS transition — no Compose equivalent at token level)

**[33] `<span>` — "content_copy Copy Address"**

1. `material-symbols-outlined`
2. `text-[20px]` → font-size: 20sp

---
**Total elements**: 33 | **Visual**: 30 | **Layout-only**: 3 | **Total classes**: 170 | **Auto-converted**: 92 (54%)

