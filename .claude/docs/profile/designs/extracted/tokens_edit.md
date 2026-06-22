# Token Inventory: stitch_edit.html

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
        .input-focus-glow:focus-within {
            box-shadow: 0 0 0 1px #F5D76E, 0 0 12px rgba(245, 215, 110, 0.15);
        }
        .glass-header {
            background-color: rgba(15, 13, 9, 0.8);
            backdrop-filter: blur(12px);
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

1. `bg-background` → background: background (#0F0D09)
2. `text-on-surface` → color: on-surface (#EDE8D5)
3. `min-h-screen` → min-height: 100vh/vw
4. `flex`
5. `flex-col`

### <!-- JSON Component: TopAppBar -->

**[3] `<header>`**

1. `fixed` (positioning: fixed — Compose: Box overlay or BottomBar slot)
2. `top-0` → top: 0dp
3. `w-full` → width: 100%
4. `z-50` (z-index — Compose has no z-index; layering is order-based)
5. `glass-header`
6. `border-b` → border-b width: 1dp
7. `border-outline-variant` → border-color: outline-variant (#3F3822)
8. `h-16` → height: 64dp
9. `flex`
10. `items-center`
11. `justify-between`
12. `px-4` → horizontal padding: 16dp

**[4] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp

**[5] `<button>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `flex`
4. `items-center`
5. `justify-center`
6. `text-primary` → color: primary (#F5D76E)
7. `active:scale-95` (motion: interaction — DROP — touch press feedback)
8. `transition-transform` (motion: transition hint — Entrance/Value family)

**[6] `<span>` — "arrow_back"**

1. `material-symbols-outlined`
2. `text-2xl` → font-size: 24sp

**[7] `<h1>` — "Edit Profile"**

1. `font-headline`
2. `text-headline-sm`
3. `font-bold` → font-weight: 700 (Bold)
4. `text-on-surface` → color: on-surface (#EDE8D5)

**[8] `<button>` — "Save"**

1. `text-primary` → color: primary (#F5D76E)
2. `font-bold` → font-weight: 700 (Bold)
3. `px-2` → horizontal padding: 8dp
4. `hover:opacity-80` (motion: web-only — DROP — pointer/hover)
5. `transition-opacity` (motion: transition hint — Entrance/Value family)
6. `active:scale-95` (motion: interaction — DROP — touch press feedback)

**[9] `<main>`**

1. `flex-1`
2. `mt-16` → top margin: 64dp
3. `px-6` → horizontal padding: 24dp
4. `pb-32` → bottom padding: 128dp

### <!-- Avatar Section -->

**[10] `<section>`**

1. `flex`
2. `flex-col`
3. `items-center`
4. `py-8` → vertical padding: 32dp

- [11] `<div>` `relative`
**[12] `<div>`**

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

**[13] `<span>` — "AJ"**

1. `text-primary` → color: primary (#F5D76E)
2. `font-extrabold` → font-weight: 800 (ExtraBold)
3. `text-2xl` → font-size: 24sp
4. `tracking-tight` → letter-spacing: -0.025em (× font-size for sp)

**[14] `<div>`**

1. `absolute` (positioning: absolute — Compose: Box overlay or BottomBar slot)
2. `bottom-0` → bottom: 0dp
3. `right-0` → right: 0dp
4. `w-7` → width: 28dp
5. `h-7` → height: 28dp
6. `bg-primary-container` → background: primary-container (#4A3200)
7. `rounded-full` → corner-radius: CircleShape
8. `flex`
9. `items-center`
10. `justify-center`
11. `border` → border-width: 1dp
12. `border-background` → border-color: background (#0F0D09)

**[15] `<span>` — "cancel"**

1. `material-symbols-outlined`
2. `text-sm` → font-size: 14sp
3. `text-primary` → color: primary (#F5D76E)
- _inline style_: `font-variation-settings: 'FILL' 1;`

**[16] `<button>` — "Change Photo"**

1. `mt-4` → top margin: 16dp
2. `text-primary` → color: primary (#F5D76E)
3. `font-medium` → font-weight: 500 (Medium)
4. `text-sm` → font-size: 14sp
5. `hover:opacity-80` (motion: web-only — DROP — pointer/hover)
6. `transition-opacity` (motion: transition hint — Entrance/Value family)

### <!-- Form Section -->

**[17] `<section>`**

1. `space-y-6` → children spaced 24dp vertically
2. `max-w-lg`
3. `mx-auto` → horizontal margin: auto

### <!-- Field 1: Full Name -->

**[18] `<div>`**

1. `space-y-2` → children spaced 8dp vertically

**[19] `<label>` — "Full Name"**

1. `block`
2. `text-sm` → font-size: 14sp
3. `font-label`
4. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
5. `px-1` → horizontal padding: 4dp

**[20] `<div>`**

1. `relative`
2. `group`
3. `input-focus-glow`
4. `rounded-xl` → corner-radius: 24dp
5. `transition-all` (motion: transition hint — Entrance/Value family)
6. `duration-200` (motion: transition hint — Entrance/Value family)

**[21] `<div>`**

1. `absolute` (positioning: absolute — Compose: Box overlay or BottomBar slot)
2. `inset-y-0` → inset-y: 0dp
3. `left-0` → left: 0dp
4. `pl-4` → left padding: 16dp
5. `flex`
6. `items-center`
7. `pointer-events-none`

**[22] `<span>` — "person"**

1. `material-symbols-outlined`
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
3. `text-xl` → font-size: 20sp

**[23] `<input>`**

1. `w-full` → width: 100%
2. `h-14` → height: 56dp
3. `bg-[#231F12]` → background: #231F12
4. `border` → border-width: 1dp
5. `border-outline` → border-color: outline (#726A48)
6. `rounded-xl` → corner-radius: 24dp
7. `pl-12` → left padding: 48dp
8. `pr-4` → right padding: 16dp
9. `text-on-surface` → color: on-surface (#EDE8D5)
10. `placeholder:text-outline`
11. `focus:outline-none` (motion: web-only — DROP — pointer/hover)
12. `focus:border-primary` (motion: web-only — DROP — pointer/hover)
13. `transition-colors` (motion: transition hint — Entrance/Value family)
14. `font-body`

### <!-- Field 2: Email Address -->

**[24] `<div>`**

1. `space-y-2` → children spaced 8dp vertically

**[25] `<label>` — "Email Address"**

1. `block`
2. `text-sm` → font-size: 14sp
3. `font-label`
4. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
5. `px-1` → horizontal padding: 4dp

**[26] `<div>`**

1. `relative`
2. `group`
3. `input-focus-glow`
4. `rounded-xl` → corner-radius: 24dp
5. `transition-all` (motion: transition hint — Entrance/Value family)
6. `duration-200` (motion: transition hint — Entrance/Value family)

**[27] `<div>`**

1. `absolute` (positioning: absolute — Compose: Box overlay or BottomBar slot)
2. `inset-y-0` → inset-y: 0dp
3. `left-0` → left: 0dp
4. `pl-4` → left padding: 16dp
5. `flex`
6. `items-center`
7. `pointer-events-none`

**[28] `<span>` — "mail"**

1. `material-symbols-outlined`
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
3. `text-xl` → font-size: 20sp

**[29] `<input>`**

1. `w-full` → width: 100%
2. `h-14` → height: 56dp
3. `bg-[#231F12]` → background: #231F12
4. `border` → border-width: 1dp
5. `border-outline` → border-color: outline (#726A48)
6. `rounded-xl` → corner-radius: 24dp
7. `pl-12` → left padding: 48dp
8. `pr-4` → right padding: 16dp
9. `text-on-surface` → color: on-surface (#EDE8D5)
10. `placeholder:text-outline`
11. `focus:outline-none` (motion: web-only — DROP — pointer/hover)
12. `focus:border-primary` (motion: web-only — DROP — pointer/hover)
13. `transition-colors` (motion: transition hint — Entrance/Value family)
14. `font-body`

**[30] `<p>` — "Changes will be reflected across the app"**

1. `text-center`
2. `text-xs` → font-size: 12sp
3. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
4. `mt-4` → top margin: 16dp
5. `px-4` → horizontal padding: 16dp
6. `leading-relaxed` → line-height: 1.625× font-size

### <!-- Aesthetic Card Block (Bento style hint) -->

**[31] `<section>`**

1. `mt-10` → top margin: 40dp
2. `max-w-lg`
3. `mx-auto` → horizontal margin: auto

**[32] `<div>`**

1. `bg-surface` → background: surface (#1C1910)
2. `border` → border-width: 1dp
3. `border-outline-variant` → border-color: outline-variant (#3F3822)
4. `p-6` → padding: 24dp
5. `rounded-2xl` → corner-radius: 16dp

**[33] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp
4. `mb-4` → bottom margin: 16dp

**[34] `<div>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `rounded-lg` → corner-radius: 16dp
4. `bg-primary-container` → background: primary-container (#4A3200)
5. `flex`
6. `items-center`
7. `justify-center`

**[35] `<span>` — "security"**

1. `material-symbols-outlined`
2. `text-primary` → color: primary (#F5D76E)

**[37] `<h3>` — "Privacy Settings"**

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `font-bold` → font-weight: 700 (Bold)

**[38] `<p>` — "Manage your visibility and data"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-xs` → font-size: 12sp

**[39] `<span>` — "chevron_right"**

1. `material-symbols-outlined`
2. `ml-auto` → left margin: auto
3. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[40] `<div>`**

1. `h-px` → height: 1dp
2. `bg-outline-variant` → background: outline-variant (#3F3822)
3. `w-full` → width: 100%
4. `my-4` → vertical margin: 16dp

**[41] `<div>`**

1. `flex`
2. `items-center`
3. `gap-4` → gap: 16dp

**[42] `<div>`**

1. `w-10` → width: 40dp
2. `h-10` → height: 40dp
3. `rounded-lg` → corner-radius: 16dp
4. `bg-surface-variant` → background: surface-variant (#302B1C)
5. `flex`
6. `items-center`
7. `justify-center`

**[43] `<span>` — "notifications"**

1. `material-symbols-outlined`
2. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

**[45] `<h3>` — "Notifications"**

1. `text-on-surface` → color: on-surface (#EDE8D5)
2. `font-bold` → font-weight: 700 (Bold)

**[46] `<p>` — "Alerts and status updates"**

1. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)
2. `text-xs` → font-size: 12sp

**[47] `<span>` — "chevron_right"**

1. `material-symbols-outlined`
2. `ml-auto` → left margin: auto
3. `text-on-surface-variant` → color: on-surface-variant (#C4BA94)

### <!-- Fixed Bottom Button -->

**[48] `<div>`**

1. `fixed` (positioning: fixed — Compose: Box overlay or BottomBar slot)
2. `bottom-0` → bottom: 0dp
3. `left-0` → left: 0dp
4. `w-full` → width: 100%
5. `p-6` → padding: 24dp
6. `bg-gradient-to-t`
7. `from-background`
8. `via-background/90`
9. `to-transparent`

**[49] `<button>`**

1. `w-full` → width: 100%
2. `h-14` → height: 56dp
3. `bg-primary` → background: primary (#F5D76E)
4. `text-on-primary` → color: on-primary (#2C1900)
5. `rounded-xl` → corner-radius: 24dp
6. `font-bold` → font-weight: 700 (Bold)
7. `flex`
8. `items-center`
9. `justify-center`
10. `gap-3` → gap: 12dp
11. `active:scale-[0.98]` (motion: interaction — DROP — touch press feedback)
12. `transition-transform` (motion: transition hint — Entrance/Value family)
13. `shadow-lg` → shadow: ~8dp elevation
14. `shadow-primary/5` → shadow-color: primary (#F5D76E) @ 5%
15. `max-w-lg`
16. `mx-auto` → horizontal margin: auto

**[50] `<span>` — "check Save Changes"**

1. `material-symbols-outlined`
- _inline style_: `font-variation-settings: 'wght' 600;`

---
**Total elements**: 50 | **Visual**: 46 | **Layout-only**: 2 | **Total classes**: 243 | **Auto-converted**: 142 (58%)

