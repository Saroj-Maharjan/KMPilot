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
            font-family: 'Manrope', sans-serif;
            -webkit-tap-highlight-color: transparent;
        }
        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
        }
        .premium-blur {
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
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

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `flex`
3. `flex-col`
4. `min-h-screen` → min-height: 100vh/vw

### <!-- TopAppBar from Shared Components Mapping -->

**[3] `<header>`**

1. `fixed` (positioning: fixed — Compose: Box overlay or BottomBar slot)
2. `top-0` → top: 0dp
3. `z-50` (z-index — Compose has no z-index; layering is order-based)
4. `w-full` → width: 100%
5. `h-16` → height: 64dp
6. `flex`
7. `items-center`
8. `px-4` → horizontal padding: 16dp
9. `bg-background/80` → background: background (#0F0D09) @ 80%
10. `premium-blur`
11. `sticky` (positioning: sticky — Compose: Box overlay or BottomBar slot)

**[4] `<div>`**

1. `flex`
2. `items-center`
3. `w-full` → width: 100%
4. `gap-4` → gap: 16dp

**[5] `<button>` — "arrow_back"**

1. `material-symbols-outlined`
2. `text-primary` → color: primary (#F5D76E)
3. `text-2xl` → font-size: 24sp
4. `active:scale-95` (motion: interaction — DROP — touch press feedback)
5. `transition-transform` (motion: transition hint — Entrance/Value family)

**[6] `<h1>` — "Profile"**

1. `font-headline`
2. `text-on-surface` → color: on-surface (#EDE8D5)
3. `font-bold` → font-weight: 700 (Bold)
4. `text-xl` → font-size: 20sp

### <!-- Main Content Canvas -->

**[7] `<main>`**

1. `flex-grow`
2. `pt-16` → top padding: 64dp
3. `pb-32` → bottom padding: 128dp
4. `px-6` → horizontal padding: 24dp
5. `flex`
6. `flex-col`

### <!-- Avatar Section -->

**[8] `<section>`**

1. `mt-8` → top margin: 32dp
2. `flex`
3. `flex-col`
4. `items-center`

- [9] `<div>` `relative`
**[10] `<div>`**

1. `w-20` → width: 80dp
2. `h-20` → height: 80dp
3. `rounded-full` → corner-radius: CircleShape
4. `bg-surface-variant` → background: surface-variant (#302B1C)
5. `border` → border-width: 1dp
6. `border-primary` → border-color: primary (#F5D76E)
7. `flex`
8. `items-center`
9. `justify-center`
10. `overflow-hidden`

**[11] `<span>` — "AJ"**

1. `font-headline`
2. `text-primary` → color: primary (#F5D76E)
3. `font-bold` → font-weight: 700 (Bold)
4. `text-2xl` → font-size: 24sp

### <!-- Premium subtle glow behind avatar -->

**[12] `<div>`**

1. `absolute` (positioning: absolute — Compose: Box overlay or BottomBar slot)
2. `inset-0` → inset: 0dp
3. `-z-10`
4. `bg-primary/10` → background: primary (#F5D76E) @ 10%
5. `blur-xl`
6. `rounded-full` → corner-radius: CircleShape
7. `scale-125`

**[13] `<h2>` — "Alex Johnson"**

1. `mt-3` → top margin: 12dp
2. `text-on-surface` → color: on-surface (#EDE8D5)
3. `font-bold` → font-weight: 700 (Bold)
4. `text-xl` → font-size: 20sp
5. `font-headline`
6. `tracking-tight` → letter-spacing: -0.025em (× font-size for sp)

**[14] `<p>` — "alex@example.com"**

1. `mt-1` → top margin: 4dp
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
3. `text-sm` → font-size: 14sp
4. `font-body`

### <!-- Account Details Card -->

**[15] `<section>`**

1. `mt-6` → top margin: 24dp

**[16] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-xl` → corner-radius: 24dp
5. `p-5` → padding: 20dp
6. `shadow-sm` → shadow: ~1dp elevation

**[17] `<h3>` — "ACCOUNT DETAILS"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `font-label`
3. `text-xs` → font-size: 12sp
4. `font-semibold` → font-weight: 600 (SemiBold)
5. `uppercase`
6. `tracking-widest` → letter-spacing: 0.1em (× font-size for sp)
7. `mb-2` → bottom margin: 8dp

### <!-- Row 1: Name -->

**[18] `<div>`**

1. `py-4` → vertical padding: 16dp
2. `flex`
3. `flex-col`
4. `gap-1` → gap: 4dp

**[19] `<span>` — "Name"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-[10px]` → font-size: 10sp
3. `font-label`
4. `uppercase`
5. `tracking-wider` → letter-spacing: 0.05em (× font-size for sp)

**[20] `<span>` — "Alex Johnson"**

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `text-base` → font-size: 16sp
3. `font-body`
4. `font-medium` → font-weight: 500 (Medium)

### <!-- Divider -->

**[21] `<div>`**

1. `h-px` → height: 1dp
2. `w-full` → width: 100%
3. `bg-outline-variant` → background: outline-variant (#3F3822)

### <!-- Row 2: Email -->

**[22] `<div>`**

1. `py-4` → vertical padding: 16dp
2. `flex`
3. `flex-col`
4. `gap-1` → gap: 4dp

**[23] `<span>` — "Email"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-[10px]` → font-size: 10sp
3. `font-label`
4. `uppercase`
5. `tracking-wider` → letter-spacing: 0.05em (× font-size for sp)

**[24] `<span>` — "alex@example.com"**

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `text-base` → font-size: 16sp
3. `font-body`
4. `font-medium` → font-weight: 500 (Medium)

### <!-- Row 3: Account ID (Extra context for Fintech feel) -->

**[25] `<div>`**

1. `h-px` → height: 1dp
2. `w-full` → width: 100%
3. `bg-outline-variant` → background: outline-variant (#3F3822)

**[26] `<div>`**

1. `py-4` → vertical padding: 16dp
2. `flex`
3. `flex-col`
4. `gap-1` → gap: 4dp

**[27] `<span>` — "Member Tier"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-[10px]` → font-size: 10sp
3. `font-label`
4. `uppercase`
5. `tracking-wider` → letter-spacing: 0.05em (× font-size for sp)

**[28] `<div>`**

1. `flex`
2. `items-center`
3. `gap-2` → gap: 8dp

**[29] `<span>` — "stars"**

1. `text-primary` → color: primary (#F5D76E)
2. `material-symbols-outlined`
3. `text-sm` → font-size: 14sp
- _inline style_: `font-variation-settings: 'FILL' 1;`

**[30] `<span>` — "Gold Private Banking"**

1. `text-primary` → color: primary (#F5D76E)
2. `text-base` → font-size: 16sp
3. `font-headline`
4. `font-semibold` → font-weight: 600 (SemiBold)

### <!-- Security & Preferences (Additional UI depth) -->

**[31] `<section>`**

1. `mt-4` → top margin: 16dp

**[32] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `rounded-xl` → corner-radius: 24dp
5. `p-5` → padding: 20dp

**[33] `<h3>` — "PREFERENCES"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `font-label`
3. `text-xs` → font-size: 12sp
4. `font-semibold` → font-weight: 600 (SemiBold)
5. `uppercase`
6. `tracking-widest` → letter-spacing: 0.1em (× font-size for sp)
7. `mb-4` → bottom margin: 16dp

**[34] `<div>`**

1. `flex`
2. `items-center`
3. `justify-between`
4. `py-2` → vertical padding: 8dp

**[35] `<div>`**

1. `flex`
2. `items-center`
3. `gap-3` → gap: 12dp

**[36] `<span>` — "shield"**

1. `material-symbols-outlined`
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[37] `<span>` — "Biometric Security"**

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `font-body`

**[38] `<div>`**

1. `w-10` → width: 40dp
2. `h-6` → height: 24dp
3. `bg-primary` → background: primary (#F5D76E)
4. `rounded-full` → corner-radius: CircleShape
5. `flex`
6. `items-center`
7. `px-1` → horizontal padding: 4dp

**[39] `<div>`**

1. `w-4` → width: 16dp
2. `h-4` → height: 16dp
3. `bg-on-primary` → background: on-primary (#2C1900)
4. `rounded-full` → corner-radius: CircleShape
5. `translate-x-4`

### <!-- Fixed Bottom Action Section -->

**[40] `<div>`**

1. `fixed` (positioning: fixed — Compose: Box overlay or BottomBar slot)
2. `bottom-0` → bottom: 0dp
3. `left-0` → left: 0dp
4. `w-full` → width: 100%
5. `p-6` → padding: 24dp
6. `bg-gradient-to-t`
7. `from-background`
8. `via-background`
9. `to-transparent`
10. `pt-12` → top padding: 48dp

**[41] `<button>`**

1. `w-full` → width: 100%
2. `h-14` → height: 56dp
3. `bg-primary` → background: primary (#F5D76E)
4. `text-on-primary` → color: on-primary (#2C1900)
5. `rounded-xl` → corner-radius: 24dp
6. `flex`
7. `items-center`
8. `justify-center`
9. `gap-2` → gap: 8dp
10. `font-headline`
11. `font-bold` → font-weight: 700 (Bold)
12. `text-base` → font-size: 16sp
13. `active:scale-[0.98]` (motion: interaction — DROP — touch press feedback)
14. `transition-all` (motion: transition hint — Entrance/Value family)
15. `hover:opacity-90` (motion: web-only — DROP — pointer/hover)

**[42] `<span>` — "edit Edit Profile"**

1. `material-symbols-outlined`
2. `text-xl` → font-size: 20sp

---
**Total elements**: 42 | **Visual**: 40 | **Layout-only**: 2 | **Total classes**: 199 | **Auto-converted**: 121 (60%)

