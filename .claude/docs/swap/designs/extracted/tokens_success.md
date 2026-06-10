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
body {
            background-color: #0F0D09;
            color: #EDE8D5;
            font-family: 'Manrope', sans-serif;
            -webkit-tap-highlight-color: transparent;
        }
        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
        }
        .shimmer-glow {
            animation: shimmer 3s infinite linear;
            background: linear-gradient(90deg, #EDE8D5 0%, #F5D76E 50%, #EDE8D5 100%);
            background-size: 200% auto;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        @keyframes shimmer {
            to { background-position: 200% center; }
        }
        .glass-footer {
            background: rgba(15, 13, 9, 0.8);
            backdrop-filter: blur(12px);
        }
body {
      min-height: max(884px, 100dvh);
    }
```

## Motion Inventory

Captured animation vocabulary. Bucket each token via the Web-Motion Policy in `.claude/skills/_shared/motion.md`: **KEEP** the 4 non-interaction families (Ambient bg, Loading/Attention loop, Entrance, Value-driven) + honor reduced-motion; **DROP** all touch press (`active:*`, ripple) and pointer/hover (`hover:*`, `group-hover:*`) feedback. Per-element `animate-*` / `transition-*` / `active:` / `hover:` tags are annotated inline in the Elements section below.

### @keyframes (<style> blocks)

- shimmer

### Keyframe magnitudes

Animated value ranges (the delta each animation moves through). Pin these in the blueprint's `## Motion` **Magnitude** column — they are the only source for scale/translate/opacity/offset amounts (duration/easing come from the shorthand above; the implementer must not invent magnitudes).

- **shimmer**: bg-position 200% center

### JS animation drivers

- timed step (Value/Entrance)

## Elements

Each class is followed by its deterministic token interpretation when one applies (e.g. `mt-4 → margin-top: 16dp`). Classes with no annotation are layout primitives, state variants, or unrecognised — interpret them yourself.

**Element formats:**
- **Visual elements** (any class converts to a visual token, or has inline style) get a full block with one line per class.
- **Layout-only elements** (only structural classes like `flex`, `items-center`, `justify-between`) get a single compact line — they still appear in order so structural mismatches (Row vs Column, arrangement, alignment) remain visible.
- **Classless text children** (e.g. `<span>Label</span>` inside a button) also appear as a one-liner with their text, so sibling DOM order inside a flex container is preserved — compare it against the Compose content lambda order.

- [1] `<html>` `dark`
**[2] `<body>`**

1. `min-h-screen` → min-height: 100vh/vw
2. `pb-40` → bottom padding: 160dp

### <!-- Managed by JSON logic: TopAppBar type small, sticky, transparent bg-background as per JSON shell layout -->

**[3] `<header>`**

1. `sticky` (positioning: sticky — Compose: Box overlay or BottomBar slot)
2. `top-0` → top: 0dp
3. `z-50` (z-index — Compose has no z-index; layering is order-based)
4. `w-full` → width: 100%
5. `h-16` → height: 64dp
6. `flex`
7. `items-center`
8. `px-6` → horizontal padding: 24dp
9. `bg-background/80` → background: background (#0F0D09) @ 80%
10. `backdrop-blur-sm`

**[4] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp
4. `w-full` → width: 100%

**[5] `<button>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `flex`
4. `items-center`
5. `justify-center`
6. `transition-colors` (motion: transition hint — Entrance/Value family)
7. `active:scale-95` (motion: interaction — DROP — touch press feedback)

**[6] `<span>` — "arrow_back"**

1. `material-symbols-outlined`
2. `text-primary` → color: primary (#F5D76E)

**[7] `<h1>` — "Swap"**

1. `font-headline`
2. `text-lg` → font-size: 18sp
3. `font-bold` → font-weight: 700 (Bold)
4. `text-on-surface` → color: on-surface (#EDE8D5)
5. `tracking-tight` → letter-spacing: -0.025em (× font-size for sp)
6. `ml-2` → left margin: 8dp

**[8] `<main>`**

1. `px-4` → horizontal padding: 16dp
2. `mt-4` → top margin: 16dp
3. `max-w-lg`
4. `mx-auto` → horizontal margin: auto
5. `space-y-4` → children spaced 16dp vertically

### <!-- From Asset Card -->

**[9] `<section>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 20dp
5. `p-6` → padding: 24dp
6. `transition-all` (motion: transition hint — Entrance/Value family)
7. `hover:border-outline/50` (motion: web-only — DROP — pointer/hover)

**[10] `<div>`**

1. `flex`
2. `items-center`
3. `justify-between`
4. `mb-4` → bottom margin: 16dp

**[11] `<div>`**

1. `flex`
2. `items-center`
3. `gap-3` → gap: 12dp

**[12] `<div>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `rounded-full` → corner-radius: CircleShape
4. `overflow-hidden`
5. `bg-primary-container` → background: primary-container (#4A3200)
6. `flex`
7. `items-center`
8. `justify-center`
9. `border` → border-width: 1dp
10. `border-outline-variant` → border-color: outline-variant (#3F3822)

**[13] `<img>`**

1. `w-6` → width: 24dp
2. `h-6` → height: 24dp

- [14] `<div>` `flex flex-col`
**[15] `<span>` — "Bitcoin"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-lg` → font-size: 18sp
3. `text-on-surface` → color: on-surface (#EDE8D5)

**[16] `<span>` — "BTC"**

1. `font-label`
2. `text-xs` → font-size: 12sp
3. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
4. `uppercase`
5. `tracking-wider` → letter-spacing: 0.05em (× font-size for sp)

**[17] `<button>` — "MAX"**

1. `bg-surface-variant` → background: surface-variant (#302B1C)
2. `px-3` → horizontal padding: 12dp
3. `py-1.5` → vertical padding: 6dp
4. `rounded-lg` → corner-radius: 16dp
5. `font-bold` → font-weight: 700 (Bold)
6. `text-xs` → font-size: 12sp
7. `text-primary` → color: primary (#F5D76E)
8. `hover:brightness-110` (motion: web-only — DROP — pointer/hover)
9. `transition-all` (motion: transition hint — Entrance/Value family)
10. `active:scale-95` (motion: interaction — DROP — touch press feedback)

**[18] `<div>`**

1. `flex`
2. `flex-col`
3. `gap-1` → gap: 4dp

**[19] `<span>` — "Balance: 0.4821 BTC"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-sm` → font-size: 14sp
3. `font-medium` → font-weight: 500 (Medium)

**[20] `<div>`**

1. `relative`
2. `mt-2` → top margin: 8dp

**[21] `<input>`**

1. `w-full` → width: 100%
2. `bg-transparent` → background: transparent
3. `border-none`
4. `p-0` → padding: 0dp
5. `text-4xl` → font-size: 36sp
6. `font-extrabold` → font-weight: 800 (ExtraBold)
7. `text-on-surface` → color: on-surface (#EDE8D5)
8. `focus:ring-0` (motion: web-only — DROP — pointer/hover)
9. `placeholder-on-surface-variant/30` → placeholder-color: on-surface-variant (#C4BA94) @ 30%

### <!-- Swap Interaction -->

**[22] `<div>`**

1. `relative`
2. `h-4` → height: 16dp
3. `z-10` (z-index — Compose has no z-index; layering is order-based)
4. `flex`
5. `justify-center`
6. `items-center`

**[23] `<button>`**

1. `w-12` → width: 48dp
2. `h-12` → height: 48dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-surface-variant` → background: surface-variant (#302B1C)
5. `border` → border-width: 1dp
6. `border-outline-variant` → border-color: outline-variant (#3F3822)
7. `flex`
8. `items-center`
9. `justify-center`
10. `shadow-lg` → shadow: ~8dp elevation
11. `hover:border-primary/50` (motion: web-only — DROP — pointer/hover)
12. `transition-all` (motion: transition hint — Entrance/Value family)
13. `active:rotate-180` (motion: interaction — DROP — touch press feedback)
14. `duration-500` (motion: transition hint — Entrance/Value family)

**[24] `<span>` — "swap_vert"**

1. `material-symbols-outlined`
2. `text-primary` → color: primary (#F5D76E)
3. `text-2xl` → font-size: 24sp

### <!-- To Asset Card -->

**[25] `<section>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 20dp
5. `p-6` → padding: 24dp
6. `transition-all` (motion: transition hint — Entrance/Value family)
7. `hover:border-outline/50` (motion: web-only — DROP — pointer/hover)

**[26] `<div>`**

1. `flex`
2. `items-center`
3. `justify-between`
4. `mb-4` → bottom margin: 16dp

**[27] `<div>`**

1. `flex`
2. `items-center`
3. `gap-3` → gap: 12dp

**[28] `<div>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `rounded-full` → corner-radius: CircleShape
4. `overflow-hidden`
5. `bg-surface-variant` → background: surface-variant (#302B1C)
6. `flex`
7. `items-center`
8. `justify-center`
9. `border` → border-width: 1dp
10. `border-outline-variant` → border-color: outline-variant (#3F3822)

**[29] `<img>`**

1. `w-6` → width: 24dp
2. `h-6` → height: 24dp

- [30] `<div>` `flex flex-col`
**[31] `<span>` — "Ethereum"**

1. `font-bold` → font-weight: 700 (Bold)
2. `text-lg` → font-size: 18sp
3. `text-on-surface` → color: on-surface (#EDE8D5)

**[32] `<span>` — "ETH"**

1. `font-label`
2. `text-xs` → font-size: 12sp
3. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
4. `uppercase`
5. `tracking-wider` → letter-spacing: 0.05em (× font-size for sp)

**[33] `<span>` — "You receive"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-sm` → font-size: 14sp
3. `font-medium` → font-weight: 500 (Medium)

**[34] `<div>`**

1. `flex`
2. `flex-col`
3. `gap-1` → gap: 4dp

**[35] `<div>` — "8.5994"**

1. `text-4xl` → font-size: 36sp
2. `font-extrabold` → font-weight: 800 (ExtraBold)
3. `shimmer-glow`

### <!-- Rate Row -->

**[36] `<div>`**

1. `flex`
2. `items-center`
3. `justify-center`
4. `gap-2` → gap: 8dp
5. `py-2` → vertical padding: 8dp

**[37] `<span>` — "1 BTC ≈ 17.84 ETH"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-sm` → font-size: 14sp
3. `font-medium` → font-weight: 500 (Medium)

**[38] `<span>` — "sync"**

1. `material-symbols-outlined`
2. `text-primary` → color: primary (#F5D76E)
3. `text-sm` → font-size: 14sp
4. `animate-spin` (motion: looping 'spin' — Loading loop)
- _inline style_: `animation-duration: 4s;`

### <!-- Details Card -->

**[39] `<section>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-2xl` → corner-radius: 20dp
5. `p-6` → padding: 24dp
6. `space-y-4` → children spaced 16dp vertically

- [40] `<div>` `flex justify-between items-center`
**[41] `<span>` — "Network Fee"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-sm` → font-size: 14sp

**[42] `<span>` — "$7.20"**

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `text-sm` → font-size: 14sp
3. `font-medium` → font-weight: 500 (Medium)

**[43] `<div>`**

1. `h-px` → height: 1dp
2. `bg-outline-variant` → background: outline-variant (#3F3822)
3. `w-full` → width: 100%

- [44] `<div>` `flex justify-between items-center group cursor-pointer`
**[45] `<span>` — "Slippage Tolerance"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-sm` → font-size: 14sp

**[46] `<div>`**

1. `flex`
2. `items-center`
3. `gap-1` → gap: 4dp

**[47] `<span>` — "0.5%"**

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `text-sm` → font-size: 14sp
3. `font-medium` → font-weight: 500 (Medium)

**[48] `<span>` — "chevron_right"**

1. `material-symbols-outlined`
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
3. `text-sm` → font-size: 14sp
4. `group-hover:text-primary` (motion: web-only — DROP — pointer/hover)
5. `transition-colors` (motion: transition hint — Entrance/Value family)

**[49] `<div>`**

1. `h-px` → height: 1dp
2. `bg-outline-variant` → background: outline-variant (#3F3822)
3. `w-full` → width: 100%

**[50] `<div>`**

1. `flex`
2. `justify-between`
3. `items-center`
4. `pt-1` → top padding: 4dp

**[51] `<span>` — "Estimated Total"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-sm` → font-size: 14sp

**[52] `<span>` — "0.4821 BTC"**

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `font-bold` → font-weight: 700 (Bold)
3. `text-lg` → font-size: 18sp

### <!-- Review Footer -->

**[53] `<footer>`**

1. `fixed` (positioning: fixed — Compose: Box overlay or BottomBar slot)
2. `bottom-0` → bottom: 0dp
3. `left-0` → left: 0dp
4. `w-full` → width: 100%
5. `glass-footer`
6. `p-6` → padding: 24dp
7. `z-50` (z-index — Compose has no z-index; layering is order-based)

**[54] `<div>`**

1. `max-w-lg`
2. `mx-auto` → horizontal margin: auto

**[55] `<button>` — "Review Swap"**

1. `w-full` → width: 100%
2. `h-14` → height: 56dp
3. `bg-primary` → background: primary (#F5D76E)
4. `text-on-primary` → color: on-primary (#2C1900)
5. `font-bold` → font-weight: 700 (Bold)
6. `text-lg` → font-size: 18sp
7. `rounded-3xl` → corner-radius: 24dp
8. `shadow-gold-glow`
9. `hover:shadow-gold-intense` (motion: web-only — DROP — pointer/hover)
10. `active:scale-[0.98]` (motion: interaction — DROP — touch press feedback)
11. `transition-all` (motion: transition hint — Entrance/Value family)
12. `duration-300` (motion: transition hint — Entrance/Value family)

---
**Total elements**: 55 | **Visual**: 50 | **Layout-only**: 5 | **Total classes**: 248 | **Auto-converted**: 148 (59%)

